package at.tugraz.oop2.shared;

import javafx.beans.binding.DoubleExpression;
import javafx.beans.property.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class JuliaRenderOptions extends FractalRenderOptions {

    public JuliaRenderOptions(int width, int height, double zoom, double power, int iterations, ColourModes mode, int fragmentNumber, int totalFragments, RenderMode renderMode,int taskPerWorker, String connection, double juliaX, double juliaY) {
        super(new SimpleIntegerProperty(width), new SimpleIntegerProperty(height), new SimpleDoubleProperty(zoom), new SimpleDoubleProperty(power), new SimpleIntegerProperty(iterations), FractalType.JULIA, new SimpleObjectProperty<ColourModes>(mode), 0, totalFragments, fragmentNumber,
                new SimpleObjectProperty<RenderMode>(renderMode),new SimpleIntegerProperty(taskPerWorker), new SimpleStringProperty(connection), 0, 0, new SimpleDoubleProperty(juliaX), new SimpleDoubleProperty(juliaY));
    }

    public JuliaRenderOptions(int width, int height, double zoom, double power, int iterations, ColourModes mode, RenderMode renderMode, int taskPerWorker, String connection, double juliaX, double juliaY) {
        this(width, height, zoom, power, iterations, mode, 0, 1, renderMode,taskPerWorker, connection,juliaX,juliaY);
    }
}
