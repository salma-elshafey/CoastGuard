import java.util.*;
import java.util.HashMap;

public class SearchProblem {
    String[] operators;
    Object[][] initialState;

    boolean reachedGoal(HashMap<String, Object> occupiedCells, Agent agent) {
        // You reach your goal when:
        boolean aShipIsNotWrecked = false;
        boolean aBlackBoxIsNotFullyDamaged = false;
        // System.out.println(occupiedCells.toString() + " Boat: " + agent.locX + "," + agent.locY);
        for (Object cell : occupiedCells.values()) {
            if(cell instanceof Ship) {
                // 1.  there are no living passengers who are not rescued
                if (!(((Ship) cell).isWreck)) {
                    aShipIsNotWrecked = true;
                }
                // 2. there are no undamaged boxes which have not been retrieved
                else if (((Ship) cell).getBlackBox().getDamage() < 20 && !(((Ship) cell).getBlackBox().isPickedUp())) {
                    aBlackBoxIsNotFullyDamaged = true;
                }
            }
        }
        if (!aShipIsNotWrecked && !aBlackBoxIsNotFullyDamaged) {
            // 3. the rescue boat is not carrying any passengers
            if (agent.currAvailableSeats == agent.capacity) {
                System.out.println(occupiedCells.toString());
                return true;
            }
        }
        return false;
    }

