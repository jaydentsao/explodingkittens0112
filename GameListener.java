public interface GameListener
{
   void turnStarted(int seat);
   void cardPlayed(int seat, int card, int count, int victim, int requestedCard);
   void cardUsed(int seat, int card, int count, int victim, int requestedCard, boolean cardStolen);
   void cardDrawn(int seat);
}