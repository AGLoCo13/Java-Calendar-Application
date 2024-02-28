package com.ems.ems;

import java.time.LocalDateTime;
import java.time.LocalTime;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
/*
 * This code serves as the controller of the AddNewEvent.fxml 
 * and is used for the adding and the editing  of an event
 * Made by it21969
 */
public class AddNewEventController {
    @FXML private TextArea eventDescription;
    @FXML private DatePicker eventStartDate;
    @FXML private Button eventSubmitBtn;
    @FXML private TextField eventTitle;
    @FXML ComboBox<Integer> hourComboBox;
    @FXML ComboBox<Integer> minuteComboBox;

    private Controller mainController;
    private Stage stage;
    private boolean isEditMode = false;
    private CustomEvent eventToEdit;
    int editIndex = -1; //Field to store the index

    public void setEditMode(boolean isEditMode) {
        this.isEditMode = isEditMode;
    }

    public void setEventToEdit(CustomEvent event , int index) {
        this.eventToEdit = event;
        this.editIndex = index;
        if(event != null ) {
            populateFields();
        }
    }
    
    private void populateFields() {
        //Populate the form fields using eventToEdit data
        eventTitle.setText(eventToEdit.getTitle());
        eventDescription.setText(eventToEdit.getDescription());
        eventStartDate.setValue(eventToEdit.getStartDate().toLocalDate());

        //Extract the hour and minute from the start date
        LocalTime startTime = eventToEdit.getStartDate().toLocalTime();
        hourComboBox.setValue(startTime.getHour());
        minuteComboBox.setValue(startTime.getMinute());
    }
    
    public void setMainController(Controller mainController) {
        this.mainController = mainController;
    }

    

    public void setStage(Stage stage) {
        this.stage = stage;
    }
    

    @FXML
    public void initialize() {
        //Populate hour and minute ComboBoxes
        for (int i=0; i<24; i++) {
            hourComboBox.getItems().add(i);
        }
        for (int i=0; i<60; i++) {
            minuteComboBox.getItems().add(i);
        }

        //Set the default values
        hourComboBox.setValue(0);
        hourComboBox.setValue(0);
    }

    @FXML
    void handleEventSubmit(ActionEvent event) {

        //Get the selected time
        int hour = hourComboBox.getValue();
        int minute = minuteComboBox.getValue();
        LocalTime time = LocalTime.of(hour , minute);
        String title = eventTitle.getText();
        String Description = eventDescription.getText();
        LocalDateTime startDate = eventStartDate.getValue().atTime(time);
         if (isEditMode && eventToEdit != null) {
            //Create a new Event with updated Details
            CustomEvent updatedEvent = new CustomEvent(title, Description, startDate);
            //update the event in the main controller
            mainController.updateEventAtIndex(updatedEvent, editIndex);
         } else {
            //Create a new Event 
            //use main controller to add event to calendar and update TableView
            CustomEvent newEvent = new CustomEvent(title, Description, startDate);
            mainController.addEventToCalendar(newEvent);
         }

        closeWindow();
    }

    private void closeWindow() {
        if(stage != null) {
            stage.close();
            System.out.println("Closing Window");
        }else{
            System.out.println("Stage is null . Window not closed");
        }
        }
    

}
