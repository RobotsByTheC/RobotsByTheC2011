/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.wpi.first.wpilibj;

/**
 *
 * @author sean
 */
public class SuperString {
    /////////////// STRING MODEFYING METHODS \\\\\\\\\\\\\\\\
    /**
     * Some important info for using the multiple choice selection stuff.
     * Strings must be formated like so:
     *
     * "[A)1]  B)2   C)3   D)4 "
     * Capitalize A, B, C, and D
     * A space at the begining and end
     * Three spaces between options
     * The brackets OVERWRITE the spaces
     *
     * STRINGS MUST BE EXACTLY LIKE THAT
     * The only things that can be changed are the numbers, they can be replaced by any
     * letters or numbers, BUT NOT SPACES
     */


    public static String selectRight(String str){
        if(getSelection(str)!=getNumberOfOptions(str)){
            int oldRightBrac = str.indexOf("]");
            str=str.replace('[', ' ');
            str=str.replace(']', ' ');
            int newLeftBrac = oldRightBrac+2;
            str=replaceAt(str, '[', newLeftBrac);
            int newRightBrac = str.indexOf(' ', newLeftBrac+1);
            if(newRightBrac!=-1)
                str=replaceAt(str, ']', newRightBrac);
            else
                str=str+"]";
        }
        else{ //if the last option is selected
            str=str.replace('[', ' ');
            str=str.replace(']', ' ');
            str="["+str.substring(1);
            str=replaceAt(str, ']', str.indexOf(' '));
            // str=str.replace(' ', '[');
            //str=replaceAt(str, ']', str.indexOf(' ')); //Add a [ to the begining, and place a ] before the first space
        }
        return str;
    }

    public static String selectLeft(String str){
        if(getSelection(str)!=1){
            int oldLeftBrac = str.indexOf('[');
            str=str.replace('[', ' ');
            str=str.replace(']', ' ');
            int newRightBrac=oldLeftBrac-1;
            str=replaceAt(str, ']', newRightBrac-1);
            str=replaceLastBefore(str, '[', newRightBrac-1);
        }
        else{
            str=str.replace('[', ' ');
            str=str.replace(']', ' ');
            str=replaceAt(str, ']', str.length());
            str=replaceAt(str, '[', str.lastIndexOf(' '));
            //str=replaceLastBefore(str, '[', str.length())+"]"; //Add a [ to the begining, and place a ] before the first space
        }
        return str;
    }

    public static String replaceLastBefore(String str, char replacement, int index){
        String strP1 = str.substring(0, index);
        str=replaceAt(str, replacement, strP1.lastIndexOf(' '));
        return str;
    }

    public static String replaceAt(String str, char replacement, int index){
        if(index!=str.length()){
            String strP1 = str.substring(0, index); //Break the string into two strings, leaving out the char at the index given
            String strP3 = str.substring(index+1);
            str = strP1+replacement+strP3;          //put the two substrings back together, but with the replacement in the middle
        }
        else
            str=str.substring(0, str.length()-1)+replacement;   //if replaceing the last char, make a string with everything but the last char, and add the replacement
        return str;
    }

    public static int getSelection(String str){
        int selection=0;
        if(str.indexOf('[')+1 == str.indexOf('A'))
            selection=1;
        else if(str.indexOf('[') + 1 == str.indexOf('B'))
            selection=2;
        else if(str.indexOf('[') + 1 == str.indexOf('C'))
            selection=3;
        else if(str.indexOf('[') + 1 == str.indexOf('D'))
            selection=4;
        return selection;
    }

    public static int getNumberOfOptions(String str){
        int numberOfOptions=4;
        if(str.indexOf('C')==-1)
            numberOfOptions=2;
        else if(str.indexOf('D')==-1)
            numberOfOptions=3;
        return numberOfOptions;
    }

//    public static String allowSelection(String str, Joystick stick){
//        if(stick.getRawButton(8)){
//            str=selectLeft(str);
//            Timer.delay(.1);
//        }
//        if(stick.getRawButton(9)){
//            str=selectRight(str);
//            Timer.delay(.1);
//        }
//        return str;
//    }
}
