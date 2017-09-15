import utils.ConfigContants.TIMEOUT_MILLIS
import utils.ConfigContants.WIND_SIZE
import utils.ProxyBuilder
import utils.logging.ColoredConsoleHandler
import watchers.ClientCounter
import watchers.OFDelayChecker
import java.util.logging.Logger

class Controller {

    private lateinit var backgroundThread: Thread
    private val builder: ProxyBuilder = ProxyBuilder.createInstance()
    private var isRunning = false

    init {
        setupLogging()
    }

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

        val mediator = builder.mediator

        val counter = ClientCounter()
        val packetVerifier = OFDelayChecker(WIND_SIZE, mediator, TIMEOUT_MILLIS)


        mediator.registerWatcher(counter)
        mediator.registerWatcher(packetVerifier)

        while (!Thread.interrupted()) {
            mediator.cycle()
        }

        mediator.close()
    }

    private fun setupLogging() {
        val globalLogger = Logger.getLogger("")

        // Remove the default console handler
        for (h in globalLogger.handlers) {
            globalLogger.removeHandler(h)
        }

        // Add custom handler
        globalLogger.addHandler(ColoredConsoleHandler())
    }
}