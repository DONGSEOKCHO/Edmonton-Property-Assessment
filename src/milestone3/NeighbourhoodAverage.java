/*
 * Name: Dongseok Cho,Zheyuan Xu
 * ID: 3021090
 * Date: 2020/11/8
 * Assignment: Milestone 3
 * Course: CMPT 305 AS01
 * Instructor: Indratmo Indratmo
 * Description: calculate the average assessment value fo a neighbourhood.
 */
package milestone3;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class NeighbourhoodAverage {
    private String neighbourhood;
    private double sum = 0;
    private double average;
    private int count=0;
    private DecimalFormat df = new DecimalFormat("$#,###,##0.00");

    /**
     * initialize
     */
    public NeighbourhoodAverage(){}

    /**
     * setting neighbourhood name and average.
     * @param neighbourhood
     * @param value
     */
    public void setNeighbourhood(String neighbourhood, int value){
        this.neighbourhood=neighbourhood;
        incrementCount(value);
    }

    /**
     * adding new number to existing and averaging the number.
     * @param num
     */
    public void incrementCount(int num){ count++; sum += num; average = sum / (double)count;  }

    /**
     * gets the neighbourhood value.
     * @return neighbourhood string
     */
    public String getNeighbourhood(){ return neighbourhood; }

    /**
     * gets the average value
     * @return formatted average value
     */
    public String getAverage(){ return df.format(average); }

    /**
     * gets the number average value
     * @return double value of average value.
     */
    public double getAverageNum(){return average;}
}
