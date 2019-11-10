package Semantic;

public class HexNumber {
   public static Integer toDecimal(String hexNumber){
       return Integer.parseInt(hexNumber, 16);
   }

   public static String toHexString(Integer i){
       return Integer.toHexString(i);
   }
}
