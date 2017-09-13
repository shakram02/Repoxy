import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    Controller controller = new Controller();
    Stage primaryStage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;

        primaryStage.setTitle("Hello World");
        UiBuilder uiBuilder = new UiBuilder(primaryStage, this::onConfigEnd, this::onServerStop);
        Parent mainNode = uiBuilder.build();

        double WINDOW_HEIGHT = 600.0;
        double WINDOW_WIDTH = 400.0;
        primaryStage.setScene(new Scene(mainNode, WINDOW_WIDTH, WINDOW_HEIGHT));
        primaryStage.setOnCloseRequest(e -> {
            this.onServerStop();
            primaryStage.close();
        });

        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }

    private void onConfigEnd(Configurator configurator) {
        controller.startServer(configurator);
    }

    private void onServerStop() {
        try {
            controller.stopServer();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}
