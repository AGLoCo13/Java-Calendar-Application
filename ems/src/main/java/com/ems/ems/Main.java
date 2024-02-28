package com.ems.ems;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
 /**
 * Main class for the JavaFX application.
 * This class extends Application and serves as the entry point for your JavaFX application.
 */
public class Main extends Application {
   /**
     * Start method for the application.
     * This is the main entry point for all JavaFX applications. 
     * The method is called after the application class is launched.
     *
     * @param primaryStage The primary stage for this application, onto which 
     *                     the application scene can be set.
     */

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/com/ems/MainView.fxml"));
        primaryStage.setTitle("MyCalendar");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }
    /**
     * Main method.
     * This is the method from where the JavaFX application is launched.
     *
     * @param args Command line arguments passed to the application.
     */

    public static void main(String[] args) {
        launch(args);
    }
}
