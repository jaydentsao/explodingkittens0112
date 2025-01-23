public class Choice
{
   private int value;
   private String name;
   
   public Choice(int value, String name)
   {
      this.value = value;
      this.name = name;
   }
   
   public String toString()
   {
      return name;
   }
   
   public int getValue()
   {
      return value;
   }
}