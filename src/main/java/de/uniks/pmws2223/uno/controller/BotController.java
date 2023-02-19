package de.uniks.pmws2223.uno.controller;

import de.uniks.pmws2223.uno.Main;
import de.uniks.pmws2223.uno.model.Bot;
import de.uniks.pmws2223.uno.model.Card;
import de.uniks.pmws2223.uno.model.Game;
import de.uniks.pmws2223.uno.service.BotService;
import de.uniks.pmws2223.uno.service.GameService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

import java.beans.PropertyChangeEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.function.Consumer;

public class BotController implements Controller {
    @FXML
    private Text botNameText;
    @FXML
    private HBox botCardsLayout;
    @FXML
    private ScrollPane botCardsPane;

    private final Bot bot;
    private final Game game;
    private final GameService gameService;
    private final BotService botService;
    private final List<CardController> cardControllers = new ArrayList<>();
    private final Timer timer = new Timer();

    public BotController(Bot bot, Game game, GameService gameService, BotService botService) {
        this.bot = bot;
        this.game = game;
        this.gameService = gameService;
        this.botService = botService;
    }

    @Override
    public String getTitle() {
        return null;
    }

    @Override
    public void init() {

    }

    @Override
    public Parent render() throws IOException {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("view/Bot.fxml"));
        loader.setControllerFactory(c -> this);
        Parent parent = loader.load();

        botNameText.setText(bot.getName());

        bot.listeners().addPropertyChangeListener(Bot.PROPERTY_CARDS, this::updateCards);
        game.listeners().addPropertyChangeListener(Game.PROPERTY_CURRENT_PLAYER, this::updateCurrentPlayer);

        updateCards(null);
        updateCurrentPlayer(null);

        return parent;
    }

    @Override
    public void destroy() {
        timer.cancel();

        bot.listeners().removePropertyChangeListener(Bot.PROPERTY_CARDS, this::updateCards);
        game.listeners().removePropertyChangeListener(Game.PROPERTY_CURRENT_PLAYER, this::updateCurrentPlayer);

        for (CardController cardController : cardControllers) {
            cardController.destroy();
        }
    }

    private void play() {
        timer.schedule(new java.util.TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    botService.play(gameService, game, bot);
                });
            }
        }, 2000);
    }

    private void updateCurrentPlayer(PropertyChangeEvent propertyChangeEvent) {
        if (propertyChangeEvent != null && propertyChangeEvent.getNewValue() == bot) {
            botCardsPane.setStyle("-fx-border-color: red;-fx-border-width: 3;");
            play();
        } else {
            botCardsPane.setStyle("");
        }
    }

    private void updateCards(PropertyChangeEvent propertyChangeEvent) {
        for (CardController cardController : cardControllers) {
            cardController.destroy();
        }
        botCardsLayout.getChildren().clear();

        for (Card card : bot.getCards()) {
            CardController cardController = new CardController(bot, gameService, game, card, false);
            cardController.init();
            cardControllers.add(cardController);

            try {
                botCardsLayout.getChildren().add(cardController.render());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