    // Breadth-first Search
    Object[] bfs(Node root) { // root is initial state
        Queue<Node> q = new LinkedList<Node>();
        int rows = root.rows;
        int cols= root.cols;
        int expandedNodes = 0;
        q.add(root);
        int retrievedBlackBoxes = 0;
        int unWreckedShips = 0; // at the beginning, number of un-wrecked ships will be equal to the size of all ships
        int depth = root.depth; // 0
        HashMap<String, Object> occupiedCells = root.occupiedCells;
        while (!q.isEmpty()) {
            Node curr = q.poll();
            Agent agent = curr.agent;
            if (curr.depth == depth + 1){
                depth++;
                occupiedCells = curr.occupiedCells;
                unWreckedShips = 0;
                for (String key : occupiedCells.keySet()) {
                    if (occupiedCells.get(key) instanceof Ship) {
                        if (((Ship) occupiedCells.get(key)).isWreck) // if it's a wreck, increase damage of blackbox
                            ((Ship) occupiedCells.get(key)).getBlackBox().setDamage(((Ship) occupiedCells.get(key)).getBlackBox().getDamage()+1);
                        else { // if it's not, a passenger expires
                            ((Ship) occupiedCells.get(key)).setNumOfPassengers(((Ship) occupiedCells.get(key)).numOfPassengers - 1);
                            unWreckedShips++;
                        }
                    }
                }
            }
            System.out.println("Depth: " + curr.depth);
            expandedNodes++;
            // check if curr is goal state
            if (reachedGoal(curr.occupiedCells, agent))
                return new Object[]{curr, expandedNodes}; // <(Node) goalNode, (Integer) numbOfExpandedNodes>

            // first check if the cell that the agent is in contains a ship ~
                // check if it's a wreck, if it is a wreck ~
                // check if there is an undamaged black box -> pick up if yes ~
                // else if it's not a wreck & has passengers, check if the agent has available seats ~
                // if the agent has available seats -> pick up ~
                // if the agent does not have available seats -> leave the cell ~
                // if it doesn't contain a ship -> if it contains a station ~
                // if the agent has passengers, drop off ~
                // if it doesn't have passengers -> leave the cell ~
                // if the cell contains nothing, leave the cell ~
            else {
                // note: make sure of redundant states
                String location = agent.locX + "," + agent.locY;
                if (curr.occupiedCells.get(location) != null) {
                    System.out.println(curr.occupiedCells.get(location) + ", Boat: " + location + ", Boat Available Seats: " + agent.currAvailableSeats);
                    if (curr.occupiedCells.get(location) instanceof Ship) {
                        System.out.println("agent is in the cell of the ship");
                        Ship currShip = ((Ship) curr.occupiedCells.get(location)).clone();
                        if (currShip.isWreck) {
                            System.out.println("SHIP IS A WRECK");
                            if (!(currShip.getBlackBox().isPickedUp()) && currShip.getBlackBox().getDamage() < 20) { // here the agent can retrieve the black box of the wreck
                                System.out.println(currShip);
                                System.out.println("AND I'LL RETRIEVE THE BLACK BOX");
                                currShip.pickUpBlackBox();
                                System.out.println(currShip);
                                HashMap<String, Object> occupiedCellsClone = (HashMap<String, Object>) occupiedCells.clone();
                                occupiedCellsClone.put(location, currShip);
                                System.out.println("retrieve blackbox");
                                q.add(new Node(occupiedCellsClone, agent, curr, curr.depth + 1, "", curr.operator +
                                        ",retrieve", retrievedBlackBoxes + 1, unWreckedShips > 0 ? curr.deathsSoFar + unWreckedShips : curr.deathsSoFar));
                            }
                        }
                        else { // ship is not a wreck
                            if (currShip.numOfPassengers > 0) {
                                if (agent.currAvailableSeats != 0) {
                                    // pick up passengers
                                    System.out.println("pickup");
                                    if (agent.currAvailableSeats <= currShip.numOfPassengers) { // agent picks SOME of the passengers on the ship
                                        currShip.setNumOfPassengers(currShip.numOfPassengers - agent.currAvailableSeats);
                                        Agent currAgent = agent.clone();
                                        currAgent.currAvailableSeats = 0;
                                        HashMap<String, Object> OccupiedCellsClone = (HashMap<String, Object>) curr.occupiedCells.clone();
                                        OccupiedCellsClone.put(location, currShip);
                                        q.add(new Node(OccupiedCellsClone, currAgent, curr, curr.depth + 1, "", curr.operator +
                                                ",pickup", retrievedBlackBoxes, unWreckedShips > 0 ? curr.deathsSoFar + unWreckedShips : curr.deathsSoFar));
                                    }
                                    else { // agent picks up ALL passenger on the ship, and it becomes a wreck
                                        Agent currAgent = agent.clone();
                                        currAgent.currAvailableSeats -= currShip.numOfPassengers;
                                        currShip.setNumOfPassengers(0);
                                        unWreckedShips--;
                                        HashMap<String, Object> occupiedCellsClone = (HashMap<String, Object>) occupiedCells.clone();
                                        occupiedCellsClone.put(location, currShip);
                                        q.add(new Node(occupiedCellsClone, currAgent, curr, curr.depth + 1, "", curr.operator +
                                                ",pickup", retrievedBlackBoxes, unWreckedShips > 0 ? curr.deathsSoFar + unWreckedShips : curr.deathsSoFar));
                                    }
                                }
                            }
                        }
                    } else if (curr.occupiedCells.get(location).equals("Station")) {
                        System.out.println("BOAT IS IN STATION");
                        if (agent.currAvailableSeats != agent.capacity) { // boat is not empty
                            System.out.println("drop-off");
                            // drop off all passengers at station
                            Agent currAgent = agent.clone();
                            currAgent.currAvailableSeats = agent.capacity;
                            q.add(new Node(occupiedCells, currAgent, curr, curr.depth + 1, "", curr.operator +
                                    ",drop", retrievedBlackBoxes, unWreckedShips > 0 ? curr.deathsSoFar + unWreckedShips : curr.deathsSoFar));
                        }
                    }
                }
                // enqueue nodes that contain "up" | "down" | "left" | "right" actions
                String latestAction = "";
                if (curr.parent != null) {
                    String[] s = curr.operator.split(",");
                    latestAction = s[s.length-1];
                    System.out.println("Latest action: " + latestAction);
                }
                // String[] directions = (leaveCell(agent, rows, cols, latestAction)).split(";");
                boolean[] directions = leaveCell(agent, rows, cols, latestAction);
                // direction: 0: up, 1: down, 2: left, 3: right
                // System.out.println("Up: " + directions[0] + ", Down: " + directions[1] + ", Left: " + directions[2] + ", Right: " + directions[3]);
                Agent currAgent;
                if (directions[0]) { // up
                    currAgent = agent.clone();
                    currAgent.locX -= 1;
                    q.add(new Node(occupiedCells, currAgent, curr, curr.depth + 1, "", curr.operator +
                            ",up", retrievedBlackBoxes, unWreckedShips > 0 ? curr.deathsSoFar + unWreckedShips : curr.deathsSoFar));
                }
                if (directions[1]) { // down
                    currAgent = agent.clone();
                    currAgent.locX += 1;
                    q.add(new Node(occupiedCells, currAgent, curr, curr.depth + 1, "", curr.operator +
                            ",down", retrievedBlackBoxes, unWreckedShips > 0 ? curr.deathsSoFar + unWreckedShips : curr.deathsSoFar));
                }
                if (directions[2]) { // left
                    currAgent = agent.clone();
                    currAgent.locY -= 1;
                    q.add(new Node(occupiedCells, currAgent, curr, curr.depth + 1, "", curr.operator +
                            ",left", retrievedBlackBoxes, unWreckedShips > 0 ? curr.deathsSoFar + unWreckedShips : curr.deathsSoFar));
                }
                if (directions[3]) { // right
                    currAgent = agent.clone();
                    currAgent.locY += 1;
                    q.add(new Node(occupiedCells, currAgent, curr, curr.depth + 1, "", curr.operator +
                            ",right", retrievedBlackBoxes, unWreckedShips > 0 ? curr.deathsSoFar + unWreckedShips : curr.deathsSoFar));
                }
            }

//            System.out.print("-que start:   ");
//            for (int i = 0; i < q.size(); i++) {
//                System.out.print("Agent:");
//                System.out.print(((LinkedList<Node>) q).get(i).agent.locX);
//                System.out.print(",");
//                System.out.print(((LinkedList<Node>) q).get(i).agent.locY);
//                System.out.print("  seats");
//                System.out.print(((LinkedList<Node>) q).get(i).agent.currAvailableSeats);
//                System.out.print(" path ");
//                System.out.print(((LinkedList<Node>) q).get(i).operator);
//                System.out.print("|  ");
//           }
//            System.out.print("-que end:   ");
//            System.out.println("---------------------------------------------");
        }

        return null;
    }

