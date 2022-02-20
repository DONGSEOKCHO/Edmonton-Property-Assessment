/*
 * Name: Dongseok Cho
 * ID: 3021090
 * Date: 2020/11/8
 * Assignment: Milestone 2
 * Course: CMPT 305 AS01
 * Instructor: Indratmo Indratmo
 * Description: Class containing neighbourhood values.
 */
package milestone3;

public class Neighbourhood {
    public String neighbourhood;

    /**
     * neighbourhood name was not given so assign it with sapce
     */
    public Neighbourhood(){neighbourhood="";};

    /**
     * assign the given neighbourhood name
     * @param neighbourhood
     */
    public Neighbourhood(String neighbourhood){
        this.neighbourhood=neighbourhood;
    }

    /**
     *
     * @return string of neighbourhood
     */
    public String toString(){return neighbourhood;}
}
