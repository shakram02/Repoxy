import utils.CommonMain
import utils.CommonMain.TIMEOUT_MILLIS
import utils.CommonMain.WIND_SIZE
import utils.ProxyBuilder
import watchers.ClientCounter
<<<<<<< HEAD
import watchers.OFDelayChecker
=======
import watchers.packet_verification.OFDelayChecker
import java.util.logging.Logger
>>>>>>> Split delay checking functionality

class Controller {

    private lateinit var backgroundThread: Thread

    private var isRunning = false

    init {
        CommonMain.setupLogging()
    }

    fun startServer(configurator: Configurator) {
        if (isRunning) return

        backgroundThread = Thread({ threadMain(configurator) })

        backgroundThread.start()
        isRunning = true
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
            portList.add(it.port.toString())
        })

        val portStringBuilder = StringBuilder()
        portList.joinTo(portStringBuilder)


        println(String.format("Local IP: [%s] Ports: [%d] [%s]",
                configurator.localIp, configurator.localPort, portStringBuilder.toString()))

        CommonMain.startProxy(configurator.localIp, configurator.localPort, configurator.getConfigs())
    }
}