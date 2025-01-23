import java.util.*;

public interface Strategy extends Constants
{
   String getName();
   void gameStarted(Table table);
   Play play();
   int defuse();
   int favor(int activeSeat);
   boolean nope(int activeSeat, Play play, List<Integer> nopers);
   void seeTheFuture(int[] futureCards);
}
