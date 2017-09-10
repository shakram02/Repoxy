import javafx.stage.FileChooser
import javafx.stage.Stage

class ConfigSaveLoadHandler {
    fun saveConfig(stage: Stage, configurator: Configurator) {
        val file = createFileChooser().showSaveDialog(stage) ?: return
        file.printWriter().use { writer ->
            val content = configurator.toString()
            writer.write(content)
        }
    }

    fun loadConfig(stage: Stage, loadDestination: Configurator) {
        val file = createFileChooser().showOpenDialog(stage) ?: return

        file.reader().use { reader ->
            return Configurator.fromString(reader.readText(), loadDestination)
        }
    }

    private fun createFileChooser(): FileChooser {
        val fileChooser = FileChooser()
        val textFileFilter = FileChooser.ExtensionFilter("Text files (*.txt)", "*.txt")

        fileChooser.initialFileName = "Proxy-config.txt"
        fileChooser.extensionFilters.add(textFileFilter)
        return fileChooser
    }
}