package de.uniks.pmws2223.uno.model;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Collections;
import java.util.Collection;
import java.beans.PropertyChangeSupport;

public class Game
{
   public static final String PROPERTY_PLAYERS = "players";
   public static final String PROPERTY_DISCARD_PILE = "discardPile";
   public static final String PROPERTY_COLOR_WISH = "colorWish";
   public static final String PROPERTY_CLOCKWISE = "clockwise";
   public static final String PROPERTY_CURRENT_PLAYER = "currentPlayer";
   private List<Player> players;
   private Card discardPile;
   protected PropertyChangeSupport listeners;
   private CardColor colorWish;
   private boolean clockwise;
   private Player currentPlayer;

   public List<Player> getPlayers()
   {
      return this.players != null ? Collections.unmodifiableList(this.players) : Collections.emptyList();
   }

   public Game withPlayers(Player value)
   {
      if (this.players == null)
      {
         this.players = new ArrayList<>();
      }
      if (!this.players.contains(value))
      {
         this.players.add(value);
         value.setGame(this);
         this.firePropertyChange(PROPERTY_PLAYERS, null, value);
      }
      return this;
   }

   public Game withPlayers(Player... value)
   {
      for (final Player item : value)
      {
         this.withPlayers(item);
      }
      return this;
   }

   public Game withPlayers(Collection<? extends Player> value)
   {
      for (final Player item : value)
      {
         this.withPlayers(item);
      }
      return this;
   }

   public Game withoutPlayers(Player value)
   {
      if (this.players != null && this.players.remove(value))
      {
         value.setGame(null);
         this.firePropertyChange(PROPERTY_PLAYERS, value, null);
      }
      return this;
   }

   public Game withoutPlayers(Player... value)
   {
      for (final Player item : value)
      {
         this.withoutPlayers(item);
      }
      return this;
   }

   public Game withoutPlayers(Collection<? extends Player> value)
   {
      for (final Player item : value)
      {
         this.withoutPlayers(item);
      }
      return this;
   }

   public Card getDiscardPile()
   {
      return this.discardPile;
   }

   public Game setDiscardPile(Card value)
   {
      if (Objects.equals(value, this.discardPile))
      {
         return this;
      }

      final Card oldValue = this.discardPile;
      this.discardPile = value;
      this.firePropertyChange(PROPERTY_DISCARD_PILE, oldValue, value);
      return this;
   }

   public CardColor getColorWish()
   {
      return this.colorWish;
   }

   public Game setColorWish(CardColor value)
   {
      if (Objects.equals(value, this.colorWish))
      {
         return this;
      }

      final CardColor oldValue = this.colorWish;
      this.colorWish = value;
      this.firePropertyChange(PROPERTY_COLOR_WISH, oldValue, value);
      return this;
   }

   public boolean isClockwise()
   {
      return this.clockwise;
   }

   public Game setClockwise(boolean value)
   {
      if (value == this.clockwise)
      {
         return this;
      }

      final boolean oldValue = this.clockwise;
      this.clockwise = value;
      this.firePropertyChange(PROPERTY_CLOCKWISE, oldValue, value);
      return this;
   }

   public Player getCurrentPlayer()
   {
      return this.currentPlayer;
   }

   public Game setCurrentPlayer(Player value)
   {
      if (this.currentPlayer == value)
      {
         return this;
      }

      final Player oldValue = this.currentPlayer;
      this.currentPlayer = value;
      this.firePropertyChange(PROPERTY_CURRENT_PLAYER, oldValue, value);
      return this;
   }

   public void removeYou()
   {
      this.withoutPlayers(new ArrayList<>(this.getPlayers()));
      this.setCurrentPlayer(null);
   }

   public boolean firePropertyChange(String propertyName, Object oldValue, Object newValue)
   {
      if (this.listeners != null)
      {
         this.listeners.firePropertyChange(propertyName, oldValue, newValue);
         return true;
      }
      return false;
   }

   public PropertyChangeSupport listeners()
   {
      if (this.listeners == null)
      {
         this.listeners = new PropertyChangeSupport(this);
      }
      return this.listeners;
   }
}
