package Semantic;

import java.util.HashMap;

public class Varibales {
    private static HashMap<String,Integer> var=new HashMap<>();

   public static Integer getValue(String key){
       return var.get(key);
   }

   public static void putVar(String key,Integer value){
       var.put(key,value);
   }

   public static boolean hasVar(String name){
       return var.containsKey(name);
   }


}
