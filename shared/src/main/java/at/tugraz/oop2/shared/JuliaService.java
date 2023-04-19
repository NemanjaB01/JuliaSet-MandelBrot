package at.tugraz.oop2.shared;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class JuliaService extends Service<Void> {

    @Override
    protected Task<Void> createTask() {
        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                int curr_iter = 1;
                double z_current_real = 0, z_current_imag = 0;
                short data[] = new short[3];
                int iterations = Julia.getInstance().getFractalRenderOptions().getIterations().get();

                for (int y = 0; y < Julia.getInstance().getFractalRenderOptions().getHeight().get(); y++){
                    for (int x = 0; x < Julia.getInstance().getFractalRenderOptions().getWidth().get(); x++){
                        double c_real = Julia.getInstance().getConstantX().get();
                        double c_imag = Julia.getInstance().getConstantY().get();

                        double z_prev_real = Calculation.transformX(Julia.getInstance(), x), z_prev_imag = Calculation.transformY(Julia.getInstance(), y);

                        while(curr_iter <= iterations){

                            z_current_real = Calculation.calcComplexX(Julia.getInstance(), z_prev_real, z_prev_imag) + c_real;
                            z_current_imag = Calculation.calcComplexY(Julia.getInstance(), z_prev_real, z_prev_imag) + c_imag;

                            if (Calculation.calcMagnitude(z_current_real, z_current_imag) >= (double)2){
                                break;
                            }
                            z_prev_real = z_current_real;
                            z_prev_imag = z_current_imag;
                            curr_iter++;
                        }

                        Julia.getInstance().getSimpleImage().setPixel(x, y, Calculation.updateColor(curr_iter, iterations));
                        curr_iter = 1;
                    }
                }
                return null;
            }
        };
    }
}