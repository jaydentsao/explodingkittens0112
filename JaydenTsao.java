import java.util.*;

public class JaydenTsao implements Strategy, GameListener
{
   private ArrayList<Integer> deck;
   private Table table;
   private int[] marcel;   
   private boolean unknown;
   private int[] totalCards;
   private int numDefuse;
   private boolean stf;
   //private int aggresion=0;
   //メ
   public String getName()
   {
      return "Malenia,⠀Bot⠀of⠀Miquella";
   }

   public void gameStarted(Table table)
   {
      totalCards=new int[13];
      this.table = table;  // save table for later
      totalCards[DEFUSE]=Math.max(6,table.getNumSeats()+2)-countUp(table.getHand())[DEFUSE];
      totalCards[ATTACK]=4;
      totalCards[FAVOR]=4;
      totalCards[NOPE]=5;
      totalCards[SHUFFLE]=4;
      totalCards[SKIP]=4;
      totalCards[SEE_THE_FUTURE]=5;
      totalCards[TACOCAT]=4;
      totalCards[CATTERMELON]=4;
      totalCards[HAIRY_POTATO_CAT]=4;
      totalCards[BEARD_CAT]=4;
      totalCards[RAINBOW_RALPHING_CAT]=4;
      numDefuse=countUp(table.getHand())[DEFUSE];
      deck=new ArrayList<>();
      for (int i = 0; i < table.getDrawPileSize(); i++) 
         deck.add(null);
      marcel= new int[table.getNumSeats()];
      Arrays.fill(marcel, 1);
      table.addListener(this);
   }

   public Play play()
   {
      List<Integer> hand=table.getHand();
      int[] count=countUp(hand);
      if(numDefuse<count[DEFUSE]){
         totalCards[DEFUSE]-=count[DEFUSE]-numDefuse;
      }
      
      if(deck.get(0)==null){
         if(table.getDrawPileSize()>getNumBombs()*10) return null;
         if(hand.contains(SEE_THE_FUTURE)){
            return new Play(SEE_THE_FUTURE);
         }
         if(table.getNumSeats()-getNumBombs()==1&&table.getDrawPileSize()>getNumBombs()*4) return null;
         if(hand.contains(FAVOR)){
            if(getFavorVictim()!=-1 && getFavorVictim()!=table.getSeat())
               return new Play(FAVOR,1,getFavorVictim());
         }
         if(count[HAIRY_POTATO_CAT]>=2||count[CATTERMELON]>=2||count[RAINBOW_RALPHING_CAT]>=2||count[BEARD_CAT]>=2||count[TACOCAT]>=2){
            Play play= miscCats(hand);
            if(play!=null) return play;
         }
         if(table.getDrawPileSize()<=getNumBombs()/2 ||(table.getDrawPileSize()>getNumBombs()/4 && !hand.contains(DEFUSE))){
            if(hand.contains(ATTACK))
               return new Play(ATTACK);
            if(hand.contains(SKIP))
               return new Play(SKIP);
         }
         //deck.remove(0);
         unknown=false;
         return null;
      }
      else if(deck.get(0)==EXPLODING_KITTEN){
         if(unknown && hand.contains(SEE_THE_FUTURE)) return new Play(SEE_THE_FUTURE);
         if(hand.contains(ATTACK))
            return new Play(ATTACK);
         if(hand.contains(SKIP))
            return new Play(SKIP);
         if(hand.contains(FAVOR)){
            if(getFavorVictim()!=-1&& getFavorVictim()!=table.getSeat())
               return new Play(FAVOR,1,getFavorVictim());
         }
         if(hand.contains(SHUFFLE)){
            if(countUp(hand)[SHUFFLE]>1&&deck.size()<getNumBombs()*4){
               if(getThreeVictim()!=-1&&getFavorVictim()!=-1){
                  if(countUp(hand)[SHUFFLE]==3) return new Play(SHUFFLE, 3, getThreeVictim(), requestedCard());
                  return new Play(SHUFFLE, 2 ,getFavorVictim());
               } 
            }
            deck=new ArrayList<>();
            for (int i = 0; i < table.getDrawPileSize(); i++) {
               deck.add(0,null);
            }
            return new Play(SHUFFLE);
         }
         if(count[HAIRY_POTATO_CAT]>=2||count[CATTERMELON]>=2||count[RAINBOW_RALPHING_CAT]>=2||count[BEARD_CAT]>=2||count[TACOCAT]>=2){
            Play play= miscCats(hand);
            if(play!=null) return play;
         }


         
         //deck.remove(0);
         unknown=false;
         return null;
      }      
      else{
         unknown=false;
         //deck.remove(0);
         return null;
      }
   }

