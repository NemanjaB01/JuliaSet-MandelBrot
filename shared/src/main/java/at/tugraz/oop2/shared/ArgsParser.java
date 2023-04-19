package at.tugraz.oop2.shared;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.canvas.Canvas;

import java.util.Map;
import java.util.TreeMap;

public class ArgsParser{
    private static ArgsParser argsParser;
    public static ArgsParser getInstance(){
        if(argsParser == null)
            argsParser = new ArgsParser();
        return  argsParser;
    }
    public int checkStringInt(String string, int default_value){
        int value;
        try{
            value = Integer.parseInt(string);
        }catch (NumberFormatException ex){
            value = default_value;
        }
        return  value;
    }


    public double checkStringDouble(String string, double default_value){
        double value;
        try{
            value = Double.parseDouble(string);
        }catch (NumberFormatException ex){
            value = default_value;
        }
        return  value;
    }
    public ColourModes checkColourMode(String string){
        ColourModes colour;
        if(string.equals("COLOUR_FADE"))
            colour = ColourModes.COLOUR_FADE;
        else
            colour = ColourModes.BLACK_WHITE;

        return colour;
    }


    RenderMode checkRenderMode(String string){
        RenderMode renderMode;
        if(string.equals("LOCAL"))
            renderMode = RenderMode.LOCAL;
        else
            renderMode = RenderMode.DISTRIBUTED;

        return  renderMode;
    }
    public void parse(Map<String,String> parameters){
        Map<String,String> new_parameters = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        new_parameters.putAll(parameters);

        int iteratations = 128;
        double power = 2.0;
        double mandelBrotX = 0.0;
        double mandelBrotY = 0.0;
        double mandelBrotZoom = 0.0;
        double juliaX = 0.0;
        double juliaY = 0.0;
        double juliaZoom = 0.0;
        ColourModes colourMode = ColourModes.BLACK_WHITE;
        int taskPerForWorker = 5;
        RenderMode renderMode = RenderMode.LOCAL;
        String connection = "localhost:8010";

        if (new_parameters.get("iterations") != null)
            iteratations = checkStringInt(new_parameters.get("iterations"),128);
        if (new_parameters.get("power") != null)
            power = checkStringDouble(new_parameters.get("power"),2.0);
        if (new_parameters.get("mandelbrotX") != null)
            mandelBrotX = checkStringDouble(new_parameters.get("mandelbrotX"),0.0);
        if (new_parameters.get("mandelbrotY") != null)
            mandelBrotY = checkStringDouble(new_parameters.get("mandelbrotY"),0.0);
        if (new_parameters.get("mandelbrotZoom") != null)
            mandelBrotZoom = checkStringDouble(new_parameters.get("mandelbrotZoom"),0.0);
        if (new_parameters.get("juliaX") != null)
            juliaX = checkStringDouble( new_parameters.get("juliaX"),0.0);
        if (new_parameters.get("juliaY") != null)
            checkStringDouble(new_parameters.get("juliaY"),0.0);
        if (new_parameters.get("juliaZoom") != null)
            juliaZoom = checkStringDouble(new_parameters.get("juliaZoom"),0.0);
        if (new_parameters.get("colourMode") != null)
            colourMode = checkColourMode(new_parameters.get("colourMode"));
        if(new_parameters.get("tasksPerWorker") != null)
            taskPerForWorker = checkStringInt(new_parameters.get("tasksPerWorker"), 5);
        if(new_parameters.get("renderMode") != null)
            renderMode = checkRenderMode(new_parameters.get("renderMode"));

        
        JuliaRenderOptions juliarOptions = new JuliaRenderOptions(0,
                0,juliaZoom, power,iteratations,colourMode,renderMode,taskPerForWorker, connection,juliaX,juliaY);
        Julia.getInstance().setRenderingOptions(juliarOptions);
        Calculation.updatePlaneComplexWidth(Julia.getInstance());
        Calculation.updatePlaneComplexHeight(Julia.getInstance());

        MandelbrotRenderOptions mandelrOptions = new MandelbrotRenderOptions(0,0
                ,mandelBrotZoom,power,iteratations,colourMode,renderMode,taskPerForWorker, connection,mandelBrotX,mandelBrotY);
        Mandelbrot.getInstance().setRenderingOptions(mandelrOptions);

        Julia.getInstance().setConstants();
        Julia.getInstance().getConstantX().bind(Mandelbrot.getInstance().getFractalRenderOptions().getSetX());
        Julia.getInstance().getConstantY().bind(Mandelbrot.getInstance().getFractalRenderOptions().getSetY());
        Julia.getInstance().getFractalRenderOptions().getIterations().bindBidirectional(Mandelbrot.getInstance().getFractalRenderOptions().getIterations());

        Mandelbrot.getInstance().getFractalRenderOptions().getHeight().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                changeSizeDependencies(Mandelbrot.getInstance());
            }
        });
        Mandelbrot.getInstance().getFractalRenderOptions().getWidth().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                changeSizeDependencies(Mandelbrot.getInstance());
            }
        });

        Julia.getInstance().getFractalRenderOptions().getHeight().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                changeSizeDependencies(Julia.getInstance());
            }
        });

        Julia.getInstance().getFractalRenderOptions().getWidth().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                changeSizeDependencies(Julia.getInstance());
            }
        });
    }

    public void changeSizeDependencies(Set set){
        Calculation.updatePlaneComplexWidth(set);
        Calculation.updatePlaneComplexHeight(set);
        set.updateImage();
    }
}
