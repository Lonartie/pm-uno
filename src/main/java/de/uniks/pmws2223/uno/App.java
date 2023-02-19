package de.uniks.pmws2223.uno;

import de.uniks.pmws2223.uno.controller.Controller;
import de.uniks.pmws2223.uno.controller.SetupController;
import de.uniks.pmws2223.uno.service.BotService;
import de.uniks.pmws2223.uno.service.GameService;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends Application {
    private Stage stage;
    private Controller controller;
    private final GameService gameService;
    private final BotService botService;

    public App() {
        this(new GameService(), new BotService());
    }

    public App(GameService gameService, BotService botService) {
        this.gameService = gameService;
        this.botService = botService;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.stage = primaryStage;
        primaryStage.setScene(new Scene(new Label("Loading...")));
        primaryStage.setTitle("Uno");

        show(new SetupController(this, gameService, botService));
        primaryStage.show();

        primaryStage.setOnCloseRequest(e -> controller.destroy());
    }

    public void show(Controller controller) {
        controller.init();
        try {
            stage.getScene().setRoot(controller.render());
        } catch (IOException ex) {
            ex.printStackTrace();
            return;
        }

        if (this.controller != null) {
            this.controller.destroy();
        }
        this.controller = controller;
        stage.setTitle(controller.getTitle());
    }
}