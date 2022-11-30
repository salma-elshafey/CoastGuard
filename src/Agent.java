public class Agent implements Cloneable{
    int locX;
    int locY;
    int capacity;
    int currAvailableSeats;

    public Agent(int locX, int locY, int capacity){
        this.locX = locX;
        this.locY = locY;
        this.capacity = capacity;
        this.currAvailableSeats = capacity;
    }

    public Agent clone(){
        Agent out = new Agent(this.locX, this.locY, this.capacity);
        out.currAvailableSeats = this.currAvailableSeats;
        return out;
    }
}
