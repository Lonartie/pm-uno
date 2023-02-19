package de.uniks.pmws2223.uno.service;

import de.uniks.pmws2223.uno.Constants;
import de.uniks.pmws2223.uno.model.*;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;

public class GameService {

    public void startGame(Game game, String playerName, int botCount) {
        game.setClockwise(true);
        game.withPlayers(new Player().setName(playerName));
        game.setCurrentPlayer(game.getPlayers().get(0));

        for (int i = 0; i < botCount; i++) {
            game.withPlayers(new Bot().setName("Bot " + (i + 1)));
        }

        dealCards(game);
        game.setDiscardPile(drawCard());

        if (requiresColorWish(game)) {
            game.setColorWish(CardColor.values()[(int) (Math.random() * 4)]);
        }
    }

    public boolean playCard(Game game, Player player, Card card) {
        boolean won = false;
        if (player == game.getCurrentPlayer() && !requiresColorWish(game) && isCardPlayable(game, card)) {
            player.withoutCards(card);
            game.setDiscardPile(card);
            game.setColorWish(null);

            System.out.println("Requires color wish: " + requiresColorWish(game));
            if (!requiresColorWish(game)) {
                nextPlayer(game, handleCardEffeckt(game, card));
                if (hasPlayerWon(player)) {
                    removePlayer(game, player);
                    won = true;
                }
            }
        }
        return won;
    }

    public boolean gameOver(Game game) {
        return game.getPlayers().size() == 1;
    }

    public boolean hasPlayerWon(Player player) {
        return player.getCards().size() == 0;
    }

    public boolean isCardPlayable(Game game, Card card) {
        if (game.getDiscardPile().getValue() != Constants.WILD) {
            return card.getValue() == game.getDiscardPile().getValue()
                    || card.getColor() == game.getDiscardPile().getColor()
                    || card.getColor() == CardColor.BLACK;
        } else {
            return card.getColor() == game.getColorWish()
                    || card.getColor() == CardColor.BLACK;
        }
    }

    public boolean wishColor(Game game, Player player, CardColor color) {
        boolean won = false;
        if (player == game.getCurrentPlayer() && requiresColorWish(game)) {
            game.setColorWish(color);
            if (hasPlayerWon(player)) {
                removePlayer(game, player);
                won = true;
            }
            nextPlayer(game, 1);
        }
        return won;
    }

    public boolean requiresColorWish(Game game) {
        return game.getDiscardPile().getValue() == Constants.WILD
                && game.getColorWish() == null;
    }

    public void drawCard(Game game, Player player) {
        if (player == game.getCurrentPlayer()) {
            Card card = drawCard();
            player.withCards(card);
            if (isCardPlayable(game, card)) {
                playCard(game, player, card);
            } else {
                nextPlayer(game, 1);
            }
        }
    }

    private void removePlayer(Game game, Player player) {
        game.withoutPlayers(player);
    }

    private void nextPlayer(Game game, int increment) {
        Player nextPlayer = getNextPlayer(game, increment);

        if (game.getCurrentPlayer().equals(nextPlayer)) {
            game.setCurrentPlayer(null);
            game.setCurrentPlayer(nextPlayer);
        } else {
            game.setCurrentPlayer(nextPlayer);
        }
    }

    private Player getNextPlayer(Game game, int turns) {
        int currentIndex = game.getPlayers().indexOf(game.getCurrentPlayer());
        int nextIndex;

        if (game.isClockwise()) {
            nextIndex = (currentIndex + turns) % game.getPlayers().size();
        } else {
            nextIndex = (currentIndex - turns + game.getPlayers().size()) % game.getPlayers().size();
        }

        return game.getPlayers().get(nextIndex);
    }

    private int handleCardEffeckt(Game game, Card card) {
        int increment = 1;
        if (card.getValue() == Constants.REVERSE) {
            game.setClockwise(!game.isClockwise());
            if (game.getPlayers().size() == 2) {
                increment = 2;
            }
        } else if (card.getValue() == Constants.SKIP) {
            increment = 2;
        } else if (game.getDiscardPile().getValue() == Constants.PLUS_TWO) {
            getNextPlayer(game, 1).withCards(drawCards(2));
            increment = 2;
        }
        System.out.println("Increment: " + increment);
        return increment;
    }

    private void dealCards(Game game) {
        for (Player player : game.getPlayers()) {
            if (player instanceof Bot) {
                player.withCards(drawCards(7));
            } else {
                player.withCards(drawCards(7));
            }
        }
    }

    private List<Card> drawCards(int amount) {
        List<Card> cards = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            cards.add(drawCard());
        }
        return cards;
    }

    private Card drawCard() {
        Card randomCard = Constants.DECK.get((int) (Math.random() * Constants.DECK.size()));
        return new Card().setValue(randomCard.getValue()).setColor(randomCard.getColor());
    }
}
