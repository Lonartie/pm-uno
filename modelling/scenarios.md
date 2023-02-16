# Aufgabe 1.1

## Scenario 1
### **Title**: `Play first round`
#### **Start**: 
> Leon is playing Uno. The game has just started. <br>
> On the discard pile is the yellow 3 and Leon has the yellow 2 and <br>
> 6 other cards.
#### **Action**:
> Leon plays the yellow 2.
#### **Result**:
> Leon now has the remaining 6 cards and the yellow 2 is on the <br>
> discard pile. Now plays the next bot.

## Scenario 2
### **Title**: `Player can't play`
#### **Start**:
> Leon is playing Uno. He has just played the red 8 and has 5 cards <br>
> left. The discard pile is now the red 8. <br>
> The bot plays a red 7.
#### **Action**:
> Leon can't play and has to draw a card.
#### **Result**:
> Leon draws a random cards which was the red 9. <br>
> Leon now has 5 cards since the red 9 went to the discard pile. <br>
> Now plays the next bot.

## Scenario 3
### **Title**: `Player wins the game`
#### **Start**:
> Leon is playing Uno. He has just played the red 8 and has 1 card <br>
> left. The discard pile is now the red 8. <br>
> The bot plays a red 7.
#### **Action**:
> Leon plays the remaining red 9.
#### **Result**:
> Leon now has 0 cards and wins the game. <br>
> The `Game Over` screen is shown.