import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

public class SearchProblem {
    String[] operators;
    Object[][] initialState;

    boolean reachedGoal(ArrayList<Ship> ships, Agent agent) {
        // You reach your goal when:
        boolean aShipIsNotWrecked = false;
        boolean aBlackBoxIsNotFullyDamaged = false;
        for (Ship ship : ships) {
            // 1.  there are no living passengers who are not rescued
            if (!ship.isWreck) {
                aShipIsNotWrecked = true;
            }
            // 2. there are no undamaged boxes which have not been retrieved
            else if (ship.blackBox.damage < 20) {
                aBlackBoxIsNotFullyDamaged = true;
            }
        }
        if (!aShipIsNotWrecked & !aBlackBoxIsNotFullyDamaged) {
            // 3. the rescue boat is not carrying any passengers
            if (agent.currAvailableSeats == agent.capacity) {
                return true;
            }
        }
        return false;
    }

    // Breadth-first Search
    Object[] bfs(Node root) { // root is initial state
        Queue<Node> q = new LinkedList<Node>();
        int expandedNodes = 0;
        q.add(root);
        ArrayList<Ship> ships = new ArrayList<Ship>();
        Agent agent = root.agent;
        int retrievedBlackBoxes = 0;
        for (int i = 0; i < root.state.length; i++) {
            for (int j = 0; j < root.state[0].length; j++) {
                if (root.state[i][j] != null) {
                    if (root.state[i][j] instanceof Ship)
                        ships.add((Ship) root.state[i][j]);
                }
            }
        }
        int unWreckedShips = ships.size(); // at the beginning, number of un-wrecked ships is equal to the size of all ships
        while (!q.isEmpty()) {
            Node curr = q.poll();
            int deaths = curr.deathsSoFar;
            expandedNodes++;
            Object[][] grid = curr.state;
            // check if curr is goal state
            if (reachedGoal(ships, agent))
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
                if (grid[agent.locX][agent.locY] != null) {
                    if (grid[agent.locX][agent.locY] instanceof Ship) {
                        Ship currShip = ((Ship) grid[agent.locX][agent.locY]).clone();
                        if (currShip.isWreck) {
                            if (!currShip.blackBox.isPickedUp) {
                                if (currShip.blackBox.damage < 20) { // here the agent can retrieve the black box of the wreck
                                    currShip.blackBox.isPickedUp = true;
                                    retrievedBlackBoxes++;
                                    grid[agent.locX][agent.locY] = currShip;
                                    q.add(new Node(grid, agent, curr, curr.depth+1, 0, curr.operator +
                                            ",retrieve", retrievedBlackBoxes, unWreckedShips > 0 ? curr.deathsSoFar + unWreckedShips: curr.deathsSoFar));
                                }
                            }
                        }
                        else { // ship is not a wreck
                            if (currShip.numOfPassengers > 0) {
                                if (agent.currAvailableSeats != 0) {
                                    // pick up passengers
                                    if (agent.currAvailableSeats < currShip.numOfPassengers) { // agent picks SOME of the passengers on the ship
                                        currShip.numOfPassengers -= agent.currAvailableSeats;
                                        // ((Ship)grid[agent.locX][agent.locY]).numOfPassengers -= agent.currAvailableSeats;
                                        Agent currAgent = agent.clone();
                                        currAgent.currAvailableSeats = 0;
                                        grid[agent.locX][agent.locY] = currShip;
                                        q.add(new Node(grid, currAgent, curr, curr.depth+1, 0, curr.operator +
                                                ",pickup", retrievedBlackBoxes, unWreckedShips > 0 ? curr.deathsSoFar + unWreckedShips: curr.deathsSoFar));
                                    }
                                    else { // agent picks up ALL passenger on the ship, and it becomes a wreck
                                        agent.currAvailableSeats -= currShip.numOfPassengers;
                                        currShip.numOfPassengers = 0;
                                        currShip.isWreck = true;
                                        unWreckedShips--;
                                        grid[agent.locX][agent.locY] = currShip;
                                        q.add(new Node(grid, agent, curr, curr.depth+1, 0, curr.operator +
                                                ",pickup", retrievedBlackBoxes, unWreckedShips > 0 ? curr.deathsSoFar + unWreckedShips: curr.deathsSoFar));
                                    }
                                }
                            }
                        }
                    }
                    else if (grid[agent.locX][agent.locY] instanceof Station) {
                        Station currStation = (Station) grid[agent.locX][agent.locY];
                        if (agent.currAvailableSeats != agent.capacity) { // boat is not empty
                            // drop off all passengers at station
                            Agent currAgent = agent.clone();
                            currAgent.currAvailableSeats = agent.capacity;
                            q.add(new Node(grid, currAgent, curr, curr.depth+1, 0, curr.operator +
                                    ",drop", retrievedBlackBoxes, unWreckedShips > 0 ? curr.deathsSoFar + unWreckedShips: curr.deathsSoFar));
                        }
                    }
                }
                // enqueue nodes that contain "up" | "down" | "left" | "right" actions
                String[] directions = (leaveCell(agent, grid.length, grid[0].length)).split(";");
                for (int i = 0; i < directions.length; i++) {
                    Agent currAgent = agent.clone();
                    if (directions[i].equals("right")) {
                        currAgent.locY += 1;
                        q.add(new Node(grid, currAgent, curr, curr.depth+1, 0, curr.operator +
                                ",right", retrievedBlackBoxes, unWreckedShips > 0 ? curr.deathsSoFar + unWreckedShips: curr.deathsSoFar));
                    }
                    else if (directions[i].equals("left")) {
                        currAgent.locY -= 1;
                        q.add(new Node(grid, currAgent, curr, curr.depth+1, 0, curr.operator +
                                ",left", retrievedBlackBoxes, unWreckedShips > 0 ? curr.deathsSoFar + unWreckedShips: curr.deathsSoFar));
                    }
                    else if (directions[i].equals("up")) {
                        currAgent.locX -= 1;
                        q.add(new Node(grid, currAgent, curr, curr.depth+1, 0, curr.operator +
                                ",up", retrievedBlackBoxes, unWreckedShips > 0 ? curr.deathsSoFar + unWreckedShips: curr.deathsSoFar));
                    }
                    else if (directions[i].equals("down")) {
                        currAgent.locX -= 1;
                        q.add(new Node(grid, currAgent, curr, curr.depth+1, 0, curr.operator +
                                ",down", retrievedBlackBoxes, unWreckedShips > 0 ? curr.deathsSoFar + unWreckedShips: curr.deathsSoFar));
                    }
                }
            }

            // update all numbers of passengers ships on the grid according to the game rule 1 death on each ship per 1 time step (action)
            // and the blackbox damage
            unWreckedShips = 0;
            for (int i = 0; i < curr.state.length; i++) {
                for (int j = 0; j < curr.state[0].length; j++) {
                    if (curr.state[i][j] != null) {
                        if (curr.state[i][j] instanceof Ship) {
                            if (((Ship) curr.state[i][j]).isWreck) // if it's a wreck, increase damage of blackbox
                                ((Ship) curr.state[i][j]).blackBox.damage++;
                            else { // if it's not, a passenger expires
                                ((Ship) curr.state[i][j]).numOfPassengers--;
                                unWreckedShips++;
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    // Depth-first Search
    Object[] dfs(Node root) { // root is initial state
        Stack<Node> s = new Stack<Node>();
        int expandedNodes = 0;
        s.push(root);
        ArrayList<Ship> ships = new ArrayList<Ship>();
        Agent agent = root.agent;
        int retrievedBlackBoxes = 0;
        for (int i = 0; i < root.state.length; i++) {
            for (int j = 0; j < root.state[0].length; j++) {
                if (root.state[i][j] != null) {
                    if (root.state[i][j] instanceof Ship)
                        ships.add((Ship) root.state[i][j]);
                }
            }
        }
        int unWreckedShips = ships.size(); // at the beginning, number of un-wrecked ships is equal to the size of all ships
        while (!s.isEmpty()) {
            Node curr = s.pop();
            int deaths = curr.deathsSoFar;
            expandedNodes++;
            Object[][] grid = curr.state;
            // check if curr is goal state
            if (reachedGoal(ships, agent))
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
                if (grid[agent.locX][agent.locY] != null) {
                    if (grid[agent.locX][agent.locY] instanceof Ship) {
                        Ship currShip = ((Ship) grid[agent.locX][agent.locY]).clone();
                        if (currShip.isWreck) {
                            if (!currShip.blackBox.isPickedUp) {
                                if (currShip.blackBox.damage < 20) { // here the agent can retrieve the black box of the wreck
                                    currShip.blackBox.isPickedUp = true;
                                    retrievedBlackBoxes++;
                                    grid[agent.locX][agent.locY] = currShip;
                                    s.push(new Node(grid, agent, curr, curr.depth+1, 0, curr.operator +
                                            ",retrieve", retrievedBlackBoxes, unWreckedShips > 0 ? curr.deathsSoFar + unWreckedShips: curr.deathsSoFar));
                                }
                            }
                        }
                        else { // ship is not a wreck
                            if (currShip.numOfPassengers > 0) {
                                if (agent.currAvailableSeats != 0) {
                                    // pick up passengers
                                    if (agent.currAvailableSeats < currShip.numOfPassengers) { // agent picks SOME of the passengers on the ship
                                        currShip.numOfPassengers -= agent.currAvailableSeats;
                                        // ((Ship)grid[agent.locX][agent.locY]).numOfPassengers -= agent.currAvailableSeats;
                                        Agent currAgent = agent.clone();
                                        currAgent.currAvailableSeats = 0;
                                        grid[agent.locX][agent.locY] = currShip;
                                        s.push(new Node(grid, currAgent, curr, curr.depth+1, 0, curr.operator +
                                                ",pickup", retrievedBlackBoxes, unWreckedShips > 0 ? curr.deathsSoFar + unWreckedShips: curr.deathsSoFar));
                                    }
                                    else { // agent picks up ALL passenger on the ship, and it becomes a wreck
                                        agent.currAvailableSeats -= currShip.numOfPassengers;
                                        currShip.numOfPassengers = 0;
                                        currShip.isWreck = true;
                                        unWreckedShips--;
                                        grid[agent.locX][agent.locY] = currShip;
                                        s.push(new Node(grid, agent, curr, curr.depth+1, 0, curr.operator +
                                                ",pickup", retrievedBlackBoxes, unWreckedShips > 0 ? curr.deathsSoFar + unWreckedShips: curr.deathsSoFar));
                                    }
                                }
                            }
                        }
                    }
                    else if (grid[agent.locX][agent.locY] instanceof Station) {
                        Station currStation = (Station) grid[agent.locX][agent.locY];
                        if (agent.currAvailableSeats != agent.capacity) { // boat is not empty
                            // drop off all passengers at station
                            Agent currAgent = agent.clone();
                            currAgent.currAvailableSeats = agent.capacity;
                            s.push(new Node(grid, currAgent, curr, curr.depth+1, 0, curr.operator +
                                    ",drop", retrievedBlackBoxes, unWreckedShips > 0 ? curr.deathsSoFar + unWreckedShips: curr.deathsSoFar));
                        }
                    }
                }
                // enqueue nodes that contain "up" | "down" | "left" | "right" actions
                String[] directions = (leaveCell(agent, grid.length, grid[0].length)).split(";");
                for (int i = 0; i < directions.length; i++) {
                    Agent currAgent = agent.clone();
                    if (directions[i].equals("right")) {
                        currAgent.locY += 1;
                        s.push(new Node(grid, currAgent, curr, curr.depth+1, 0, curr.operator +
                                ",right", retrievedBlackBoxes, unWreckedShips > 0 ? curr.deathsSoFar + unWreckedShips: curr.deathsSoFar));
                    }
                    else if (directions[i].equals("left")) {
                        currAgent.locY -= 1;
                        s.push(new Node(grid, currAgent, curr, curr.depth+1, 0, curr.operator +
                                ",left", retrievedBlackBoxes, unWreckedShips > 0 ? curr.deathsSoFar + unWreckedShips: curr.deathsSoFar));
                    }
                    else if (directions[i].equals("up")) {
                        currAgent.locX -= 1;
                        s.push(new Node(grid, currAgent, curr, curr.depth+1, 0, curr.operator +
                                ",up", retrievedBlackBoxes, unWreckedShips > 0 ? curr.deathsSoFar + unWreckedShips: curr.deathsSoFar));
                    }
                    else if (directions[i].equals("down")) {
                        currAgent.locX -= 1;
                        s.push(new Node(grid, currAgent, curr, curr.depth+1, 0, curr.operator +
                                ",down", retrievedBlackBoxes, unWreckedShips > 0 ? curr.deathsSoFar + unWreckedShips: curr.deathsSoFar));
                    }
                }
            }

            // update all numbers of passengers ships on the grid according to the game rule 1 death on each ship per 1 time step (action)
            // and the blackbox damage
            unWreckedShips = 0;
            for (int i = 0; i < curr.state.length; i++) {
                for (int j = 0; j < curr.state[0].length; j++) {
                    if (curr.state[i][j] != null) {
                        if (curr.state[i][j] instanceof Ship) {
                            if (((Ship) curr.state[i][j]).isWreck) // if it's a wreck, increase damage of blackbox
                                ((Ship) curr.state[i][j]).blackBox.damage++;
                            else { // if it's not, a passenger expires
                                ((Ship) curr.state[i][j]).numOfPassengers--;
                                unWreckedShips++;
                            }
                        }
                    }
                }
            }
        }
        return null;
    }


    String leaveCell(Agent agent, int rows, int columns){
        String availableActions = "";
        if (agent.locY == 0) { // left of the grid
            if (agent.locX == 0) // left upper corner -> can't go left or up
                return "right;down";
            else if (agent.locX == rows - 1) // left lower corner -> can't go left or down
                return "right;up";
            else // agent can't go left only
                return "right;down;up";
        }
        if (agent.locY == columns - 1) { // right of the grid
            if (agent.locX == 0) // right upper corner -> can't go right or up
                return "left;down";
            else if (agent.locX == rows - 1) // right lower corner -> can't go right or down
                return "left;up";
            else // agent can't go right only
                return "left;down;up";
        }
        if (agent.locX == 0) // can't go up
            return "left;right;down";
        if (agent.locX == rows - 1) // can't go down
            return "left;right;up";
        return "left;right;down;up";
    }
}
