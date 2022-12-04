import java.util.HashMap;

public class OccupiedCells implements Cloneable {
    HashMap<String, Object> occupiedCells;

    public OccupiedCells (HashMap<String, Object> occupiedCells) {
        this.occupiedCells = occupiedCells;
    }

    public HashMap<String, Object> clone() {
        HashMap<String, Object> output = new HashMap<String, Object>();
        for (String key : this.occupiedCells.keySet()) {
            if (this.occupiedCells.get(key) instanceof Ship) {
                Ship ship = ((Ship) this.occupiedCells.get(key)).clone();
                output.put(key, ship);
            }
            else
                output.put(key, "Station");
        }
        return output;
    }
}
