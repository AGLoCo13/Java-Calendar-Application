package com.ems.ems;

import java.time.Duration;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.collections.ObservableList;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

import biweekly.ICalendar;
import javafx.util.Callback;
/*
 * This is the Main Controller of the GUI. 
 * Written by it219103
 */
public class Controller {
    @FXML private TableColumn<Task, LocalDateTime> deadlineColumn;
    @FXML private TableColumn<CustomEvent, String> descriptionColumn;
    @FXML private TableColumn<Appointment, Duration> durationColumn;
    @FXML private TableView<CustomEvent> eventsTableView;
    @FXML private MenuItem importCalendarMenuItem;
    @FXML private TableColumn<Task, Boolean> completedColumn;
    @FXML private TableColumn<CustomEvent, LocalDateTime> startDateColumn;
    @FXML private TableColumn<CustomEvent, String> titleColumn;
    @FXML private ChoiceBox<String> displayEventsBox;
    @FXML private Button AddEventButton;
    @FXML private Button editEventButton;
    @FXML private MenuItem saveButton;
    @FXML private MenuItem closeBtn;
    // List to hold and display events in the TableView
    private ObservableList<CustomEvent> eventList = FXCollections.observableArrayList();
    private ReminderService reminderService;
    //Call of EventManagerImplementation
    private EventManagerImpl eventManager = new EventManagerImpl();
     
    // List for filtering display options in the ChoiceBox
    ObservableList<String> displayEventsList = FXCollections.observableArrayList("All" , "End of day" , "End of week" , "End of month" ,
     "Past day" , "Past week" , "Past month" , "To dos" , "Due tasks");

     /*
      * Method to set the EventManagerImpl instance
      */