   private int getFavorVictim(){
      int[] ind=new int[]{-1,Integer.MAX_VALUE};
      for (int i = 0; i < table.getNumSeats(); i++) {
         if(i!=table.getSeat()&&table.getHandSize(i)<ind[1]&&table.getHandSize(i)!=0&&!table.hasExploded(i))
            ind=new int[]{i,table.getHandSize(i)};
      }
      return ind[0];
   }

   private int getNumBombs(){
      int nym=table.getNumSeats();
      for (int i = nym-1; i >=0 ; i--) {
         if(table.hasExploded(i)) nym--;
      }
      return nym;
   }

   private int[] countUp(List<Integer> nym){
      int[] sanyma=new int[13];
      for (int i : nym) 
         sanyma[i]++;
      return sanyma;
   }
   
   public int defuse()
   {
      int ind=(int)(Math.random()*table.getDrawPileSize());
      if(table.getDrawPileSize()<2) return 0;
      while(ind%getNumBombs()+1==table.getSeat()) ind=(int)(Math.random()*table.getDrawPileSize());
      deck.add(ind,EXPLODING_KITTEN);
      return ind;
   }

   private int getThreeVictim(){
      List<Integer> list= new ArrayList<>();
      for (int i = 0; i < table.getNumSeats(); i++) {
         if(table.getSeat()==i) continue;
         if(table.hasExploded(i)) continue;
         list.add(i);
      }
      int n=-1;
      for (Integer i : list) {
         if(marcel[i]==1)
            n=n==-1?i: (marcel[i]>marcel[n]?i:table.getHandSize(n)<table.getHandSize(i)?i:n);
      }
      if(n==-1)
         for (Integer i : list) 
            if(n==-1 || table.getHandSize(n)<table.getHandSize(i)) n=i;
      return n;
   }

   private int requestedCard(){
      if(totalCards[DEFUSE]!=0) return DEFUSE;
      if(totalCards[ATTACK]!=0) return ATTACK;
      if(totalCards[SKIP]!=0) return ATTACK;
      if(totalCards[FAVOR]!=0) return ATTACK;
      return DEFUSE;
   }

   private Play miscCats(List<Integer> hand){
      int[] count=countUp(hand);
      if(getFavorVictim()==-1||getThreeVictim()==-1) return null;
      if(count[HAIRY_POTATO_CAT]>=2){
         if(count[HAIRY_POTATO_CAT]==3){
            return new Play(HAIRY_POTATO_CAT,3,getThreeVictim(),requestedCard());
         }
         return new Play(HAIRY_POTATO_CAT,2,getFavorVictim());
      }
      if(count[CATTERMELON]>=2){
         if(count[CATTERMELON]==3){
            return new Play(CATTERMELON,3,getFavorVictim(),requestedCard());
         }
         return new Play(CATTERMELON,2,getFavorVictim());
      }
      if(count[RAINBOW_RALPHING_CAT]>=2){
         if(count[RAINBOW_RALPHING_CAT]==3){
            return new Play(RAINBOW_RALPHING_CAT,3,getThreeVictim(),requestedCard());
         }
         return new Play(RAINBOW_RALPHING_CAT,2,getFavorVictim());
      }
      if(count[BEARD_CAT]>=2){
         if(count[BEARD_CAT]==3){
            return new Play(BEARD_CAT,3,getThreeVictim(),requestedCard());
         }
         return new Play(BEARD_CAT,2,getFavorVictim());
      }
      if(count[TACOCAT]>=2){
         if(count[TACOCAT]==3){
            return new Play(TACOCAT,3,getThreeVictim(),requestedCard());
         }
         return new Play(TACOCAT,2,getFavorVictim());
      }
      unknown=false;
      return null;
   }
   
