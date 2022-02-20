/*
 * Name: Dongseok Cho
 * ID: 3021090
 * Date: 2020/11/8
 * Assignment: Milestone 2
 * Course: CMPT 305 AS01
 * Instructor: Indratmo Indratmo
 * Description: class containing the assessment class.
 */
package milestone3;

public class AssessmentClass {
    public String assessmentClass;

    /**
     * if nothing is given assign space
     */
    public AssessmentClass(){assessmentClass="";}

    /**
     * assign with given value
     * @param assessmentClass
     */
    public AssessmentClass(String assessmentClass){
        this.assessmentClass=assessmentClass;
    }

    public String toString(){return assessmentClass;}
}
