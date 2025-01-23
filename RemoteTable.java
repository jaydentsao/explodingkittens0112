import java.io.*;
import java.net.*;
import java.util.*;

public class RemoteTable extends Remote implements Table
{
   private Strategy strategy;
   private ArrayList<Integer> hand;
   private int[] handSizes;
   private int seat;
   private boolean[] exploded;
   private int drawPileSize;
   private int numCardsToDraw;
   private GameListener listener;

   public RemoteTable(String hostIPAddress, Strategy strategy)
   {
      super(createSocket(hostIPAddress));
      this.strategy = strategy;
   }
   
   private static Socket createSocket(String hostIPAddress)
   {
      try
      {
         return new Socket(hostIPAddress, 9216);
      }
      catch(IOException e)
      {
         throw new RuntimeException(e);
      }
   }
   
   public void received(String[] tokens)
   {
      String command = tokens[0];
      if (command.equals("hand"))
      {
         hand = new ArrayList<Integer>();
         for (int i = 1; i < tokens.length; i++)
            hand.add(Integer.parseInt(tokens[i]));
      }
      else if (command.equals("handSize"))
      {
         for (int i = 1; i < tokens.length; i++)
            handSizes[i - 1] = Integer.parseInt(tokens[i]);
      }
      else if (command.equals("numSeats"))
      {
         int numSeats = Integer.parseInt(tokens[1]);
         handSizes = new int[numSeats];
         exploded = new boolean[numSeats];
      }
      else if (command.equals("seat"))
         seat = Integer.parseInt(tokens[1]);
      else if (command.equals("exploded"))
      {
         for (int i = 1; i < tokens.length; i++)
            exploded[Integer.parseInt(tokens[i])] = true;
      }
      else if (command.equals("draw"))
      {
         drawPileSize = Integer.parseInt(tokens[1]);
         numCardsToDraw = Integer.parseInt(tokens[2]);
      }
      else if (command.equals("gameStarted"))
         strategy.gameStarted(this);
      else if (command.equals("turnStarted"))
      {
         if (listener != null)
            listener.turnStarted(Integer.parseInt(tokens[1]));
      }
      else if (command.equals("cardDrawn"))
      {
         if (listener != null)
            listener.cardDrawn(Integer.parseInt(tokens[1]));
      }
      else if (command.equals("cardPlayed"))
      {
         if (listener != null)
            listener.cardPlayed(Integer.parseInt(tokens[1]), Integer.parseInt(tokens[2]),
               Integer.parseInt(tokens[3]), Integer.parseInt(tokens[4]), Integer.parseInt(tokens[5]));
      }
      else if (command.equals("cardUsed"))
      {
         if (listener != null)
            listener.cardUsed(Integer.parseInt(tokens[1]), Integer.parseInt(tokens[2]),
               Integer.parseInt(tokens[3]), Integer.parseInt(tokens[4]), Integer.parseInt(tokens[5]),
               tokens[6].equals("1"));
      }
      else if (command.equals("play"))
      {
         Play play = strategy.play();
         if (play == null)
            send("passed");
         else
            send("played " + toString(play));
      }
      else if (command.equals("nope"))
      {
         //active seat, play, nopers
         int activeSeat = Integer.parseInt(tokens[1]);
         Play play = new Play(Integer.parseInt(tokens[2]), Integer.parseInt(tokens[3]),
            Integer.parseInt(tokens[4]), Integer.parseInt(tokens[5]));
         ArrayList<Integer> nopers = new ArrayList<Integer>();
         for (int i = 6; i < tokens.length; i++)
            nopers.add(Integer.parseInt(tokens[i]));
      
         send("noped " + (strategy.nope(activeSeat, play, nopers)?1:0));
      }
      else if (command.equals("defuse"))
         send("defused " + strategy.defuse());
      else if (command.equals("favor"))
         send("favored " + strategy.favor(Integer.parseInt(tokens[1])));
      else if (command.equals("future"))
      {
         int[] cards = new int[tokens.length - 1];
         for (int i = 0; i < cards.length; i++)
            cards[i] = Integer.parseInt(tokens[i + 1]);
         strategy.seeTheFuture(cards);
      }
      else if (command.equals("name"))
         send("named " + strategy.getName());
      else
         throw new RuntimeException("invalid command: " + command);
   }

   public int getSeat()
   {
      return seat;
   }
   
   public List<Integer> getHand()
   {
      return Collections.unmodifiableList(hand);
   }

   public int getNumSeats()
   {
      return handSizes.length;
   }
   
   public int getHandSize(int seat)
   {
      return handSizes[seat];
   }
   
   public boolean hasExploded(int seat)
   {
      return exploded[seat];
   }

   public int getDrawPileSize()
   {
      return drawPileSize;
   }
   
   public int getNumCardsToDraw()
   {
      return numCardsToDraw;
   }

   public void addListener(GameListener listener)
   {
      this.listener = listener;
   }
}