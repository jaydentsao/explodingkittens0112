import java.util.*;

public class Main
{
   public static void main(String[] args)
   {
      main4();
   }

   private static void main1(){  
      Human human= new Human();
      Game game = new Game(new PlaybotCarti(), human);
      Display display = new Display(game, 500);
      human.setDisplay(display);
     // display.hideHands(0);
      game.setDisplay(display);
      int winner = game.play();
   }

   private static void main2(){
      Game game = new Game(new JaydenTsao(), new RemoteStrategy());
      Display display = new Display(game, 400);   
      game.setDisplay(display);
      int winner = game.play();
   }

   private static void main3(){
      int[] win= new int[4];
      int games=100000;
      for (int i = 0; i < games; i++){
         Game game = new Game(new PlaybotCarti(), new Catnap());
         if(game.play()==0) win[0]++;
         game = new Game(new PlaybotCarti(), new Catnap(), new Catnap());
         if(game.play()==0) win[1]++;
         game = new Game(new PlaybotCarti(), new Catnap(), new Catnap(),new Catnap());
         if(game.play()==0) win[2]++;
         game = new Game(new PlaybotCarti(), new Catnap(), new Catnap(),new Catnap(), new Catnap());
         if(game.play()==0) win[3]++;
      }
      System.out.println("Winrate in "+ games+ " games: "+ ((double)win[0]*100)/games + "% against "+1 +" player");
      System.out.println("Winrate in "+ games+ " games: "+ ((double)win[1]*100)/games + "% against "+2 +" players");
      System.out.println("Winrate in "+ games+ " games: "+ ((double)win[2]*100)/games + "% against "+3 +" players");
      System.out.println("Winrate in "+ games+ " games: "+ ((double)win[3]*100)/games + "% against "+4 +" players");
   }

   private static void main4(){
      int x=0;
      int y=0;
      RemoteStrategy remote=new RemoteStrategy();
      for (int i = 0; i < 500; i++) {
         Game game = new Game(new JaydenTsao(), remote);
         if(game.play()==0) x++;
         else y++;
         game = new Game(remote, new JaydenTsao());
         if(game.play()==0) y++;
         else x++;
         System.out.println(i);
      }
      System.out.println("Jayden "+x);
      System.out.println("Remote" +y);
   }

   private static void main5(){
      RemoteTable table = new RemoteTable("169.254.187.90", new JaydenTsao());
   }

   public static void main6() {
      for (int i = 0; i < 10000; i++) {
         Game game = new Game(new JaydenTsao(), new JaydenTsao());
         game.play();
      }
   }
}