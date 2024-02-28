package com.ems.ems;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * Controller for handling the event type selection in the GUI.
 * This class manages the selection between different types of events (Task, Appointment, Event)
 * and opens the corresponding form for each type.
 * Written by it21969
 */
public class EventTypeSelectionController {

    private Controller mainController;
    //Method to accept and store a reference to the main Controller
    public void setMainController(Controller mainController) {
        this.mainController = mainController;
    }
    private Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
    }
    @FXML
    private RadioButton TaskRadioBtn;

    @FXML
    private RadioButton appointmentRadioBtn;

    @FXML
    private RadioButton eventRadioBtn;

    @FXML
    private AnchorPane selectEventType;

    @FXML
    private Button submitEventType;

    @FXML
    void handleEventTypeSelection(ActionEvent event) {
        try {
            String fxmlFile = "";
            if (TaskRadioBtn.isSelected()) {
                fxmlFile = "/com/ems/AddNewTask.fxml";
            } else if (appointmentRadioBtn.isSelected()) {
                fxmlFile = "/com/ems/AddNewAppointment.fxml";
            } else if (eventRadioBtn.isSelected()) {
                fxmlFile = "/com/ems/AddNewEvent.fxml";
            }

            if (!fxmlFile.isEmpty()) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
                Parent root = loader.load();

                Stage newStage = new Stage(); // New stage for the new window
                newStage.setScene(new Scene(root));

                if (TaskRadioBtn.isSelected()) {
                    AddNewTaskController taskController = loader.getController();
                    taskController.setMainController(mainController);
                    taskController.setStage(newStage);
                    newStage.setTitle("Add a New Task");
                } else if (appointmentRadioBtn.isSelected()) {
                    AddNewAppointmentController appointmentController = loader.getController();
                    appointmentController.setMainController(mainController);
                    appointmentController.setStage(newStage);
                    newStage.setTitle("Add a New Appointment");
                } else {
                    AddNewEventController eventController = loader.getController();
                    eventController.setMainController(mainController);
                    eventController.setStage(newStage);
                    newStage.setTitle("Add a New Event");
                }

                newStage.show();

                // Close the current stage
                if (this.stage != null) {
                    this.stage.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}