import javafx.collections.ObservableList
import utils.LocalhostIpSupplier

private val LOOP_BACK_IP = "127.0.0.1"
private val OF_DEFAULT_PORT = 6833


class Configurator(private val controllerConfigs: ObservableList<ControllerConfig>
                   , var localIp: String = LOOP_BACK_IP,
                   var localPort: Int = OF_DEFAULT_PORT, private val ipPrefix: String = "192") {

    fun addController(addressInfo: Pair<String, Int>) {
        val ip = addressInfo.first
        val port = addressInfo.second

        controllerConfigs.add(ControllerConfig(ip, port))
    }

    fun getConfigs(): Array<ControllerConfig> {
        if (controllerConfigs.isEmpty()) {
            createOneMachineDefaults()
        }

        if (localIp.contentEquals(LOOP_BACK_IP)) {
            localIp = getDefaultInterfaceAddress()
        }

        return controllerConfigs.toTypedArray()
    }

    private fun createOneMachineDefaults() {
        addController(Pair("192.168.1.104", 6834))
        addController(Pair("192.168.1.105", 6835))
    }

    fun getDefaultInterfaceAddress(): String {
        try {
            return LocalhostIpSupplier.getLocalHostLANAddress(ipPrefix)
        } catch (e: Exception) {
            println(e.message)
        }

        return LOOP_BACK_IP
    }

    override fun toString(): String {
        val builder = StringBuilder()
        builder.appendln(localIp)
        builder.appendln(localPort)

        this.controllerConfigs.forEach({ (ip, port) ->
            builder.appendln(ip)
            builder.appendln(port)
        })

        return builder.toString()
    }

    companion object {
        fun fromString(str: String, destination: Configurator) {
            val lines = str.split("\n").filter { it.isNotEmpty() }
            val iterator = lines.iterator()
            destination.localIp = iterator.next()
            destination.localPort = Integer.parseInt(iterator.next())


            while (iterator.hasNext()) {
                val ip = iterator.next()
                val port = Integer.parseInt(iterator.next())
                destination.addController(Pair(ip, port))
            }
        }
    }

    data class ControllerConfig(val ip: String, val port: Int) {
        override fun toString(): String {
            return "IP:$ip, Port:$port"
        }
    }
}
