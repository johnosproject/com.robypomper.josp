package com.robypomper.java;

public class JavaString {

    public static int occurenceCount(String str, String subStr) {
        int lastIndex = 0;
        int count = 0;

        while(lastIndex != -1){

            lastIndex = str.indexOf(subStr,lastIndex);

            if(lastIndex != -1){
                count ++;
                lastIndex += subStr.length();
            }
        }

        return count;
    }

}
