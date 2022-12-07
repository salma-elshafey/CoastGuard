import java.util.*;

public class SearchProblem2 {

    public boolean includesState(ArrayList<HashMap<String, String>> states, HashMap<String, String> currentState) {
        for (HashMap<String, String> state : states) {
            if (state.equals(currentState))
                return true;
        }
        return false;
    }
    // HashMap <String, String> -> <Location, "Type (Station, Ship),numOfPassengers,wrecked(true/false),blackBoxDamage,blackBoxIsRetrieved(true/false)"
    Object[] bfs (Node2 root) {
        Node2 goal;
        Queue<Node2> q = new LinkedList<Node2>();
        ArrayList<HashMap<String, String>> states = new ArrayList<HashMap<String, String>>();
        int rows = root.rows;
        int cols= root.cols;
        int expandedNodes = 0;
        // String[] agent = root.occupiedCells.get("Agent").split(","); // "locX,locY,capacity,availableSeats"
        q.add(root);
        int depth = root.depth; // 0
        HashMap<String, String> occupiedCells = root.occupiedCells;
        states.add(root.occupiedCells);
        while (!q.isEmpty()) {
            int unWreckedShips = 0;
            Node2 curr = q.poll();
            int retrievedBlackBoxes = curr.retrievedBoxes;
            String currAgent = curr.occupiedCells.get("Agent");
            System.out.println("Depth: " + curr.depth);

            if (curr.equals(root)) {
                for (String key : curr.occupiedCells.keySet()) {
                    String[] value = curr.occupiedCells.get(key).split(",");
                    if (value[0].equals("Ship"))
                        if (value[2].equals("false")) {
                            unWreckedShips++;
                        }
                }
            }else {
                unWreckedShips = 0;
                for (String key : curr.occupiedCells.keySet()) {
                    String[] value = curr.occupiedCells.get(key).split(",");
                    if (value[0].equals("Ship")) {
                        int damage = Integer.parseInt(value[3]);
                        if (value[2].equals("true")) { // if it's a wreck, increase damage of blackbox
                            if (damage < 20 && value[4].equals("false")) {
                                damage++;
                                curr.occupiedCells.put(key, "Ship," + value[1] + ",true," + damage + ",false");
                            } else if (damage >= 20)
                                curr.occupiedCells.put(key, "Ship," + value[1] + ",true," + damage + ",true");
                        } else { // if it's not, a passenger expires
                            int numOfPassengers = Integer.parseInt(value[1]);
                            if (numOfPassengers == 1) // ship sinks after the last passenger dies
                                curr.occupiedCells.put(key, "Ship,0,true,1,false");
                            else {
                                curr.occupiedCells.put(key, "Ship," + (numOfPassengers - 1) + ",false,0,false");
                                unWreckedShips++;
                            }
                        }
                    }
                }
            }

            expandedNodes++;
            // check if curr is goal state
            if (reachedGoal(curr.occupiedCells, currAgent))
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
                String[] agentSplit = currAgent.split(",");
                int agentCapacity = Integer.parseInt(agentSplit[2]);
                int agentLocX = Integer.parseInt(agentSplit[0]);
                int agentLocY = Integer.parseInt(agentSplit[1]);
                int agentAvailableSeats = Integer.parseInt(agentSplit[3]);
                String location = agentSplit[0] + "," + agentSplit[1];
                boolean move = true;
                if (curr.occupiedCells.get(location) != null) {
                    String[] currCell = curr.occupiedCells.get(location).split(",");
                    // System.out.println(curr.occupiedCells.get(location) + ", Boat: " + location + ", Boat Available Seats: " + agentAvailableSeats);
                    if (currCell[0].equals("Ship")) {
                        int numOfPassengers = Integer.parseInt(currCell[1]);
                        if (currCell[2].equals("true")) { // ship is a wreck
                            if (currCell[4].equals("false")) { // here the agent can retrieve the black box of the wreck
                                HashMap<String, String> occupiedCellsClone = curr.cloneOccupiedCells();
                                occupiedCellsClone.put(location, "Ship," + numOfPassengers + ",true," + currCell[3] + ",true");
                                if (!includesState(states, occupiedCellsClone)) {
                                    // System.out.println("RETRIEVE");
                                    states.add(occupiedCellsClone);
                                    q.add(new Node2(occupiedCellsClone, curr, curr.depth + 1, curr.operator +
                                            ",retrieve", retrievedBlackBoxes + 1, curr.deathsSoFar + unWreckedShips));
                                    move = false;
                                }
                            }
                        } else { // ship is not a wreck
                            if (Integer.parseInt(currCell[1]) > 0) {
                                if (agentAvailableSeats != 0) {
                                    String newAgent;
                                    // pick up passengers
                                    if (agentAvailableSeats < numOfPassengers) { // agent picks SOME of the passengers on the ship
                                        newAgent = agentLocX + "," + agentLocY + "," + agentCapacity + ",0";
                                        HashMap<String, String> occupiedCellsClone = curr.cloneOccupiedCells();
                                        occupiedCellsClone.put(location, "Ship," + (numOfPassengers - agentAvailableSeats) + ",false,0,false");
                                        occupiedCellsClone.put("Agent", newAgent);
                                        if (!includesState(states, occupiedCellsClone)) {
                                            // System.out.println("PICKUP");
                                            states.add(occupiedCellsClone);
                                            q.add(new Node2(occupiedCellsClone, curr, curr.depth + 1, curr.operator +
                                                    ",pickup", retrievedBlackBoxes, curr.deathsSoFar + unWreckedShips));
                                            move = false;
                                        }
                                    } else { // agent picks up ALL passenger on the ship, and it becomes a wreck
                                        newAgent = agentLocX + "," + agentLocY + "," + agentCapacity + "," + (agentAvailableSeats - numOfPassengers);
                                        unWreckedShips--;
                                        HashMap<String, String> occupiedCellsClone = curr.cloneOccupiedCells();
                                        occupiedCellsClone.put(location, "Ship,0,true,1,false");
                                        occupiedCellsClone.put("Agent", newAgent);
                                        if (!includesState(states, occupiedCellsClone)) {
                                            // System.out.println("PICKUP");
                                            states.add(occupiedCellsClone);
                                            q.add(new Node2(occupiedCellsClone, curr, curr.depth + 1, curr.operator +
                                                    ",pickup", retrievedBlackBoxes, unWreckedShips > 0 ? (curr.deathsSoFar + unWreckedShips) : curr.deathsSoFar));
                                            move = false;
                                        }
                                    }
                                }
                            }
                        }
                    } else if (curr.occupiedCells.get(location).equals("Station")) {
                        if (agentAvailableSeats != agentCapacity) { // boat is not empty
                            // drop off all passengers at station
                            String newAgent = agentLocX + "," + agentLocY + "," + agentCapacity + "," + agentCapacity;
                            HashMap<String, String> occupiedCellsClone = curr.cloneOccupiedCells();
                            occupiedCellsClone.put("Agent", newAgent);
                            if (!includesState(states, occupiedCellsClone)) {
                                // System.out.println("DROP");
                                states.add(occupiedCellsClone);
                                q.add(new Node2(occupiedCellsClone, curr, curr.depth + 1, curr.operator +
                                        ",drop", retrievedBlackBoxes, curr.deathsSoFar + unWreckedShips));
                                move = false;
                            }
                        }
                    }
                }
                if (move) {
                    // enqueue nodes that contain "up" | "down" | "left" | "right" actions
                    String latestAction = "";
                    String secondToLatestAction = "";
                    if (curr.parent != null) {
                        String[] s = curr.operator.split(",");
                        latestAction = s[s.length - 1];
                        if (s.length >= 2 && (latestAction.equals("up") || latestAction.equals("down") || latestAction.equals("left") || latestAction.equals("right")))
                            secondToLatestAction = s[s.length - 2];
                    }
                    //System.out.println("Latest action: " + curr.operator);
                    boolean[] directions = leaveCell(agentLocX, agentLocY, rows, cols, latestAction, secondToLatestAction);
                    // direction: 0: up, 1: down, 2: left, 3: right
                    if (directions[0]) { // up
                        HashMap<String, String> occupiedCellsClone = curr.cloneOccupiedCells();
                        currAgent = (agentLocX - 1) + "," + agentLocY + "," + agentCapacity + "," + agentAvailableSeats;
                        occupiedCellsClone.put("Agent", currAgent);
                        if (!includesState(states, occupiedCellsClone)) {
                            // System.out.println("UP");
                            states.add(occupiedCellsClone);
                            q.add(new Node2(occupiedCellsClone, curr, curr.depth + 1, curr.operator +
                                    ",up", retrievedBlackBoxes, unWreckedShips > 0 ? (curr.deathsSoFar + unWreckedShips) : curr.deathsSoFar));
                        }
                    }
                    if (directions[1]) { // down
                        HashMap<String, String> occupiedCellsClone = curr.cloneOccupiedCells();
                        currAgent = (agentLocX + 1) + "," + agentLocY + "," + agentCapacity + "," + agentAvailableSeats;
                        occupiedCellsClone.put("Agent", currAgent);
                        if (!includesState(states, occupiedCellsClone)) {
                            // System.out.println("DOWN");
                            states.add(occupiedCellsClone);
                            q.add(new Node2(occupiedCellsClone, curr, curr.depth + 1, curr.operator +
                                    ",down", retrievedBlackBoxes, unWreckedShips > 0 ? (curr.deathsSoFar + unWreckedShips) : curr.deathsSoFar));
                        }
                    }
                    if (directions[2]) { // left
                        HashMap<String, String> occupiedCellsClone = curr.cloneOccupiedCells();
                        currAgent = agentLocX + "," + (agentLocY - 1) + "," + agentCapacity + "," + agentAvailableSeats;
                        occupiedCellsClone.put("Agent", currAgent);
                        if (!includesState(states, occupiedCellsClone)) {
                            // System.out.println("LEFT");
                            states.add(occupiedCellsClone);
                            q.add(new Node2(occupiedCellsClone, curr, curr.depth + 1, curr.operator +
                                    ",left", retrievedBlackBoxes, unWreckedShips > 0 ? (curr.deathsSoFar + unWreckedShips) : curr.deathsSoFar));
                        }
                    }
                    if (directions[3]) { // right
                        HashMap<String, String> occupiedCellsClone = curr.cloneOccupiedCells();
                        currAgent = agentLocX + "," + (agentLocY + 1) + "," + agentCapacity + "," + agentAvailableSeats;
                        occupiedCellsClone.put("Agent", currAgent);
                        if (!includesState(states, occupiedCellsClone)) {
                            // System.out.println("RIGHT");
                            states.add(occupiedCellsClone);
                            q.add(new Node2(occupiedCellsClone, curr, curr.depth + 1, curr.operator +
                                    ",right", retrievedBlackBoxes, unWreckedShips > 0 ? (curr.deathsSoFar + unWreckedShips) : curr.deathsSoFar));
                        }
                    }
                }
            }
            System.out.println("Latest action: " + curr.operator);
            System.out.println(curr.occupiedCells);
            System.out.println("path cost:"+curr.pathCost);
        }
        return null;
    }



    Object[] Greedy1 (Node2 root) {
       // Comparator<Node2> heuristicOrder = Comparator.comparing(Node2::getHeuristic_cost1).thenComparing(Node2::getHeuristic_cost1_part2);
        PriorityQueue<Node2> q = new PriorityQueue<>();
        Node2 goal;
        //Queue<Node2> q = new LinkedList<Node2>();
        ArrayList<HashMap<String, String>> states = new ArrayList<HashMap<String, String>>();
        int rows = root.rows;
        int cols= root.cols;
        int expandedNodes = 0;
        // String[] agent = root.occupiedCells.get("Agent").split(","); // "locX,locY,capacity,availableSeats"
        q.add(root);
        int depth = root.depth; // 0
        HashMap<String, String> occupiedCells = root.occupiedCells;
        states.add(root.occupiedCells);
        while (!q.isEmpty()) {
            int unWreckedShips = 0;
            Node2 curr = q.poll();
            int retrievedBlackBoxes = curr.retrievedBoxes;
            String currAgent = curr.occupiedCells.get("Agent");
         //   System.out.println("Depth: " + curr.depth);


            if (curr.equals(root)) {
                for (String key : curr.occupiedCells.keySet()) {
                    String[] value = curr.occupiedCells.get(key).split(",");
                    if (value[0].equals("Ship"))
                        if (value[2].equals("false")) {
                            unWreckedShips++;
                        }
                }
            }else {
                unWreckedShips = 0;
                for (String key : curr.occupiedCells.keySet()) {
                    String[] value = curr.occupiedCells.get(key).split(",");
                    if (value[0].equals("Ship")) {
                        int damage = Integer.parseInt(value[3]);
                        if (value[2].equals("true")) { // if it's a wreck, increase damage of blackbox
                            if (damage < 20 && value[4].equals("false")) {
                                damage++;
                                curr.occupiedCells.put(key, "Ship," + value[1] + ",true," + damage + ",false");
                            } else if (damage >= 20)
                                curr.occupiedCells.put(key, "Ship," + value[1] + ",true," + damage + ",true");
                        } else { // if it's not, a passenger expires
                            int numOfPassengers = Integer.parseInt(value[1]);
                            if (numOfPassengers == 1) // ship sinks after the last passenger dies
                                curr.occupiedCells.put(key, "Ship,0,true,1,false");
                            else {
                                curr.occupiedCells.put(key, "Ship," + (numOfPassengers - 1) + ",false,0,false");
                                unWreckedShips++;
                            }
                        }
                    }
                }
            }

            expandedNodes++;
            // check if curr is goal state
            if (reachedGoal(curr.occupiedCells, currAgent))
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
                String[] agentSplit = currAgent.split(",");
                int agentCapacity = Integer.parseInt(agentSplit[2]);
                int agentLocX = Integer.parseInt(agentSplit[0]);
                int agentLocY = Integer.parseInt(agentSplit[1]);
                int agentAvailableSeats = Integer.parseInt(agentSplit[3]);
                String location = agentSplit[0] + "," + agentSplit[1];
                boolean move = true;
                if (curr.occupiedCells.get(location) != null) {
                    String[] currCell = curr.occupiedCells.get(location).split(",");
                    // System.out.println(curr.occupiedCells.get(location) + ", Boat: " + location + ", Boat Available Seats: " + agentAvailableSeats);
                    if (currCell[0].equals("Ship")) {
                        int numOfPassengers = Integer.parseInt(currCell[1]);
                        if (currCell[2].equals("true")) { // ship is a wreck
                            if (currCell[4].equals("false")) { // here the agent can retrieve the black box of the wreck
                                HashMap<String, String> occupiedCellsClone = curr.cloneOccupiedCells();
                                occupiedCellsClone.put(location, "Ship," + numOfPassengers + ",true," + currCell[3] + ",true");
                                if (!includesState(states, occupiedCellsClone)) {
                                    // System.out.println("RETRIEVE");
                                    states.add(occupiedCellsClone);
                                    q.add(new Node2(occupiedCellsClone, curr, curr.depth + 1, curr.operator +
                                            ",retrieve", retrievedBlackBoxes + 1, curr.deathsSoFar + unWreckedShips));
                                    move = false;
                                }
                            }
                        } else { // ship is not a wreck
                            if (Integer.parseInt(currCell[1]) > 0) {
                                if (agentAvailableSeats != 0) {
                                    String newAgent;
                                    // pick up passengers
                                    if (agentAvailableSeats < numOfPassengers) { // agent picks SOME of the passengers on the ship
                                        newAgent = agentLocX + "," + agentLocY + "," + agentCapacity + ",0";
                                        HashMap<String, String> occupiedCellsClone = curr.cloneOccupiedCells();
                                        occupiedCellsClone.put(location, "Ship," + (numOfPassengers - agentAvailableSeats) + ",false,0,false");
                                        occupiedCellsClone.put("Agent", newAgent);
                                        if (!includesState(states, occupiedCellsClone)) {
                                            // System.out.println("PICKUP");
                                            states.add(occupiedCellsClone);
                                            q.add(new Node2(occupiedCellsClone, curr, curr.depth + 1, curr.operator +
                                                    ",pickup", retrievedBlackBoxes, curr.deathsSoFar + unWreckedShips));
                                            move = false;
                                        }
                                    } else { // agent picks up ALL passenger on the ship, and it becomes a wreck
                                        newAgent = agentLocX + "," + agentLocY + "," + agentCapacity + "," + (agentAvailableSeats - numOfPassengers);
                                        unWreckedShips--;
                                        HashMap<String, String> occupiedCellsClone = curr.cloneOccupiedCells();
                                        occupiedCellsClone.put(location, "Ship,0,true,1,false");
                                        occupiedCellsClone.put("Agent", newAgent);
                                        if (!includesState(states, occupiedCellsClone)) {
                                            // System.out.println("PICKUP");
                                            states.add(occupiedCellsClone);
                                            q.add(new Node2(occupiedCellsClone, curr, curr.depth + 1, curr.operator +
                                                    ",pickup", retrievedBlackBoxes, unWreckedShips > 0 ? (curr.deathsSoFar + unWreckedShips) : curr.deathsSoFar));
                                            move = false;
                                        }
                                    }
                                }
                            }
                        }
                    } else if (curr.occupiedCells.get(location).equals("Station")) {
                        if (agentAvailableSeats != agentCapacity) { // boat is not empty
                            // drop off all passengers at station
                            String newAgent = agentLocX + "," + agentLocY + "," + agentCapacity + "," + agentCapacity;
                            HashMap<String, String> occupiedCellsClone = curr.cloneOccupiedCells();
                            occupiedCellsClone.put("Agent", newAgent);
                            if (!includesState(states, occupiedCellsClone)) {
                                // System.out.println("DROP");
                                states.add(occupiedCellsClone);
                                q.add(new Node2(occupiedCellsClone, curr, curr.depth + 1, curr.operator +
                                        ",drop", retrievedBlackBoxes, curr.deathsSoFar + unWreckedShips));
                                move = false;
                            }
                        }
                    }
                }
                if (move) {
                    // enqueue nodes that contain "up" | "down" | "left" | "right" actions
                    String latestAction = "";
                    String secondToLatestAction = "";
                    if (curr.parent != null) {
                        String[] s = curr.operator.split(",");
                        latestAction = s[s.length - 1];
                        if (s.length >= 2 && (latestAction.equals("up") || latestAction.equals("down") || latestAction.equals("left") || latestAction.equals("right")))
                            secondToLatestAction = s[s.length - 2];
                    }
                    //System.out.println("Latest action: " + curr.operator);
                    boolean[] directions = leaveCell(agentLocX, agentLocY, rows, cols, latestAction, secondToLatestAction);
                    // direction: 0: up, 1: down, 2: left, 3: right
                    if (directions[0]) { // up
                        HashMap<String, String> occupiedCellsClone = curr.cloneOccupiedCells();
                        currAgent = (agentLocX - 1) + "," + agentLocY + "," + agentCapacity + "," + agentAvailableSeats;
                        occupiedCellsClone.put("Agent", currAgent);
                        if (!includesState(states, occupiedCellsClone)) {
                            // System.out.println("UP");
                            states.add(occupiedCellsClone);
                            q.add(new Node2(occupiedCellsClone, curr, curr.depth + 1, curr.operator +
                                    ",up", retrievedBlackBoxes, unWreckedShips > 0 ? (curr.deathsSoFar + unWreckedShips) : curr.deathsSoFar));
                        }
                    }
                    if (directions[1]) { // down
                        HashMap<String, String> occupiedCellsClone = curr.cloneOccupiedCells();
                        currAgent = (agentLocX + 1) + "," + agentLocY + "," + agentCapacity + "," + agentAvailableSeats;
                        occupiedCellsClone.put("Agent", currAgent);
                        if (!includesState(states, occupiedCellsClone)) {
                            // System.out.println("DOWN");
                            states.add(occupiedCellsClone);
                            q.add(new Node2(occupiedCellsClone, curr, curr.depth + 1, curr.operator +
                                    ",down", retrievedBlackBoxes, unWreckedShips > 0 ? (curr.deathsSoFar + unWreckedShips) : curr.deathsSoFar));
                        }
                    }
                    if (directions[2]) { // left
                        HashMap<String, String> occupiedCellsClone = curr.cloneOccupiedCells();
                        currAgent = agentLocX + "," + (agentLocY - 1) + "," + agentCapacity + "," + agentAvailableSeats;
                        occupiedCellsClone.put("Agent", currAgent);
                        if (!includesState(states, occupiedCellsClone)) {
                            // System.out.println("LEFT");
                            states.add(occupiedCellsClone);
                            q.add(new Node2(occupiedCellsClone, curr, curr.depth + 1, curr.operator +
                                    ",left", retrievedBlackBoxes, unWreckedShips > 0 ? (curr.deathsSoFar + unWreckedShips) : curr.deathsSoFar));
                        }
                    }
                    if (directions[3]) { // right
                        HashMap<String, String> occupiedCellsClone = curr.cloneOccupiedCells();
                        currAgent = agentLocX + "," + (agentLocY + 1) + "," + agentCapacity + "," + agentAvailableSeats;
                        occupiedCellsClone.put("Agent", currAgent);
                        if (!includesState(states, occupiedCellsClone)) {
                            // System.out.println("RIGHT");
                            states.add(occupiedCellsClone);
                            q.add(new Node2(occupiedCellsClone, curr, curr.depth + 1, curr.operator +
                                    ",right", retrievedBlackBoxes, unWreckedShips > 0 ? (curr.deathsSoFar + unWreckedShips) : curr.deathsSoFar));
                        }
                    }
                }
            }
//            System.out.println("Latest action: " + curr.operator);
//            System.out.println(curr.occupiedCells);
        }
        return null;
    }

    Object[] AS1 (Node2 root) {
        Comparator<Node2> heuristicOrder = Comparator.comparing(Node2::getHeuristic_cost1).thenComparing(Node2::getHeuristic_cost1_part2);
       // Comparator<Node2> heuristicOrder = Comparator.comparing(Node2::getHeuristic_cost1);
        PriorityQueue<Node2> q = new PriorityQueue<>( heuristicOrder );

        Node2 goal;
        //Queue<Node2> q = new LinkedList<Node2>();
        ArrayList<HashMap<String, String>> states = new ArrayList<HashMap<String, String>>();
        int rows = root.rows;
        int cols= root.cols;
        int expandedNodes = 0;
        // String[] agent = root.occupiedCells.get("Agent").split(","); // "locX,locY,capacity,availableSeats"
        q.add(root);
        int depth = root.depth; // 0
        HashMap<String, String> occupiedCells = root.occupiedCells;
        states.add(root.occupiedCells);
        while (!q.isEmpty()) {
            int unWreckedShips = 0;
            Node2 curr = q.poll();
            int retrievedBlackBoxes = curr.retrievedBoxes;
            String currAgent = curr.occupiedCells.get("Agent");
          //  System.out.println("Depth: " + curr.depth);

            if (curr.equals(root)) {
                for (String key : curr.occupiedCells.keySet()) {
                    String[] value = curr.occupiedCells.get(key).split(",");
                    if (value[0].equals("Ship"))
                        if (value[2].equals("false")) {
                            unWreckedShips++;
                        }
                }
            }else {
                unWreckedShips = 0;
                for (String key : curr.occupiedCells.keySet()) {
                    String[] value = curr.occupiedCells.get(key).split(",");
                    if (value[0].equals("Ship")) {
                        int damage = Integer.parseInt(value[3]);
                        if (value[2].equals("true")) { // if it's a wreck, increase damage of blackbox
                            if (damage < 20 && value[4].equals("false")) {
                                damage++;
                                curr.occupiedCells.put(key, "Ship," + value[1] + ",true," + damage + ",false");
                            } else if (damage >= 20)
                                curr.occupiedCells.put(key, "Ship," + value[1] + ",true," + damage + ",true");
                        } else { // if it's not, a passenger expires
                            int numOfPassengers = Integer.parseInt(value[1]);
                            if (numOfPassengers == 1) // ship sinks after the last passenger dies
                                curr.occupiedCells.put(key, "Ship,0,true,1,false");
                            else {
                                curr.occupiedCells.put(key, "Ship," + (numOfPassengers - 1) + ",false,0,false");
                                unWreckedShips++;
                            }
                        }
                    }
                }
            }


            expandedNodes++;
            // check if curr is goal state
            if (reachedGoal(curr.occupiedCells, currAgent))
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
                String[] agentSplit = currAgent.split(",");
                int agentCapacity = Integer.parseInt(agentSplit[2]);
                int agentLocX = Integer.parseInt(agentSplit[0]);
                int agentLocY = Integer.parseInt(agentSplit[1]);
                int agentAvailableSeats = Integer.parseInt(agentSplit[3]);
                String location = agentSplit[0] + "," + agentSplit[1];
                boolean move = true;
                if (curr.occupiedCells.get(location) != null) {
                    String[] currCell = curr.occupiedCells.get(location).split(",");
                    // System.out.println(curr.occupiedCells.get(location) + ", Boat: " + location + ", Boat Available Seats: " + agentAvailableSeats);
                    if (currCell[0].equals("Ship")) {
                        int numOfPassengers = Integer.parseInt(currCell[1]);
                        if (currCell[2].equals("true")) { // ship is a wreck
                            if (currCell[4].equals("false")) { // here the agent can retrieve the black box of the wreck
                                HashMap<String, String> occupiedCellsClone = curr.cloneOccupiedCells();
                                occupiedCellsClone.put(location, "Ship," + numOfPassengers + ",true," + currCell[3] + ",true");
                                if (!includesState(states, occupiedCellsClone)) {
                                    // System.out.println("RETRIEVE");
                                    states.add(occupiedCellsClone);
                                    q.add(new Node2(occupiedCellsClone, curr, curr.depth + 1, curr.operator +
                                            ",retrieve", retrievedBlackBoxes + 1, curr.deathsSoFar + unWreckedShips));
                                    move = false;
                                }
                            }
                        } else { // ship is not a wreck
                            if (Integer.parseInt(currCell[1]) > 0) {
                                if (agentAvailableSeats != 0) {
                                    String newAgent;
                                    // pick up passengers
                                    if (agentAvailableSeats < numOfPassengers) { // agent picks SOME of the passengers on the ship
                                        newAgent = agentLocX + "," + agentLocY + "," + agentCapacity + ",0";
                                        HashMap<String, String> occupiedCellsClone = curr.cloneOccupiedCells();
                                        occupiedCellsClone.put(location, "Ship," + (numOfPassengers - agentAvailableSeats) + ",false,0,false");
                                        occupiedCellsClone.put("Agent", newAgent);
                                        if (!includesState(states, occupiedCellsClone)) {
                                            // System.out.println("PICKUP");
                                            states.add(occupiedCellsClone);
                                            q.add(new Node2(occupiedCellsClone, curr, curr.depth + 1,  curr.operator +
                                                    ",pickup", retrievedBlackBoxes, curr.deathsSoFar + unWreckedShips));
                                            move = false;
                                        }
                                    } else { // agent picks up ALL passenger on the ship, and it becomes a wreck
                                        newAgent = agentLocX + "," + agentLocY + "," + agentCapacity + "," + (agentAvailableSeats - numOfPassengers);
                                        unWreckedShips--;
                                        HashMap<String, String> occupiedCellsClone = curr.cloneOccupiedCells();
                                        occupiedCellsClone.put(location, "Ship,0,true,1,false");
                                        occupiedCellsClone.put("Agent", newAgent);
                                        if (!includesState(states, occupiedCellsClone)) {
                                            // System.out.println("PICKUP");
                                            states.add(occupiedCellsClone);
                                            q.add(new Node2(occupiedCellsClone, curr, curr.depth + 1, curr.operator +
                                                    ",pickup", retrievedBlackBoxes, unWreckedShips > 0 ? (curr.deathsSoFar + unWreckedShips) : curr.deathsSoFar));
                                            move = false;
                                        }
                                    }
                                }
                            }
                        }
                    } else if (curr.occupiedCells.get(location).equals("Station")) {
                        if (agentAvailableSeats != agentCapacity) { // boat is not empty
                            // drop off all passengers at station
                            String newAgent = agentLocX + "," + agentLocY + "," + agentCapacity + "," + agentCapacity;
                            HashMap<String, String> occupiedCellsClone = curr.cloneOccupiedCells();
                            occupiedCellsClone.put("Agent", newAgent);
                            if (!includesState(states, occupiedCellsClone)) {
                                // System.out.println("DROP");
                                states.add(occupiedCellsClone);
                                q.add(new Node2(occupiedCellsClone, curr, curr.depth + 1,  curr.operator +
                                        ",drop", retrievedBlackBoxes, curr.deathsSoFar + unWreckedShips));
                                move = false;
                            }
                        }
                    }
                }
                if (move) {
                    // enqueue nodes that contain "up" | "down" | "left" | "right" actions
                    String latestAction = "";
                    String secondToLatestAction = "";
                    if (curr.parent != null) {
                        String[] s = curr.operator.split(",");
                        latestAction = s[s.length - 1];
                        if (s.length >= 2 && (latestAction.equals("up") || latestAction.equals("down") || latestAction.equals("left") || latestAction.equals("right")))
                            secondToLatestAction = s[s.length - 2];
                    }
                    //System.out.println("Latest action: " + curr.operator);
                    boolean[] directions = leaveCell(agentLocX, agentLocY, rows, cols, latestAction, secondToLatestAction);
                    // direction: 0: up, 1: down, 2: left, 3: right
                    if (directions[0]) { // up
                        HashMap<String, String> occupiedCellsClone = curr.cloneOccupiedCells();
                        currAgent = (agentLocX - 1) + "," + agentLocY + "," + agentCapacity + "," + agentAvailableSeats;
                        occupiedCellsClone.put("Agent", currAgent);
                        if (!includesState(states, occupiedCellsClone)) {
                            // System.out.println("UP");
                            states.add(occupiedCellsClone);
                            q.add(new Node2(occupiedCellsClone, curr, curr.depth + 1,  curr.operator +
                                    ",up", retrievedBlackBoxes, unWreckedShips > 0 ? (curr.deathsSoFar + unWreckedShips) : curr.deathsSoFar));
                        }
                    }
                    if (directions[1]) { // down
                        HashMap<String, String> occupiedCellsClone = curr.cloneOccupiedCells();
                        currAgent = (agentLocX + 1) + "," + agentLocY + "," + agentCapacity + "," + agentAvailableSeats;
                        occupiedCellsClone.put("Agent", currAgent);
                        if (!includesState(states, occupiedCellsClone)) {
                            // System.out.println("DOWN");
                            states.add(occupiedCellsClone);
                            q.add(new Node2(occupiedCellsClone, curr, curr.depth + 1,  curr.operator +
                                    ",down", retrievedBlackBoxes, unWreckedShips > 0 ? (curr.deathsSoFar + unWreckedShips) : curr.deathsSoFar));
                        }
                    }
                    if (directions[2]) { // left
                        HashMap<String, String> occupiedCellsClone = curr.cloneOccupiedCells();
                        currAgent = agentLocX + "," + (agentLocY - 1) + "," + agentCapacity + "," + agentAvailableSeats;
                        occupiedCellsClone.put("Agent", currAgent);
                        if (!includesState(states, occupiedCellsClone)) {
                            // System.out.println("LEFT");
                            states.add(occupiedCellsClone);
                            q.add(new Node2(occupiedCellsClone, curr, curr.depth + 1,  curr.operator +
                                    ",left", retrievedBlackBoxes, unWreckedShips > 0 ? (curr.deathsSoFar + unWreckedShips) : curr.deathsSoFar));
                        }
                    }
                    if (directions[3]) { // right
                        HashMap<String, String> occupiedCellsClone = curr.cloneOccupiedCells();
                        currAgent = agentLocX + "," + (agentLocY + 1) + "," + agentCapacity + "," + agentAvailableSeats;
                        occupiedCellsClone.put("Agent", currAgent);
                        if (!includesState(states, occupiedCellsClone)) {
                            // System.out.println("RIGHT");
                            states.add(occupiedCellsClone);
                            q.add(new Node2(occupiedCellsClone, curr, curr.depth + 1,  curr.operator +
                                    ",right", retrievedBlackBoxes, unWreckedShips > 0 ? (curr.deathsSoFar + unWreckedShips) : curr.deathsSoFar));
                        }
                    }
                }
            }
//            System.out.print("Que start:-->" + q.toString());
//            System.out.println();
            //System.out.println("Latest action: " + curr.operator);
//            Iterator iterator = q.iterator();
//
//            while (iterator.hasNext()) {
//                System.out.print(iterator.next() + " ");
//            }

//            System.out.println("Latest action: " + curr.operator);
//            System.out.println(curr.occupiedCells);
//            System.out.println("path cost:"+curr.pathCost);
//            System.out.println("heuristic+path cost:"+curr.getHeuristic_cost1() );
//            System.out.println("heuristic+path cost:"+curr.getHeuristic_cost1_part2());
        }
        return null;
    }

    Object[] AS2 (Node2 root) {
        Comparator<Node2> heuristicOrder = Comparator.comparing(Node2::getHeuristic_cost2).thenComparing(Node2::getHeuristic_cost2_part2);
        // Comparator<Node2> heuristicOrder = Comparator.comparing(Node2::getHeuristic_cost1);
        PriorityQueue<Node2> q = new PriorityQueue<>( heuristicOrder );

        Node2 goal;
        //Queue<Node2> q = new LinkedList<Node2>();
        ArrayList<HashMap<String, String>> states = new ArrayList<HashMap<String, String>>();
        int rows = root.rows;
        int cols= root.cols;
        int expandedNodes = 0;
        // String[] agent = root.occupiedCells.get("Agent").split(","); // "locX,locY,capacity,availableSeats"
        q.add(root);
        int depth = root.depth; // 0
        HashMap<String, String> occupiedCells = root.occupiedCells;
        states.add(root.occupiedCells);
        while (!q.isEmpty()) {
            int unWreckedShips = 0;
            Node2 curr = q.poll();
            int retrievedBlackBoxes = curr.retrievedBoxes;
            String currAgent = curr.occupiedCells.get("Agent");
            System.out.println("Depth: " + curr.depth);

            if (curr.equals(root)) {
                for (String key : curr.occupiedCells.keySet()) {
                    String[] value = curr.occupiedCells.get(key).split(",");
                    if (value[0].equals("Ship"))
                        if (value[2].equals("false")) {
                            unWreckedShips++;
                        }
                }
            }else {
                unWreckedShips = 0;
                for (String key : curr.occupiedCells.keySet()) {
                    String[] value = curr.occupiedCells.get(key).split(",");
                    if (value[0].equals("Ship")) {
                        int damage = Integer.parseInt(value[3]);
                        if (value[2].equals("true")) { // if it's a wreck, increase damage of blackbox
                            if (damage < 20 && value[4].equals("false")) {
                                damage++;
                                curr.occupiedCells.put(key, "Ship," + value[1] + ",true," + damage + ",false");
                            } else if (damage >= 20)
                                curr.occupiedCells.put(key, "Ship," + value[1] + ",true," + damage + ",true");
                        } else { // if it's not, a passenger expires
                            int numOfPassengers = Integer.parseInt(value[1]);
                            if (numOfPassengers == 1) // ship sinks after the last passenger dies
                                curr.occupiedCells.put(key, "Ship,0,true,1,false");
                            else {
                                curr.occupiedCells.put(key, "Ship," + (numOfPassengers - 1) + ",false,0,false");
                                unWreckedShips++;
                            }
                        }
                    }
                }
            }


            expandedNodes++;
            // check if curr is goal state
            if (reachedGoal(curr.occupiedCells, currAgent))
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
                String[] agentSplit = currAgent.split(",");
                int agentCapacity = Integer.parseInt(agentSplit[2]);
                int agentLocX = Integer.parseInt(agentSplit[0]);
                int agentLocY = Integer.parseInt(agentSplit[1]);
                int agentAvailableSeats = Integer.parseInt(agentSplit[3]);
                String location = agentSplit[0] + "," + agentSplit[1];
                boolean move = true;
                if (curr.occupiedCells.get(location) != null) {
                    String[] currCell = curr.occupiedCells.get(location).split(",");
                    // System.out.println(curr.occupiedCells.get(location) + ", Boat: " + location + ", Boat Available Seats: " + agentAvailableSeats);
                    if (currCell[0].equals("Ship")) {
                        int numOfPassengers = Integer.parseInt(currCell[1]);
                        if (currCell[2].equals("true")) { // ship is a wreck
                            if (currCell[4].equals("false")) { // here the agent can retrieve the black box of the wreck
                                HashMap<String, String> occupiedCellsClone = curr.cloneOccupiedCells();
                                occupiedCellsClone.put(location, "Ship," + numOfPassengers + ",true," + currCell[3] + ",true");
                                if (!includesState(states, occupiedCellsClone)) {
                                    // System.out.println("RETRIEVE");
                                    states.add(occupiedCellsClone);
                                    q.add(new Node2(occupiedCellsClone, curr, curr.depth + 1,  curr.operator +
                                            ",retrieve", retrievedBlackBoxes + 1, curr.deathsSoFar + unWreckedShips));
                                    move = false;
                                }
                            }
                        } else { // ship is not a wreck
                            if (Integer.parseInt(currCell[1]) > 0) {
                                if (agentAvailableSeats != 0) {
                                    String newAgent;
                                    // pick up passengers
                                    if (agentAvailableSeats < numOfPassengers) { // agent picks SOME of the passengers on the ship
                                        newAgent = agentLocX + "," + agentLocY + "," + agentCapacity + ",0";
                                        HashMap<String, String> occupiedCellsClone = curr.cloneOccupiedCells();
                                        occupiedCellsClone.put(location, "Ship," + (numOfPassengers - agentAvailableSeats) + ",false,0,false");
                                        occupiedCellsClone.put("Agent", newAgent);
                                        if (!includesState(states, occupiedCellsClone)) {
                                            // System.out.println("PICKUP");
                                            states.add(occupiedCellsClone);
                                            q.add(new Node2(occupiedCellsClone, curr, curr.depth + 1,  curr.operator +
                                                    ",pickup", retrievedBlackBoxes, curr.deathsSoFar + unWreckedShips));
                                            move = false;
                                        }
                                    } else { // agent picks up ALL passenger on the ship, and it becomes a wreck
                                        newAgent = agentLocX + "," + agentLocY + "," + agentCapacity + "," + (agentAvailableSeats - numOfPassengers);
                                        unWreckedShips--;
                                        HashMap<String, String> occupiedCellsClone = curr.cloneOccupiedCells();
                                        occupiedCellsClone.put(location, "Ship,0,true,1,false");
                                        occupiedCellsClone.put("Agent", newAgent);
                                        if (!includesState(states, occupiedCellsClone)) {
                                            // System.out.println("PICKUP");
                                            states.add(occupiedCellsClone);
                                            q.add(new Node2(occupiedCellsClone, curr, curr.depth + 1,  curr.operator +
                                                    ",pickup", retrievedBlackBoxes, unWreckedShips > 0 ? (curr.deathsSoFar + unWreckedShips) : curr.deathsSoFar));
                                            move = false;
                                        }
                                    }
                                }
                            }
                        }
                    } else if (curr.occupiedCells.get(location).equals("Station")) {
                        if (agentAvailableSeats != agentCapacity) { // boat is not empty
                            // drop off all passengers at station
                            String newAgent = agentLocX + "," + agentLocY + "," + agentCapacity + "," + agentCapacity;
                            HashMap<String, String> occupiedCellsClone = curr.cloneOccupiedCells();
                            occupiedCellsClone.put("Agent", newAgent);
                            if (!includesState(states, occupiedCellsClone)) {
                                // System.out.println("DROP");
                                states.add(occupiedCellsClone);
                                q.add(new Node2(occupiedCellsClone, curr, curr.depth + 1,  curr.operator +
                                        ",drop", retrievedBlackBoxes, curr.deathsSoFar + unWreckedShips));
                                move = false;
                            }
                        }
                    }
                }
                if (move) {
                    // enqueue nodes that contain "up" | "down" | "left" | "right" actions
                    String latestAction = "";
                    String secondToLatestAction = "";
                    if (curr.parent != null) {
                        String[] s = curr.operator.split(",");
                        latestAction = s[s.length - 1];
                        if (s.length >= 2 && (latestAction.equals("up") || latestAction.equals("down") || latestAction.equals("left") || latestAction.equals("right")))
                            secondToLatestAction = s[s.length - 2];
                    }
                    //System.out.println("Latest action: " + curr.operator);
                    boolean[] directions = leaveCell(agentLocX, agentLocY, rows, cols, latestAction, secondToLatestAction);
                    // direction: 0: up, 1: down, 2: left, 3: right
                    if (directions[0]) { // up
                        HashMap<String, String> occupiedCellsClone = curr.cloneOccupiedCells();
                        currAgent = (agentLocX - 1) + "," + agentLocY + "," + agentCapacity + "," + agentAvailableSeats;
                        occupiedCellsClone.put("Agent", currAgent);
                        if (!includesState(states, occupiedCellsClone)) {
                            // System.out.println("UP");
                            states.add(occupiedCellsClone);
                            q.add(new Node2(occupiedCellsClone, curr, curr.depth + 1,  curr.operator +
                                    ",up", retrievedBlackBoxes, unWreckedShips > 0 ? (curr.deathsSoFar + unWreckedShips) : curr.deathsSoFar));
                        }
                    }
                    if (directions[1]) { // down
                        HashMap<String, String> occupiedCellsClone = curr.cloneOccupiedCells();
                        currAgent = (agentLocX + 1) + "," + agentLocY + "," + agentCapacity + "," + agentAvailableSeats;
                        occupiedCellsClone.put("Agent", currAgent);
                        if (!includesState(states, occupiedCellsClone)) {
                            // System.out.println("DOWN");
                            states.add(occupiedCellsClone);
                            q.add(new Node2(occupiedCellsClone, curr, curr.depth + 1,  curr.operator +
                                    ",down", retrievedBlackBoxes, unWreckedShips > 0 ? (curr.deathsSoFar + unWreckedShips) : curr.deathsSoFar));
                        }
                    }
                    if (directions[2]) { // left
                        HashMap<String, String> occupiedCellsClone = curr.cloneOccupiedCells();
                        currAgent = agentLocX + "," + (agentLocY - 1) + "," + agentCapacity + "," + agentAvailableSeats;
                        occupiedCellsClone.put("Agent", currAgent);
                        if (!includesState(states, occupiedCellsClone)) {
                            // System.out.println("LEFT");
                            states.add(occupiedCellsClone);
                            q.add(new Node2(occupiedCellsClone, curr, curr.depth + 1,  curr.operator +
                                    ",left", retrievedBlackBoxes, unWreckedShips > 0 ? (curr.deathsSoFar + unWreckedShips) : curr.deathsSoFar));
                        }
                    }
                    if (directions[3]) { // right
                        HashMap<String, String> occupiedCellsClone = curr.cloneOccupiedCells();
                        currAgent = agentLocX + "," + (agentLocY + 1) + "," + agentCapacity + "," + agentAvailableSeats;
                        occupiedCellsClone.put("Agent", currAgent);
                        if (!includesState(states, occupiedCellsClone)) {
                            // System.out.println("RIGHT");
                            states.add(occupiedCellsClone);
                            q.add(new Node2(occupiedCellsClone, curr, curr.depth + 1,  curr.operator +
                                    ",right", retrievedBlackBoxes, unWreckedShips > 0 ? (curr.deathsSoFar + unWreckedShips) : curr.deathsSoFar));
                        }
                    }
                }
            }
            System.out.print("Que start:-->" + q.toString());
            System.out.println();
            //System.out.println("Latest action: " + curr.operator);
//            Iterator iterator = q.iterator();
//
//            while (iterator.hasNext()) {
//                System.out.print(iterator.next() + " ");
//            }

//            System.out.println("Latest action: " + curr.operator);
//            System.out.println(curr.occupiedCells);
//            System.out.println("path cost:"+curr.pathCost);
//            System.out.println("heuristic+path cost:"+curr.getHeuristic_cost1() );
//            System.out.println("heuristic+path cost:"+curr.getHeuristic_cost1_part2());
        }
        return null;
    }

    boolean reachedGoal(HashMap<String, String> occupiedCells, String agent) {
        // You reach your goal when:
        boolean aShipIsNotWrecked = false;
        boolean aBlackBoxIsNotFullyDamaged = false;
        for (String cell : occupiedCells.values()) {
            String[] cellContent = cell.split(",");
            if(cellContent[0].equals("Ship")) {
                // 1.  there are no living passengers who are not rescued
                if (!(cellContent[2].equals("true"))) {
                    aShipIsNotWrecked = true;
                }
                // 2. there are no undamaged boxes which have not been retrieved
                else if (Integer.parseInt(cellContent[3]) < 20 && cellContent[4].equals(("false"))) {
                    aBlackBoxIsNotFullyDamaged = true;
                }
            }
        }
        if (!aShipIsNotWrecked && !aBlackBoxIsNotFullyDamaged) {
            // 3. the rescue boat is not carrying any passengers
            if (Integer.parseInt(agent.split(",")[2]) == Integer.parseInt(agent.split(",")[3])) // agent = "locX,locY,capacity,availableSeats"
                return true;
        }
        return false;
    }

    boolean[] leaveCell(int locX, int locY, int rows, int columns, String latestAction, String secondToLatestAction){
        boolean[] directions = new boolean[4]; // up, down, left, right
        if (locY == 0) { // left of the grid
            if (locX == 0) // left upper corner -> can't go left or up
            {
                directions[1] = true; // down
                directions[3] = true; // right
            }
            else if (locX == rows - 1) // left lower corner -> can't go left or down
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
        else if (locY == columns - 1) { // right of the grid
            if (locX == 0) // right upper corner -> can't go right or up
            {
                directions[2] = true; // left
                directions[1] = true; // down
            }
            else if (locX == rows - 1) // right lower corner -> can't go right or down
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
        else if (locX == 0) // can't go up
        {
            directions[1] = true; // down
            directions[2] = true; // left
            directions[3] = true; // right
        }
        else if (locX == rows - 1) // can't go down
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
        if (latestAction.equals("down")
                || (secondToLatestAction.equals("down") && (latestAction.equals("left") || latestAction.equals("right")))
        )
            directions[0] = false; // up
        else if (latestAction.equals("up")
                || (secondToLatestAction.equals("up") && (latestAction.equals("left") || latestAction.equals("right")))
        )
            directions[1] = false; // down
        if (latestAction.equals("right")
                || (secondToLatestAction.equals("right") && (latestAction.equals("up") || latestAction.equals("down")))
        )
            directions[2] = false; // left
        else if (latestAction.equals("left")
                || (secondToLatestAction.equals("left") && ((latestAction.equals("up") || latestAction.equals("down"))))
        )
            directions[3] = false; // right
        return directions;
    }
    // right,up,left

}
