package at.tugraz.oop2.gui;

import at.tugraz.oop2.shared.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.PixelFormat;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import lombok.Getter;
import lombok.ToString;

import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.ByteArrayInputStream;
import java.time.temporal.JulianFields;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.ToDoubleFunction;


public class FractalApplication extends Application {

    private GridPane mainPane;
    private Canvas rightCanvas;
    private Canvas leftCanvas;
    private GridPane controlPane;
    @Getter
    private DoubleProperty leftHeight = new SimpleDoubleProperty();
    @Getter
    private DoubleProperty leftWidth = new SimpleDoubleProperty();
    @Getter
    private DoubleProperty rightHeight = new SimpleDoubleProperty();
    @Getter
    private DoubleProperty rightWidth = new SimpleDoubleProperty();
    MandelbrotService mandelbrotService = new MandelbrotService();
    JuliaService juliaService = new JuliaService();
    private TextField MandelbrotXTextField = new TextField();
    private TextField MandelbrotYTextField = new TextField();
    private TextField juliaXTextField = new TextField();
    private TextField juliaYTextField = new TextField();
    private TextField mandelbrotZoomTextField = new TextField();
    private TextField juliaZoomTextField = new TextField();


    private void updateSizes() {

        Bounds leftSize = mainPane.getCellBounds(0, 0);
        leftCanvas.widthProperty().set(leftSize.getWidth());
        leftCanvas.heightProperty().set(leftSize.getHeight());


        Bounds rightSize = mainPane.getCellBounds(1, 0);
        rightCanvas.widthProperty().set(rightSize.getWidth());
        rightCanvas.heightProperty().set(rightSize.getHeight());

        FractalLogger.logRenderCallGUI(Julia.getInstance().getFractalRenderOptions());
        FractalLogger.logRenderCallGUI(Mandelbrot.getInstance().getFractalRenderOptions());
        mandelbrotService.restart();
        juliaService.restart();
    }
    @Override
    public void start(Stage primaryStage) throws Exception {

        mandelbrotService.setOnSucceeded((workerStateEvent) -> {
            FractalLogger.logRenderFinishedGUI(FractalType.MANDELBROT, Mandelbrot.getInstance().getSimpleImage());
            var data = Mandelbrot.getInstance().getSimpleImage().getByteData();
            var format = PixelFormat.getByteRgbInstance();

            Platform.runLater(() -> {
                var p_writer = leftCanvas.getGraphicsContext2D().getPixelWriter();
                p_writer.setPixels(0, 0, Mandelbrot.getInstance().getSimpleImage().getWidth(),
                        Mandelbrot.getInstance().getSimpleImage().getHeight(), format, data, 0,
                        Mandelbrot.getInstance().getSimpleImage().getWidth() * 3);
                FractalLogger.logDrawDoneGUI(FractalType.MANDELBROT);
            });
        });
        juliaService.setOnSucceeded((workerStateEvent) -> {
            FractalLogger.logRenderFinishedGUI(FractalType.JULIA, Mandelbrot.getInstance().getSimpleImage());
            var data = Julia.getInstance().getSimpleImage().getByteData();
            var format = PixelFormat.getByteRgbInstance();

            Platform.runLater(() -> {
                var p_writer = rightCanvas.getGraphicsContext2D().getPixelWriter();
                p_writer.setPixels(0, 0, Julia.getInstance().getSimpleImage().getWidth(),
                        Julia.getInstance().getSimpleImage().getHeight(), format, data, 0,
                        Julia.getInstance().getSimpleImage().getWidth() * 3);
                FractalLogger.logDrawDoneGUI(FractalType.JULIA);
            });
        });

        Parameters parameters = getParameters();
        Map<String,String> param = parameters.getNamed();
        ArgsParser.getInstance().parse(param);

        FractalRenderOptions mOptions = Mandelbrot.getInstance().getFractalRenderOptions();
        FractalRenderOptions jOptions = Julia.getInstance().getFractalRenderOptions();
        FractalLogger.logArgumentsGUI(mOptions.getSetX(), mOptions.getSetY(), mOptions.getZoom(),
                mOptions.getPower(), mOptions.getIterations(), jOptions.getSetX(), jOptions.getSetY(), jOptions.getZoom(), jOptions.getMode());

        manageGuiListeners();

        mainPane = new GridPane();


        leftCanvas = new Canvas();
        leftCanvas.setCursor(Cursor.HAND);
        Mandelbrot.getInstance().getFractalRenderOptions().getHeight().bind(leftCanvas.heightProperty());
        Mandelbrot.getInstance().getFractalRenderOptions().getWidth().bind(leftCanvas.widthProperty());

        mainPane.setGridLinesVisible(true);
        mainPane.add(leftCanvas, 0, 0);

        rightCanvas = new Canvas();
        rightCanvas.setCursor(Cursor.HAND);
        Julia.getInstance().getFractalRenderOptions().getHeight().bind(rightCanvas.heightProperty());
        Julia.getInstance().getFractalRenderOptions().getWidth().bind(rightCanvas.widthProperty());


        mainPane.add(rightCanvas, 1, 0);

        ColumnConstraints cc1 =
                new ColumnConstraints(100, 100, -1, Priority.ALWAYS, HPos.CENTER, true);
        ColumnConstraints cc2 =
                new ColumnConstraints(100, 100, -1, Priority.ALWAYS, HPos.CENTER, true);
        ColumnConstraints cc3 =
                new ColumnConstraints(400, 400, 400, Priority.ALWAYS, HPos.CENTER, true);

        mainPane.getColumnConstraints().addAll(cc1, cc2, cc3);


        RowConstraints rc1 =
                new RowConstraints(400, 400, -1, Priority.ALWAYS, VPos.CENTER, true);

        mainPane.getRowConstraints().addAll(rc1);

        leftHeight.bind(leftCanvas.heightProperty());
        leftWidth.bind(leftCanvas.widthProperty());
        rightHeight.bind(rightCanvas.heightProperty());
        rightWidth.bind(rightCanvas.widthProperty());

        mainPane.widthProperty().addListener(observable -> updateSizes());
        mainPane.heightProperty().addListener(observable -> updateSizes());


        controlPane = new GridPane();
        ColumnConstraints controlLabelColConstraint =
                new ColumnConstraints(195, 195, 200, Priority.ALWAYS, HPos.CENTER, true);
        ColumnConstraints controlControlColConstraint =
                new ColumnConstraints(195, 195, 195, Priority.ALWAYS, HPos.CENTER, true);
        controlPane.getColumnConstraints().addAll(controlLabelColConstraint, controlControlColConstraint);
        mainPane.add(controlPane, 2, 0);

        manageControlPane();

        Scene scene = new Scene(mainPane);

        primaryStage.setTitle("Fractal Displayer");

        primaryStage.addEventHandler(WindowEvent.WINDOW_SHOWING, event -> {
            updateSizes();
        });
        primaryStage.addEventHandler(WindowEvent.WINDOW_SHOWN, event -> {
            updateSizes();
        });
        primaryStage.addEventHandler(WindowEvent.ANY, event -> {
            updateSizes();
        });

        primaryStage.setWidth(1080);
        primaryStage.setHeight(720);

        addZoomFeature();
        addPanningFeature();
        primaryStage.setScene(scene);
        primaryStage.show();

        Platform.runLater(() -> {
            updateSizes();
            FractalLogger.logInitializedGUI(mainPane, primaryStage, leftCanvas, rightCanvas);
            FractalLogger.logDistributionArgumentsGUI(Julia.getInstance().getFractalRenderOptions().getRenderMode(),
                    Julia.getInstance().getFractalRenderOptions().getTaskPerWorker(), Julia.getInstance().getFractalRenderOptions().getConnection());
        });
    }
    public void addZoomFeature(){

        leftCanvas.setOnScroll(new EventHandler<ScrollEvent>(){
            @Override
            public void handle(ScrollEvent scrollEvent) {
                double zoomFactor = 0.02;
                double deltaY = scrollEvent.getDeltaY();

                if (deltaY < 0){
                    zoomFactor *= -1;
                }
                Mandelbrot.getInstance().getFractalRenderOptions().getZoom().set(Mandelbrot.getInstance().getFractalRenderOptions().getZoom().get() + zoomFactor);
                FractalLogger.logZoomGUI(Mandelbrot.getInstance().getFractalRenderOptions().getZoom().get(), FractalType.MANDELBROT);
                Calculation.updateComplexPLane(Mandelbrot.getInstance());
                mandelbrotZoomTextField.setText(Double.toString(Mandelbrot.getInstance().getFractalRenderOptions().getZoom().get()));
                FractalLogger.logRenderCallGUI(Mandelbrot.getInstance().getFractalRenderOptions());
                mandelbrotService.restart();
            }
        });
        rightCanvas.setOnScroll(new EventHandler<ScrollEvent>(){
            @Override
            public void handle(ScrollEvent scrollEvent) {
                double zoomFactor = 0.02;
                double deltaY = scrollEvent.getDeltaY();

                if (deltaY < 0){
                    zoomFactor *= -1;
                }
                Julia.getInstance().getFractalRenderOptions().getZoom().set(Julia.getInstance().getFractalRenderOptions().getZoom().get() + zoomFactor);
                FractalLogger.logZoomGUI(Julia.getInstance().getFractalRenderOptions().getZoom().get(), FractalType.JULIA);
                Calculation.updateComplexPLane(Julia.getInstance());
                juliaZoomTextField.setText(Double.toString(Julia.getInstance().getFractalRenderOptions().getZoom().get() + zoomFactor));
                FractalLogger.logRenderCallGUI(Julia.getInstance().getFractalRenderOptions());
                juliaService.restart();
            }
        });
    }

