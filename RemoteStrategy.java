import java.io.*;
import java.net.*;
import java.util.*;

public class RemoteStrategy extends Remote implements Strategy, GameListener
{
   private static ServerSocket server;

   private Table table;
   private String name;
   private boolean valueReturned;
   private Play played;
   private boolean noped;
   private int defused;
   private int favored;

   public RemoteStrategy()
   {
      super(accept());
      
      send("name");
      waitForReturnValue();      
   }
   
   private static Socket accept()
   {
      try
      {
         if (server == null)
         {
            System.out.println("starting server ...");
            server = new ServerSocket(9216);
            System.out.println("server started");
         }
         return server.accept();
      }
      catch(IOException e)
      {
         throw new RuntimeException(e);
      }
   }

   public String getName()
   {
      return name + " (remote)";
   }

   public void gameStarted(Table table)
   {
      this.table = table;
      table.addListener(this);
      send("numSeats " + table.getNumSeats());
      send("seat " + table.getSeat());
      sendTable();
      send("gameStarted");
   }

   public Play play()
   {
      sendTable();
      send("play");
      waitForReturnValue();
      return played;
   }
   
   public int defuse()
   {
      sendTable();
      send("defuse");
      waitForReturnValue();
      return defused;
   }
   
   public int favor(int activeSeat)
   {
      sendTable();
      send("favor " + activeSeat);
      waitForReturnValue();
      return favored;
   }
   
   public boolean nope(int activeSeat, Play play, List<Integer> nopers)
   {
      sendTable();
      StringBuilder sb = new StringBuilder();
      sb.append("nope ");
      sb.append(activeSeat);
      sb.append(" ");
      sb.append(toString(play));
      for (int seat : nopers)
      {
         sb.append(" ");
         sb.append(seat);
      }
      send(sb.toString());
      waitForReturnValue();
      return noped;
   }
   
   public void seeTheFuture(int[] futureCards)
   {
      sendTable();
      StringBuilder sb = new StringBuilder();
      sb.append("future");
      for (int card : futureCards)
      {
         sb.append(" ");
         sb.append(card);
      }
      send(sb.toString());
   }

   public void received(String[] tokens)
   {
      String command = tokens[0];
      if (command.equals("played"))
         played = new Play(Integer.parseInt(tokens[1]), Integer.parseInt(tokens[2]),
            Integer.parseInt(tokens[3]), Integer.parseInt(tokens[4]));
      else if (command.equals("passed"))
         played = null;
      else if (command.equals("noped"))
         noped = tokens[1].equals("1");
      else if (command.equals("favored"))
         favored = Integer.parseInt(tokens[1]);
      else if (command.equals("defused"))
         defused = Integer.parseInt(tokens[1]);
      else if (command.equals("named"))
         name = tokens[1];
      else
         throw new RuntimeException("invalid command: " + command);
         
      valueReturned = true;
   }
   
   private void sendTable()
   {
      StringBuilder sb = new StringBuilder();
      sb.append("hand");
      for (int card : table.getHand())
      {
         sb.append(" ");
         sb.append(card);
      }
      send(sb.toString());
      
      sb = new StringBuilder();
      sb.append("handSize");
      for (int seat = 0; seat < table.getNumSeats(); seat++)
      {
         sb.append(" ");
         sb.append(table.getHandSize(seat));
      }
      send(sb.toString());

      sb = new StringBuilder();
      sb.append("exploded");
      for (int seat = 0; seat < table.getNumSeats(); seat++)
      {
         if (table.hasExploded(seat))
         {
            sb.append(" ");
            sb.append(seat);
         }
      }
      send(sb.toString());

      send("draw " + table.getDrawPileSize() + " " + table.getNumCardsToDraw());
   }
   
   public void turnStarted(int seat)
   {
      send("turnStarted " + seat);
   }
   
   public void cardPlayed(int seat, int card, int count, int victim, int requestedCard)
   {
      send("cardPlayed " + seat + " " + card + " " + count + " " + victim + " " + requestedCard);
   }

   public void cardUsed(int seat, int card, int count, int victim, int requestedCard, boolean cardStolen)
   {
      send("cardUsed " + seat + " " + card + " " + count + " " + victim + " " + requestedCard + " " + (cardStolen?"1":"0"));
   }

   public void cardDrawn(int seat)
   {
      send("cardDrawn " + seat);
   }
   
   private void waitForReturnValue()
   {
      valueReturned = false;
      while (!valueReturned)
      {
         try{Thread.sleep(1);}catch(Exception e){}
      }
   }
}