import java.util.*;

public class Game implements Constants
{
   private Player[] players;
   private ArrayList<Integer> deck;
   private int activeSeat;  // -1 if game over
   private int numCardsToDraw;
   private Play play;
   private ArrayList<Integer> nopers;
   private int lastActor;
   private Display display;
   private ArrayList<GameListener> listeners;

   public Game(Strategy ... strategies)
   {
      if (strategies.length < 2 || strategies.length > 5)
         throw new RuntimeException("must have 2 to 5 players");
      players = new Player[strategies.length];
      for (int seat = 0; seat < players.length; seat++)
      {
         players[seat] = new Player(seat, this, strategies[seat]);
         players[seat].getHand().add(DEFUSE);
      }
      deck = new ArrayList<Integer>();
      deck.add(DEFUSE);
      if (players.length < 5)
         deck.add(DEFUSE);
      for (int i = 0; i < 4; i++)
      {
         deck.add(ATTACK);
         deck.add(FAVOR);
         deck.add(NOPE);
         deck.add(SHUFFLE);
         deck.add(SKIP);
         deck.add(SEE_THE_FUTURE);
         deck.add(TACOCAT);
         deck.add(CATTERMELON);
         deck.add(HAIRY_POTATO_CAT);
         deck.add(BEARD_CAT);
         deck.add(RAINBOW_RALPHING_CAT);
      }
      deck.add(NOPE);
      deck.add(SEE_THE_FUTURE);
      Collections.shuffle(deck);
      for (int i = 0; i < 7; i++)
      {
         for (int seat = 0; seat < players.length; seat++)
            players[seat].getHand().add(deck.remove(deck.size() - 1));
      }
      for (int i = 0; i < strategies.length - 1; i++)
         deck.add(EXPLODING_KITTEN);
      Collections.shuffle(deck);
      activeSeat = 0;
      numCardsToDraw = 1;
      play = null;
      nopers = new ArrayList<Integer>();
      lastActor = -1;
      display = null;
      listeners = new ArrayList<GameListener>();
   }
   
   private int turn()
   {
      //play cards or pass
      play = players[activeSeat].play();
      if (play == null)
      {
         int winner = draw(); 
         if (winner != -1)
         {
            activeSeat = -1;
            return winner;
         }
         if (numCardsToDraw == 0)
         {
            numCardsToDraw = 1;
            advance();
         }
         
         return -1;
      }
      else
      {
         for (GameListener listener : listeners)
            listener.cardPlayed(activeSeat, play.getCard(), play.getCount(), play.getVictim(), play.getRequestedCard());
      
         //this play is just a proposal for now
         //everyone now gets the chance to nope it, and nope that, until no more noping
         lastActor = activeSeat;
         noping();
         
         if (nopers.size() % 2 == 0)
         {
            //it was yupped
            int card = play.getCard();
            int count = play.getCount();
            int victim = play.getVictim();
            int cardUser = activeSeat;
            boolean advancing = false;
            boolean cardStolen = false;
  
            if (count == 1)
            {
               if (card == ATTACK)
               {
                  numCardsToDraw++;
                  advancing = true;
               }
               else if (card == FAVOR)
               {
                  //should it crash if you target a dead player?
                  //should it crash if you target someone with no cards?
                  //should it crash if you target yourself?
                  if (victim != activeSeat && players[victim].isAlive() && players[victim].getHand().size() > 0)
                  {
                     players[activeSeat].getHand().add(players[victim].favor(activeSeat));
                     cardStolen = true;
                  }
               }
               else if (card == SHUFFLE)
                  Collections.shuffle(deck);
               else if (card == SKIP)
               {
                  numCardsToDraw--;
                  if (numCardsToDraw == 0)
                  {
                     numCardsToDraw = 1;
                     advancing = true;
                  }
               }
               else if (card == SEE_THE_FUTURE)
                  players[activeSeat].seeTheFuture();
               else
                  throw new RuntimeException("unsupported card type: " + card);
            }
            else if (count == 2)
            {
               ArrayList<Integer> targetHand = players[victim].getHand();
               if (victim != activeSeat && players[victim].isAlive() && targetHand.size() > 0)
               {
                  players[activeSeat].getHand().add(targetHand.remove((int)(Math.random() * targetHand.size())));
                  cardStolen = true;
               }
            }
            else // count == 3
            {
               int requestedCard = play.getRequestedCard();
               ArrayList<Integer> targetHand = players[victim].getHand();
               if (victim != activeSeat && players[victim].isAlive() && targetHand.remove((Integer)requestedCard))
               {
                  players[activeSeat].getHand().add(requestedCard);
                  cardStolen = true;
               }
            }
            
            for (GameListener listener : listeners)
               listener.cardUsed(cardUser, card, count, victim, play.getRequestedCard(), cardStolen);
            
            if (advancing)
               advance();
         }
      
         nopers = new ArrayList<Integer>();

         return -1;
      }
   }
   
   public ArrayList<Integer> getDeck()
   {
      return deck;
   }
   
   public int play()
   {
      if (activeSeat == -1)
         throw new RuntimeException("cannot play a second game with same Game object");
      for (Player p : players)
         p.gameStarted();
   
      for (GameListener listener : listeners)
         listener.turnStarted(activeSeat);
   
      int winner;
      do
         winner = turn();
      while(winner == -1);
      return winner;
   }
   
   private void noping()
   {
      int seat = (lastActor + 1) % players.length;
      while (seat != lastActor)
      {
         if (players[seat].isAlive() && players[seat].nope(activeSeat, play, nopers))
         {
            nopers.add(seat);
            lastActor = seat;
            
            for (GameListener listener : listeners)
               listener.cardPlayed(seat, NOPE, 1, -1, -1);
            
            noping();
            return;
         }
         seat = (seat + 1) % players.length;
      }
   }
   
   private int draw()
   {
      //draw
      int card = deck.remove(deck.size() - 1);
      numCardsToDraw--;
            
      if (card == EXPLODING_KITTEN)
      {
         for (GameListener listener : listeners)
            listener.cardDrawn(activeSeat);

         int index = players[activeSeat].defuse();
         if (index == -1)
         {
            players[activeSeat].die();
            numCardsToDraw = 0;
            
            for (GameListener listener : listeners)
               listener.cardUsed(activeSeat, EXPLODING_KITTEN, 1, -1, -1, false);
            
            int winner = -1;
            int count = 0;
            for (int seat = 0; seat < players.length; seat++)
            {
               if (players[seat].isAlive())
               {
                  winner = seat;
                  count++;
               }
            }
            if (count == 1)
               return winner;
         }
         else
         {
            deck.add(deck.size() - index, EXPLODING_KITTEN);
            
            for (GameListener listener : listeners)
               listener.cardUsed(activeSeat, DEFUSE, 1, -1, -1, false);
         }
      }
      else
      {
         players[activeSeat].getHand().add(card);
         for (GameListener listener : listeners)
            listener.cardDrawn(activeSeat);
      }
      return -1;
   }
   
   //pre: must set number of cards to draw before advancing
   private void advance()
   {
      activeSeat = (activeSeat + 1) % players.length;
      while (!players[activeSeat].isAlive())
         activeSeat = (activeSeat + 1) % players.length;
         
      for (GameListener listener : listeners)
         listener.turnStarted(activeSeat);
   }
   
   public Player[] getPlayers()
   {
      return players;
   }
   
   public void setDisplay(Display display)
   {
      this.display = display;
      listeners.add(display);
   }
   
   public void addListener(GameListener listener)
   {
      listeners.add(listener);
   }
   
   public int getNumCardsToDraw()
   {
      return numCardsToDraw;
   }
}