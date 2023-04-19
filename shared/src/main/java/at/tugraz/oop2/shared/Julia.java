package at.tugraz.oop2.shared;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

public class Julia extends Set{
    private static Julia julia;
    private DoubleProperty constantX;
    private DoubleProperty constantY;
    public static Julia getInstance(){
        if (julia == null)
            julia = new Julia();
        return julia;
    }
    public DoubleProperty getConstantX() {
        return constantX;
    }

    public void setConstants(){
        constantX = new SimpleDoubleProperty();
        constantY = new SimpleDoubleProperty();
    }

    public DoubleProperty getConstantY() {
        return constantY;
    }
}
