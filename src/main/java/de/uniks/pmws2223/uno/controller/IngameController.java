package de.uniks.pmws2223.uno.controller;

import de.uniks.pmws2223.uno.App;
import de.uniks.pmws2223.uno.Constants;
import de.uniks.pmws2223.uno.Main;
import de.uniks.pmws2223.uno.model.*;
import de.uniks.pmws2223.uno.service.BotService;
import de.uniks.pmws2223.uno.service.GameService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;

import java.beans.PropertyChangeEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class IngameController implements Controller {

    @FXML
    private HBox playerCardsLayout;
    @FXML
    private ScrollPane playerCardsPane;
    @FXML
    private HBox botsLayout;
    @FXML
    private HBox pilesLayout;

    private final App app;
    private final Game game;
    private final GameService gameService;
    private final BotService botService;
    private final Player player;
    private final List<CardController> cardControllers = new ArrayList<>();
    private final List<BotController> botControllers = new ArrayList<>();
    private final List<String> winners = new ArrayList<>();
    private CardController discardPileController;
    private Parent discardPileParent;

    public IngameController(App app, Game game, GameService gameService, BotService botService) {
        this.app = app;
        this.game = game;
        this.gameService = gameService;
        this.botService = botService;
        //noinspection OptionalGetWithoutIsPresent
        player = game.getPlayers()
                .stream()
                .filter(p -> !(p instanceof Bot))
                .findFirst()
                .get();
    }

    @Override
    public String getTitle() {
        return "Ingame";
    }

    @Override
    public void init() {
    }

    @Override
    public Parent render() throws IOException {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("view/Ingame.fxml"));
        loader.setControllerFactory(c -> this);
        Parent parent = loader.load();

        for (Bot bot : game.getPlayers()
                .stream()
                .filter(p -> p instanceof Bot)
                .map(p -> (Bot) p)
                .toList()) {
            BotController botController = new BotController(bot, game, gameService, botService);
            botController.init();
            botControllers.add(botController);
            botsLayout.getChildren().add(botController.render());
        }

        player.listeners().addPropertyChangeListener(Player.PROPERTY_CARDS, this::updateCards);
        game.listeners().addPropertyChangeListener(Game.PROPERTY_DISCARD_PILE, this::updateDiscardPile);
        game.listeners().addPropertyChangeListener(Game.PROPERTY_CURRENT_PLAYER, this::updateCurrentPlayer);
        game.listeners().addPropertyChangeListener(Game.PROPERTY_COLOR_WISH, this::updateColorWish);
        if (gameService.getGameMode().equals(Constants.GAME_MODE_1)) {
            game.listeners().addPropertyChangeListener(Game.PROPERTY_DISCARD_PILE, this::checkGameOver);
        } else {
            game.listeners().addPropertyChangeListener(Game.PROPERTY_PLAYERS, this::checkGameOver);
        }

        updateCards(null);
        updateDiscardPile(null);
        updateCurrentPlayer(null);
        updateColorWish(null);

        return parent;
    }

    @Override
    public void destroy() {
        player.listeners().removePropertyChangeListener(Player.PROPERTY_CARDS, this::updateCards);
        game.listeners().removePropertyChangeListener(Game.PROPERTY_DISCARD_PILE, this::updateDiscardPile);
        game.listeners().removePropertyChangeListener(Game.PROPERTY_CURRENT_PLAYER, this::updateCurrentPlayer);
        game.listeners().removePropertyChangeListener(Game.PROPERTY_COLOR_WISH, this::updateColorWish);
        if (gameService.getGameMode().equals(Constants.GAME_MODE_1)) {
            game.listeners().removePropertyChangeListener(Game.PROPERTY_DISCARD_PILE, this::checkGameOver);
        } else {
            game.listeners().removePropertyChangeListener(Game.PROPERTY_PLAYERS, this::checkGameOver);
        }

        if (discardPileController != null) {
            discardPileController.destroy();
        }

        for (CardController cardController : cardControllers) {
            cardController.destroy();
        }
        for (BotController botController : botControllers) {
            botController.destroy();
        }
    }

    private void checkGameOver(PropertyChangeEvent propertyChangeEvent) {
        if (gameService.getGameMode().equals(Constants.GAME_MODE_2)) {
            if (propertyChangeEvent != null && propertyChangeEvent.getNewValue() == null) {
                winners.add(((Player) propertyChangeEvent.getOldValue()).getName());
            }
        }

        if (gameService.isGameOver(game)) {
            if (gameService.getGameMode().equals(Constants.GAME_MODE_2)) {
                winners.add(game.getPlayers().get(0).getName());
            } else {
                winners.add(gameService.getWinner(game).toString());
            }
            app.show(new GameOverController(app, gameService, botService, winners));
        }
    }

    private void updateColorWish(PropertyChangeEvent propertyChangeEvent) {
        if (game.getColorWish() == null) {
            discardPileParent.setStyle("-fx-border-color: black;");
        } else {
            discardPileParent.setStyle("-fx-border-width: 2;-fx-border-color: " + game.getColorWish().name().toLowerCase() + ";");
        }
    }

    private void updateDiscardPile(PropertyChangeEvent propertyChangeEvent) {
        if (discardPileController != null) {
            pilesLayout.getChildren().remove(0);
            discardPileController.destroy();
            discardPileParent = null;
        }

        discardPileController = new CardController(null, gameService, game, game.getDiscardPile(), true);
        discardPileController.init();
        try {
            discardPileParent = discardPileController.render();
            pilesLayout.getChildren().add(0, discardPileParent);
            updateColorWish(null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateCards(PropertyChangeEvent propertyChangeEvent) {
        for (CardController cardController : cardControllers) {
            cardController.destroy();
        }
        playerCardsLayout.getChildren().clear();

        for (Card card : player.getCards()) {
            CardController cardController = new CardController(player, gameService, game, card, true);
            cardController.init();
            cardControllers.add(cardController);

            try {
                playerCardsLayout.getChildren().add(cardController.render());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void updateCurrentPlayer(PropertyChangeEvent propertyChangeEvent) {
        if (propertyChangeEvent == null || propertyChangeEvent.getNewValue() == player) {
            playerCardsPane.setStyle("-fx-border-color: red;-fx-border-width: 3;");
        } else {
            playerCardsPane.setStyle("");
        }
    }

    public void wishYellow() {
        gameService.wishColor(game, player, CardColor.YELLOW);
    }

    public void wishGreen() {
        gameService.wishColor(game, player, CardColor.GREEN);
    }

    public void wishBlue() {
        gameService.wishColor(game, player, CardColor.BLUE);
    }

    public void wishRed() {
        gameService.wishColor(game, player, CardColor.RED);
    }

    public void drawCard() {
        gameService.drawCard(game, player);
    }
}
