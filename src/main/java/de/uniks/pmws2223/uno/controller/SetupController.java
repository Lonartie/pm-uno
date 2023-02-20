package de.uniks.pmws2223.uno.controller;

import de.uniks.pmws2223.uno.App;
import de.uniks.pmws2223.uno.Constants;
import de.uniks.pmws2223.uno.Main;
import de.uniks.pmws2223.uno.model.Game;
import de.uniks.pmws2223.uno.service.BotService;
import de.uniks.pmws2223.uno.service.GameService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.util.List;

public class SetupController implements Controller {
    @FXML
    public TextField nameField;
    @FXML
    public Slider botsSlider;
    @FXML
    private ComboBox<String> modeComboBox;
    private final App app;
    private final GameService gameService;
    private final BotService botService;
    private final Game game;

    public SetupController(App app, GameService gameService, BotService botService) {
        this.app = app;
        this.gameService = gameService;
        this.botService = botService;
        game = new Game();
    }

    @Override
    public String getTitle() {
        return "Setup";
    }

    @Override
    public void init() {

    }

    @Override
    public Parent render() throws IOException {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("view/Setup.fxml"));
        loader.setControllerFactory(c -> this);
        Parent parent = loader.load();
        modeComboBox.setItems(FXCollections.observableArrayList(List.of(Constants.GAME_MODE_1, Constants.GAME_MODE_2)));
        modeComboBox.getSelectionModel().select(0);
        return parent;
    }

    @Override
    public void destroy() {

    }

    public void play() {
        gameService.setGameMode(modeComboBox.getValue());
        gameService.startGame(game, nameField.getText(), (int) botsSlider.getValue());
        app.show(new IngameController(app, game, gameService, botService));
    }
}
