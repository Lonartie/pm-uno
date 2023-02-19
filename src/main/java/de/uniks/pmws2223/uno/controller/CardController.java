package de.uniks.pmws2223.uno.controller;

import de.uniks.pmws2223.uno.Constants;
import de.uniks.pmws2223.uno.Main;
import de.uniks.pmws2223.uno.model.Bot;
import de.uniks.pmws2223.uno.model.Card;
import de.uniks.pmws2223.uno.model.Game;
import de.uniks.pmws2223.uno.model.Player;
import de.uniks.pmws2223.uno.service.GameService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

import java.io.IOException;

public class CardController implements Controller {
    @FXML
    private Text topText;
    @FXML
    private Text bottomText;
    @FXML
    private Text unoText;
    @FXML
    private HBox colorLayout;

    private final Player player;
    private final Card card;
    private final boolean faceUp;
    private final GameService gameService;
    private final Game game;

    public CardController(Player player, GameService gameService, Game game, Card card, boolean faceUp) {
        this.player = player;
        this.card = card;
        this.faceUp = faceUp;
        this.gameService = gameService;
        this.game = game;
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
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("view/Card.fxml"));
        loader.setControllerFactory(c -> this);
        Parent parent = loader.load();

        if (faceUp) {
            String cardName = Constants.CARD_NAMES.get(card.getValue());
            topText.setText(cardName);
            bottomText.setText(cardName);
            colorLayout.setStyle("-fx-background-color: " + card.getColor().name().toLowerCase() + ";");
            unoText.setText("");
        } else {
            topText.setText("");
            bottomText.setText("");
        }

        return parent;
    }

    @Override
    public void destroy() {

    }

    public void clicked() {
        if (player != null && !(player instanceof Bot)) {
            gameService.playCard(game, player, card);
        }
    }
}
