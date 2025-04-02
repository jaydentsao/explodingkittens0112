import java.util.*;

public class Main
{
   public static void main(String[] args)
   {
      main5();
   }

   private static void main1(){  
      Human human= new Human();
      Game game = new Game(new JaydenTsao(), human);
      Display display = new Display(game, 500);
      human.setDisplay(display);
     // display.hideHands(0);
      game.setDisplay(display);
      int winner = game.play();
   }

   private static void main2(){
      Game game = new Game(new JaydenTsao(), new RemoteStrategy());
      Display display = new Display(game, 500);   
      game.setDisplay(display);
      int winner = game.play();
   }

   private static void main3(){
      int[] win= new int[4];
      int games=100000;
      for (int i = 0; i < games; i++){
         Game game = new Game(new JaydenTsao(), new Catnap());
         if(game.play()==0) win[0]++;
         game = new Game(new JaydenTsao(), new Catnap(), new Catnap());
         if(game.play()==0) win[1]++;
         game = new Game(new JaydenTsao(), new Catnap(), new Catnap(),new Catnap());
         if(game.play()==0) win[2]++;
         game = new Game(new JaydenTsao(), new Catnap(), new Catnap(),new Catnap(), new Catnap());
         if(game.play()==0) win[3]++;
      }
      System.out.println("Winrate in "+ games+ " games: "+ ((double)win[0]*100)/games + "% against "+1 +" player");
      System.out.println("Winrate in "+ games+ " games: "+ ((double)win[1]*100)/games + "% against "+2 +" players");
      System.out.println("Winrate in "+ games+ " games: "+ ((double)win[2]*100)/games + "% against "+3 +" players");
      System.out.println("Winrate in "+ games+ " games: "+ ((double)win[3]*100)/games + "% against "+4 +" players");
   }

   private static void main4(){
      //Remote.setDebug();
      int[] a= new int[5];
      RemoteStrategy remote=new RemoteStrategy();
      // RemoteStrategy remote1=new RemoteStrategy();
      // RemoteStrategy remote2=new RemoteStrategy();
      // RemoteStrategy remote3=new RemoteStrategy();
      for (int i = 0; i < 500; i++) {
         Game game = new Game(new JaydenTsao(), remote);
         int x=game.play();
         a[x]++;
        
         System.out.println(i);
      }
      System.out.println("Jayden "+a[0]);
      System.out.println(remote.getName()+" " +a[1]);
      // System.out.println(remote1.getName()+" " +a[2]);
      // System.out.println(remote2.getName()+" " +a[3]);
      // System.out.println(remote3.getName()+" " +a[4]);
   }

   private static void main5(){
      //Remote.setDebug();
      RemoteTable table = new RemoteTable("10.13.98.34", new JaydenTsao());
   }

   public static void main6() {
      int[] arr=new int[5];
      for (int i = 0; i < 100000; i++) {
         Game game = new Game(new JaydenTsao(), new JaydenTsao(), new JaydenTsao());
         arr[game.play()]++;
      }
      for (int i : arr) {
         System.out.println(i);
      }
   }
}