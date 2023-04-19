package at.tugraz.oop2.shared;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.Property;
import javafx.beans.property.StringProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public abstract class FractalRenderOptions implements Serializable {
    protected IntegerProperty width;
    protected IntegerProperty height;
    protected DoubleProperty zoom;
    protected DoubleProperty power;
    protected IntegerProperty iterations;
    protected FractalType type;
    protected Property<ColourModes> mode;
    protected long requestId;
    protected int totalFragments;
    protected int fragmentNumber;
    private Property<RenderMode> renderMode;

    protected IntegerProperty taskPerWorker;

    protected StringProperty connection;

    protected double w_complex_plane;
    protected double h_complex_plane;
    private DoubleProperty setX;
    private DoubleProperty setY;
}
