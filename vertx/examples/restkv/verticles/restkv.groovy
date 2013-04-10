import org.vertx.groovy.core.http.RouteMatcher
import groovy.json.JsonBuilder

def logger = container.logger
def eb = vertx.eventBus

def rm = new RouteMatcher()

def formUrlEncodedToMap(buff) {
    buff.toString().split('&').collect { 
        it.split('=').collect { URLDecoder.decode(it) }
    }.findAll { it.size == 2 }.collectEntries()
}

String invokeAs( String pname, String...pargs) {
    JsonBuilder invocationBuilder = new JsonBuilder()
    invocationBuilder {
        name pname
        params pargs
    }
    invocationBuilder.toString()
}

rm.get('/volt/kv/:key') { req ->
    eb.send("volt", invokeAs("Get", req.params['key'])) { reply ->
        req.response.headers["Content-Type"] = 'application/json; charset=UTF-8'
        req.response.end reply.body
    }
}

rm.post('/volt/kv') { req ->
    req.bodyHandler { body ->
        def params = formUrlEncodedToMap(body).subMap(['key','value'])
        eb.send("volt", invokeAs("Put", params.key, params.value)) { reply ->
            req.response.headers["Content-Type"] = 'application/json; charset=UTF-8'
            req.response.end reply.body
        }
    }
}

rm.delete('/volt/kv/:key') { req ->
    eb.send("volt", invokeAs("Remove", req.params['key'])) { reply ->
        req.response.headers["Content-Type"] = 'application/json; charset=UTF-8'
        req.response.end reply.body
    }
}

/*
container.deployModule('com.voltdb.persistor-v1.0',[ hosts: ['localhost'] ], 1) { deplID ->
    logger.info "VoltDB module deployment id: ${deplID}"
}
*/

vertx.createHttpServer().requestHandler(rm.asClosure()).listen(9000)
