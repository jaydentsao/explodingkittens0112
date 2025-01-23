import java.util.*;

public interface Table
{
   int getSeat();
   List<Integer> getHand();

   int getNumSeats();
   int getHandSize(int seat);
   boolean hasExploded(int seat);

   int getDrawPileSize();
   int getNumCardsToDraw();

   void addListener(GameListener listener);
}  