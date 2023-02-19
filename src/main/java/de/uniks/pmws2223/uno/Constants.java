package de.uniks.pmws2223.uno;

import de.uniks.pmws2223.uno.model.Card;
import de.uniks.pmws2223.uno.model.CardColor;

import java.util.*;

public class Constants {

    public final static List<Card> DECK = new ArrayList<>();
    public static final int PLUS_TWO = 10;
    public static final int REVERSE = 11;
    public static final int SKIP = 12;
    public static final int WILD = 13;
    public static final Map<Integer, String> CARD_NAMES = new HashMap<>();
    public static final long BOT_DELAY = 1000;
    public static final int INITIAL_CARD_COUNT = 7;

    static {
        for (CardColor color : Arrays.stream(CardColor.values())
                .filter(c -> c != CardColor.BLACK)
                .toList()) {
            for (int i = 0; i <= 12; i++) {
                DECK.add(new Card().setValue(i).setColor(color));
            }
        }

        for (int i = 0; i < 4; i++) {
            DECK.add(new Card().setValue(13).setColor(CardColor.BLACK));
        }

        for (int i = 0; i < 10; i++) {
            CARD_NAMES.put(i, String.valueOf(i));
        }
        CARD_NAMES.put(PLUS_TWO, "+2");
        CARD_NAMES.put(REVERSE, "R");
        CARD_NAMES.put(SKIP, "S");
        CARD_NAMES.put(WILD, "W");
    }
}
