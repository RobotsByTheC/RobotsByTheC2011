/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.wpi.first.wpilibj;

/**
 *
 * @author Halloran Film Studio
 */
public class RobotMenu{
    String[] line;          //each line to be printed on the LCD, made up of a row of selection
    String[][] selection;   //the options in a menue
    boolean title;          //if there is a title
    int rows, cols;

    public RobotMenu(int r, int c){     //constructor with no title
        rows = r;
        cols = c;
        title=false;
        line = new String[rows];
        selection = new String[rows][cols];
        for(int i=0;i<rows;i++){
            line[i]="";
            for(int p=0;p<cols;p++)
                selection[i][p]=" NOT_SET ";
        }
        selection[0][0]="~NOT_SET~";
    }

    public RobotMenu(int r, int c, String str){   //constructor sets the title to selection[0][0]
        rows = r;
        cols = c;
        title=true;
        line = new String[rows];
        selection = new String[rows][cols];
        for(int i=0;i<rows;i++){
            line[i]="";
            for(int p=0;p<cols;p++)
                selection[i][p]=" NOT_SET ";
        }
        selection[0][0]=str;
        selection[1][0]="~NOT_SET~";
    }

    public RobotMenu(int r, int c, String[][] str){   //constructor that takes a string matrix for the selection
        rows = r;
        cols = c;
        title=false;
        line = new String[rows];
        selection=str;
        for(int i=0;i<rows;i++)
           line[i]="";
        fixSpacing();
    }

    public RobotMenu(int r, int c, String newTitle, String[][] strMatrix){ //takes a string matrix and a title
        rows = r;
        cols = c;
        title=true;
        line = new String[rows];
        selection=strMatrix;
        selection[0][0]=newTitle;
        for(int i=0;i<rows;i++)
           line[i]="";
        fixSpacing();
    }

    public void setSelection(int row, int col, String str){
        selection[row][col] = str;
    }
    public String getSelection(int row, int col){
        return selection[row][col];
    }

    public void setTitle(String str){
        title=true;
        selection[0][0]=str;
        for(int i=1;i<cols;i++)
            selection[0][i]="";
    }
    public String getTitle(){
        return (title) ? selection[0][0] : "";
    }

    public void select(String direction){
        direction=direction.toUpperCase();
        int oldRow=getIndexSelected()[0];
        int oldCol=getIndexSelected()[1];
        selection[oldRow][oldCol]=selection[oldRow][oldCol].replace('~', ' ');
        int newRow=-1, newCol=-1;
        if(direction.equals("UP")){
            if(!title)  newRow = (oldRow!=0) ? oldRow-1 : rows-1;
            else        newRow = (oldRow!=1) ? oldRow-1 : rows-1;
            newCol=oldCol;
        }
        else if(direction.equals("DOWN")){
            if(!title)  newRow = (oldRow!=rows-1) ? oldRow+1 : 0;
            else        newRow = (oldRow!=rows-1) ? oldRow+1 : 1;
            newCol=oldCol;
        }
        else if(direction.equals("LEFT")){
            newRow=oldRow;
            newCol = (oldCol!=0) ? oldCol-1 : cols-1;
        }
        else if(direction.equals("RIGHT")){
            newRow=oldRow;
            newCol = (oldCol!=cols-1) ? oldCol+1 : 0;
        }
        selection[newRow][newCol]=selection[newRow][newCol].substring(1);
        selection[newRow][newCol]=selection[newRow][newCol].substring(0, selection[newRow][newCol].indexOf('~'));
        selection[newRow][newCol]=" "+selection[newRow][newCol]+" ";
    }
    public int[] getIndexSelected(){
        int[] rowcol = {-1,-1};
        for(int i=0;i<rows;i++){
            for(int q=0;q<cols;q++){
                if(selection[i][q].startsWith("~")){
                    rowcol[0]=i;
                    rowcol[1]=q;
                }
            }
        }
        return rowcol;
    }
    public String getSelected(){
        String str = selection[getIndexSelected()[0]][getIndexSelected()[1]];
        str = str.substring(1);
        str = str.substring(0, str.indexOf('~')-1);
        return str;
    }

    public String getLine(int x){
        line[x]="";
        for(int i=0; i<rows; i++){
            line[x]+=selection[x][i];
        }
        return line[x];
    }

    private void fixSpacing(){
        for(int i=(!title)?0:1; i<rows; i++){
            for(int q=0; q<cols; q++){
                if(selection[i][q].length()<12){
                    int spaces = 12-selection[i][q].length();
                    for(int w=0;w<spaces;w++)
                        selection[i][q]+=" ";
                }
            }
        }
    }

    public String[] getMenu(){
        fixSpacing();
        String[] menu = new String[rows];
        for(int i=0;i<rows;i++){
            menu[i]=getLine(i);
        }
        return menu;
    }

    private String replaceAt(String str, char replacement, int index){
        if(index!=str.length()){
            String strP1 = str.substring(0, index); //Break the string into two strings, leaving out the char at the index given
            String strP3 = str.substring(index+1);
            str = strP1+replacement+strP3;          //put the two substrings back together, but with the replacement in the middle
        }
        else
            str=str.substring(0, str.length()-1)+replacement;   //if replaceing the last char, make a string with everything but the last char, and add the replacement
        return str;
    }
}
