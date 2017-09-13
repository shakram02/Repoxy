import utils.ProxyBuilder

class Controller {

    private lateinit var backgroundThread: Thread
    private val builder: ProxyBuilder = ProxyBuilder.createInstance()
    private var isRunning = false

    fun startServer(configurator: Configurator) {
        if (isRunning) return

        backgroundThread = Thread({ threadMain(configurator) })

        backgroundThread.start()
        isRunning = true
    }

    private fun addController(ip: String, port: Int) {
        builder.addController(ip, port)
    }

    fun stopServer() {
        if (!isRunning) return

        backgroundThread.interrupt()
        isRunning = false
    }

    private fun threadMain(configurator: Configurator) {

        val portList = ArrayList<String>()


        // Add other controllers
        configurator.getConfigs().forEach({
            addController(it.ip, it.port)
            portList.add(it.port.toString())
        })

        val portStringBuilder = StringBuilder()
        portList.joinTo(portStringBuilder)

        builder.startServer(configurator.localIp, configurator.localPort)
        println(String.format("Local IP: [%s] Ports: [%d] [%s]",
                configurator.localIp, configurator.localPort, portStringBuilder.toString()))
    }
}