/* This file is part of VoltDB.
 * Copyright (C) 2008-2013 VoltDB Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS BE LIABLE FOR ANY CLAIM, DAMAGES OR
 * OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */

package org.voltdb.vertx.mods

import org.vertx.java.core.logging.Logger

import org.voltdb.VoltTable
import org.voltdb.ClientResponseImpl
import org.voltdb.exceptions.SerializableException

import org.voltdb.client.*

import java.util.concurrent.*
import java.util.concurrent.atomic.*

import groovy.transform.TypeChecked

@TypeChecked
class VoltClient {
    AtomicBoolean doShutdown = new AtomicBoolean(false)
    AtomicStampedReference<Client> clref = new AtomicStampedReference<Client>(null, 0)
    volatile int clientStamp = 0;
    List<String> hosts = new ArrayList<String>();

    Logger log

    static final ExecutorService es = Executors.newCachedThreadPool(new ThreadFactory() {
        public Thread newThread(Runnable arg0) {
            Thread thread = new Thread(arg0, "Volt Connection Retrier");
            thread.setDaemon(true);
            return thread;
        }
    })

    Closure connect = {  String hostname ->
        int[] stamp = new int[1]
        Client client = clref.get(stamp)
        int sleep = 1000
        while (!doShutdown.get() && client != null && clref.getStamp() == stamp[0]) {
            Exception connExc = null
            try {
                client.createConnection(hostname)
                log.info "Connected to ${hostname}"
                return
            } catch (java.net.ConnectException ex) {
                log.warn "failed connection to ${hostname}: [${ex.getClass().getName()}] ${ex.getMessage()}"
            } catch (java.io.IOException ex) {
                connExc = ex
                return
            } catch (Exception ex) {
                connExc = ex
            } finally {
                if (connExc) {
                    log.error "failed connection to ${hostname}: [${connExc.getClass().getName()}] ${connExc.getMessage()}"
                    try { client.close()} catch (Exception ignoreIt) {}
                    clref.compareAndSet(client,null,stamp[0],stamp[0]+1)
                }
            }
            try { Thread.sleep(sleep) } catch (InterruptedException ignoreIt) {}
            if (sleep < 8000) sleep += sleep;
        }
    }

    class StatusListener extends ClientStatusListenerExt {
        Closure connectionLost

        public void connectionLost(
                String hostname,
                int port,
                int connectionsLeft,
                ClientStatusListenerExt.DisconnectCause cause) {
            if (!doShutdown.get()) {
                log.warn "detected connection loss to ${hostname}:${port}"
                es.submit( connectionLost.curry("${hostname}:${port}") as Runnable)
            }
        }
    }

    ClientConfig clconf = new ClientConfig("","", new StatusListener(connectionLost: connect))

    boolean createOrReplaceClient(Client old, int oldStamp) {
        Client young = ClientFactory.createClient(clconf)
        Client closeThis = young
        if (clref.compareAndSet(old,young,oldStamp,oldStamp+1)) {
            closeThis = old
            clientStamp = oldStamp + 1
        }

        try { closeThis?.close() } catch (Exception ignoreIt) {}
        if (closeThis == old) connectToAtLeastOne(hosts)
        return closeThis == old
    }

    public VoltClient(List<String> hostAndPorts, Logger logger ) {
        log = logger
        hosts.addAll(hostAndPorts)
        createOrReplaceClient(null,0)
    }

    public void shutdown() {
        doShutdown.set(true)
        int[] stamp = new int[1]
        Client client = clref.get(stamp)
        if (clref.compareAndSet(client,null,stamp[0],stamp[0]+1)) {
            try { client?.close() } catch (Exception ignoreIt) {}
        }
    }

    public Client getClient() {
        int [] stamp = new int[1]
        Client client = clref.get(stamp)
        while (client == null) {
            createOrReplaceClient(client,stamp[0])
            client = clref.get(stamp)
        }
        return client
    }

    void connectToAtLeastOne( List<String> hostAndPorts) {
        if ( !hostAndPorts) return

        final CountDownLatch connections = new CountDownLatch(1)

        hostAndPorts.each {
            final Closure curriedConnect = connect.curry(it)
            Thread.startDaemon("Volt connection attempter to ${it}") {
                curriedConnect()
                connections.countDown()
            }
        }
        connections.await()
    }

    boolean callProcedure(Closure callback, String procName, Object ... params) {
        try {
            return getClient().callProcedure( callback as ProcedureCallback, procName, params)
        } catch (Exception ex) {
            callback(new ClientResponseImpl(
                    (byte)-7,
                    new VoltTable[0],
                    "immediate invocation exception",
                    new SerializableException(ex)
            ))
            return false
        }
    }

    public boolean callProcedure( VoltInvocation invocation, Closure callback ) {
        return callProcedure( callback, invocation.name, invocation.params)
    }
}
