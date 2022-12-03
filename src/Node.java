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
    int deathsSoFar; // part of state
    Node parent;
    String operator; // up, down, left, right, pickup, retrieve, drop
    int depth;
    int pathCost;
    Agent agent;
    int rows;
    int cols;
    //int heuristic;
    public Node(HashMap<String, Object> OccupiedCells, Agent agent, Node parent, int depth, int pathCost, String operator, int retrievedBoxes, int deathsSoFar, int rows, int cols){
        // State is on the form <agent X location, agent Y location>
        this.occupiedCells = OccupiedCells;
        this.agent = agent;
        this.parent = parent;
        this.depth = depth;
        this.pathCost = pathCost;
        this.operator = operator;
        this.retrievedBoxes = retrievedBoxes;
        this.deathsSoFar = deathsSoFar;
        this.rows=rows;
        this.cols=cols;
    }
    public Node(HashMap<String, Object> OccupiedCells, Agent agent, Node parent, int depth, int pathCost, String operator, int retrievedBoxes, int deathsSoFar){
        // State is on the form <agent X location, agent Y location>
        this.occupiedCells = OccupiedCells;
        this.agent = agent;
        this.parent = parent;
        this.depth = depth;
        this.pathCost = pathCost;
        this.operator = operator;
        this.retrievedBoxes = retrievedBoxes;
        this.deathsSoFar = deathsSoFar;
    }

}
