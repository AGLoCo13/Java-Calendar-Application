package com.ems.ems;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Timer;
import java.util.TimerTask;

import gr.hua.dit.oop2.calendar.TimeService;
import javafx.application.Platform;
import javafx.scene.control.Alert;
/**
 * This service is responsible for periodically checking the calendar for upcoming events
 * and tasks, and displaying reminders to the user.
 */
public class ReminderService {
    private CustomCalendar calendar;
    private final Timer timer = new Timer();
/**
     * Constructor for ReminderService.
     * @param calendar The calendar to monitor for reminders.
     */

public ReminderService(CustomCalendar calendar) {
    this.calendar = calendar;
}
 /**
     * Starts the periodic reminder checks.
     */
public void startReminderChecks() {
    TimerTask reminderTask = new TimerTask() {
        @Override
        public void run() {
            checkForUpcomingEvents();
        }
    };
    //Schedule the task to run every minute 
    timer.scheduleAtFixedRate(reminderTask, 0, 60 * 1000);
}
/**
     * Checks for any events or tasks occurring within the rest of the current day.
     */
private void checkForUpcomingEvents() {
    // Check if the calendar is set
    if (this.calendar == null) {
        //Calendar not set, skip check
        return;
    }
    LocalDateTime now = TimeService.getTeller().now();
    LocalDateTime endOfDay = LocalDateTime.of(now.toLocalDate() , LocalTime.MAX);

    for (CustomEvent event : calendar.getEvents()) {
        if (event instanceof Appointment) {
            Appointment appointment = (Appointment) event;
            if(appointment.getStartDate().isAfter(now) && appointment.getStartDate().isBefore(endOfDay)) {
                showReminder("You have an appointment:" + appointment.getTitle() + "at" + appointment.getStartDate());
            }
        }else if (event instanceof Task) {
            Task task = (Task) event;
            if(!task.isCompleted() && task.getDeadline().isAfter(now) && task.getDeadline().isBefore(endOfDay)){
                showReminder("You have a task deadline: " + task.getTitle() + " at " + task.getDeadline());
            }
         }
        }
       

    }
     /**
     * Displays a reminder alert with the given message.
     * @param message The message to display in the reminder.
     */
      private void showReminder(String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Reminder");
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }
    /**
     * Stops the reminder checks by cancelling the timer.
     */
    public void stop() {
        timer.cancel();
    }
    /**
     * Sets a new calendar for the reminder checks.
     * @param calendar The new calendar to be set.
     */
    public void setCalendar(CustomCalendar calendar) {
        this.calendar = calendar;
    }
    
}

