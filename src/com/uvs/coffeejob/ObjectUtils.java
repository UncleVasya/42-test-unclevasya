package com.uvs.coffeejob;

public class ObjectUtils {
    
    public static boolean bothNullOrEqual(Object a, Object b) {
        return a == null && b == null || 
               a != null && b != null && a.equals(b);
    }

}
