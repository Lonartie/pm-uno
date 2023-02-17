package de.uniks.pmws2223.uno.model;

import org.fulib.builder.ClassModelDecorator;
import org.fulib.builder.ClassModelManager;
import org.fulib.builder.reflect.Link;
import org.fulib.builder.reflect.Type;

import java.util.List;

public class GenModel implements ClassModelDecorator {

    public class Game {
        @Link
        public Player currentPlayer;

        @Link("game")
        public List<Player> players;

        public Card discardPile;
    }

    public class Player {
        public String name;

        @Link("players")
        public Game game;

        public List<Card> cards;
    }

    public class Bot extends Player {

    }

    public class Card {
        public int value;

        @Type("CardColor")
        public Object color;
    }


    @Override
    public void decorate(ClassModelManager mm) {
        mm.haveNestedClasses(GenModel.class);
    }
}
