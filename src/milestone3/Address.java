/*
 * Name: Dongseok Cho
 * ID: 3021090
 * Date: 2020/11/8
 * Assignment: Milestone 2
 * Course: CMPT 305 AS01
 * Instructor: Indratmo Indratmo
 * Description: class containing the address value.
 */
package milestone3;

public class Address {
    private String streetName,suite,houseNumber;

    /**
     * assign street name suite house number.
     * @param suite
     * @param houseNumber
     * @param streetName
     */
    public void add(String suite, String houseNumber, String streetName){
        this.suite=suite;
        this.streetName=streetName;
        this.houseNumber=houseNumber;
    }

    public String toString(){ return suite + " " + houseNumber + " " + streetName; }

}
