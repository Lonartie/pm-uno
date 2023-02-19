package de.uniks.pmws2223.uno.model;
import java.beans.PropertyChangeSupport;
import java.util.Objects;

public class Card
{
   public static final String PROPERTY_VALUE = "value";
   public static final String PROPERTY_COLOR = "color";
   private int value;
   private CardColor color;
   protected PropertyChangeSupport listeners;

   public int getValue()
   {
      return this.value;
   }

   public Card setValue(int value)
   {
      if (value == this.value)
      {
         return this;
      }

      final int oldValue = this.value;
      this.value = value;
      this.firePropertyChange(PROPERTY_VALUE, oldValue, value);
      return this;
   }

   public CardColor getColor()
   {
      return this.color;
   }

   public Card setColor(CardColor value)
   {
      if (Objects.equals(value, this.color))
      {
         return this;
      }

      final CardColor oldValue = this.color;
      this.color = value;
      this.firePropertyChange(PROPERTY_COLOR, oldValue, value);
      return this;
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
