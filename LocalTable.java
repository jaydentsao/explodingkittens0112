import java.util.*;

public class LocalTable implements Table
{
   private int seat;
   private Game game;
   
   public LocalTable(int seat, Game game)
   {
      this.seat = seat;
      this.game = game;
   }

   public int getDrawPileSize()
   {
      return game.getDeck().size();
   }
   
   public List<Integer> getHand()
   {
      return Collections.unmodifiableList(game.getPlayers()[seat].getHand());
   }
   
   public boolean hasExploded(int seat)
   {
      return !game.getPlayers()[seat].isAlive();
   }
   
   public int getSeat()
   {
      return seat;
   }
   
   public int getNumSeats()
   {
      return game.getPlayers().length;
   }
   
   public void addListener(GameListener listener)
   {
      game.addListener(listener);
   }
   
   public int getHandSize(int seat)
   {
      return game.getPlayers()[seat].getHand().size();
   }
   
   public int getNumCardsToDraw()
   {
      return game.getNumCardsToDraw();
   }
}
