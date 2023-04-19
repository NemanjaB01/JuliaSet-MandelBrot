package at.tugraz.oop2.shared;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class MandelbrotRenderOptions extends FractalRenderOptions {


    public MandelbrotRenderOptions(int width, int height, double zoom, double power, int iterations, ColourModes mode, int fragmentNumber, int totalFragments, RenderMode renderMode,int taskPerWorker, String connection, double mandelBrotX, double mandelBrotY) {
        super(new SimpleIntegerProperty(width), new SimpleIntegerProperty(height), new SimpleDoubleProperty(zoom), new SimpleDoubleProperty(power), new SimpleIntegerProperty(iterations), FractalType.MANDELBROT, new SimpleObjectProperty<ColourModes>(mode), 0, totalFragments, fragmentNumber,
                new SimpleObjectProperty<RenderMode>(renderMode),new SimpleIntegerProperty(taskPerWorker), new SimpleStringProperty(connection), 0, 0, new SimpleDoubleProperty(mandelBrotX), new SimpleDoubleProperty(mandelBrotY));
    }

    public MandelbrotRenderOptions(int width, int height, double zoom, double power, int iterations, ColourModes mode, RenderMode renderMode,int taskPerWorker, String connection, double mandelBrotX, double mandelBrotY) {
        this(width, height, zoom, power, iterations, mode, 0, 1, renderMode,taskPerWorker, connection, mandelBrotX, mandelBrotY);
    }

}
