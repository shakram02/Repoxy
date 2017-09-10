import javafx.collections.ObservableList

val LOOP_BACK_IP = "127.0.0.1"
val OF_DEFAULT_PORT = 6833


class Configurator(private val controllerConfigs: ObservableList<Pair<String, Int>>
                   , var localIp: String = LOOP_BACK_IP, var localPort: Int = OF_DEFAULT_PORT) {

    fun addController(addressInfo: Pair<String, Int>) {
        val ip = addressInfo.first
        val port = addressInfo.second

        controllerConfigs.add(Pair(ip, port))
    }

    fun getConfigs(): Array<Pair<String, Int>> {
        if (controllerConfigs.isEmpty()) {
            createOneMachineDefaults()
        }
        return controllerConfigs.toTypedArray()
    }

    private fun createOneMachineDefaults() {
        addController(Pair("127.0.0.1", 6834))
        addController(Pair("127.0.0.1", 6835))
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
}