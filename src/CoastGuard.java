public class CoastGuard {

    public static String GenGrid(){
        String s = "";
        String ships = "";
        String stations = "";
        // Create grid with random dimensions
        int n = (int) (Math.random()*(15-5+1)+5);
        int m = (int) (Math.random()*(15-5+1)+5);
        Object[][] grid = new Object[n][m];

        // Create agent with random location and add to grid
        int locX = (int) (Math.random()*((n-1)-0+1)+0);
        int locY = (int) (Math.random()*((m-1)-0+1)+0);
        int capacity = (int) (Math.random()*(100-30+1)+30);
        Agent agent = new Agent(locX ,locY, capacity);
        grid[agent.locX][agent.locY] = agent;

        // Create ship with random location and add to grid cell if it's empty
        locX = (int) (Math.random()*((n-1)+1));
        locY = (int) (Math.random()*((m-1)+1));
        int numOfPassengers = (int) (Math.random()*(100+1));
        while (grid[locX][locY] != null){
            locX = (int) (Math.random()*((n-1)+1));
            locY = (int) (Math.random()*((m-1)+1));
        }
        grid[locX][locY] = new Ship(locX, locY, numOfPassengers);
        ships += locX + "," + locY + "," + ((Ship)grid[locX][locY]).numOfPassengers;

        // Create station with random location and add to grid cell if it's empty
        locX = (int) (Math.random()*((n-1)+1));
        locY = (int) (Math.random()*((m-1)+1));
        while (grid[locX][locY] != null){
            locX = (int) (Math.random()*((n-1)+1));
            locY = (int) (Math.random()*((m-1)+1));
        }
        grid[locX][locY] = new Station(locX, locY);
        stations += locX + "," + locY;

        int numOfShips = 1;
        int numOfStations = 1;

        // Fill in other cells randomly with either ships, stations or nothing
        for (int i = 0; i < n; i++){
            for (int j = 0; j < m; j++){
                int whichObject = (int) (Math.random()*(3));
                if (whichObject == 0){
                    // empty cell
                }
                else if (whichObject == 1){
                    // add ship
                    if (grid[i][j] == null) {
                        grid[i][j] = new Ship(i, j, (int) (Math.random()*(100+1)));
                        ships += "," + i + "," + j + "," + ((Ship) grid[i][j]).numOfPassengers;
                    }
                }
                else {
                    // add station
                    if (grid[i][j] == null) {
                        grid[i][j] = new Station(i, j);
                        stations += "," + i + "," + j;
                    }
                }
            }
        }
        s += n + "," + m + ";" + agent.capacity + ";" + agent.locX + "," + agent.locY + ";" + stations + ";" + ships + ";";
        return s;
    }

    // Solve(grid,strategy, visualize)
    public static String solve(String gr, String strategy, boolean visualize){
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
        Object[][] grid = new Object[m][n];
        // the agent
        Agent agent = new Agent(locX, locY, Integer.parseInt(splits[1]));
        // stations
        String[] stationsSplits = splits[3].split(",");
        for (int i = 0; i < stationsSplits.length-1; i+=2){
            locX = Integer.parseInt(""+stationsSplits[i]);
            locY = Integer.parseInt(""+stationsSplits[i+1]);
            grid[locX][locY] = new Station(locX, locY);
        }
        String[] shipSplits = splits[4].split(",");
        for (int i = 0; i < shipSplits.length-2; i+=3){
            locX = Integer.parseInt(""+shipSplits[i]);
            locY = Integer.parseInt(""+shipSplits[i+1]);
            int passengers = Integer.parseInt(""+shipSplits[i+2]);
            grid[locX][locY] = new Ship(locX, locY, passengers);
        }
        SearchProblem solver = new SearchProblem();
        // Breadth-first Search
        if (strategy.equals("BF")) {
            Node root = new Node(grid, agent, null, 0, 0, "", 0, 0);
            Object[] sol = solver.bfs(root);
            Node solution = (Node) sol[0];
            int expandedNodes = (Integer) sol[1];
            if (solution == null)
                return ""; // ?
            else {
                return solution.operator + ";" + solution.deathsSoFar + ";" + solution.retrievedBoxes + ";" + expandedNodes;
            }
        }

        // Depth-first Search
        if (strategy.equals(("DF"))) {
            Node root = new Node(grid, agent, null, 0, 0, "", 0, 0);
            Object[] sol = solver.dfs(root);
            Node solution = (Node) sol[0];
            int expandedNodes = (Integer) sol[1];
            if (solution == null)
                return ""; // ?
            else {
                return solution.operator + ";" + solution.deathsSoFar + ";" + solution.retrievedBoxes + ";" + expandedNodes;
            }
        }
        return s;
    }

    public static void main (String[] args){
        String grid0 = "5,6;50;0,1;0,4,3,3;1,1,90;";
        System.out.println(solve(grid0, "BF", false));
    }
}
