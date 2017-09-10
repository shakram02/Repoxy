import utils.ProxyBuilder

class Controller {

    private lateinit var backgroundThread: Thread
    private val builder: ProxyBuilder = ProxyBuilder.createInstance()


    fun startServer(configurator: Configurator) {
        // Add other controllers
        configurator.getConfigs().forEach({ addController(it.first, it.second) })

        backgroundThread = Thread({ builder.startServer(configurator.localIp, configurator.localPort) })
        backgroundThread.start()
    }

    private fun addController(ip: String, port: Int) {
        builder.addController(ip, port)
    }
}