    void addPanningFeature(){
        leftCanvas.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                double start_x = Calculation.transformX(Mandelbrot.getInstance(), (int)event.getSceneX());
                double start_y = Calculation.transformY(Mandelbrot.getInstance(), (int)event.getSceneY());
                leftCanvas.setOnMouseReleased(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent evt) {
                        double finish_x = Calculation.transformX(Mandelbrot.getInstance(), (int)evt.getSceneX());
                        double finish_y = Calculation.transformY(Mandelbrot.getInstance(), (int)evt.getSceneY());

                        panning(start_x, start_y, finish_x, finish_y, Mandelbrot.getInstance());
                        FractalLogger.logRenderCallGUI(Julia.getInstance().getFractalRenderOptions());
                        FractalLogger.logRenderCallGUI(Mandelbrot.getInstance().getFractalRenderOptions());
                        mandelbrotService.restart();
                        juliaService.restart();
                    }
                });
            }
        });
        rightCanvas.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                double start_x = Calculation.transformX(Julia.getInstance(), (int)event.getSceneX());
                double start_y = Calculation.transformY(Julia.getInstance(), (int)event.getSceneY());
                rightCanvas.setOnMouseReleased(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent evt) {
                        double finish_x = Calculation.transformX(Julia.getInstance(), (int)evt.getSceneX());
                        double finish_y = Calculation.transformY(Mandelbrot.getInstance(), (int)evt.getSceneY());

                        panning(start_x, start_y, finish_x, finish_y, Julia.getInstance());
                        FractalLogger.logRenderCallGUI(Julia.getInstance().getFractalRenderOptions());
                        juliaService.restart();
                    }
                });
            }
        });
    }
    public void manageGuiListeners(){
        Julia.getInstance().getConstantX().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                FractalLogger.logRenderCallGUI(Julia.getInstance().getFractalRenderOptions());
                juliaService.restart();
            }
        });
        Julia.getInstance().getConstantY().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                FractalLogger.logRenderCallGUI(Julia.getInstance().getFractalRenderOptions());
                juliaService.restart();
            }
        });
    }

    public void manageControlPane() {
        Label iterations = new Label("Iterations");
        TextField iterationsTextField = new TextField();
        controlPane.addRow(0, iterations, iterationsTextField);

        iterationsTextField.setText(Integer.toString(Mandelbrot.getInstance().getFractalRenderOptions().getIterations().get()));
        iterationsTextField.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean lostFocus, Boolean onFocus) {
                if (lostFocus) {
                    Mandelbrot.getInstance().getFractalRenderOptions().getIterations().set(ArgsParser.getInstance().checkStringInt(iterationsTextField.getText(), Mandelbrot.getInstance().getFractalRenderOptions().getIterations().get()));
                    iterationsTextField.setText(Integer.toString(Mandelbrot.getInstance().getFractalRenderOptions().getIterations().get()));
                    FractalLogger.logRenderCallGUI(Julia.getInstance().getFractalRenderOptions());
                    FractalLogger.logRenderCallGUI(Mandelbrot.getInstance().getFractalRenderOptions());
                    mandelbrotService.restart();
                    juliaService.restart();
                }
            }
        });

        Label powerLabel = new Label("Power");
        TextField powerTextField = new TextField();
        controlPane.addRow(1, powerLabel, powerTextField);
        powerTextField.setText(Double.toString(Mandelbrot.getInstance().getFractalRenderOptions().getPower().get()));

        powerTextField.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean lostFocus, Boolean onFocus) {
                if (lostFocus) {
                    Mandelbrot.getInstance().getFractalRenderOptions().getPower().set(ArgsParser.getInstance().checkStringDouble(powerTextField.getText(), Mandelbrot.getInstance().getFractalRenderOptions().getPower().get()));
                    powerTextField.setText(Double.toString(Mandelbrot.getInstance().getFractalRenderOptions().getPower().get()));
                    Julia.getInstance().getFractalRenderOptions().setPower(Mandelbrot.getInstance().getFractalRenderOptions().getPower());
                    FractalLogger.logRenderCallGUI(Julia.getInstance().getFractalRenderOptions());
                    FractalLogger.logRenderCallGUI(Mandelbrot.getInstance().getFractalRenderOptions());
                    mandelbrotService.restart();
                    juliaService.restart();
                }
            }
        });


        Label MandelbrotXLabel = new Label("Mandelbrot X");
        controlPane.addRow(2, MandelbrotXLabel, MandelbrotXTextField);

        MandelbrotXTextField.setText(Double.toString(Mandelbrot.getInstance().getFractalRenderOptions().getSetX().get()));

        MandelbrotXTextField.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean lostFocus, Boolean onFocus) {
                if (lostFocus) {
                    Mandelbrot.getInstance().getFractalRenderOptions().getSetX().set(ArgsParser.getInstance().checkStringDouble(MandelbrotXTextField.getText(), Mandelbrot.getInstance().getFractalRenderOptions().getSetX().get()));
                    MandelbrotXTextField.setText(Double.toString(Mandelbrot.getInstance().getFractalRenderOptions().getSetX().get()));
                    FractalLogger.logRenderCallGUI(Mandelbrot.getInstance().getFractalRenderOptions());
                    mandelbrotService.restart();
                }
            }
        });

        Label MandelbrotYLabel = new Label("Mandelbrot Y");
        controlPane.addRow(3, MandelbrotYLabel, MandelbrotYTextField);

        MandelbrotYTextField.setText(Double.toString(Mandelbrot.getInstance().getFractalRenderOptions().getSetY().get()));
        MandelbrotYTextField.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean lostFocus, Boolean onFocus) {
                if (lostFocus) {
                    Mandelbrot.getInstance().getFractalRenderOptions().getSetY().set(ArgsParser.getInstance().checkStringDouble(MandelbrotYTextField.getText(), Mandelbrot.getInstance().getFractalRenderOptions().getSetY().get()));
                    MandelbrotYTextField.setText(Double.toString(Mandelbrot.getInstance().getFractalRenderOptions().getSetY().get()));
                    FractalLogger.logRenderCallGUI(Mandelbrot.getInstance().getFractalRenderOptions());
                    mandelbrotService.restart();
                }
            }
        });

        Label mandelbrotZoomLabel = new Label("Mandelbrot Zoom");
        controlPane.addRow(4, mandelbrotZoomLabel, mandelbrotZoomTextField);

        mandelbrotZoomTextField.setText(Double.toString(Mandelbrot.getInstance().getFractalRenderOptions().getZoom().get()));
        mandelbrotZoomTextField.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean lostFocus, Boolean onFocus) {
                if (lostFocus) {
                    Mandelbrot.getInstance().getFractalRenderOptions().getZoom().set(ArgsParser.getInstance().checkStringDouble(mandelbrotZoomTextField.getText(), Mandelbrot.getInstance().getFractalRenderOptions().getZoom().get()));
                    mandelbrotZoomTextField.setText(Double.toString(Mandelbrot.getInstance().getFractalRenderOptions().getZoom().get()));
                    Calculation.updateComplexPLane(Mandelbrot.getInstance());
                    FractalLogger.logRenderCallGUI(Mandelbrot.getInstance().getFractalRenderOptions());
                    mandelbrotService.restart();
                }
            }
        });

        Label juliaXLabel = new Label("Julia X");
        controlPane.addRow(5, juliaXLabel, juliaXTextField);
        juliaXTextField.setText(Double.toString(Julia.getInstance().getFractalRenderOptions().getSetX().get()));
        juliaXTextField.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean lostFocus, Boolean onFocus) {
                if (lostFocus) {
                    Julia.getInstance().getFractalRenderOptions().getSetX().set(ArgsParser.getInstance().checkStringDouble(juliaXTextField.getText(), Julia.getInstance().getFractalRenderOptions().getSetX().get()));
                    juliaXTextField.setText(Double.toString(Julia.getInstance().getFractalRenderOptions().getSetX().get()));
                    FractalLogger.logRenderCallGUI(Julia.getInstance().getFractalRenderOptions());
                    juliaService.restart();
                }
            }
        });

        Label juliaYLabel = new Label("Julia Y");
        controlPane.addRow(6, juliaYLabel, juliaYTextField);
        juliaYTextField.setText(Double.toString(Julia.getInstance().getFractalRenderOptions().getSetY().get()));
        juliaYTextField.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean lostFocus, Boolean onFocus) {
                if (lostFocus) {
                    Julia.getInstance().getFractalRenderOptions().getSetY().set(ArgsParser.getInstance().checkStringDouble(juliaYTextField.getText(), Julia.getInstance().getFractalRenderOptions().getSetY().get()));
                    juliaYTextField.setText(Double.toString(Julia.getInstance().getFractalRenderOptions().getSetY().get()));
                    FractalLogger.logRenderCallGUI(Julia.getInstance().getFractalRenderOptions());
                    juliaService.restart();
                }
            }
        });

        Label juliaZoomLabel = new Label("Julia Zoom");
        controlPane.addRow(7, juliaZoomLabel, juliaZoomTextField);

        juliaZoomTextField.setText(Double.toString(Julia.getInstance().getFractalRenderOptions().getZoom().get()));
        juliaZoomTextField.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean lostFocus, Boolean onFocus) {
                if (lostFocus) {
                    Julia.getInstance().getFractalRenderOptions().getZoom().set(ArgsParser.getInstance().checkStringDouble(juliaZoomTextField.getText(), Julia.getInstance().getFractalRenderOptions().getZoom().get()));
                    juliaZoomTextField.setText(Double.toString(Julia.getInstance().getFractalRenderOptions().getZoom().get()));
                    Calculation.updateComplexPLane(Julia.getInstance());
                    FractalLogger.logRenderCallGUI(Julia.getInstance().getFractalRenderOptions());
                    juliaService.restart();
                }
            }
        });

        Label colourModeLabel = new Label("Colour mode");
        ComboBox<ColourModes> colourModesComboBox = new ComboBox<ColourModes>();
        colourModesComboBox.getItems().addAll(ColourModes.BLACK_WHITE, ColourModes.COLOUR_FADE);
        colourModesComboBox.getSelectionModel().select(ArgsParser.getInstance().checkColourMode(Julia.getInstance().getFractalRenderOptions().getMode().getValue().toString()));
        controlPane.addRow(8, colourModeLabel, colourModesComboBox);

        colourModesComboBox.valueProperty().addListener(new ChangeListener<ColourModes>() {
            @Override
            public void changed(ObservableValue<? extends ColourModes> observableValue, ColourModes newValue, ColourModes oldValue) {
                if (newValue == oldValue)
                    return;
                Julia.getInstance().getFractalRenderOptions().getMode().setValue(colourModesComboBox.getValue());
                Mandelbrot.getInstance().getFractalRenderOptions().setMode(Julia.getInstance().getFractalRenderOptions().getMode());
                FractalLogger.logRenderCallGUI(Julia.getInstance().getFractalRenderOptions());
                FractalLogger.logRenderCallGUI(Mandelbrot.getInstance().getFractalRenderOptions());
                juliaService.restart();
                mandelbrotService.restart();
            }
        });
    }

    public void panning(double startingPositionX, double startingPositonY, double finishPositionX, double finishPositionY, Set set)
    {
        double diff_x = finishPositionX - startingPositionX;
        double diff_y = finishPositionY - startingPositonY;

        double prevCenter_x = set.getFractalRenderOptions().getSetX().get();
        double prevCenter_y = set.getFractalRenderOptions().getSetY().get();

        double xFinal = prevCenter_x + diff_x;
        double yFinal = prevCenter_y + diff_y;

        if (set == Mandelbrot.getInstance()) {
            MandelbrotXTextField.setText(Double.toString(xFinal));
            MandelbrotYTextField.setText(Double.toString(yFinal));
        }
        else{
            juliaXTextField.setText(Double.toString(xFinal));
            juliaYTextField.setText(Double.toString(yFinal));
        }

        set.getFractalRenderOptions().getSetX().set(xFinal);
        set.getFractalRenderOptions().getSetY().set(yFinal);
        FractalLogger.logDragGUI(xFinal, yFinal, set.getFractalRenderOptions().getType());
    }
}