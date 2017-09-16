import Configurator.ControllerConfig
import javafx.beans.property.SimpleBooleanProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.geometry.Insets
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.control.*
import javafx.scene.layout.GridPane
import javafx.stage.Stage
import java.util.function.Consumer


const val HGAP_SIZE = 6.0
const val VGAP_SIZE = 6.0
const val INSET_SIZE = 6.0

class UiBuilder(private val stage: Stage, private val onRun: Consumer<Configurator>,
                private val onStop: Runnable) {

    private var controllerInfoList: ObservableList<ControllerConfig>
            = FXCollections.observableArrayList()
    private var configurator: Configurator = Configurator(controllerInfoList)
    private var disableEditing = SimpleBooleanProperty(false)

    private lateinit var startButton: Button
    private lateinit var stopButton: Button

    fun build(): Parent {
        val mainGrid = GridPane()
        mainGrid.padding = Insets(INSET_SIZE, INSET_SIZE, INSET_SIZE, INSET_SIZE)

        this.createWithOrder(mainGrid)
        return mainGrid
    }

    private fun createWithOrder(gridPane: GridPane) {
        gridPane.append(fxAddHostInfo())
        gridPane.append(fxAddController())
        gridPane.append(fxCreateControllerList())
        gridPane.append(fxSaveLoadButtons())
        gridPane.append(fxCreateControlButtons())
    }

    private fun fxAddHostInfo(): Node {
        val gridPane = createGrid()

        val ipEntry = fxFormEntry("Host IP", configurator.getDefaultInterfaceAddress())
        fxAddFormEntryToGrid(ipEntry, gridPane)

        val ipEntryField = ipEntry.field
        ipEntryField.onAction = EventHandler { configurator.localIp = ipEntryField.text }

        val portEntry = fxFormEntry("Host Port", configurator.localPort.toString())
        fxAddFormEntryToGrid(portEntry, gridPane)

        val portEntryField = portEntry.field
        portEntryField.onAction = EventHandler { configurator.localPort = Integer.parseInt(portEntryField.text) }

        return gridPane
    }

    private fun fxAddController(): Node {
        val gridPane = createGrid()

        val ipEntry = fxFormEntry("Controller IP", "")
        fxAddFormEntryToGrid(ipEntry, gridPane)
        val ipEntryField = ipEntry.field

        val portEntry = fxFormEntry("Controller Port", "")
        fxAddFormEntryToGrid(portEntry, gridPane)
        val portEntryField = portEntry.field


        createEditModeButton("Add controller",
                gridPane,
                EventHandler {
                    val controllerIp = ipEntryField.text
                    val controllerPort = Integer.parseInt(portEntryField.text)
                    val addressInfo = ControllerConfig(controllerIp, controllerPort)

                    controllerInfoList.add(addressInfo)

                }
        )

        return gridPane
    }

    private fun fxCreateControllerList(): Node {
        val infoList = ListView<ControllerConfig>()
        infoList.items = controllerInfoList
        val listGridPane = createGrid()
        listGridPane.append(infoList)

        return listGridPane
    }

    private fun fxCreateConsoleView(): Node {
        TODO("Create a textArea to redirect console output to")
    }

    private fun fxCreateControlButtons(): Node {
        startButton = Button()
        startButton.text = "Start"
        startButton.onAction = EventHandler {
            onRun.accept(configurator)
            disableEditing.set(true)
        }

        stopButton = Button()
        stopButton.text = "Stop"
        stopButton.onAction = EventHandler {
            this.onStop.run()
            this.disableEditing.set(false)
        }

        val closeButton = Button()
        closeButton.text = "Close"
        closeButton.onAction = EventHandler {
            try {
                this.onStop.run()
            } catch (e: Exception) {
                System.err.println(e.message)
            }
            stage.close()
        }

        val controlGrid = createGrid()
        controlGrid.append(startButton)
        controlGrid.appendBesideLast(stopButton)
        controlGrid.append(closeButton)

        return controlGrid
    }

    private fun fxSaveLoadButtons(): Node {
        val configSaveLoad = ConfigSaveLoadHandler()
        val grid = createGrid()

        val exportButton = Button()
        exportButton.text = "Export Config"
        exportButton.onAction = EventHandler { configSaveLoad.saveConfig(stage, configurator) }

        createEditModeButton(
                "Load config",
                grid,
                EventHandler { configSaveLoad.loadConfig(stage, this.configurator) }
        )

        grid.appendBesideLast(exportButton)


        return grid
    }

    private fun fxFormEntry(labelText: String, fieldText: String)
            : FormEntry {
        return FormEntry(fxLabel(labelText), fxTextField(fieldText))
    }

    private fun fxAddFormEntryToGrid(formEntry: FormEntry, grid: GridPane) {
        grid.append(formEntry.label)
        grid.appendBesideLast(formEntry.field)
    }

    private fun fxTextField(initialText: String): TextField {
        val textField = TextField()
        textField.text = initialText
        textField.disableProperty().bind(this.disableEditing)
        return textField
    }

    private fun fxLabel(text: String): Label {
        val label = Label()
        label.text = text

        return label
    }

    private fun createGrid(): GridPane {
        val gridPane = GridPane()
        gridPane.hgap = HGAP_SIZE
        gridPane.vgap = VGAP_SIZE

        gridPane.padding = Insets(INSET_SIZE, INSET_SIZE, INSET_SIZE, INSET_SIZE)
        return gridPane
    }

    private fun createEditModeButton(text: String, gridPane: GridPane? = null,
                                     eventHandler: EventHandler<ActionEvent>): Button {
        val button = createButton(text, eventHandler, gridPane)
        button.disableProperty().bind(this.disableEditing)

        return button
    }

    private fun createButton(text: String, eventHandler: EventHandler<ActionEvent>
                             , gridPane: GridPane? = null): Button {
        val button = Button()
        button.text = text
        button.onAction = eventHandler

        if (gridPane != null) {
            gridPane.append(button)
        }

        return button
    }

    private fun GridPane.append(node: Node) {
        // Get the biggest row index
        val maxElement = this.children.maxBy { GridPane.getRowIndex(it) }

        val maxRow: Int = if (maxElement != null) GridPane.getRowIndex(maxElement) else -1
        this.add(node, 0, maxRow + 1)
    }

    private fun GridPane.appendBesideLast(node: Node) {
        val maxElement = this.children.maxBy { GridPane.getRowIndex(it) }
        val maxRow: Int = if (maxElement != null) GridPane.getRowIndex(maxElement) else -1
        val maxCol: Int = if (maxElement != null) GridPane.getColumnIndex(maxElement) else -1

        this.add(node, maxCol + 1, maxRow)
    }

    private data class FormEntry(val label: Label, val field: TextField)
}