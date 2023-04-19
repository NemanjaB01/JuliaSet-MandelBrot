package at.tugraz.oop2.shared;

import javafx.beans.property.*;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

public class FractalLogger {

  private static Logger _logger;

  private static Logger logger() {
    if (_logger == null) {
      _logger = Logger.getLogger("FRACTALLOGGER");
      BasicConfigurator.configure();
    }
    return _logger;
  }

  /**
   * Log when either pane gets dragged
   *
   * @param x    the fractal space x coordinate
   * @param y    the fractal space y coordinate
   * @param type which pane was dragged
   */
  public static void logDragGUI(double x, double y, FractalType type) {
    logger().debug("Dragged view to" + x + "/" + y + ", type:" + type);
  }

  /**
   * When a zoom event occured
   *
   * @param zoom the new zoom value
   * @param type which pane was dragged
   */
  public static void logZoomGUI(double zoom, FractalType type) {
    logger().debug("Zoomed view to" + zoom + ", type:" + type);
  }

  /**
   * Log when a render call was issued
   *
   * @param options
   */
  public static void logRenderCallGUI(FractalRenderOptions options) {
    logger().debug("Render call issued for type " + options.type);
  }

  /**
   * Log once the whole render (before drawing to screen) is finished
   *
   * @param type
   * @param image
   */
  public static void logRenderFinishedGUI(FractalType type, SimpleImage image) {
    short[] center_colour = new short[image.getDepth()];
    try {
      center_colour = image.getPixel(image.getWidth() / 2, image.getHeight() / 2);
    } catch (Exception e) {
      e.printStackTrace();
    }
    logger().debug("Finished render: " + type + " center colour: rgb(" + center_colour[0] + "," + center_colour[1] + ","
            + center_colour[2] + ")");
  }

  /**
   * Log when the drawing to screen is finished
   *
   * @param type
   */
  public static void logDrawDoneGUI(FractalType type) {
    logger().debug("Draw call issued:  " + type);
  }

  /**
   * Log the initial, but still dynamic properties
   *
   * @param mandelbrotX
   * @param mandelbrotY
   * @param mandelbrotZoom
   * @param power
   * @param iteration
   * @param juliaX
   * @param juliaY
   * @param juliaZoom
   * @param mode
   */
  public static void logArgumentsGUI(DoubleProperty mandelbrotX, DoubleProperty mandelbrotY,
                                     DoubleProperty mandelbrotZoom, DoubleProperty power, IntegerProperty iteration,
                                     DoubleProperty juliaX, DoubleProperty juliaY, DoubleProperty juliaZoom, Property<ColourModes> mode) {

    logger().debug("Set up arguments: mandelbrotX: " + mandelbrotX.get() + ", mandelbrotY: " + mandelbrotY.get()
            + ", mandelbrotZoom: " + mandelbrotZoom.get()
            + ", power: " + power.get() + ", juliaX: " + juliaX.get() + ", juliaY: " + juliaY.get() + ", juliaZoom: "
            + juliaZoom.get() + ", iterations: " + iteration.get() + ", colourMode: " + mode.getValue());
    mandelbrotX.addListener(observable -> {
      logger().debug("Changed mandelbrotX");
    });
    mandelbrotY.addListener(observable -> {
      logger().debug("Changed mandelbrotY");
    });
    mandelbrotZoom.addListener(observable -> {
      logger().debug("Changed mandelbrotZoom");
    });
    power.addListener(observable -> {
      logger().debug("Changed power");
    });

    juliaX.addListener(observable -> {
      logger().debug("Changed juliaX");
    });
    juliaY.addListener(observable -> {
      logger().debug("Changed juliaY");
    });
    juliaZoom.addListener(observable -> {
      logger().debug("Changed juliaZoom");
    });

    iteration.addListener(observable -> {
      logger().debug("Changed iterations");
    });
    mode.addListener(observable -> {
      logger().debug("Changed colourMode");
    });

  }

  /**
   * Log once the GUI is finished initializing (already done for you)
   *
   * @param mainPane
   * @param primaryStage
   * @param leftCanvas
   * @param rightCanvas
   */
  public static void logInitializedGUI(Pane mainPane, Stage primaryStage, Canvas leftCanvas, Canvas rightCanvas) {
    logger().debug("Initialized UI");
  }

  /**
   * Log the new reactive properties (like above)
   *
   * @param mode
   * @param tasksPerWorker
   * @param connection     doesn't have to be reactive, just pass the correct
   *                       value
   */
  public static void logDistributionArgumentsGUI(Property<RenderMode> mode, IntegerProperty tasksPerWorker,
                                                 StringProperty connection) {
    logger().debug(
            "Logged distribution arguments: mode: " + mode.getValue() + ", tasksPerWorker: " + tasksPerWorker.getValue() +
                    ", connection:" + connection.getValue());
    mode.addListener(observable -> {
      logger().debug("Changed RenderMode");
    });
    tasksPerWorker.addListener(observable -> {
      logger().debug("Changed tasksPerWorker");
    });
  }

  /**
   * Call when the worker (server) starts
   *
   * @param port
   */
  public static void logStartWorker(int port) {
    logger().debug("Started worker on port " + port);
  }

  /**
   * Log after successfully establishing a connection to the worker
   *
   * @param targetAddress
   * @param targetPort
   */
  public static void logConnectionOpenedGUI(String targetAddress, int targetPort) {
    logger().debug("GUI connected to " + targetAddress + ":" + targetPort);
  }

  /**
   * Log after disconnecting from worker
   *
   * @param targetAddress
   * @param targetPort
   */
  public static void logConnectionLostGUI(String targetAddress, int targetPort) {
    logger().debug("GUI lost connection to " + targetAddress + ":" + targetPort);

  }

  /**
   * Log if the worker accepted a new connection
   */
  public static void logConnectionOpenedWorker() {
    logger().debug("Worker got a new connection!");

  }

  /**
   * Log after disconnecting from the GUI
   */
  public static void logConnectionLostWorker() {
    logger().debug("Worker lost a connection...");
  }

  /**
   * Log upon sending or re-issuing a package
   *
   * @param options the options to render
   * @param index   the index of the package within this render call
   * @param total   the total number of packages this render call will dispatch
   */
  public static void logSendPackageGUI(FractalRenderOptions options, int index, int total) {
    logger().debug(
            String.format("Sending a single package of type %s to the worker (%d/%d)", options.getType(), index, total));

  }

  /**
   * Log upon receiving a work package in the worker from the GUI
   *
   * @param options
   */
  public static void logReceivePackageWorker(FractalRenderOptions options) {
    logger().debug(String.format("Worker got a single package of type %s", options.getType()));
  }

  /**
   * Log upon sending a work package in the worker to the GUI
   *
   * @param options
   */
  public static void logSendingPackageWorker(FractalRenderOptions options) {
    logger().debug(String.format("Worker got a single package of t ype %s", options.getType()));
  }

  /**
   * Log upon receiving a finished work package in the GUI
   *
   * @param options
   */
  public static void logReceivePackageGUI(FractalRenderOptions options) {
    logger().debug(String.format("GUI got a finished package of type %s", options.getType()));
  }

  /**
   * Log upon a failed package (connection lost during rendering) in the GUI
   *
   * @param options
   */
  public static void logFailedPackageGUI(FractalRenderOptions options) {
    logger()
            .debug(String.format("GUI got a single failed package of type back (should re-issue) %s", options.getType()));
  }

}

