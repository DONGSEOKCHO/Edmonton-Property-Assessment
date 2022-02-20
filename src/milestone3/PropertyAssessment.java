/*
 * Name: Dongseok Cho
 * ID: 3021090
 * Date: 2020/11/8
 * Assignment: Milestone 2
 * Course: CMPT 305 AS01
 * Instructor: Indratmo Indratmo
 * Description: Calculate the Statistic value of property assessment.
 */
package milestone3;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class PropertyAssessment{
    private int max;
    private int min;
    private int range;
    private long mean;
    private int median;
    private int sd;
    private int n;
    List<AssessmentValue> values;

    public PropertyAssessment(List<AssessmentValue> values){
        this.values = values;
    }

    /**
     * Calculate and assign the Statistic Value from the list of AssessmentValue.
     * @param values
     */
    public void stat(List<AssessmentValue> values){
        reset();
        n= values.size();
        int acNum,i=0,begin=0;
        while(i<values.size()) {
            acNum = values.get(i).assessedValue;
            if(min==0 && begin==0) {min=acNum;begin=1;}//initializing minimum value;
            this.maxMin(acNum);
            mean += acNum;
            i++;
        }
        this.statCalc(values);
    }// looping to get values.

    /**
     * Calculate and set the mean, median,standard deviation, and range value.
     * @param values
     */
    private void statCalc(List<AssessmentValue> values){
        this.mean = Math.round((double) mean/n);
        range = max;
        this.md(values);
        this.sd(values);
    }

    /**
     * Calculate and set median value from the AssessmentValue list.
     * @param values
     */
    private void  md(List<AssessmentValue> values) {
        int i=0;
        int[] list = new int[n];
        while(i < n){list[i]=values.get(i).assessedValue;i++;}//filling list
        Arrays.sort(list);//to get the median. values must be sorted.
        if((n%2)==1){median = list[((n/2)+1)-1];}//if odd population.
        else{median = (list[((n/2)+1)-1]+list[(n/2)-1])/2;}//if even population.
    }//median calculation.

    /**
     * Calculate and set the standard deviation from the AssessmentValue list.
     * @param values
     */
    private void sd(List<AssessmentValue> values) {
        long num = 0;
        int i=0;
        while(i<values.size()) {
            num += (long) Math.pow((values.get(i).assessedValue - mean), 2);
            i++;
        }
        double fn = Math.pow((num/n),0.5);
        this.sd = (int) Math.round(fn);//rounding up the double value
    }//standard deviation calculation.

    /**
     * compare numbers to set the min and max value;
     * @param x
     */
    private void maxMin(int x){
        if(max < x){max = x;}
        else if(min > x){min = x;}
    }//setting max and min value

    /**
     * print out all values in order.
     * @return string of values
     */
    public String toString(){
        DecimalFormat df = new DecimalFormat("$#,###");
        return "Statistics of Assessed Values:\n\n" +
                "Number of properties: " + n +"\n" +
                "Min: " + df.format(min) + "\n" +
                "Max: " + df.format(max) + "\n" +
                "Range: " + df.format(range) + "\n" +
                "Mean: " + df.format(mean) + "\n"+
                "Median: " + df.format(median) + "\n"+
                "Standard deviation: " + df.format(sd);

    }

    /**
     * resetting values to default to .
     */
    private void reset(){
        this.sd=0;
        this.max=0;
        this.mean=0;
        this.median=0;
        this.range=0;
        this.min=0;
        this.n=0;
    }

    /**
     * create a list that only contains the provided neighbourhood.
     * @param values
     * @param str
     * @return if there is a match return matching value list, if not return original
     */
    public List<AssessmentValue> assessNeighbourhood(List<AssessmentValue> values,String str){
        List<AssessmentValue> neighbourhood = values.stream().filter(d -> d.getNeighbourhood().toLowerCase().contains(str)).collect(Collectors.toList());
        if(!neighbourhood.isEmpty()){return neighbourhood;}
        return values;
    }

    /**
     * create a list that only contains the provided address.
     * @param values
     * @param str
     * @return if there is a match return matching value list, if not return original
     */
    public List<AssessmentValue> assessAddress(List<AssessmentValue> values,String str){
        List<AssessmentValue> address = values.stream().filter(d -> d.getAddress().toLowerCase().contains(str)).collect(Collectors.toList());
        if(!address.isEmpty()){return address;}
        return values;
    }

    /**
     * create a list that only contains the provided account number.
     * @param values
     * @param str
     * @returnif there is a match return matching value list, if not return original
     */
    public List<AssessmentValue> assessAccountNum(List<AssessmentValue> values,String str){
        List<AssessmentValue> accountNum = values.stream().filter(d -> d.getAccountNumber().contains(str)).collect(Collectors.toList());
        if(!accountNum.isEmpty()){return accountNum;}
        return values;
    }

    /**
     * create a list that only contains the provided assessment class.
     * @param values
     * @param str
     * @returnif there is a match return matching value list, if not return original
     */
    public List<AssessmentValue> assessClass(List<AssessmentValue> values,String str){
        List<AssessmentValue> assessmentClass = values.stream().filter(d -> d.getAssessmentClass().toLowerCase().equals(str)).collect(Collectors.toList());
        if(!assessmentClass.isEmpty()){return assessmentClass;} // this is the last searching sequence. to this point if nothing is found return empty.
        return values;
    }
}
