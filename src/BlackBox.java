public class BlackBox {


    int damage;
    boolean isPickedUp;
    boolean isRetrievable;

    public BlackBox(){
        damage = 0;
        isPickedUp = false;
        isRetrievable = true;
    }
    public BlackBox(int damage, boolean isPickedUp){
        this.damage = damage;
        this.isPickedUp = isPickedUp;
        isRetrievable = true;
    }

    public void increaseDamage() {
        if (this.damage < 100 && this.isRetrievable)
            this.damage++;
        else if (this.damage >= 100)
            this.isRetrievable = false;
    }

    public int getDamage() {
        return damage;
    }

    public void setPickedUp(boolean isPickedUp) {
        this.isPickedUp = isPickedUp;
        this.isRetrievable = false;
    }

    public boolean isPickedUp() {
        return isPickedUp;
    }
    public boolean isRetrievable() { return this.isRetrievable; }

}

