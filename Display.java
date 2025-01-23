import java.awt.*;
import java.io.*;
import java.util.*;
import javax.imageio.*;
import javax.swing.*;

public class Display extends JComponent implements Constants, GameListener
{
   public static final Color CARD_COLOR = new Color(170, 70, 50);
   
   static
   {
      try
      {
         UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
      }
      catch(Exception e)
      {
         throw new RuntimeException(e);
      }
   }

   private Game game;
   private int delay;
   private JFrame frame;
   private PlayerPanel[] playerPanels;
   private Image[] images;
   private int discard;
   private int targetSeat;
   private Image arrow;

   public Display(Game game, int delay)
   {
      this.game = game;
      this.delay = delay;
      discard = -1;
      targetSeat = -1;
      
      String[] imageNames = {
         "explodingkitten.png", "defuse.png", "attack.png", "favor.png",
         "nope.png", "shuffle.png", "skip.png", "seethefuture.png",
         "tacocat.png", "cattermelon.png", "hairypotatocat.png", "beardcat.png", "rainbowralphingcat.png"};
      
      images = new Image[13];
      for (int i = 0; i < 13; i++)
      {
         try
         {
            images[i] = ImageIO.read(new File(imageNames[i]));
         }
         catch (IOException e)
         {
            throw new RuntimeException(e);
         }
         if (images[i] == null)
            throw new RuntimeException("unable to load image: " + imageNames[i]);
      }
      
      try
      {
         arrow = ImageIO.read(new File("arrow.png"));
      }
      catch(IOException e)
      {
         throw new RuntimeException(e);
      }
      
      frame = new JFrame("Exploding Kittens");
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      
      frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.LINE_AXIS));
      
      setPreferredSize(new Dimension(200, 400));
      frame.getContentPane().add(this);
      
      JPanel rightPanel = new JPanel();
      int numSeats = game.getPlayers().length;
      rightPanel.setLayout(new GridLayout(numSeats, 1));
      frame.getContentPane().add(rightPanel);
      
      playerPanels = new PlayerPanel[numSeats];
      for (int seat = 0; seat < playerPanels.length; seat++)
      {
         playerPanels[seat] = new PlayerPanel(game, seat, this);
         rightPanel.add(playerPanels[seat]);
      }
      
      frame.pack();
      frame.setVisible(true);
   }

   private void update()
   {
      repaint();
      for (PlayerPanel panel : playerPanels)
         panel.repaint();
      try{Thread.sleep(delay);}
      catch(Exception e){}
   }
   
   public Image getImage(int card)
   {
      return images[card];
   }
   
   private static String message(int card, int count, int targetSeat, int requestedCard, boolean cardStolen)
   {
      String message;
      if (count == 2)
         message = "Random Card";
      else if (count == 3)
         message = Play.toString(requestedCard);
      else
         message = Play.toString(card);
      if (targetSeat != -1)
         message += " from Seat #" + targetSeat;
      return message;
   }
   
   public Play play(int seat)
   {
      update();
      int[] freqs = new int[13];
      for (int card : game.getPlayers()[seat].getHand())
         freqs[card]++;
      ArrayList<Choice> choiceList = new ArrayList<Choice>();
      choiceList.add(new Choice(0, "Draw a Card"));
      if (freqs[ATTACK] >= 1)
         choiceList.add(new Choice(ATTACK, Play.toString(ATTACK)));
      if (freqs[FAVOR] >= 1)
         choiceList.add(new Choice(FAVOR, Play.toString(FAVOR)));
      if (freqs[SHUFFLE] >= 1)
         choiceList.add(new Choice(SHUFFLE, Play.toString(SHUFFLE)));
      if (freqs[SKIP] >= 1)
         choiceList.add(new Choice(SKIP, Play.toString(SKIP)));
      if (freqs[SEE_THE_FUTURE] >= 1)
         choiceList.add(new Choice(SEE_THE_FUTURE, Play.toString(SEE_THE_FUTURE)));
      for (int card = 1; card <= 12; card++)
      {
         if (freqs[card] >= 2)
            choiceList.add(new Choice(200 + card, "2 × " + Play.toString(card)));
         if (freqs[card] >= 3)
            choiceList.add(new Choice(300 + card, "3 × " + Play.toString(card)));
      }
      Object[] choices = new Object[choiceList.size()];
      for (int i = 0; i < choices.length; i++)
         choices[i] = choiceList.get(i);
      Choice choice = (Choice)JOptionPane.showInputDialog(
         frame,
         "Choose Card to Play",
         "Play Card",
         JOptionPane.PLAIN_MESSAGE,
         null,
         choices,
         choices[0]);
      if (choice == null || choice.getValue() == 0)
         return null;
      
      int card = choice.getValue();
      int count = 1;
      
      if (card >= 300)
      {
         count = 3;
         card -= 300;
      }
      else if (card >= 200)
      {
         count = 2;
         card -= 200;
      }
      
      if (card == FAVOR || count >= 2)
      {            
         //choose target seat
         ArrayList<Integer> opponentList = new ArrayList<Integer>();
         Player[] players = game.getPlayers();
         for (int targetSeat = 0; targetSeat < players.length; targetSeat++)
         {
            if (targetSeat != seat && players[targetSeat].isAlive())
               opponentList.add(targetSeat);
         }
         Object[] opponents = new Object[opponentList.size()];
         for (int i = 0; i < opponents.length; i++)
            opponents[i] = opponentList.get(i);
         Integer opponent = (Integer)JOptionPane.showInputDialog(
            frame,
            "Which seat to take from?",
            "Choose Seat",
            JOptionPane.PLAIN_MESSAGE,
            null,
            opponents,
            opponents[0]);
         if (opponent == null)
            return null;
         int targetSeat = opponent;
         
         if (count == 3)
         {
            choices = new Choice[12];
            for (int i = 0; i < 12; i++)
               choices[i] = new Choice(i + 1, Play.toString(i + 1));
            choice = (Choice)JOptionPane.showInputDialog(
               frame,
               "Which card to request?",
               "Request Card",
               JOptionPane.PLAIN_MESSAGE,
               null,
               choices,
               choices[0]);
            if (choice == null)
               return null;
            int requestedCard = choice.getValue();
            return new Play(card, count, targetSeat, requestedCard);
         }
         else
            return new Play(card, count, targetSeat, -1);
      }
      else
         return new Play(card);
   }
   
   public int favor(int seat)
   {
      update();
      int[] freqs = new int[13];
      for (int card : game.getPlayers()[seat].getHand())
         freqs[card]++;
      ArrayList<Choice> choiceList = new ArrayList<Choice>();
      for (int card = 0; card <= 12; card++)
      {
         if (freqs[card] >= 1)
         {
            String name = Play.toString(card);
            if (freqs[card] >= 2)
               name += " (" + freqs[card] + ")";
            choiceList.add(new Choice(card, name));
         }
      }
      Object[] choices = new Object[choiceList.size()];
      for (int i = 0; i < choices.length; i++)
         choices[i] = choiceList.get(i);
      Choice choice = (Choice)JOptionPane.showInputDialog(
         frame,
         "Choose Card to Give Away",
         "Favor",
         JOptionPane.PLAIN_MESSAGE,
         null,
         choices,
         choices[choices.length - 1]);
      if (choice == null)
         return choiceList.get(choices.length - 1).getValue();
      
      return choice.getValue();
   }
   
   public int defuse()
   {
      update();
      int deckSize = game.getDeck().size();
      Object[] choices = new Object[deckSize + 1];
      for (int i = 0; i <= deckSize; i++)
         choices[i] = i;
      Integer choice = (Integer)JOptionPane.showInputDialog(
         frame,
         "How many cards down to insert Exploding Kitten?",
         "Insert Exploding Kitten",
         JOptionPane.PLAIN_MESSAGE,
         null,
         choices,
         choices[0]);
      if (choice == null)
         return 0;
      else
         return choice;
   }
   
   public boolean nope()
   {
      update();
      int option = JOptionPane.showConfirmDialog(frame, "Play Nope Card?", "Nope", JOptionPane.OK_CANCEL_OPTION);
      return option == JOptionPane.OK_OPTION;
   }
   
   public void paintComponent(Graphics g)
   {
      g.setColor(new Color(0, 127, 0));
      g.fillRect(0, 0, 200, 400);

      g.setColor(CARD_COLOR);
      g.fillRect(36, 50, 128, 128);
      g.setColor(Color.BLACK);
      g.drawRect(36, 50, 128, 128);    

      g.setFont(new Font(null, Font.BOLD, 72));
      String text = "" + game.getDeck().size();
      FontMetrics fm = g.getFontMetrics();
      int width = fm.stringWidth(text);
      int x = (200 - width) / 2;
      int y = 136;
      g.setColor(Color.BLACK);
      g.drawString(text, x, y - 1);
      g.drawString(text, x - 1, y);
      g.drawString(text, x + 1, y);
      g.drawString(text, x, y + 1);
      int deckSize = game.getDeck().size(); // out of 56 maximum
      if (deckSize <= 6)
         g.setColor(Color.RED);
      else if (deckSize <= 12)
         g.setColor(Color.ORANGE);
      else if (deckSize <= 18)
         g.setColor(Color.YELLOW);
      else
         g.setColor(Color.GREEN);
      g.drawString(text, x, y);
      
      if (discard != -1)
      {
         g.drawImage(images[discard], 36, 236, null);
         g.setColor(Color.BLACK);
         g.drawRect(36, 236, 128, 128);
         
         g.setColor(Color.WHITE);
         text = Play.toString(discard);
         g.setFont(new Font(null, Font.PLAIN, 18));
         fm = g.getFontMetrics();
         x = (200 - fm.stringWidth(text)) / 2;
         g.drawString(text, x, 385);
      }
   }
   
   public void seeTheFuture(int[] futureCards)
   {
      update();
      new FutureDialog(frame, futureCards, this);
   }
   
   public void hideHands(int ... seats)
   {
      for (int seat : seats)
         playerPanels[seat].hideHand();
   }
   
   public void turnStarted(int seat)
   {
      for (PlayerPanel panel : playerPanels)
         panel.setMessage("");
      for (PlayerPanel panel : playerPanels)
         panel.setNumCardsToDraw(0);
      playerPanels[seat].setNumCardsToDraw(game.getNumCardsToDraw());
      update();
   }
   
   public void cardUsed(int seat, int card, int count, int targetSeat, int requestedCard, boolean cardStolen)
   {
      discard = card;
      this.targetSeat = -1;
      playerPanels[seat].setMessage(message(card, count, targetSeat, requestedCard, cardStolen) + "!");
      update();
   }
   
   public void cardDrawn(int seat)
   {
      for (PlayerPanel panel : playerPanels)
         panel.setNumCardsToDraw(0);
      playerPanels[seat].setNumCardsToDraw(game.getNumCardsToDraw());
      update();

   }
   
   public void cardPlayed(int seat, int card, int count, int targetSeat, int requestedCard)
   {
      discard = card;
      this.targetSeat = targetSeat;
      if (card != NOPE)
      {
         for (PlayerPanel panel : playerPanels)
            panel.setMessage("");
      }
      String message = message(card, count, targetSeat, requestedCard, false);
      if (card == NOPE || card == DEFUSE)
         message += "!";
      else
         message += "?";
      playerPanels[seat].setMessage(message);
      update();
   }
   
   public Image getArrow()
   {
      return arrow;
   }
}