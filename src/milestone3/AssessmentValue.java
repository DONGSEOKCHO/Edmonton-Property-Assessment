/*
 * Name: Dongseok Cho
 * ID: 3021090
 * Date: 2020/11/8
 * Assignment: Milestone 2
 * Course: CMPT 305 AS01
 * Instructor: Indratmo Indratmo
 * Description: class for storing the single assessment value.
 */
package milestone3;

public class AssessmentValue {
    public int accountNumber;
    public int assessedValue;
    public Neighbourhood neighbourhood;
    private Address address = new Address();
    private Location location = new Location();
    public AssessmentClass assessmentClass;

    public AssessmentValue() {
    }

    /**
     * assigning account number
     * @param accountNumber
     */
    public void setAccountNumber(int accountNumber){
        this.accountNumber = accountNumber;
    }

    /**
     * assigning assessed value
     * @param assessedValue
     */
    public void setAssessedValue(int assessedValue){
        this.assessedValue = assessedValue;
    }

    /**
     * assigning neighbourhood name
     * @param neighbourhood
     */
    public void setNeighbourhood(String neighbourhood){
        this.neighbourhood = new Neighbourhood(neighbourhood);
    }

    /**
     * assigning addresses
     * @param streetName
     * @param suit
     * @param houseNumber
     */
    public void setAddress(String streetName, String suit, String houseNumber){
        address.add(suit,houseNumber,streetName);
    }

    /**
     * assigning latitude and longitude
     * @param latitude
     * @param longitude
     */
    public void setLocation(double latitude,double longitude){
        location.add(latitude,longitude);
    }

    /**
     * assigning assessment class
     * @param assessmentClass
     */
    public void setAssessmentClass(String assessmentClass){
        this.assessmentClass= new AssessmentClass(assessmentClass);
    }

    /**
     * gets accountNumber
     * @return accont number string
     */
    public String getAccountNumber(){return Integer.toString(accountNumber);}

    /**
     * gets assessed value
     * @return assessedValue
     */
    public int getAssessedValue(){return assessedValue;}

    /**
     * gets assessment class
     * @return string of assessment class
     */
    public String getAssessmentClass(){return assessmentClass.toString();}

    /**
     * gets neighbourhood name
     * @return neighbourhood string
     */
    public String getNeighbourhood(){return neighbourhood.toString();}

    /**
     * gets longitude
     * @return longitude number
     */
    public double getLongitude() {return location.getLongitude();}

    /**
     * gets latitude
     * @return latitude number
     */
    public double getLatitude(){return location.getLatitude();}

    /**
     * gets addresses
     * @return string from of address.
     */
    public String getAddress() {return address.toString();}
}