     public void setEventManager(EventManagerImpl eventManager) {
        this.eventManager = eventManager;
     } 
    /**
     * Method invoked when the "Add New Event" button is clicked.
     * Opens a new window for event type selection.
     */
   @FXML
   private void showEventTypeSelection() {
    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ems/SelectEventType.fxml"));
        Parent root = loader.load();

        EventTypeSelectionController controller = loader.getController();
        Stage stage = new Stage();
        controller.setStage(stage);
        controller.setMainController(this); // Pass the reference

        stage.setScene(new Scene(root));
        stage.setTitle("Select Event Type:");
        stage.show();
    }catch (IOException e) {
        e.printStackTrace();
    }
    }

    /**
     * Initialization method for the main window of the application.
     * Sets up TableView columns, choice boxes, and loads initial events.
     */
    @FXML
    private void initialize() {
        titleColumn.setCellValueFactory(cellData -> cellData.getValue().titleProperty());
        descriptionColumn.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());
        
        startDateColumn.setCellValueFactory(cellData -> {
            CustomEvent customEvent = cellData.getValue();
            LocalDateTime startDate = null;
        
            if (customEvent instanceof Appointment) {
                startDate = ((Appointment) customEvent).getStartDate();
            } else if (customEvent instanceof Task) {
                startDate = ((Task) customEvent).getStartDate();
            }
            return new SimpleObjectProperty<>(startDate);
        });

        durationColumn.setCellValueFactory(cellData -> {
            if (cellData.getValue() instanceof Appointment) {
                return new SimpleObjectProperty<Duration>(((Appointment) cellData.getValue()).getDuration());
            }
            return null;
        });
    
        // Custom cell factory
        durationColumn.setCellFactory(new Callback<TableColumn<Appointment, Duration>, TableCell<Appointment, Duration>>() {
            @Override
            public TableCell<Appointment, Duration> call(TableColumn<Appointment, Duration> param) {
                return new TableCell<Appointment, Duration>() {
                    @Override
                    protected void updateItem(Duration item, boolean empty) {
                        super.updateItem(item, empty);
    
                        if (empty || item == null) {
                            setText(null);
                        } else {
                            setText(formatDuration(item));
                        }
                    }
                };
            }
        });

        deadlineColumn.setCellValueFactory(cellData -> {
            if (cellData.getValue() instanceof Task) {
                return new SimpleObjectProperty<>(((Task) cellData.getValue()).getDeadline());
            }
            return null;
        });

        completedColumn.setCellValueFactory(cellData -> {
            if (cellData.getValue() instanceof Task) {
                return new SimpleBooleanProperty(((Task) cellData.getValue()).isCompleted());
            }
            return null;
        });

       displayEventsBox.setValue("All");
       displayEventsBox.setItems(displayEventsList);
        
       //Handle Choice Box selection changes
       displayEventsBox.getSelectionModel().selectedItemProperty().addListener((observable , oldValue , newValue) -> {
        handleDisplayEventsChoiceChanged((String) newValue);
       });

       /* Enable Selection in tableview */
       eventsTableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

       loadInitialEvents();

        // Initialization logic
        reminderService = new ReminderService(eventManager.getCalendar());
        reminderService.startReminderChecks();
    }

    private void loadInitialEvents() {
        List<CustomEvent> initialEvents = eventManager.getInitialEvents(); // Implement this method
        eventList.addAll(initialEvents);
        updateTableView(eventList);
    }

    //This method is called when the user selects a display option from the choiceBox.
    private void handleDisplayEventsChoiceChanged(String selectedOption) {
        List<CustomEvent> filteredEvents = eventManager.getFilteredEvents(selectedOption);
        updateTableView(filteredEvents);
    }

    // Method to load ical file when pressing import Calendar(*ical)
    @FXML
    void handleLoadEvents() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Events File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Calendar Files", "*.ics", "*.txt", "*.csv"));
        // Show open file dialog
        Stage stage = (Stage) importCalendarMenuItem.getParentPopup().getOwnerWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);

        // Handle the selected file (e.g., read events from the file)
        if (selectedFile != null) {
            // Call a method to handle the selected file
            CustomCalendar loadedCalendar = BiweeklyOperations.processIcalFile(selectedFile.getAbsolutePath());
            // CustomCalendar has a method to get the list of events
            List<CustomEvent> loadedEvents = loadedCalendar.getEvents();
            // Having a method to update the TableView with the loaded events
            eventList.clear();
            // Clear the current eventList and add all loaded events
            eventList.addAll(loadedEvents);

            //Update the TableView
            updateTableView(loadedEvents);

            //Update EventManagerImpl with the loaded calendar
            eventManager.setCalendar(loadedCalendar);

            //Update ReminderService with the new calendar
            reminderService.setCalendar(loadedCalendar);
        }

    }

    // Method to update the TableView with loaded events
    private void updateTableView(List<CustomEvent> events) {
        // Sort the events by their start time
        events.sort(Comparator.comparing(event -> {
            LocalDateTime startDate = event.getStartDate();
            return startDate != null ? startDate : null;
        }, Comparator.nullsLast(Comparator.naturalOrder())));
        // Clear existing items
        eventsTableView.getItems().clear();

        // Add the loaded events to the TableView
        eventsTableView.getItems().addAll(events);
    }

    //Method to add the new event to the CustomCalendar and update TableView
    public void addEventToCalendar(CustomEvent event)  {
        eventManager.getCalendar().addEvent(event);
        eventList.add(event);
        updateTableView(eventManager.getCalendar().getEvents());
        
        //Update the calendar in ReminderService
        reminderService.setCalendar(eventManager.getCalendar());

    }

    public void addAppointmentToCalendar(Appointment appointment) {
        eventManager.getCalendar().addEvent(appointment);
        updateTableView(eventManager.getCalendar().getEvents());
    }

    public void addTaskToCalendar(Task task) {
        eventManager.getCalendar().addEvent(task);
        updateTableView(eventManager.getCalendar().getEvents());
    }

    private String formatDuration(Duration duration) {
        long hours = duration.toHours();
        int minutes = duration.toMinutesPart();
        return hours + " (Hrs)" + minutes + " (Mins)";
    }
    //Method for handling the selected events from the Tableview.
    @FXML
    private void handleEditEvent() {
        int selectedIndex = eventsTableView.getSelectionModel().getSelectedIndex();
        CustomEvent selectedEvent = eventsTableView.getSelectionModel().getSelectedItem();
        if(selectedIndex >= 0 ) {
            openEditDialog(selectedEvent, selectedIndex);
        } else {
            //Show alert or log that no event is selected
        }
    
    }
    private void openEditDialog(CustomEvent event, int index) {
        try {
            String fxmlFile = "/com/ems/AddNewEvent.fxml";

            Stage stage = new Stage();
            if (event instanceof Appointment) {
                fxmlFile = "/com/ems/AddNewAppointment.fxml";
            } else if (event instanceof Task) {
                fxmlFile = "/com/ems/AddNewTask.fxml";
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();

            if (event instanceof Appointment) {
                AddNewAppointmentController controller = loader.getController();
                controller.setStage(stage);
                controller.setMainController(this);
                controller.setEditMode(true);
                controller.setAppointmentToEdit((Appointment) event, index);
            } else if (event instanceof Task) {
                AddNewTaskController controller = loader.getController();
                controller.setStage(stage);
                controller.setMainController(this);
                controller.setEditMode(true);
                controller.setTaskToEdit((Task) event, index);
            } else {
                AddNewEventController controller = loader.getController();
                controller.setStage(stage);
                controller.setEventToEdit(event, index);
            }
            stage.setScene(new Scene(root));
            stage.setTitle("Edit Event");
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    

    public void updateEventAtIndex(CustomEvent updatedEvent , int index) {
        if (index >= 0 && index < eventList.size()) {
            // Replace the old event with a new instance containing updated information
            CustomEvent oldEvent = eventList.get(index);
    
            if (oldEvent instanceof Appointment && updatedEvent instanceof Appointment) {
                Appointment updatedAppointment = new Appointment(
                    updatedEvent.getTitle(),
                    updatedEvent.getDescription(),
                    updatedEvent.getStartDate(),
                    ((Appointment) updatedEvent).getDuration()
                );
                // Replace in the eventList
                eventList.set(index, updatedAppointment);
                // Replace in the CustomCalendar
                eventManager.getCalendar().replaceEventAtIndex(updatedEvent, index);
            } else if (oldEvent instanceof Task && updatedEvent instanceof Task) {
                Task updatedTask = new Task(
                    updatedEvent.getTitle(),
                    updatedEvent.getDescription(),
                    updatedEvent.getStartDate(),
                    ((Task) updatedEvent).getDeadline(),
                    ((Task) updatedEvent).isCompleted()
                );
                //Replace in the eventList
                eventList.set(index, updatedTask);
                //Replace in the CustomCalendar
                eventManager.getCalendar().replaceEventAtIndex(updatedEvent, index);
            } else {
                // For other types of events, handle accordingly
                eventList.set(index, updatedEvent);
                eventManager.getCalendar().replaceEventAtIndex(updatedEvent, index);
            }
                 // Update the calendar in ReminderService
        reminderService.setCalendar(eventManager.getCalendar());
            updateTableView(eventList);
            System.out.println("Event updated at index:" + index);
        } else {
            System.out.println("Invalid index for updating event:" + index);
        }
    }

    

    @FXML
    private void handleSaveCalendar() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Calendar");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("iCalendar Files (*.ics)", "*.ics"));
       
        //Use any control to get the current stage. 
        Stage currenStage = (Stage) eventsTableView.getScene().getWindow();

        File file = fileChooser.showSaveDialog(currenStage);

        if (file != null ) {
            saveCalendarToFile(file);
        }
    }

    private void saveCalendarToFile(File file) {
        try {
            // Get the current CustomCalendar instance from eventManager
        CustomCalendar currentCalendar = eventManager.getCalendar();

        // Serialize the current calendar's events to an ICalendar object
        ICalendar iCalendar = BiweeklyOperations.serializeCalendarToICalendar(currentCalendar);

        // Write the ICalendar object to the chosen file
        BiweeklyOperations.writeIcalendarToIcsFile(file.getAbsolutePath(), iCalendar);

        System.out.println("Calendar saved successfully to " + file.getAbsolutePath());

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error saving calendar: " + e.getMessage());
        }
        }

        @FXML
        private void handleCloseCalendar() {
            //Clear the event list
            eventList.clear();
            updateTableView(eventList);

            //Reset or clear other states as needed.
            //Call of resetCalendar from EventManagerImpl
            eventManager.resetCalendar();

            //Provide feedback 
            System.out.println("Calendar closed.");
        }
    }
    
