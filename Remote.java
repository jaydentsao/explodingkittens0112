import java.io.*;
import java.net.*;
import java.util.*;

public abstract class Remote implements Runnable
{
    private BufferedReader in;
    private PrintWriter out;
    private static boolean debug = false;
    
    public static void setDebug()
    {
        debug = true;
    }

    public Remote(Socket socket)
    {
        try
        {
            System.out.println("connected to " + socket.getInetAddress());
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            new Thread(this).start();
        }
        catch(IOException e)
        {
            throw new RuntimeException(e);
        }            
    }
    
    public void send(String message)
    {
        if (debug)
            System.out.println(getClass().getName() + " sending: " + message);
        out.println(message);
    }
    
    public void run()
    {
        try
        {
            while (true)
            {
                String line = in.readLine();
                if (debug)
                    System.out.println(getClass().getName() + " received: " + line);
                received(line.split(" "));
            }
        }
        catch(IOException e)
        {
            throw new RuntimeException(e);
        }
    }
    
    public static String toString(Play play)
    {
      return play.getCard() + " " + play.getCount() + " " + play.getVictim() + " " + play.getRequestedCard();
    }
    
    public abstract void received(String[] tokens);
}