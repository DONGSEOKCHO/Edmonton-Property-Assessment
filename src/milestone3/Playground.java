/*
 * Name: Dongseok Cho,Zheyuan Xu
 * ID: 3021090
 * Date: 2020/11/8
 * Assignment: Milestone 3
 * Course: CMPT 305 AS01
 * Instructor: Indratmo Indratmo
 * Description: holds tha number of playgrounds in neighbourhood.
 */
package milestone3;

public class Playground {
    private String neighbourhood;
    private int playgroundNumber = 0;

    public Playground(){}

    /**
     * set the neighbourhood name and playground number
     * @param neighbourhood
     */
    public void setPlayground(String neighbourhood){
        this.neighbourhood = neighbourhood;
        incrementPlayground();
    }

    /**
     * increment the playground number.
     */
    public void incrementPlayground(){playgroundNumber++;}

    /**
     * gets the neighbourhood name
     * @return neighbourhood name
     */
    public String getNeighbourhood(){return neighbourhood;}

    /**
     * gets the number of playground.
     * @return playground number
     */
    public int getPlaygroundNumber(){return playgroundNumber;}
}
