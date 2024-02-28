package com.ems.ems;

import java.time.LocalDateTime;
import java.time.LocalTime;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
/*
 * This code serves as the controller of the AddNewTask.fxml 
 * and is used for the adding and the editing  of an event of type Task.
 * Written by it21969
 */
public class AddNewTaskController {
    @FXML private ChoiceBox<String> completionStatus;
    @FXML private Button submitTask;
    @FXML private DatePicker taskDeadline;
    @FXML private TextArea taskDescription;
    @FXML private TextField taskTitle;
    @FXML private DatePicker taskStartDate;
    @FXML ComboBox<Integer> taskHourComboBox;
    @FXML ComboBox<Integer> taskMinuteComboBox;
    @FXML ComboBox<Integer> endHourComboBox;
    @FXML ComboBox<Integer> endMinuteComboBox;

    private Controller mainController;
    private boolean isEditMode = false;
    private Task taskToEdit;
    ObservableList<String> displayCompletionStatus = FXCollections.observableArrayList("Completed" , "Not Completed");
    private Stage stage;


    int editIndex = -1; //Field to store the index

    public void setEditMode(boolean isEditMode) {
        this.isEditMode = isEditMode;
    }

    public void setTaskToEdit(Task task , int index) {
        this.taskToEdit = task;
        this.editIndex = index;
        if(task != null) {
            populateFields();
        }
    }

    private void populateFields() {
        //Populate form fields using taskToEdit Data


        taskTitle.setText(taskToEdit.getTitle());
        taskDescription.setText(taskToEdit.getDescription());


         // Check for null before using startDate
    if (taskToEdit.getStartDate() != null) {
        taskStartDate.setValue(taskToEdit.getStartDate().toLocalDate());
        LocalTime startTime = taskToEdit.getStartDate().toLocalTime();
        taskHourComboBox.setValue(startTime.getHour());
        taskMinuteComboBox.setValue(startTime.getMinute());
    } else {
        // Handle the case where startDate is null
        taskStartDate.setValue(null); // Or set a default value
        taskHourComboBox.setValue(0);
        taskMinuteComboBox.setValue(0);
    }

    // Check for null before using deadline
    if (taskToEdit.getDeadline() != null) {
        taskDeadline.setValue(taskToEdit.getDeadline().toLocalDate());
        LocalTime endTime = taskToEdit.getDeadline().toLocalTime();
        endHourComboBox.setValue(endTime.getHour());
        endMinuteComboBox.setValue(endTime.getMinute());
    } else {
        // Handle the case where deadline is null
        taskDeadline.setValue(null); // Or set a default value
        endHourComboBox.setValue(0);
        endMinuteComboBox.setValue(0);
    }

    completionStatus.setValue(taskToEdit.isCompleted() ? "Completed" : "Not Completed");

    }

    public void setMainController(Controller mainController){
        this.mainController = mainController;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
   

    @FXML
    private void initialize() {
        //Populate hour and minute ComboBoxes
        for (int i=0; i<24; i++) {
            taskHourComboBox.getItems().add(i);
            endHourComboBox.getItems().add(i);
        }
        for (int i=0; i<60; i++) {
            taskMinuteComboBox.getItems().add(i);
            endMinuteComboBox.getItems().add(i);
        }
        //Set some default values 
        taskHourComboBox.setValue(0);
        taskMinuteComboBox.setValue(0);
        endHourComboBox.setValue(0);
        endMinuteComboBox.setValue(0);

        //initializing the choice box of the completion status
        completionStatus.setValue("Not Completed");
        completionStatus.setItems(displayCompletionStatus);
    }
    



    @FXML
    void handleSubmitTask(ActionEvent event) {
         //Get the selected task start time 
        int hour = taskHourComboBox.getValue();
        int minute = taskMinuteComboBox.getValue();
        //get the selected end time
        int endHour= endHourComboBox.getValue();
        int endMinute = endMinuteComboBox.getValue();
        //initialize isCompleted value in order to have a completion status
        //of the task
        //Get the selected completion status as a String

        String selectedCompletionStatus = completionStatus.getValue();
        boolean isCompleted = "Completed".equals(selectedCompletionStatus);
        LocalTime endTime = LocalTime.of(endHour, endMinute);

        LocalTime time = LocalTime.of(hour, minute);
        //Get task title 
        String title = taskTitle.getText();
        //Get task Description 
        String Description = taskDescription.getText();
        // ...start Date
        LocalDateTime startDate = taskStartDate.getValue().atTime(time);

        //End Time (Deadline)
        LocalDateTime deadline = taskDeadline.getValue().atTime(endTime);
        //Comparing Start Date and Deadline
        if (deadline.isBefore(startDate)) {
            //Handle the case where deadline is before start Date
            Alert alert = new Alert(AlertType.ERROR) ;
            alert.setTitle("Invalid Deadline");
            alert.setHeaderText("Deadline Error");
            alert.setContentText("The deadline cannot be before start date");

            alert.showAndWait(); // Show the alert and wait for the user to close it
            return; // Do not proceed further

        }

        if (isEditMode && taskToEdit !=null) {
            //Create a new Task with Updated Details 
            Task updatedTask = new Task(title , Description , startDate , deadline , isCompleted);
            //Update the event in the main controller
            mainController.updateEventAtIndex(updatedTask, editIndex);
        } else {
            //Create a new Task
            Task newTask = new Task(title, Description, startDate, deadline, isCompleted);
            //use main controller to add event to calendar and update TableView
            mainController.addTaskToCalendar(newTask);
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
