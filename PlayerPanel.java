import java.awt.*;
import java.util.*;
import javax.swing.*;

public class PlayerPanel extends JComponent
{
   private Game game;
   private int seat;
   private Display display;
   private String message;
   private boolean showHand;
   private int numCardsToDraw;

   public PlayerPanel(Game game, int seat, Display display)
   {
      this.game = game;
      this.seat = seat;
      this.display = display;
      message = "";
      showHand = true;
      numCardsToDraw = 0;
      setPreferredSize(new Dimension(800, 100));
      setBorder(BorderFactory.createTitledBorder(game.getPlayers()[seat].toString()));
      setBackground(new Color(255, 233, 188));
      setOpaque(true);
   }
   
   public void paintComponent(Graphics g)
   {
      if (numCardsToDraw > 0)
      {
         g.drawImage(display.getArrow(), 16, 16, 64, 64, null);
         if (numCardsToDraw > 1)
         {
            g.setColor(Color.BLACK);
            g.setFont(new Font(null, Font.BOLD, 24));
            g.drawString("" + numCardsToDraw, 26, 57);
         }
      }
      
      Player player = game.getPlayers()[seat];
      ArrayList<Integer> hand = player.getHand();
      for (int i = 0; i < hand.size(); i++)
      {
         int card = hand.get(i);
         int x = 64 * i + 100;
         if (showHand)
            g.drawImage(display.getImage(card), x, 16, 64, 64, null);
         else
         {
            g.setColor(Display.CARD_COLOR);
            g.fillRect(x, 16, 64, 64);
         }
         g.setColor(Color.BLACK);
         g.drawRect(x, 16, 64, 64);
      }
      
      if (!player.isAlive())
      {
         g.setColor(new Color(255, 0, 0, 127));
         g.fillRect(0, 0, getWidth(), getHeight());
      }
      int fontSize = 36;
      g.setFont(new Font(null, Font.BOLD, fontSize));  
      FontMetrics fm = g.getFontMetrics();
      int x = (getWidth() - fm.stringWidth(message)) / 2;
      int y = (getHeight() - fontSize) / 2 + fontSize;
      g.setColor(Color.BLACK);
      g.drawString(message, x, y - 1);
      g.drawString(message, x - 1, y);
      g.drawString(message, x + 1, y);
      g.drawString(message, x, y + 1);
      if (message.endsWith("?"))
         g.setColor(Color.CYAN);
      else
         g.setColor(Color.RED);
      g.drawString(message, x, y);
   }
   
   public void setMessage(String message)
   {
      this.message = message;
   }
   
   public void hideHand()
   {
      showHand = false;
   }
   
   public void setNumCardsToDraw(int numCardsToDraw)
   {
      this.numCardsToDraw = numCardsToDraw;
   }
}