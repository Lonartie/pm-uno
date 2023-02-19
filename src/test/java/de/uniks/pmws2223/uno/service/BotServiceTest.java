package de.uniks.pmws2223.uno.service;

import de.uniks.pmws2223.uno.Constants;
import de.uniks.pmws2223.uno.model.*;
import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class BotServiceTest {
    GameService gameService = new GameService();
    BotService botService = new BotService();
    Game game = new Game();

    @Test
    public void BotPrefersLeastColorTest() {
        Bot bot = new Bot();
        game.withPlayers(bot);
        bot.withCards(
                new Card().setColor(CardColor.YELLOW).setValue(1),
                new Card().setColor(CardColor.YELLOW).setValue(2),
                new Card().setColor(CardColor.RED).setValue(2)
        );

        game.setDiscardPile(new Card().setColor(CardColor.BLUE).setValue(2));
        game.setCurrentPlayer(bot);
        botService.play(gameService, game, bot);

        // bot prefers to play the card with the color that he has the least of (red)
        assertTrue(bot.getCards().size() == 2);
        assertTrue(hasCard(bot, CardColor.YELLOW, 1));
        assertTrue(hasCard(bot, CardColor.YELLOW, 2));
        assertFalse(hasCard(bot, CardColor.RED, 2));

        assertTrue(isSame(game.getDiscardPile(), new Card().setColor(CardColor.RED).setValue(2)));
    }

    @Test
    public void BotWishesMostColorTest() {
        Bot bot = new Bot();
        game.withPlayers(bot);
        bot.withCards(
                new Card().setColor(CardColor.BLACK).setValue(Constants.WILD),
                new Card().setColor(CardColor.YELLOW).setValue(1),
                new Card().setColor(CardColor.YELLOW).setValue(2),
                new Card().setColor(CardColor.RED).setValue(2)
        );

        game.setDiscardPile(new Card().setColor(CardColor.BLUE).setValue(3));
        game.setCurrentPlayer(bot);
        botService.play(gameService, game, bot);

        // bot wishes to play the first card with the color that he has the most of (yellow)
        assertTrue(bot.getCards().size() == 3);
        assertFalse(hasCard(bot, CardColor.BLACK, Constants.WILD));
        assertTrue(hasCard(bot, CardColor.YELLOW, 1));
        assertTrue(hasCard(bot, CardColor.YELLOW, 2));
        assertTrue(hasCard(bot, CardColor.RED, 2));

        assertTrue(isSame(game.getDiscardPile(), new Card().setColor(CardColor.BLACK).setValue(Constants.WILD)));
        assertTrue(game.getColorWish() == CardColor.YELLOW);
    }

    @Test
    public void BotWishesRandomColorTest() {Bot bot = new Bot();
        botService.setRandom(new Random(42));
        game.withPlayers(bot);
        bot.withCards(
                new Card().setColor(CardColor.BLACK).setValue(Constants.WILD),
                new Card().setColor(CardColor.BLACK).setValue(Constants.WILD)
        );

        game.setDiscardPile(new Card().setColor(CardColor.BLUE).setValue(3));
        game.setCurrentPlayer(bot);
        botService.play(gameService, game, bot);

        // bot wishes to play the first card with the color that he has the most of (yellow)
        assertTrue(bot.getCards().size() == 1);
        assertTrue(isSame(game.getDiscardPile(), new Card().setColor(CardColor.BLACK).setValue(Constants.WILD)));
        assertTrue(game.getColorWish() == CardColor.GREEN);
    }

    private boolean hasCard(Player player, CardColor color, int value) {
        for (Card card : player.getCards()) {
            if (card.getColor() == color && card.getValue() == value) {
                return true;
            }
        }
        return false;
    }

    private boolean isSame(Card a, Card b) {
        return a.getColor() == b.getColor() && a.getValue() == b.getValue();
    }
}