   public int favor(int activeSeat)
   {
      List<Integer> hand=table.getHand();
      int[] nym=countUp(hand);
      int pot=nym[HAIRY_POTATO_CAT];
      int rain=nym[RAINBOW_RALPHING_CAT];
      int taco=nym[TACOCAT];
      int melon=nym[CATTERMELON];
      int beard=nym[BEARD_CAT];
      if(pot==1) return HAIRY_POTATO_CAT;
      if(rain==1) return RAINBOW_RALPHING_CAT;
      if(taco==1) return TACOCAT;
      if(melon==1) return CATTERMELON;
      if(beard==1) return BEARD_CAT;
      if(nym[SHUFFLE]!=0)
         return SHUFFLE;
      if(nym[FAVOR]!=0)
         return FAVOR;
      if(nym[NOPE]!=0)
         return NOPE;
      if(nym[SKIP]!=0)
         return SKIP;
      if(nym[ATTACK]!=0)
         return ATTACK;
      if(nym[SEE_THE_FUTURE]!=0)
         return SEE_THE_FUTURE;
      return hand.get(0);
   }

   public boolean nope(int activeSeat, Play play, List<Integer> nopers)
   {
      if(play.getVictim()==table.getSeat()) {
         if(play.getCard()==SKIP||play.getCard()==ATTACK||play.getCount()==3){
            return true;
         }
         if(play.getCard()==FAVOR){
            if(favor(activeSeat)==DEFUSE||favor(activeSeat)==ATTACK||favor(activeSeat)==SEE_THE_FUTURE)
               return true;
         }
         if(play.getCount()==2){
            if((double)countUp(table.getHand())[DEFUSE]/table.getHandSize(table.getSeat())<0.2) return true;
         }
      }
      return false;
   }
   
   public void seeTheFuture(int[] futureCards)
   {
      for (int i = 0; i< futureCards.length; i++) 
         deck.remove(0);
      for (int i = futureCards.length-1; i >= 0; i--) 
         deck.add(0,futureCards[i]);
      
   }

 
   public void turnStarted(int seat) {
      
   }

   public void cardPlayed(int seat, int card, int count, int victim, int requestedCard) {

   }

   public void cardUsed(int seat, int card, int count, int victim, int requestedCard, boolean cardStolen) {
      if(requestedCard==DEFUSE&&cardStolen){
         marcel[seat]++;
         marcel[victim]++;
      }
      totalCards[card]--;
      if(card==DEFUSE){
         deck.add(0,EXPLODING_KITTEN);
      }
      if(card==DEFUSE && marcel[seat]!=0){
         marcel[seat]--;
         unknown=true;
         
      }
      if(card==SHUFFLE){
         deck=new ArrayList<>();
            for (int i = 0; i < table.getDrawPileSize(); i++) {
               deck.add(0,null);
            }
         return;
      }
      if(card==SEE_THE_FUTURE)
         stf=true;
      if(card==SKIP||card==ATTACK){
         if(deck.get(0)!=null) return;
         if(stf) deck.add(0,EXPLODING_KITTEN);
         else {
            deck.add(0,EXPLODING_KITTEN);
            unknown=true;
         }
      }
   }

   public void cardDrawn(int seat) {
      stf=false;
      if(unknown) unknown=false;
      deck.remove(0);
   }
}