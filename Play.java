public class Play implements Constants
{
   private static final String[] cardNames = {
      "Exploding Kitten", "Defuse", "Attack", "Favor", "Nope", "Shuffle", "Skip", "See The Future",
      "Tacocat", "Cattermelon", "Hairy Potato Cat", "Beard Cat", "Rainbow Ralphing Cat"};
   
   private int card;
   private int count;
   private int victim;
   private int requestedCard;
   
   public Play(int card)
   {
      this(card, 1, -1, -1);
   }
   
   public Play(int card, int count, int victim)
   {
      this(card, count, victim, -1);
   }

   public Play(int card, int count, int victim, int requestedCard)
   {
      if (card < 0 || card > 12)
         throw new RuntimeException("invalid card " + card);
      if (count < 1 || count > 3)
         throw new RuntimeException("invalid count " + count);
      if (victim < -1)
         throw new RuntimeException("invalid victim " + victim);
      if (requestedCard < -1 || card > 12)
         throw new RuntimeException("invalid requested card " + requestedCard);
         
      if (count == 1)
      {
         if (card == EXPLODING_KITTEN || card == DEFUSE || card == NOPE || card >= 8)
            throw new RuntimeException("cannot play 1 " + cardNames[card]);
         if (card == FAVOR && victim == -1)
            throw new RuntimeException("cannot play 1 Favor with victim -1");
         if (card != FAVOR && victim != -1)
            throw new RuntimeException("cannot play 1 " + cardNames[card] + " with victim " + victim);
         if (requestedCard != -1)
            throw new RuntimeException("cannot play 1 card with requested card " + requestedCard);
      }
      else if (count == 2)
      {
         if (victim == -1)
            throw new RuntimeException("cannot play 2 cards with victim -1");
         if (requestedCard != -1)
            throw new RuntimeException("cannot play 2 cards with requested card " + requestedCard);
      }
      else if (count == 3)
      {
         if (victim == -1)
            throw new RuntimeException("cannot play 3 cards with victim -1");
         if (requestedCard == -1)
            throw new RuntimeException("cannot play 3 cards with requested card -1");
      }

      this.card = card;
      this.count = count;
      this.victim = victim;
      this.requestedCard = requestedCard;
   }

   public int getCount()
   {
      return count;
   }
   
   public int getCard()
   {
      return card;
   }
   
   public static String toString(int card)
   {
      return cardNames[card];
   }
   
   public String toString()
   {
      String s = cardNames[card];
      if (count != 1)
         s += "(x" + count + ")";
      if (victim != -1)
         s += "->" + victim;
      if (requestedCard != -1)
         s += ":" + cardNames[requestedCard];
      return s;
   }
   
   public int getVictim()
   {
      return victim;
   }
   
   public int getRequestedCard()
   {
      return requestedCard;
   }
}