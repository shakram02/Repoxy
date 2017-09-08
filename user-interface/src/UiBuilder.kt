import javafx.event.EventHandler
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.Scene
import javafx.scene.control.CheckBox
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.layout.FlowPane
import javafx.scene.layout.GridPane
import javafx.scene.layout.Pane


const val HGAP_SIZE = 10.0
const val VGAP_SIZE = 10.0
const val INSET_SIZE = 10.0

class UiBuilder {
    val buildingFunctions = ArrayList<() -> Node>()
    private val configurator: Configurator = Configurator()

    fun build(): Scene {
        val grid = GridPane()
        grid.padding = Insets(INSET_SIZE, INSET_SIZE, INSET_SIZE, INSET_SIZE)
        this.createWithOrder(grid)
        return Scene(grid, 400.0, 600.0)
    }

    private fun createWithOrder(gridPane: GridPane) {
        this.buildingFunctions.add(this::fxUseOneMachineOnly)
        this.buildingFunctions.add(this::fxAddHostInfo)

        this.buildingFunctions.forEachIndexed { index, action ->
            gridPane.add(action(), 0, index)
        }
    }

    private fun fxUseOneMachineOnly(): Node {
        val box = CheckBox()
        box.text = "Single machine mode"
        return box
    }

    private fun fxAddHostInfo(): Node {
        val flowPane = FlowPane()
        flowPane.hgap = HGAP_SIZE
        flowPane.vgap = VGAP_SIZE

        fxLabel("Host IP", flowPane)
        val ipEntryField = fxTextField(configurator.localIp, flowPane)
        ipEntryField.onAction = EventHandler { configurator.localIp = ipEntryField.text }

        return flowPane
    }

    private fun fxAddController(): Node {
        TODO("not implemented")
    }

    private fun fxControllerList(): Node {
        TODO("not implemented")
    }

    private fun fxTextField(initialText: String, container: Pane): TextField {
        val textField = TextField()
        textField.text = initialText
        container.children.add(textField)
        return textField
    }

    private fun fxLabel(text: String, container: Pane) {
        val label = Label()
        label.text = text
        container.children.add(label)
    }

}