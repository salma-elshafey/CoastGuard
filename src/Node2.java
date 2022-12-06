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
    static int count = 0;

    int heuristic1;
    int heuristic2;  // assigned inside distance_to_theNearestShip function
    //int heuristic;
    public String toString(){
        return getHeuristic_cost1()+","+count;
    }
    public Node2(HashMap<String, String> occupiedCells, Node2 parent, int depth, String pathCost, String operator,
                 int retrievedBoxes, int deathsSoFar, int rows, int cols){
        // State is on the form <agent X location, agent Y location>
        this.occupiedCells = occupiedCells;
        this.parent = parent;
        this.depth = depth;
        this.pathCost = deathsSoFar+","+numberOfLostBoxes();;
        this.operator = operator;
        this.retrievedBoxes = retrievedBoxes;
        this.deathsSoFar = deathsSoFar;
        this.rows=rows;
        this.cols=cols;
        this.heuristic1 = distance_to_theNearestShip();
        count++;

    }
    public Node2(HashMap<String, String> occupiedCells, Node2 parent, int depth, String pathCost, String operator,
                int retrievedBoxes, int deathsSoFar){
        // State is on the form <agent X location, agent Y location>
        this.occupiedCells = occupiedCells;
        this.parent = parent;
        this.depth = depth;
        this.pathCost = deathsSoFar+","+numberOfLostBoxes();;
        this.operator = operator;
        this.retrievedBoxes = retrievedBoxes;
        this.deathsSoFar = deathsSoFar;
        this.heuristic1 = distance_to_theNearestShip();
        count++;

    }
    public HashMap<String, String> cloneOccupiedCells() {
        HashMap<String, String> output = new HashMap<String, String>();
        for (String key: this.occupiedCells.keySet()) {
            output.put(key, this.occupiedCells.get(key));
        }
        return output;
    }
    public int getHeuristic1() {
        return heuristic1;
    }

    public int getHeuristic2() {
        return heuristic2;
    }
    public int getHeuristic_cost1() {
        String[] cost =pathCost.split(",");
     //   cost[0] = String.valueOf(Integer.parseInt(cost[0])+heuristic1);
        return Integer.parseInt(cost[0])+heuristic1;
    }
    public int getHeuristic_cost1_part2() {
        String[] cost =pathCost.split(",");
      //  cost[0] = String.valueOf(Integer.parseInt(cost[0])+heuristic1);
        return Integer.parseInt(cost[0])+heuristic1;
    }
    public int getHeuristic_cost2() {
        String[] cost =pathCost.split(",");
       // cost[0] = String.valueOf(Integer.parseInt(cost[0])+heuristic2);
        return Integer.parseInt(cost[0])+heuristic2;

    }
    public int getHeuristic_cost2_part2() {
        String[] cost =pathCost.split(",");
       // cost[0] = String.valueOf(Integer.parseInt(cost[0])+heuristic2);
        return Integer.parseInt(cost[0])+heuristic2;

    }
    public int numberOfLostBoxes() {
        int num = 0;
        for (String key : occupiedCells.keySet()) {
            if (occupiedCells.get(key).split(",")[0].equals("Ship")) {
                int blackBoxDamage =  Integer.parseInt(occupiedCells.get(key).split(",")[3]);
                // 1.  there are no living passengers who are not rescued
                if (blackBoxDamage>=20) {
                    num++;
                }

            }
        }
        return num;
    }

    public int distance_to_theNearestShip() { // to calculate the distance from me to the nearest ship
        //  <"Agent", "locX,locY,capacity,availableSeats">
        String agent = occupiedCells.get("Agent");
        String [] agentArr = agent.split(",");
        int smallest_distance = rows*cols ;
        int dis=0; // expected to die
        int small=0;
        int lostPassengers_inTheFuture=0; // used for the second heuristic function

        for (String key : occupiedCells.keySet()) {
            if (occupiedCells.get(key).split(",")[0].equals("Ship")) {
                String isWreck =  occupiedCells.get(key).split(",")[2];
                int shipPassengers =  Integer.parseInt(occupiedCells.get(key).split(",")[1]);
                if ( isWreck.equals("false")) {
                    int shipX=Integer.parseInt(key.split(",")[0]);
                    int shipY=Integer.parseInt(key.split(",")[1]);
                    dis = shipX-Integer.parseInt(agentArr[0]) + shipY-Integer.parseInt(agentArr[1]);
                    dis = Math.abs(dis);

                    if(dis<shipPassengers){  // as maybe the number of passenger remaining in the nearst ship is less that the step
                        // needed to go there
                        small=dis;
                    }
                    else {
                        small=shipPassengers;
                    }

                    if(small<smallest_distance){
                        smallest_distance = small;
                    }
                    lostPassengers_inTheFuture++;


                }


            }
        }
        heuristic2 = lostPassengers_inTheFuture;
        return (smallest_distance==cols*rows)?0:smallest_distance;
    }



}
