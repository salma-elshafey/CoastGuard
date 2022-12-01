public class BlackBox {


    int damage;
    boolean isPickedUp;

    public BlackBox(){
        damage = 0;
        isPickedUp = false;
    }
    public BlackBox(int damage, boolean isPickedUp){
        this.damage = damage;
        this.isPickedUp = isPickedUp;
    }

    public void setDamage(int damage) {
        this.damage = damage;
        if (this.damage >= 20)
            this.setPickedUp(true);
    }

    public int getDamage() {
        return damage;
    }

    public void setPickedUp(boolean isPickedUp) {
        this.isPickedUp = isPickedUp;
    }

    public boolean isPickedUp() {
        return isPickedUp;
    }

}

