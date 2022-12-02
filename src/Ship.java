public class Ship implements Cloneable{
    boolean isWreck;

    public void setNumOfPassengers(int numOfPassengers) {
        this.numOfPassengers = numOfPassengers;
        if (this.numOfPassengers <= 0) {
            this.isWreck = true;
        }
    }

    int numOfPassengers;
    int locX;
    int locY;

    public void setBlackBox(BlackBox blackBox) {
        this.blackBox = blackBox;
    }

    BlackBox blackBox;

    public Ship(int locX, int locY, int numOfPassengers){
        this.locX = locX;
        this.locY = locY;
        this.isWreck = false;
        this.numOfPassengers = numOfPassengers;
        this.blackBox = new BlackBox();
    }

    public void expirePassenger(){
        if (numOfPassengers > 1)
            numOfPassengers--;
        else {
            isWreck = true;
            blackBox.damage = 1;
        }
    }

    // the coast guard boat can save several passengers at once, hence the integer parameter passed to the method
    public void savePassengers(int number){
        if (numOfPassengers > number){
            numOfPassengers -= number;
        }
        else {
            numOfPassengers = 0;
            isWreck = true;
            blackBox.damage = 1;
        }
    }

    public Ship clone(){
        Ship ship = new Ship(this.locX, this.locY, this.numOfPassengers);
        ship.isWreck = this.isWreck;
        ship.setBlackBox(new BlackBox(this.blackBox.getDamage(), this.blackBox.isPickedUp()));
        return ship;
    }

    public String toString() {
        return "Ship: numOfPassengers: " + numOfPassengers + ", isWreck: " + isWreck + ", BlackBox Damage: " + blackBox.damage + ", BlackBox is Picked Up: " + blackBox.isPickedUp();
    }

    public void pickUpBlackBox() {
        this.blackBox.setPickedUp(true);
    }

    public BlackBox getBlackBox() {
        return this.blackBox;
    }
}
