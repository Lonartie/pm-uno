package de.uniks.pmws2223.uno.service;

import de.uniks.pmws2223.uno.Constants;
import de.uniks.pmws2223.uno.model.*;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Random;

import static org.junit.Assert.*;

public class GameServiceTest {
    Random random = new Random(42);
    GameService gameService = new GameService().setRandom(random);
    Game game = new Game();

    @Test
    public void startGameTest() {
        gameService.startGame(game, "Lonartie", 2);

        // game has 3 players, 1 human and 2 bots
        assertEquals(3, game.getPlayers().size());

        assertEquals("Lonartie", game.getPlayers().get(0).getName());
        assertFalse(game.getPlayers().get(0) instanceof Bot);
        assertTrue(game.getPlayers().get(1).getName().startsWith("Bot 1"));
        assertTrue(game.getPlayers().get(1) instanceof Bot);
        assertTrue(game.getPlayers().get(2).getName().startsWith("Bot 2"));
        assertTrue(game.getPlayers().get(2) instanceof Bot);

        // every player has 7 cards
        assertEquals(7, game.getPlayers().get(0).getCards().size());
        assertEquals(7, game.getPlayers().get(1).getCards().size());
        assertEquals(7, game.getPlayers().get(2).getCards().size());

        // current player is the human
        assertSame(game.getCurrentPlayer(), game.getPlayers().get(0));
    }

    @Test
    public void playCardTest() {
        gameService.startGame(game, "Lonartie", 2);

        Player player = game.getPlayers().get(0);
        Card card = player.getCards().get(1); // blue 6 card, discard pile is blue 2
        assertFalse(gameService.playCard(game, player, card));

        // player played the card successfully
        assertTrue(isSame(game.getDiscardPile(), new Card().setColor(CardColor.BLUE).setValue(6)));
        assertFalse(hasCard(player, CardColor.BLUE, 6));
        assertEquals(6, player.getCards().size());

        // bot 1 is current player
        assertSame(game.getCurrentPlayer(), game.getPlayers().get(1));

        game.setCurrentPlayer(player);
        card = player.getCards().get(2); // black wildcard
        assertFalse(gameService.playCard(game, player, card));

        // player is not done yet since he has to wish for a color
        assertSame(game.getCurrentPlayer(), player);
        assertTrue(gameService.requiresColorWish(game));

        gameService.wishColor(game, player, CardColor.RED);

        // player is done now
        assertSame(game.getCurrentPlayer(), game.getPlayers().get(1));
        assertFalse(gameService.requiresColorWish(game));

        player.withCards(new Card().setColor(CardColor.RED).setValue(Constants.SKIP));
        game.setCurrentPlayer(player);
        card = player.getCards().get(5); // red skip

        assertFalse(gameService.playCard(game, player, card));

        // first bot is skipped
        assertSame(game.getCurrentPlayer(), game.getPlayers().get(2));

        player.withCards(new Card().setColor(CardColor.RED).setValue(Constants.REVERSE));
        game.setCurrentPlayer(player);
        card = player.getCards().get(5); // red reverse

        assertFalse(gameService.playCard(game, player, card));

        // last bot is current player and direction changed
        assertSame(game.getCurrentPlayer(), game.getPlayers().get(2));
        assertFalse(game.isClockwise());

        player.withCards(new Card().setColor(CardColor.RED).setValue(Constants.PLUS_TWO));
        game.setCurrentPlayer(player);
        card = player.getCards().get(5); // red +2

        assertFalse(gameService.playCard(game, player, card));

        // last bot has to draw 2 cards and may not play so bot 1 is current player
        assertSame(game.getCurrentPlayer(), game.getPlayers().get(1));
        assertEquals(9, game.getPlayers().get(2).getCards().size());

        player.withoutCards(new ArrayList<>(player.getCards()));
        player.withCards(new Card().setColor(CardColor.BLACK).setValue(Constants.WILD));
        game.withoutPlayers(game.getPlayers().get(2));
        game.setCurrentPlayer(player);
        card = player.getCards().get(0); // black wildcard

        // player wins the game and is removed from the players list
        assertFalse(gameService.playCard(game, player, card));
        assertTrue(gameService.wishColor(game, player, CardColor.RED));
        assertEquals(1, game.getPlayers().size());

        // game is over
        assertTrue(gameService.isGameOver(game));
        assertTrue(gameService.hasPlayerWon(player));
        assertFalse(gameService.hasPlayerWon(game.getPlayers().get(0)));
    }

    @Test
    public void drawCardTest() {
        gameService.startGame(game, "Lonartie", 2);
        Player player = game.getPlayers().get(0);

        gameService.drawCard(game, player);

        // player has 8 cards now and bot 1 is current player
        assertEquals(8, player.getCards().size());
        assertSame(game.getCurrentPlayer(), game.getPlayers().get(1));

        game.setCurrentPlayer(player);

        Card blue6 = player.getCards().get(1);
        Card yellowReverse = player.getCards().get(0);

        // discard pile is blue 2
        // check blue6 is playable but red7 is not
        assertFalse(gameService.isCardPlayable(game, yellowReverse));
        assertFalse(gameService.playCard(game, player, yellowReverse));
        assertTrue(gameService.isCardPlayable(game, blue6));
        assertFalse(gameService.playCard(game, player, blue6));
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