    // Depth-first Search
//    Object[] dfs(Node root) { // root is initial state
//        Stack<Node> s = new Stack<Node>();
//        int expandedNodes = 0;
//        s.push(root);
//        ArrayList<Ship> ships = new ArrayList<Ship>();
//        Agent agent = root.agent;
//        int retrievedBlackBoxes = 0;
//        for (int i = 0; i < root.state.length; i++) {
//            for (int j = 0; j < root.state[0].length; j++) {
//                if (root.state[i][j] != null) {
//                    if (root.state[i][j] instanceof Ship)
//                        ships.add((Ship) root.state[i][j]);
//                }
//            }
//        }
//        int unWreckedShips = ships.size(); // at the beginning, number of un-wrecked ships is equal to the size of all ships
//        while (!s.isEmpty()) {
//            Node curr = s.pop();
//            int deaths = curr.deathsSoFar;
//            expandedNodes++;
//            Object[][] grid = curr.state;
//            // check if curr is goal state
//            if (reachedGoal(ships, agent))
//                return new Object[]{curr, expandedNodes}; // <(Node) goalNode, (Integer) numbOfExpandedNodes>
//
//                // first check if the cell that the agent is in contains a ship ~
//                // check if it's a wreck, if it is a wreck ~
//                // check if there is an undamaged black box -> pick up if yes ~
//                // else if it's not a wreck & has passengers, check if the agent has available seats ~
//                // if the agent has available seats -> pick up ~
//                // if the agent does not have available seats -> leave the cell ~
//                // if it doesn't contain a ship -> if it contains a station ~
//                // if the agent has passengers, drop off ~
//                // if it doesn't have passengers -> leave the cell ~
//                // if the cell contains nothing, leave the cell ~
//            else {
//                // note: make sure of redundant states
//                if (grid[agent.locX][agent.locY] != null) {
//                    if (grid[agent.locX][agent.locY] instanceof Ship) {
//                        Ship currShip = ((Ship) grid[agent.locX][agent.locY]).clone();
//                        if (currShip.isWreck) {
//                            if (!currShip.blackBox.isPickedUp) {
//                                if (currShip.blackBox.damage < 20) { // here the agent can retrieve the black box of the wreck
//                                    currShip.blackBox.isPickedUp = true;
//                                    retrievedBlackBoxes++;
//                                    grid[agent.locX][agent.locY] = currShip;
//                                    s.push(new Node(grid, agent, curr, curr.depth+1, 0, curr.operator +
//                                            ",retrieve", retrievedBlackBoxes, unWreckedShips > 0 ? curr.deathsSoFar + unWreckedShips: curr.deathsSoFar));
//                                }
//                            }
//                        }
//                        else { // ship is not a wreck
//                            if (currShip.numOfPassengers > 0) {
//                                if (agent.currAvailableSeats != 0) {
//                                    // pick up passengers
//                                    if (agent.currAvailableSeats < currShip.numOfPassengers) { // agent picks SOME of the passengers on the ship
//                                        currShip.numOfPassengers -= agent.currAvailableSeats;
//                                        // ((Ship)grid[agent.locX][agent.locY]).numOfPassengers -= agent.currAvailableSeats;
//                                        Agent currAgent = agent.clone();
//                                        currAgent.currAvailableSeats = 0;
//                                        grid[agent.locX][agent.locY] = currShip;
//                                        s.push(new Node(grid, currAgent, curr, curr.depth+1, 0, curr.operator +
//                                                ",pickup", retrievedBlackBoxes, unWreckedShips > 0 ? curr.deathsSoFar + unWreckedShips: curr.deathsSoFar));
//                                    }
//                                    else { // agent picks up ALL passenger on the ship, and it becomes a wreck
//                                        agent.currAvailableSeats -= currShip.numOfPassengers;
//                                        currShip.numOfPassengers = 0;
//                                        currShip.isWreck = true;
//                                        unWreckedShips--;
//                                        grid[agent.locX][agent.locY] = currShip;
//                                        s.push(new Node(grid, agent, curr, curr.depth+1, 0, curr.operator +
//                                                ",pickup", retrievedBlackBoxes, unWreckedShips > 0 ? curr.deathsSoFar + unWreckedShips: curr.deathsSoFar));
//                                    }
//                                }
//                            }
//                        }
//                    }
//                    else if (grid[agent.locX][agent.locY] instanceof Station) {
//                        Station currStation = (Station) grid[agent.locX][agent.locY];
//                        if (agent.currAvailableSeats != agent.capacity) { // boat is not empty
//                            // drop off all passengers at station
//                            Agent currAgent = agent.clone();
//                            currAgent.currAvailableSeats = agent.capacity;
//                            s.push(new Node(grid, currAgent, curr, curr.depth+1, 0, curr.operator +
//                                    ",drop", retrievedBlackBoxes, unWreckedShips > 0 ? curr.deathsSoFar + unWreckedShips: curr.deathsSoFar));
//                        }
//                    }
//                }
//                // enqueue nodes that contain "up" | "down" | "left" | "right" actions
//                String[] directions = (leaveCell(agent, grid.length, grid[0].length)).split(";");
//                for (int i = 0; i < directions.length; i++) {
//                    Agent currAgent = agent.clone();
//                    if (directions[i].equals("right")) {
//                        currAgent.locY += 1;
//                        s.push(new Node(grid, currAgent, curr, curr.depth+1, 0, curr.operator +
//                                ",right", retrievedBlackBoxes, unWreckedShips > 0 ? curr.deathsSoFar + unWreckedShips: curr.deathsSoFar));
//                    }
//                    else if (directions[i].equals("left")) {
//                        currAgent.locY -= 1;
//                        s.push(new Node(grid, currAgent, curr, curr.depth+1, 0, curr.operator +
//                                ",left", retrievedBlackBoxes, unWreckedShips > 0 ? curr.deathsSoFar + unWreckedShips: curr.deathsSoFar));
//                    }
//                    else if (directions[i].equals("up")) {
//                        currAgent.locX -= 1;
//                        s.push(new Node(grid, currAgent, curr, curr.depth+1, 0, curr.operator +
//                                ",up", retrievedBlackBoxes, unWreckedShips > 0 ? curr.deathsSoFar + unWreckedShips: curr.deathsSoFar));
//                    }
//                    else if (directions[i].equals("down")) {
//                        currAgent.locX -= 1;
//                        s.push(new Node(grid, currAgent, curr, curr.depth+1, 0, curr.operator +
//                                ",down", retrievedBlackBoxes, unWreckedShips > 0 ? curr.deathsSoFar + unWreckedShips: curr.deathsSoFar));
//                    }
//                }
//            }
//
//            // update all numbers of passengers ships on the grid according to the game rule 1 death on each ship per 1 time step (action)
//            // and the blackbox damage
//            unWreckedShips = 0;
//            for (int i = 0; i < curr.state.length; i++) {
//                for (int j = 0; j < curr.state[0].length; j++) {
//                    if (curr.state[i][j] != null) {
//                        if (curr.state[i][j] instanceof Ship) {
//                            if (((Ship) curr.state[i][j]).isWreck) // if it's a wreck, increase damage of blackbox
//                                ((Ship) curr.state[i][j]).blackBox.damage++;
//                            else { // if it's not, a passenger expires
//                                ((Ship) curr.state[i][j]).numOfPassengers--;
//                                unWreckedShips++;
//                            }
//                        }
//                    }
//                }
//            }
//        }
//        return null;
//    }


