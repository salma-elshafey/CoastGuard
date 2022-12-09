import java.util.HashMap;
public class CoastGuard {

//    public static String GenGrid(){
//        String s = "";
//        String ships = "";
//        String stations = "";
//        // Create grid with random dimensions
//        int n = (int) (Math.random()*(15-5+1)+5);
//        int m = (int) (Math.random()*(15-5+1)+5);
//        Object[][] grid = new Object[m][n];
//
//        // Create agent with random location and add to grid
//        int locX = (int) (Math.random()*((m-1)+1)+0);
//        int locY = (int) (Math.random()*((n-1)+1)+0);
//        int capacity = (int) (Math.random()*(100-30+1)+30);
//        Agent agent = new Agent(locX ,locY, capacity);
//        grid[agent.locX][agent.locY] = agent;
//
//        // Create ship with random location and add to grid cell if it's empty
//        locX = (int) (Math.random()*((m-1)+1));
//        locY = (int) (Math.random()*((n-1)+1));
//        int numOfPassengers = (int) (Math.random()*(100+1));
//        while (grid[locX][locY] != null){
//            locX = (int) (Math.random()*((m-1)+1));
//            locY = (int) (Math.random()*((n-1)+1));
//        }
//        grid[locX][locY] = new Ship(locX, locY, numOfPassengers);
//        ships += locX + "," + locY + "," + ((Ship)grid[locX][locY]).numOfPassengers;
//
//        // Create station with random location and add to grid cell if it's empty
//        locX = (int) (Math.random()*((m-1)+1));
//        locY = (int) (Math.random()*((n-1)+1));
//        while (grid[locX][locY] != null){
//            locX = (int) (Math.random()*((m-1)+1));
//            locY = (int) (Math.random()*((n-1)+1));
//        }
//        grid[locX][locY] = new Station(locX, locY);
//        stations += locX + "," + locY;
//
//        int numOfShips = 1;
//        int numOfStations = 1;
//
//        // Fill in other cells randomly with either ships, stations or nothing
//        for (int i = 0; i < m; i++){
//            for (int j = 0; j < n; j++){
//                int whichObject = (int) (Math.random()*(3));
//                if (whichObject == 0){
//                    // empty cell
//                }
//                else if (whichObject == 1){
//                    // add ship
//                    if (grid[i][j] == null) {
//                        grid[i][j] = new Ship(i, j, (int) (Math.random()*(100+1)));
//                        ships += "," + i + "," + j + "," + ((Ship) grid[i][j]).numOfPassengers;
//                    }
//                }
//                else {
//                    // add station
//                    if (grid[i][j] == null) {
//                        grid[i][j] = new Station(i, j);
//                        stations += "," + i + "," + j;
//                    }
//                }
//            }
//        }
//        s += m + "," + n + ";" + agent.capacity + ";" + agent.locX + "," + agent.locY + ";" + stations + ";" + ships + ";";
//        return s;
//    }

