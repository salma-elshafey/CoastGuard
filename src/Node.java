import java.util.HashMap;

public class Node {
    // Nodes are 5-tuples
    // 1. The state of the state space that this node corresponds to
    // 2. The parent node
    // 3. The operator applied to generate this node
    // 4. The depth of the node in the tree
    // 5. The path cost from the root
    //Object[][] state;
    HashMap<String, Object> occupiedCells;
    int retrievedBoxes; // part of state
    int deathsSoFar; // part of state // for all the grid
    Node parent;
    String operator; // up, down, left, right, pickup, retrieve, drop
    int depth;
    String pathCost;
    Agent agent;
    int rows;
    int cols;
    int heuristic1;
    int heuristic2;  // assigned inside distance_to_theNearestShip function
    public Node(HashMap<String, Object> OccupiedCells, Agent agent, Node parent, int depth, String pathCost, String operator, int retrievedBoxes, int deathsSoFar, int rows, int cols){
        // State is on the form <agent X location, agent Y location>
        this.occupiedCells = OccupiedCells;
        this.agent = agent;
        this.parent = parent;
        this.depth = depth;
        this.pathCost = deathsSoFar+","+numberOfLastBoxes();
        this.operator = operator;
        this.retrievedBoxes = retrievedBoxes;
        this.deathsSoFar = deathsSoFar;
        this.rows=rows;
        this.cols=cols;
        this.heuristic1 = distance_to_theNearestShip();
    }
    public Node(HashMap<String, Object> OccupiedCells, Agent agent, Node parent, int depth, String pathCost, String operator, int retrievedBoxes, int deathsSoFar){
        // State is on the form <agent X location, agent Y location>
        this.occupiedCells = OccupiedCells;
        this.agent = agent;
        this.parent = parent;
        this.depth = depth;
        this.pathCost = deathsSoFar+","+numberOfLastBoxes();
        this.operator = operator;
        this.retrievedBoxes = retrievedBoxes;
        this.deathsSoFar = deathsSoFar;
        this.heuristic1 = distance_to_theNearestShip();

    }
    public int numberOfLastBoxes() {
        int num = 0;

        for (Object cell : occupiedCells.values()) {
            if (cell instanceof Ship) {
                // 1.  there are no living passengers who are not rescued
                if ((((Ship) cell).blackBox).damage>=100) {
                    num++;
                }

            }
        }
        return num;
    }
    public int distance_to_theNearestShip() { // to calculate the distance from me to the nearest ship
        int smallest_distance = 0;
        int dis=0;
        int small=0;
        int lostPassengers_inTheFuture=0; // used for the second heuristic function

        for (Object cell : occupiedCells.values()) {
            if (cell instanceof Ship) {

                if (!(((Ship) cell).isWreck)) {
                    dis+= ((Ship) cell).locX-agent.locX + ((Ship) cell).locY-agent.locY;
                    dis = Math.abs(dis);

                    if(dis<((Ship) cell).numOfPassengers){  // as maybe the number of passenger remaining in the nearst ship is less that the step
                                                           // needed to go there
                        small=dis;
                    }
                    else {
                        small=((Ship) cell).numOfPassengers;
                    }

                    if(small<smallest_distance){
                        smallest_distance = small;
                    }
                    lostPassengers_inTheFuture++;


                }


            }
        }
        heuristic2 = lostPassengers_inTheFuture;
        return smallest_distance;
    }

}