    boolean[] leaveCell(Agent agent, int rows, int columns, String latestAction){
        boolean[] directions = new boolean[4]; // up, down, left, right
        if (agent.locY == 0) { // left of the grid
            if (agent.locX == 0) // left upper corner -> can't go left or up
            {
                directions[1] = true; // down
                directions[3] = true; // right
            }
            else if (agent.locX == rows - 1) // left lower corner -> can't go left or down
            {
                directions[0] = true; // up
                directions[3] = true; // right
            }
            else // agent can't go left only
            {
                directions[0] = true; // up
                directions[1] = true; // down
                directions[3] = true; // right
            }
        }
        else if (agent.locY == columns - 1) { // right of the grid
            if (agent.locX == 0) // right upper corner -> can't go right or up
            {
                directions[2] = true; // left
                directions[1] = true; // down
            }
            else if (agent.locX == rows - 1) // right lower corner -> can't go right or down
            {
                directions[0] = true; // up
                directions[2] = true; // left
            }
            else // agent can't go right only
            {
                directions[0] = true; // up
                directions[1] = true; // down
                directions[2] = true; // left
            }
        }
        else if (agent.locX == 0) // can't go up
        {
            directions[1] = true; // down
            directions[2] = true; // left
            directions[3] = true; // right
        }
        else if (agent.locX == rows - 1) // can't go down
        {
            directions[0] = true; // up
            directions[2] = true; // left
            directions[3] = true; // right
        }
        else
        {
            directions[0] = true; // up
            directions[1] = true; // down
            directions[2] = true; // left
            directions[3] = true; // right
        }
        // if the previous actions was in the opposite direction, don't go
        if (latestAction.equals("down"))
            directions[0] = false; // up
        else if (latestAction.equals("up"))
            directions[1] = false; // down
        else if (latestAction.equals("right"))
            directions[2] = false; // left
        else if (latestAction.equals("left"))
            directions[3] = false; // right
        return directions;
    }
}
