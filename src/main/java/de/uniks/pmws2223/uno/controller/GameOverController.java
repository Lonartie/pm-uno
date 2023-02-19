package de.uniks.pmws2223.uno.controller;

import de.uniks.pmws2223.uno.App;
import de.uniks.pmws2223.uno.Main;
import de.uniks.pmws2223.uno.service.BotService;
import de.uniks.pmws2223.uno.service.GameService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.text.Text;

import java.io.IOException;
import java.util.List;

public class GameOverController implements Controller {
    @FXML
    private Text stateText;

    private final App app;
    private final GameService gameService;
    private final BotService botService;
    private final List<String> winners;

    public GameOverController(App app, GameService gameService, BotService botService, List<String> winners) {
        this.app = app;
        this.gameService = gameService;
        this.botService = botService;
        this.winners = winners;
    }

    @Override
    public String getTitle() {
        return "Game Over";
    }

    @Override
    public void init() {

    }

    @Override
    public Parent render() throws IOException {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("view/GameOver.fxml"));
        loader.setControllerFactory(c -> this);
        Parent parent = loader.load();

        StringBuilder builder = new StringBuilder();
        for (int i = 1; i <= winners.size(); i++) {
            builder.append(i).append(".: ").append(winners.get(i - 1)).append("\n");
        }
        stateText.setText(builder.toString());

        return parent;
    }

    @Override
    public void destroy() {

    }

    public void setup() {
        app.show(new SetupController(app, gameService, botService));
    }
}
