package de.uniks.pmws2223.uno;

import de.uniks.pmws2223.uno.model.CardColor;
import de.uniks.pmws2223.uno.service.BotService;
import de.uniks.pmws2223.uno.service.GameService;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class GameTest extends ApplicationTest {
    private App app;
    private Stage stage;
    private final GameService gameService = new GameService();
    private final BotService botService = new BotService();

    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;
        app = new App(gameService, botService);
        app.start(stage);
    }

    private void initRandom() {
        Random random = new Random(42);
        gameService.setRandom(random);
        botService.setRandom(random);
    }

    @Test
    public void gameTest() {
        initRandom();

        // starting with setup screen
        assertEquals("Setup", stage.getTitle());

        clickOn("#nameField").write("Lonartie");
        Slider slider = lookup("#botsSlider").queryAs(Slider.class);
        Point2D middleLeft = slider.localToScreen(slider.getLayoutBounds().getMinX(), slider.getLayoutBounds().getMinY());
        Point2D middleRight = slider.localToScreen(slider.getLayoutBounds().getMaxX(), slider.getLayoutBounds().getMaxY());
        drag(middleLeft.getX(), middleLeft.getY()).dropTo(middleRight.getX(), middleRight.getY());
        clickOn("#playButton");

        // screen changes to ingame screen
        assertEquals("Ingame", stage.getTitle());

        // check everyone has 7 cards
        assertHasNCards(botCardBox(0), 7);
        assertHasNCards(botCardBox(1), 7);
        assertHasNCards(botCardBox(2), 7);
        assertHasNCards(playerCardBox(), 7);

        // discard pile has red reverse card
        checkDiscardPile(CardColor.RED, Constants.REVERSE);

        clickOn(nthCard(playerCardBox(), 0));

        // player played yellow reverse card
        assertHasNCards(playerCardBox(), 6);
        checkDiscardPile(CardColor.YELLOW, Constants.REVERSE);

        waitCycles(1);

        // bot 3 played yellow reverse card
        assertHasNCards(botCardBox(2), 6);
        checkDiscardPile(CardColor.YELLOW, Constants.REVERSE);

        clickOn(nthCard(playerCardBox(), 1));
        // player played yellow 9
        assertHasNCards(playerCardBox(), 5);
        checkDiscardPile(CardColor.YELLOW, 9);

        waitCycles(1);

        // bot 1 played yellow 6
        assertHasNCards(botCardBox(0), 6);
        checkDiscardPile(CardColor.YELLOW, 6);

        waitCycles(1);

        // bot 2 played yellow 1
        assertHasNCards(botCardBox(1), 6);
        checkDiscardPile(CardColor.YELLOW, 1);

        waitCycles(1);

        // bot 3 played blue 1
        assertHasNCards(botCardBox(2), 5);
        checkDiscardPile(CardColor.BLUE, 1);

        clickOn(nthCard(playerCardBox(), 0));
        // player played blue 6
        assertHasNCards(playerCardBox(), 4);
        checkDiscardPile(CardColor.BLUE, 6);

        waitCycles(1);

        // bot 1 played wildcard and wished green
        assertHasNCards(botCardBox(0), 5);
        checkDiscardPile(CardColor.BLACK, Constants.WILD);
        assertWishesColor(CardColor.GREEN);

        waitCycles(1);

        clickOn(nthCard(playerCardBox(), 1));
        waitCycles(2);

        clickOn(nthCard(playerCardBox(), 0));
        wishColor(CardColor.YELLOW);
        waitCycles(3);

        clickOn(nthCard(playerCardBox(), 1));
        waitCycles(3);

        clickOn(nthCard(playerCardBox(), 0));
        wishColor(CardColor.RED);

        waitCycles(15);

        // check game is over
        assertEquals("Game Over", stage.getTitle());
        Text text = lookup("#stateText").queryAs(Text.class);
        assertTrue(text.getText().contains("1.: Lonartie"));
        assertTrue(text.getText().contains("2.: Bot 3"));
        assertTrue(text.getText().contains("3.: Bot 2"));
        assertTrue(text.getText().contains("4.: Bot 1"));

        clickOn("#setupButton");

        // screen changes to setup screen
        assertEquals("Setup", stage.getTitle());
    }

    private void wishColor(CardColor color) {
        clickOn("#" + color.name().toLowerCase() + "Button");
    }

    private void checkDiscardPile(CardColor color, int value) {
        HBox pilesLayout = lookup("#pilesLayout").queryAs(HBox.class);
        VBox discardPile = (VBox) pilesLayout.getChildren().get(0);
        Text topText = (Text) discardPile.lookup("#topText");
        Text bottomText = (Text) discardPile.lookup("#bottomText");
        HBox colorLayout = (HBox) discardPile.lookup("#colorLayout");

        assertEquals(topText.getText(), Constants.CARD_NAMES.get(value));
        assertEquals(bottomText.getText(), Constants.CARD_NAMES.get(value));
        assertTrue(colorLayout.getStyle().contains("-fx-background-color: " + color.name().toLowerCase() + ";"));
    }

    private void assertWishesColor(CardColor color) {
        HBox pilesLayout = lookup("#pilesLayout").queryAs(HBox.class);
        VBox discardPile = (VBox) pilesLayout.getChildren().get(0);
        assertTrue(discardPile.getStyle().contains("-fx-border-color: " + color.name().toLowerCase() + ";"));
    }

    private void waitCycles(int cycles) {
        try {
            Thread.sleep((int)((cycles * 1.15) * (Constants.BOT_DELAY)));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private HBox botCardBox(int index) {
        VBox mainLayout = lookup("#mainLayout").queryAs(VBox.class);
        HBox botsLayout = (HBox) mainLayout.lookup("#botsLayout");
        HBox botCardBox = (HBox) botsLayout.lookupAll("#botCardsLayout").stream().toList().get(index);
        return botCardBox;
    }

    private void assertHasNCards(HBox cardBox, int n) {
        assertEquals(n, cardBox.getChildren().size());
    }

    private HBox playerCardBox() {
        HBox playerCardBox = lookup("#playerCardsLayout").queryAs(HBox.class);
        return playerCardBox;
    }

    private Node nthCard(HBox cardBox, int index) {
        return cardBox.getChildren().get(index);
    }
}