    // Solve(grid,strategy, visualize)
//    public static String solve(String gr, String strategy, boolean visualize){
//        String s = "";
//
//        // recreate grid
//        String[] splits = gr.split(";");
//        // M, N; C; cgX, cgY ;
//        // I1X, I1Y, I2X, I2Y, ...IiX, IiY ;
//        // S1X, S1Y, S1Passengers, S2X, S2Y, S2Passengers, ...SjX, SjY, SjPassengers;
//        // splits = ["m,n", "c", "cgX,cgY", "I1X,I1Y", "S1X,S1Y,S1Passengers"]
//        int m = Integer.parseInt(splits[0].split(",")[0]);
//        int n = Integer.parseInt(splits[0].split(",")[1]);
//        int locX = Integer.parseInt(splits[2].split(",")[0]);
//        int locY = Integer.parseInt(splits[2].split(",")[1]);
//        //Object[][] grid = new Object[m][n];
//        // the agent
//        Agent agent = new Agent(locX, locY, Integer.parseInt(splits[1]));
//        // stations
//        // OccupiedCells= cells occupied by stations and cells
//        HashMap<String, Object> OccupiedCells = new HashMap<String, Object>();
//
//        String[] stationsSplits = splits[3].split(",");
//        for (int i = 0; i < stationsSplits.length-1; i+=2){
//            locX = Integer.parseInt(""+stationsSplits[i]);
//            locY = Integer.parseInt(""+stationsSplits[i+1]);
//            String location = locX + "," + locY;
//            //grid[locX][locY] = new Station(locX, locY);
//            OccupiedCells.put(location,"Station");
//
//        }
//        String[] shipSplits = splits[4].split(",");
//        for (int i = 0; i < shipSplits.length-2; i+=3){
//            locX = Integer.parseInt(""+shipSplits[i]);
//            locY = Integer.parseInt(""+shipSplits[i+1]);
//            int passengers = Integer.parseInt(""+shipSplits[i+2]);
//            System.out.println(passengers);
//            String location = locX + "," + locY;
//            // grid[locX][locY] = new Ship(locX, locY, passengers);
//            OccupiedCells.put(location,new Ship(locX, locY, passengers));
//        }
//        SearchProblem solver = new SearchProblem();
//        // Breadth-first Search
//        if (strategy.equals("BF")) {
//            Node root = new Node(OccupiedCells, agent, null, 0, 0, "", 0, 0, m, n);
//            Object[] sol = solver.bfs(root);
//            Node solution = (Node) sol[0];
//            int expandedNodes = (Integer) sol[1];
//            if (solution == null)
//                s= "no sol"; // ?
//            else {
//                s= solution.operator + ";" + solution.deathsSoFar + ";" + solution.retrievedBoxes + ";" + expandedNodes;
//            }
//        }
//
//        // Depth-first Search
////        if (strategy.equals(("DF"))) {
////            Node root = new Node(OccupiedCells, agent, null, 0, 0, "", 0, 0, m, n);
////            Object[] sol = solver.dfs(root);
////            Node solution = (Node) sol[0];
////            int expandedNodes = (Integer) sol[1];
////            if (solution == null)
////                return ""; // ?
////            else {
////                return solution.operator + ";" + solution.deathsSoFar + ";" + solution.retrievedBoxes + ";" + expandedNodes;
////            }
////        }
//        return s.substring(1);
//        //return s;
//
//    }

