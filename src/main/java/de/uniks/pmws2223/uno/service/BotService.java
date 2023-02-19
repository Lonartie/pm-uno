package de.uniks.pmws2223.uno.service;

import de.uniks.pmws2223.uno.model.Bot;
import de.uniks.pmws2223.uno.model.Card;
import de.uniks.pmws2223.uno.model.CardColor;
import de.uniks.pmws2223.uno.model.Game;

import java.util.Optional;
import java.util.Random;

public class BotService {

    private Random random = new Random();

    public void setRandom(Random random) {
        this.random = random;
    }

    public boolean play(GameService gameService, Game game, Bot bot) {
        boolean won = false;
        Optional<Card> bestCard = getBestCard(gameService, game, bot);

        if (bestCard.isPresent()) {
            won = gameService.playCard(game, bot, bestCard.get()) || won;
        } else {
            gameService.drawCard(game, bot);
        }

        won = wischColor(gameService, game, bot) || won;
        return won;
    }

    private Optional<Card> getBestCard(GameService gameService, Game game, Bot bot) {
        Optional<Card> bestCard = bot.getCards().stream()
                .filter(c -> gameService.isCardPlayable(game, c) && c.getColor() != CardColor.BLACK)
                .min((c1, c2) -> countColorCards(bot, c1.getColor()) - countColorCards(bot, c2.getColor()));
        if (bestCard.isPresent()) {
            return bestCard;
        }

        return bot.getCards().stream()
                .filter(c -> gameService.isCardPlayable(game, c))
                .min((c1, c2) -> countColorCards(bot, c1.getColor()) - countColorCards(bot, c2.getColor()));
    }

    private Optional<CardColor> getBestColor(Bot bot) {
        Optional<Card> bestCard = bot.getCards().stream()
                .filter(c -> c.getColor() != CardColor.BLACK)
                .max((c1, c2) -> countColorCards(bot, c1.getColor()) - countColorCards(bot, c2.getColor()));

        return bestCard.map(Card::getColor);
    }

    private int countColorCards(Bot bot, CardColor color) {
        return (int) bot.getCards().stream()
                .filter(card -> card.getColor() == color)
                .count();
    }

    private boolean wischColor(GameService gameService, Game game, Bot bot) {
        if (gameService.requiresColorWish(game)) {
            Optional<CardColor> nextCard = getBestColor(bot);
            CardColor wish;
            if (nextCard.isPresent() && nextCard.get() != CardColor.BLACK) {
                wish = nextCard.get();
            } else {
                wish = CardColor.values()[random.nextInt(CardColor.values().length - 1)];
            }

            return gameService.wishColor(game, bot, wish);
        }
        return false;
    }
}
