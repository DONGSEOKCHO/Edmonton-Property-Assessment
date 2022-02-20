/*
 * Name: Dongseok Cho
 * ID: 3021090
 * Date: 2020/11/8
 * Assignment: Milestone 2
 * Course: CMPT 305 AS01
 * Instructor: Indratmo Indratmo
 * Description: class storing the location value.
 */
package milestone3;

public class Location {
    private double latitude,longitude;

    /**
     * assigning latitude and longitude
     * @param latitude
     * @param longitude
     */
    public void add(double latitude,double longitude){
        this.latitude=latitude;
        this.longitude=longitude;
    }

    /**
     * print out lattitude and longitude together
     * @return latitude longitude
     */
    public String toString(){
        return "("+latitude+", "+longitude+")";
    }

    /**
     * getting latitude
     * @return double latitude
     */
    public double getLatitude(){return latitude;}

    /**
     * gets longitude
     * @return double longitude
     */
    public double getLongitude(){return longitude;}
}
