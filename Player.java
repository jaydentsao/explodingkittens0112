import java.util.*;

public class Player implements Constants
{
   private Strategy strategy;
   private ArrayList<Integer> hand;
   private Game game;
   private boolean alive;
   private int seat;

   public Player(int seat, Game game, Strategy strategy)
   {
      this.seat = seat;
      this.game = game;
      this.strategy = strategy;
      hand = new ArrayList<Integer>();
      alive = true;
   }
   
   public Play play()
   {
      Play play = strategy.play();
      if (play == null)
         return null;
      for (int i = 0; i < play.getCount(); i++)
      {
         if (!hand.remove((Integer)play.getCard()))
            throw new RuntimeException(this + " does not have " + play);
      }
      return play;
   }
   
   public int defuse()
   {
      if (hand.remove((Integer)DEFUSE))
      {
         int index = strategy.defuse();
         if (index < 0 || index > game.getDeck().size())
            throw new RuntimeException("cannot insert into deck at index " + index);
         return index;
      }
      else
         return -1;
   }
   
   public void die()
   {
      alive = false;
   }
   
   public boolean isAlive()
   {
      return alive;
   }
   
   public void gameStarted()
   {
      strategy.gameStarted(new LocalTable(seat, game));
   }
   
   public ArrayList<Integer> getHand()
   {
      return hand;
   }
   
   public boolean nope(int activeSeat, Play play, ArrayList<Integer> nopers)
   {
      //give player an opportunity to nope
      if (hand.contains(NOPE) && strategy.nope(activeSeat, play, Collections.unmodifiableList(nopers)))
      {
         hand.remove((Integer)NOPE);
         return true;
      }
      else
         return false;
   }
   
   public int favor(int activeSeat)
   {
      int card = strategy.favor(activeSeat);
      if (hand.remove((Integer)card))
         return card;
      else
         throw new RuntimeException(this + " does not have " + Play.toString(card));
   }
   
   public void seeTheFuture()
   {
      ArrayList<Integer> deck = game.getDeck();
      int[] future = new int[Math.min(3, deck.size())];
      for (int i = 0; i < future.length; i++)
         future[i] = deck.get(deck.size() - 1 - i);
      strategy.seeTheFuture(future);
   }
   
   public String toString()
   {
      return "#" + seat + ": " + strategy.getName();
   }
}
