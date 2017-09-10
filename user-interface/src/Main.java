import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Hello World");
        UiBuilder uiBuilder = new UiBuilder(primaryStage,this::onConfigEnd);
        primaryStage.setScene(uiBuilder.build());
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }

    private void onConfigEnd(Configurator configurator) {

    }
}
