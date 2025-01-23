import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class FutureDialog extends JDialog implements MouseListener
{
   public FutureDialog(Frame frame, int[] future, Display display)
   {
      super(frame, true);
      
      setTitle("The Future");
      getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.PAGE_AXIS));
      
      for (int card : future)
         getContentPane().add(new JLabel(new ImageIcon(display.getImage(card))));

      addMouseListener(this);

      pack();
      setVisible(true);
   }
   
   public void mousePressed(MouseEvent e)
   {
      dispose();
   }
   
   public void mouseReleased(MouseEvent e)
   {
   }

   public void mouseClicked(MouseEvent e)
   {
   }

   public void mouseEntered(MouseEvent e)
   {
   }
   
   public void mouseExited(MouseEvent e)
   {
   }
}
