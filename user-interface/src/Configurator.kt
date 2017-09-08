class Configurator(var localIp: String = "127.0.0.1", var localPort: Int = 6833) {
    private val controllerConfigs = ArrayList<Pair<String, Int>>()

    fun addController(ip: String, port: Int) {
        controllerConfigs.add(Pair(ip, port))
    }

    fun getConfigs(): ArrayList<Pair<String, Int>> {
        if (controllerConfigs.isEmpty()) {
            createOneMachineDefaults()
        }
        return controllerConfigs
    }

    private fun createOneMachineDefaults() {
        addController("127.0.0.1", 6834)
        addController("127.0.0.1", 6835)
    }
}