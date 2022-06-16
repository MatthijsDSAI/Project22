package mapgenerator;

import org.w3c.dom.ls.LSOutput;

import javax.sound.sampled.Line;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.constant.Constable;
import java.util.ArrayList;

public class SimpleMapReader {
    public static void main(String[] args){
        readInCoordinatesOfWall();
    }

    // This method will read in values from the txt file that have only 1 value, e.g. scaling
    //As param, pass the exact name that the object has in the txt file
    public static double readInConstant(String constant){
        String line;
        double value = 0;
        try{
            BufferedReader br = new BufferedReader(new FileReader("experimentMap_1.txt"));

            while((line = br.readLine()) != null){

                if(line.startsWith(constant)){
                    //adds a Line2D object to the list

                    value = getValue(line, constant);
                }
            }
        }
        catch(IOException e){
            System.out.println("File not found");
        }
        return value;
    }

    //used in readInConstant
    private static double getValue(String s, String constant){

        //sb that stores the whole line
        StringBuilder sb = new StringBuilder(s);
        //sb that the individual coords will be appended to
        StringBuilder tempSb = new StringBuilder();
        for(int i=constant.length()+3; i<sb.length(); i++){
            char nextChar = sb.charAt(i);

            if(i==sb.length()-1){
                tempSb.append(nextChar);
                return Double.parseDouble(tempSb.toString());
            }
            //the char is a number, append to the temp sb
            else {
                tempSb.append(nextChar);
            }
        }
        System.err.println("Constant " + constant + " has not been found");
        return 0;

    }

    //This method is similar to readInConstant, only it is useful for e.g. coordinates, as it gives a list.
    //Please mind indexing
    //As param, pass the exact name that the object has in the txt file
    //
    public static ArrayList<Double> readInList(String constant, String fileName){
        String line;

        ArrayList<Double> list = null;
        try{
            BufferedReader br = new BufferedReader(new FileReader(fileName));

            while((line = br.readLine()) != null){

                if(line.startsWith(constant)){

                    list = getValueAsList(line, constant);
                }
            }
        }
        catch(IOException e){
            System.out.println("File not found");
        }
        return list;
    }

    //Used in readInList
    private static ArrayList<Double> getValueAsList(String s, String constant){
        ArrayList<Double> list = new ArrayList<>();
        //sb that stores the whole line
        StringBuilder sb = new StringBuilder(s);
        //sb that the individual coords will be appended to
        StringBuilder tempSb = new StringBuilder();
        for(int i=constant.length()+3; i<sb.length(); i++){
            char nextChar = sb.charAt(i);

            if (nextChar == ' ') {
                list.add(Double.parseDouble(tempSb.toString()));
                tempSb = new StringBuilder();
            }

            //if on the last char, append the char and add to list
            else if(i==sb.length()-1){
                tempSb.append(nextChar);
                list.add(Double.parseDouble(tempSb.toString()));
                return list;
            }
            //the char is a number, append to the temp sb
            else {
                tempSb.append(nextChar);
            }
        }
        System.err.println("Something went wrong with reading in " + s);
        return null;

    }

    //Only to be used for wall generation (Uses Line2D objects)
    public static ArrayList<Rectangle2D> readInCoordinatesOfWall(){
        //Store the coordinates in a Line2D. i am doing this now because I want a very simple GUI to see what the map generation is doing, we can easily change this if needed
        ArrayList<Rectangle2D> list = new ArrayList<>();

        String line;

        //goes over all lines of the file and checks whether the line is about @param object
        try{
            BufferedReader br = new BufferedReader(new FileReader("testmap.txt"));

            while((line = br.readLine()) != null){

                if(line.startsWith("wall")){
                    //adds a Line2D object to the list

                    list.add(addRectangle2D(line, "wall"));
                }
            }
        }
        catch(IOException e){
            System.out.println("File not found");
        }
        return list;
    }

    //Only to be used for wall generation (Uses Line2D objects)
    public static Rectangle2D addRectangle2D(String s, String object){
        //sb that stores the whole line
        StringBuilder sb = new StringBuilder(s);
        //sb that the individual coords will be appended to
        StringBuilder tempSb = new StringBuilder();
        //store coords in this list
        ArrayList<Double> list = new ArrayList<>();
        System.out.println(s);
        //starts at first number after = sign, goes until end of line
        for(int i=object.length()+3; i<sb.length(); i++){
            char nextChar = sb.charAt(i);

            //if a space, add to list and reset the temp sb
            if (nextChar == ' ') {
                list.add(Double.parseDouble(tempSb.toString()));
                tempSb = new StringBuilder();
            }

            //if on the last char, append the char and add to list
            else if(i==sb.length()-1){
                tempSb.append(nextChar);
                list.add(Double.parseDouble(tempSb.toString()));
            }
            //the char is a number, append to the temp sb
            else {
                tempSb.append(nextChar);
            }
        }

        //return line
        double x = list.get(0);
        double y = list.get(3);
        double w = list.get(1)-x;
        double h = list.get(2)-y;
        return new Rectangle2D.Double(x,y,w,h);
    }
}
