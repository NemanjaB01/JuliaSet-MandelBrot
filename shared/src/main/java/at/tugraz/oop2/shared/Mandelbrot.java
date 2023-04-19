package at.tugraz.oop2.shared;

public class Mandelbrot extends Set{
    private static Mandelbrot mandelbrot;
    public static Mandelbrot getInstance(){
        if (mandelbrot == null)
            mandelbrot = new Mandelbrot();
        return mandelbrot;
    }
}