    public static String solve(String gr, String strategy, boolean visualize) {
        String s = "";

        // recreate grid
        String[] splits = gr.split(";");
        // M, N; C; cgX, cgY ;
        // I1X, I1Y, I2X, I2Y, ...IiX, IiY ;
        // S1X, S1Y, S1Passengers, S2X, S2Y, S2Passengers, ...SjX, SjY, SjPassengers;
        // splits = ["m,n", "c", "cgX,cgY", "I1X,I1Y", "S1X,S1Y,S1Passengers"]
        int m = Integer.parseInt(splits[0].split(",")[0]);
        int n = Integer.parseInt(splits[0].split(",")[1]);
        int locX = Integer.parseInt(splits[2].split(",")[0]);
        int locY = Integer.parseInt(splits[2].split(",")[1]);
        //Object[][] grid = new Object[m][n];
        // the agent
        String agent = locX + "," + locY + "," + Integer.parseInt(splits[1]) + "," + Integer.parseInt(splits[1]); // agent = "locX,locY,capacity,availableSeats"
        // stations
        // occupiedCells = cells occupied by stations and cells
        // HashMap <String, String> -> <Location, "Type (Station, Ship),numOfPassengers,wrecked(true/false),blackBoxDamage"
        HashMap<String, String> occupiedCells = new HashMap<String, String>();
        occupiedCells.put("Agent", agent);

        String[] stationsSplits = splits[3].split(",");
        for (int i = 0; i < stationsSplits.length-1; i+=2){
            locX = Integer.parseInt(""+stationsSplits[i]);
            locY = Integer.parseInt(""+stationsSplits[i+1]);
            String location = locX + "," + locY;
            occupiedCells.put(location,"Station");

        }
        String[] shipSplits = splits[4].split(",");
        for (int i = 0; i < shipSplits.length-2; i+=3){
            locX = Integer.parseInt(""+shipSplits[i]);
            locY = Integer.parseInt(""+shipSplits[i+1]);
            int passengers = Integer.parseInt(""+shipSplits[i+2]);
            System.out.println(passengers);
            String location = locX + "," + locY;
            occupiedCells.put(location, "Ship," + passengers + ",false,0,false"); // (Ship),numOfPassengers,wrecked(true/false),blackBoxDamage,blackBoxIsRetrieved"
        }
        SearchProblem2 solver = new SearchProblem2();
        // Breadth-first Search
        if (strategy.equals("BF")) {
            Node2 root = new Node2(occupiedCells, null, 0, "", 0, 0, n, m,0);
            Object[] sol = solver.bfs(root);
            Node2 solution = (Node2) sol[0];
            int expandedNodes = (Integer) sol[1];
            if (solution == null)
                s= "no sol"; // ?
            else {
                s= solution.operator + ";" + solution.deathsSoFar + ";" + solution.retrievedBoxes + ";" + expandedNodes;
            }
            if(visualize == true) {
                Node2 solution2 = (Node2) sol[0];
                System.out.println("Step: " + solution2.depth);
                System.out.println("ACTION  " + solution2.operator);
                System.out.println("STATE  " + solution2.occupiedCells);
                System.out.println();
                while (!(solution2.parent == null)) {
                    solution2 = solution2.parent;
                    System.out.println("Step: " + solution2.depth);
                    System.out.println("ACTION  " + solution2.operator);
                    System.out.println("STATE  " + solution2.occupiedCells);
                    System.out.println();
                }
            }
        }
        else if (strategy.equals("DF")) {
            Node2 root = new Node2(occupiedCells, null, 0, "", 0, 0, n, m,0);
            Object[] sol = solver.dfs(root);
            Node2 solution = (Node2) sol[0];
            int expandedNodes = (Integer) sol[1];
            if (solution == null)
                s= "no sol"; // ?
            else {
                s= solution.operator + ";" + solution.deathsSoFar + ";" + solution.retrievedBoxes + ";" + expandedNodes;
            }
            if(visualize == true) {
                Node2 solution2 = (Node2) sol[0];
                System.out.println("Step: " + solution2.depth);
                System.out.println("ACTION  " + solution2.operator);
                System.out.println("STATE  " + solution2.occupiedCells);
                System.out.println();
                while (!(solution2.parent == null)) {
                    solution2 = solution2.parent;
                    System.out.println("Step: " + solution2.depth);
                    System.out.println("ACTION  " + solution2.operator);
                    System.out.println("STATE  " + solution2.occupiedCells);
                    System.out.println();
                }
            }
        }
        else if (strategy.equals("ID")) {
            Node2 root = new Node2(occupiedCells, null, 0, "", 0, 0, n, m,0);
            Object[] sol = solver.ids(root);
            Node2 solution = (Node2) sol[0];
            int expandedNodes = (Integer) sol[1];
            if (solution == null)
                s= "no sol"; // ?
            else {
                s= solution.operator + ";" + solution.deathsSoFar + ";" + solution.retrievedBoxes + ";" + expandedNodes;
            }
            if(visualize == true) {
                Node2 solution2 = (Node2) sol[0];
                System.out.println("Step: " + solution2.depth);
                System.out.println("ACTION  " + solution2.operator);
                System.out.println("STATE  " + solution2.occupiedCells);
                System.out.println();
                while (!(solution2.parent == null)) {
                    solution2 = solution2.parent;
                    System.out.println("Step: " + solution2.depth);
                    System.out.println("ACTION  " + solution2.operator);
                    System.out.println("STATE  " + solution2.occupiedCells);
                    System.out.println();
                }
            }
        }
        else if (strategy.equals("UC")) {
            Node2 root = new Node2(occupiedCells, null, 0, "", 0, 0, n, m,0);
            Object[] sol = solver.ucs(root);
            Node2 solution = (Node2) sol[0];
            int expandedNodes = (Integer) sol[1];
            if (solution == null)
                s= "no sol"; // ?
            else {
                s= solution.operator + ";" + solution.deathsSoFar + ";" + solution.retrievedBoxes + ";" + expandedNodes;
            }
            if(visualize == true) {
                Node2 solution2 = (Node2) sol[0];
                System.out.println("Step: " + solution2.depth);
                System.out.println("ACTION  " + solution2.operator);
                System.out.println("STATE  " + solution2.occupiedCells);
                System.out.println();
                while (!(solution2.parent == null)) {
                    solution2 = solution2.parent;
                    System.out.println("Step: " + solution2.depth);
                    System.out.println("ACTION  " + solution2.operator);
                    System.out.println("STATE  " + solution2.occupiedCells);
                    System.out.println();
                }
            }
        }
        if (strategy.equals("GR1")) {
            Node2 root = new Node2(occupiedCells, null, 0, "",  0, 0, n, m,1);
            Object[] sol = solver.Greedy1(root);
            Node2 solution = (Node2) sol[0];
            int expandedNodes = (Integer) sol[1];
            if (solution == null)
                s= "no sol"; // ?
            else {
                s= solution.operator + ";" + solution.deathsSoFar + ";" + solution.retrievedBoxes + ";" + expandedNodes;
            }
            if(visualize == true) {
                Node2 solution2 = (Node2) sol[0];
                System.out.println("Step: " + solution2.depth);
                System.out.println("ACTION  " + solution2.operator);
                System.out.println("STATE  " + solution2.occupiedCells);
                System.out.println();
                while (!(solution2.parent == null)) {
                    solution2 = solution2.parent;
                    System.out.println("Step: " + solution2.depth);
                    System.out.println("ACTION  " + solution2.operator);
                    System.out.println("STATE  " + solution2.occupiedCells);
                    System.out.println();
                }
            }
        }
        if (strategy.equals("GR2")) {
            Node2 root = new Node2(occupiedCells, null, 0, "", 0, 0, n, m,2);
            Object[] sol = solver.Greedy1(root);
            Node2 solution = (Node2) sol[0];
            int expandedNodes = (Integer) sol[1];
            if (solution == null)
                s= "no sol"; // ?
            else {
                s= solution.operator + ";" + solution.deathsSoFar + ";" + solution.retrievedBoxes + ";" + expandedNodes;
            }
            if(visualize == true) {
                Node2 solution2 = (Node2) sol[0];
                System.out.println("Step: " + solution2.depth);
                System.out.println("ACTION  " + solution2.operator);
                System.out.println("STATE  " + solution2.occupiedCells);
                System.out.println();
                while (!(solution2.parent == null)) {
                    solution2 = solution2.parent;
                    System.out.println("Step: " + solution2.depth);
                    System.out.println("ACTION  " + solution2.operator);
                    System.out.println("STATE  " + solution2.occupiedCells);
                    System.out.println();
                }
            }
        }

        if (strategy.equals("AS1")) {
            Node2 root = new Node2(occupiedCells, null, 0, "", 0, 0, n, m,0);
            Object[] sol = solver.AS1(root);
            Node2 solution = (Node2) sol[0];
            int expandedNodes = (Integer) sol[1];
            if (solution == null)
                s= "no sol"; // ?
            else {
                s= solution.operator + ";" + solution.deathsSoFar + ";" + solution.retrievedBoxes + ";" + expandedNodes;
            }
            if(visualize == true) {
                Node2 solution2 = (Node2) sol[0];
                System.out.println("Step: " + solution2.depth);
                System.out.println("ACTION  " + solution2.operator);
                System.out.println("STATE  " + solution2.occupiedCells);
                System.out.println();
                while (!(solution2.parent == null)) {
                    solution2 = solution2.parent;
                    System.out.println("Step: " + solution2.depth);
                    System.out.println("ACTION  " + solution2.operator);
                    System.out.println("STATE  " + solution2.occupiedCells);
                    System.out.println();
                }
            }
        }

        if (strategy.equals("AS2")) {
            Node2 root = new Node2(occupiedCells, null, 0, "", 0, 0, n, m,0);
            Object[] sol = solver.AS2(root);
            Node2 solution = (Node2) sol[0];
            int expandedNodes = (Integer) sol[1];
            if (solution == null)
                s= "no sol"; // ?
            else {
                s= solution.operator + ";" + solution.deathsSoFar + ";" + solution.retrievedBoxes + ";" + expandedNodes;
            }
            if(visualize == true) {
                Node2 solution2 = (Node2) sol[0];
                System.out.println("Step: " + solution2.depth);
                System.out.println("ACTION  " + solution2.operator);
                System.out.println("STATE  " + solution2.occupiedCells);
                System.out.println();
                while (!(solution2.parent == null)) {
                    solution2 = solution2.parent;
                    System.out.println("Step: " + solution2.depth);
                    System.out.println("ACTION  " + solution2.operator);
                    System.out.println("STATE  " + solution2.occupiedCells);
                    System.out.println();
                }
            }
        }
        // Depth-first Search
//        if (strategy.equals(("DF"))) {
//            Node root = new Node(OccupiedCells, agent, null, 0, 0, "", 0, 0, m, n);
//            Object[] sol = solver.dfs(root);
//            Node solution = (Node) sol[0];
//            int expandedNodes = (Integer) sol[1];
//            if (solution == null)
//                return ""; // ?
//            else {
//                return solution.operator + ";" + solution.deathsSoFar + ";" + solution.retrievedBoxes + ";" + expandedNodes;
//            }
//        }
        return s.substring(1);
        //return s;

    }

    public static void main (String[] args){
        String grid0 = "5,6;50;0,1;0,4,3,3;1,1,90;";
        String grid2 = "2,2;50;0,1;1,0;1,1,40";
        String grid3 = "2,1;50;0,0;0,0;1,0,40";
        System.out.println(solve("7,5;100;3,4;2,6,3,5;0,0,4,0,1,8,1,4,77,1,5,1,3,2,94,4,3,46;", "BF", false));
    }
}
