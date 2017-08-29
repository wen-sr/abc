
public class Points {
    private double x;
    private double y;
    private int index = 0;
    public double getX() {
        return x;
    }
    public void setX(double x) {
        this.x = x;
    }
    public double getY() {
        return y;
    }
    public void setY(double y) {
        this.y = y;
    }
    public int getIndex() {
        return index;
    }
    public void setIndex(int index) {
        this.index = index;
    }
    public Points(double x, double y, int index){
        this.x=x;
        this.y=y;
        this.index=index;
    }

}
