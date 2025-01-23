import java.util.*;

public class Catnap implements Strategy
{
   private Table table;

   public String getName()
   {
      return "Catnap";
   }
   
   public void gameStarted(Table table)
   {
      this.table = table;  // save table for later
   }

   public Play play()
   {
      return null;  // I never play a card before drawing a card
   }
   
   public int defuse()
   {
      return 0;  // I always insert an Exploding Kitten at the top of the draw pile
   }
   
   public int favor(int activeSeat)
   {
      return table.getHand().get(0);  // When asked for a favor, I always return the first card in my hand
   }

   public boolean nope(int activeSeat, Play play, List<Integer> nopers)
   {
      return false;  // I never play a Nope card
   }
   
   public void seeTheFuture(int[] futureCards)
   {
      throw new RuntimeException("I never see the future");
   }
}