package com.ems.ems;

import java.time.LocalDateTime;
import java.time.LocalTime;

import java.time.Duration;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
/* 
 * This code serves as the controller of the AddNewAppointment.fxml 
 * and is used for the adding and the editing  of an event of type Appointment
 * Made by it21969
 */
public class AddNewAppointmentController {
    @FXML private TextArea appointmentDescription;
    @FXML private TextField appointmentDuration;
    @FXML private DatePicker appointmentStartDate;
    @FXML private TextField appointmentTitle;
    @FXML private Button submitAppointment;
    @FXML ComboBox<Integer> hourComboBox;
    @FXML ComboBox<Integer> minuteComboBox;
    @FXML ComboBox<Integer> durHourComboBox;
    @FXML ComboBox<Integer> durMinuteComboBox;

    private Controller mainController;
    private Stage stage;
    private boolean isEditMode = false;
    private Appointment appointmentToEdit;
    int editIndex = -1; //New field to store the index

    public void setEditMode(boolean isEditMode) {
        this.isEditMode = isEditMode;
    }

    public void setAppointmentToEdit(Appointment appointment , int index) {
        this.appointmentToEdit = appointment;
        this.editIndex = index; //set the index
        if (appointment != null) {
            populateFields();
        }
    }

    private void populateFields() {
        //Populate form fields using eventToEdit data
        appointmentTitle.setText(appointmentToEdit.getTitle());
        appointmentDescription.setText(appointmentToEdit.getDescription());
        appointmentStartDate.setValue(appointmentToEdit.getStartDate().toLocalDate());
        
        //Extract the hour and minute from the start date 
        LocalTime startTime = appointmentToEdit.getStartDate().toLocalTime();
        hourComboBox.setValue(startTime.getHour());
        minuteComboBox.setValue(startTime.getMinute());

        //Duration Handling
        Duration duration = appointmentToEdit.getDuration();
        long hours = duration.toHours();
        int minutes = duration.toMinutesPart();
        durHourComboBox.setValue((int) hours);
        durMinuteComboBox.setValue(minutes);

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
            durHourComboBox.getItems().add(i);
        }
        for (int i=0; i<60; i++) {
            minuteComboBox.getItems().add(i);
            durMinuteComboBox.getItems().add(i);
        }

        //Set the default values
        hourComboBox.setValue(0);
        hourComboBox.setValue(0);
        durHourComboBox.setValue(0);
        durMinuteComboBox.setValue(0);
    }

    

    @FXML
    void handleSubmitAppointment(ActionEvent event) {
        //Get the selected appointment start time 
        int hour = hourComboBox.getValue();
        int minute = minuteComboBox.getValue();
        //Get the selected appointment duration time
        int durationHour = durHourComboBox.getValue();
        int durationMinute = durMinuteComboBox.getValue();
        LocalTime time = LocalTime.of(hour, minute);
        //Get appointment title 
        String title = appointmentTitle.getText();
        //Get appointment Description 
        String Description = appointmentDescription.getText();
        // ...start Date
        LocalDateTime startDate = appointmentStartDate.getValue().atTime(time);
        //..Duration 
        Duration duration = Duration.ofHours(durationHour).plus(Duration.ofMinutes(durationMinute));
       
        if (isEditMode && appointmentToEdit != null ) {
            //Create a new Appointment with updated Details
            Appointment updatedAppointment = new Appointment(title, Description, startDate, duration);
            //Update the event in the main controller
            mainController.updateEventAtIndex(updatedAppointment, editIndex);
        } else {
            //Create the new Appointment 
            //Use main controller to add appointment to the calendar and update the tableview
            Appointment newAppointment = new Appointment(title, Description, startDate, duration);
            mainController.addAppointmentToCalendar(newAppointment);
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
