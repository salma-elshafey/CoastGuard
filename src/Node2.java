import java.util.HashMap;

public class Node2 {
    HashMap<String, String> occupiedCells; // <Location, "Type (Station, Ship),numOfPassengers,wrecked(true/false),blackBoxDamage,blackBoxIsRetrievable(true/false)>"
    // OR <"Agent", "locX,locY,capacity,availableSeats">
    int retrievedBoxes; // part of state
    int deathsSoFar; // part of state
    Node2 parent;
    String operator; // up, down, left, right, pickup, retrieve, drop
    int depth;
    String pathCost;
    int rows;
    int cols;
    //int heuristic;
    public Node2(HashMap<String, String> occupiedCells, Node2 parent, int depth, String operator, int retrievedBoxes, int deathsSoFar,
                 int rows, int cols){
        // State is on the form <agent X location, agent Y location>
        this.occupiedCells = occupiedCells;
        this.parent = parent;
        this.depth = depth;
        this.pathCost = deathsSoFar + "," + numberOfLostBoxes();
        this.operator = operator;
        this.retrievedBoxes = retrievedBoxes;
        this.deathsSoFar = deathsSoFar;
        this.rows = rows;
        this.cols = cols;
    }
    public Node2(HashMap<String, String> occupiedCells, Node2 parent, int depth, String operator, int retrievedBoxes, int deathsSoFar){
        // State is on the form <agent X location, agent Y location>
        this.occupiedCells = occupiedCells;
        this.parent = parent;
        this.depth = depth;
        this.pathCost = deathsSoFar + "," + numberOfLostBoxes();
        this.operator = operator;
        this.retrievedBoxes = retrievedBoxes;
        this.deathsSoFar = deathsSoFar;
    }
    public HashMap<String, String> cloneOccupiedCells() {
        HashMap<String, String> output = new HashMap<String, String>();
        for (String key: this.occupiedCells.keySet()) {
            output.put(key, this.occupiedCells.get(key));
        }
        return output;
    }

    public int getPathCost() {
        return Integer.parseInt(this.pathCost.split(",")[0]) + Integer.parseInt(this.pathCost.split(",")[1]);
    }

    public int numberOfLostBoxes() {
        int num = 0;
        for (String key : occupiedCells.keySet()) {
            if (occupiedCells.get(key).split(",")[0].equals("Ship")) {
                int blackBoxDamage =  Integer.parseInt(occupiedCells.get(key).split(",")[3]);
                // 1.  there are no living passengers who are not rescued
                if (blackBoxDamage >= 20)
                    num++;
            }
        }
        return num;
    }

}
