import java.util.*;

public class Human implements Strategy
{
   private Table table;
   private Display display;
   
   public String getName()
   {
      return "You";
   }
   
   public void gameStarted(Table table)
   {
      this.table = table;
   }
   
   public Play play()
   {
      return display.play(table.getSeat());
   }
   
   public int defuse()
   {
      return display.defuse();
   }
   
   public int favor(int activeSeat)
   {
      return display.favor(table.getSeat());
   }
   
   public boolean nope(int activeSeat, Play play, List<Integer> nopers)
   {
      return display.nope();
   }
   
   public void seeTheFuture(int[] futureCards)
   {
      display.seeTheFuture(futureCards);
   }
   
   public void setDisplay(Display display)
   {
      this.display = display;
   }
}