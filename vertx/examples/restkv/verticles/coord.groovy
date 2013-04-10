
def logger = container.logger
def conf = [hosts: ['volt3a','volt3b','volt3c']]

container.deployModule('com.voltdb.persistor-v1.0', conf, 2) { deplID ->
    logger.info "VoltDB module deployment id: ${deplID}"
}

container.deployVerticle('vertone.groovy',[:], 8) { deplID ->
    logger.info "Verticle deployment id: $deplID"
}
