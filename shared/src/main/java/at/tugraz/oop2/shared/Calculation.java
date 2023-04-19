package at.tugraz.oop2.shared;


public class Calculation {

    public static void updateComplexPLane(Set set){
        updatePlaneComplexWidth(set);
        updatePlaneComplexHeight(set);
    }
    public static void updatePlaneComplexWidth(Set set){

        double complex_w = Math.pow(2, 2 - set.getFractalRenderOptions().getZoom().get());
        set.getFractalRenderOptions().setW_complex_plane(complex_w);
    }

    public static void updatePlaneComplexHeight(Set set){
        double complex_h = (set.getFractalRenderOptions().getHeight().get() / (float)set.getFractalRenderOptions().getWidth().get()) *
                Math.pow(2, 2 - set.getFractalRenderOptions().getZoom().get());

        set.getFractalRenderOptions().setH_complex_plane(complex_h);
    }

    public static double transformX(Set set, int x){
        FractalRenderOptions set_options = set.getFractalRenderOptions();

        return x * (set_options.getW_complex_plane() / (set_options.getWidth().get() - 1)) -
                set_options.getW_complex_plane()  / 2 + set_options.getSetX().get();
    }

    public static double transformY(Set set, int y){
        FractalRenderOptions set_options = set.getFractalRenderOptions();

        return y * (set_options.getH_complex_plane() / (set_options.getHeight().get() - 1)) -
                set_options.getH_complex_plane()  / 2 + set_options.getSetY().get();
    }

    public static double calcAngle(double x, double y){
        return Math.atan2(y, x);
    }

    public static double calcMagnitude(double x, double y){
        double value = Math.pow(x, 2) + Math.pow(y, 2);
        return Math.sqrt(value);
    }

    public static double calcCos(double angle, double power){
        return Math.cos(angle * power);
    }

    public static double calcSin(double angle, double power){
        return Math.sin(angle * power);
    }

    public static double calcComplexX(Set set, double x, double y){
        double angle = calcAngle(x, y);
        return Math.pow(calcMagnitude(x, y), set.getFractalRenderOptions().getPower().get()) * calcCos(angle, set.getFractalRenderOptions().getPower().get());
    }

    public static double calcComplexY(Set set, double x, double y){
        double angle = calcAngle(x, y);
        return Math.pow(calcMagnitude(x, y), set.getFractalRenderOptions().getPower().get()) * calcSin(angle, set.getFractalRenderOptions().getPower().get());
    }

    private static final short[] RED_COLOR = {255, 0, 0};
    private static final short[] BLUE_COLOR = {0, 0, 255};

    public static short[] updateColor(int exit_iterations, int total_iterations){
        short data[] = new short[3];
        ColourModes mode = Mandelbrot.getInstance().getFractalRenderOptions().getMode().getValue();

        if (mode == ColourModes.BLACK_WHITE || (mode == ColourModes.COLOUR_FADE && exit_iterations == total_iterations + 1)){
            if (exit_iterations <= total_iterations){
                data[0] = 255;
                data[1] = 255;
                data[2] = 255;
            }
            else{ // exit_iterations == total_iterations + 1 || mode == ColourModes.COLOUR_FADE
                data[0] = 0;
                data[1] = 0;
                data[2] = 0;
            }
        }
        else if (mode == ColourModes.COLOUR_FADE){
            double t = exit_iterations / (double)total_iterations;
            data[0] = (short)(RED_COLOR[0] * t + BLUE_COLOR[0] * (1 - t));
            data[1] = (short)(RED_COLOR[1] * t + BLUE_COLOR[1] * (1 - t));
            data[2] = (short)(RED_COLOR[2] * t + BLUE_COLOR[2] * (1 - t));
        }
        return data;
    }
}