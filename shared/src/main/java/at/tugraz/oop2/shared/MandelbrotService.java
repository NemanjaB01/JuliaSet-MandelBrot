package at.tugraz.oop2.shared;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
public class MandelbrotService extends Service<Void> {

    @Override
    protected Task<Void> createTask() {
        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                int curr_iter = 1;
                double z_current_real = 0, z_current_imag = 0;
                int iterations = Mandelbrot.getInstance().getFractalRenderOptions().getIterations().get();

                for (int y = 0; y < Mandelbrot.getInstance().getFractalRenderOptions().getHeight().get(); y++){
                    for (int x = 0; x < Mandelbrot.getInstance().getFractalRenderOptions().getWidth().get(); x++){
                        double c_real = Calculation.transformX(Mandelbrot.getInstance(), x);
                        double c_imag = Calculation.transformY(Mandelbrot.getInstance(), y);

                        double z_prev_real = 0, z_prev_imag = 0;

                        while(curr_iter <= iterations){

                            z_current_real = Calculation.calcComplexX(Mandelbrot.getInstance(), z_prev_real, z_prev_imag) + c_real;
                            z_current_imag = Calculation.calcComplexY(Mandelbrot.getInstance(), z_prev_real, z_prev_imag) + c_imag;

                            if (Calculation.calcMagnitude(z_current_real, z_current_imag) >= (double)2){
                                break;
                            }
                            z_prev_real = z_current_real;
                            z_prev_imag = z_current_imag;
                            curr_iter++;
                        }

                        Mandelbrot.getInstance().getSimpleImage().setPixel(x, y, Calculation.updateColor(curr_iter, iterations));
                        curr_iter = 1;
                    }
                }
                return null;
            }
        };
    }
}