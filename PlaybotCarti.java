import java.util.*;

public class PlaybotCarti implements Strategy, GameListener {
    private Table table;
    private double aggression;
    private int numSeats;
    private ArrayList<Integer> deck;
    private boolean stf;

    public String getName()
    {
        return "IAMMUSIC";
    }
   
    public void gameStarted(Table table)
    {
        this.table = table;  // save table for later
        numSeats=table.getNumSeats();
        deck=new ArrayList<>();
        for (int i = 0; i < table.getDrawPileSize(); i++) 
            deck.add(null);
    }

    public Play play()
    {
        List<Integer> hand=table.getHand();
        int[] count=countUp(hand);
        boolean skip=true;
        
        if(deck.get(0)==EXPLODING_KITTEN) aggression=100;
        if(deck.get(1)==EXPLODING_KITTEN) skip=false;

        if(numSeats>table.getNumSeats()){
            numSeats=table.getNumSeats();
            aggression=getNumBombs()*100/table.getDrawPileSize();
        }
        if(aggression>20) {
            if(aggression<50){
                if(deck.get(0)==null){
                    if(count[SEE_THE_FUTURE]!=0 && deck.get(0)==null) return new Play(SEE_THE_FUTURE);
                }
                if(count[SHUFFLE]!=0) return new Play(SHUFFLE);
                if(aggression>40&&count[SKIP]!=0 &&skip) return new Play(SKIP);
            }
            else {
                if(aggression<60&&count[SKIP]!=0&&skip) return new Play(SKIP);
            }
        }
        return null;
    }
    
    private int getNumBombs(){
        int nym=table.getNumSeats();
        for (int i = nym-1; i >=0 ; i--) {
           if(table.hasExploded(i)) nym--;
        }
        return nym;
     }

    public int defuse()
    {
        //TODO: add unimplemented method
        return 0;  // I always insert an Exploding Kitten at the top of the draw pile
    }
    
    public int favor(int activeSeat)
    {
        //TODO: add unimplemented method
        return table.getHand().get(0);  // When asked for a favor, I always return the first card in my hand
    }

    public boolean nope(int activeSeat, Play play, List<Integer> nopers)
    {
        //TODO: add unimplemented method
        return false;  // I never play a Nope card
    }
    
    public void seeTheFuture(int[] futureCards)
    {
        for (int i : futureCards) 
            deck.remove(0);
        for (int i = futureCards.length; i > 0; i--) 
            deck.add(0);
    }

    public void turnStarted(int seat) {

    }

    public void cardPlayed(int seat, int card, int count, int victim, int requestedCard) {

    }

    public void cardUsed(int seat, int card, int count, int victim, int requestedCard, boolean cardStolen) {
        switch (card){
            case SKIP: aggression+=stf?100:30;
            case ATTACK: aggression+=stf?300:40;
            case DEFUSE: aggression +=60;
            case SEE_THE_FUTURE: stf=true;
            case SHUFFLE: stf=false;
        }
    }

    private int[] countUp(List<Integer> nym){
        int[] sanyma=new int[13];
        for (int i : nym) 
           sanyma[i]++;
        return sanyma;
     }

    public void cardDrawn(int seat) {
        aggression+=table.getHand().contains(DEFUSE)?(getNumBombs()/table.getDrawPileSize())/4:(getNumBombs()/table.getDrawPileSize()/2);
        stf=false;
        System.out.println(aggression);
    }
}
