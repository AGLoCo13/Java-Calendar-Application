package com.ems.ems;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import gr.hua.dit.oop2.calendar.TimeService;

public class CustomCalendar {
    /**
     *Custom Calendar Class is implemented to create a Calendar which is actually a List of Events
     * (Gave the name custom calendar so that there is no jam with the Biweekly Calendar's Classes and methods)
     * Written by it21587 and it219103
     */
    private List<CustomEvent> events = new ArrayList<CustomEvent>();

    // Method to change the elements of an existing event
    public void changeEventDetails(CustomEvent event, String newTitle, String newDescription, LocalDateTime newDate) {
        // Check if the event is in the calendar
        if (events.contains(event)) {
            // Update the event details based on the type
            if (event instanceof Appointment) {
                Appointment appointment = (Appointment) event;
                appointment.setTitle(newTitle);
                appointment.setDescription(newDescription);
                appointment.setStartDate(newDate);
            } else if (event instanceof Task) {
                Task task = (Task) event;
                task.setTitle(newTitle);
                task.setDescription(newDescription);
                // For tasks, updating the deadline may have additional considerations
                task.setDeadline(newDate);
            } else {
                throw new IllegalArgumentException("Unsupported event type: " + event.getClass().getSimpleName());
            }
        } else {
            throw new IllegalArgumentException("Event not found in the calendar.");
        }
    }

    // Method to change the state of a task
    public void changeTaskState(Task task, boolean completed) {
        // Check if the task is in the calendar
        if (events.contains(task) && task instanceof Task) {
            Task existingTask = (Task) task;
            existingTask.setCompleted(completed);
        } else {
            throw new IllegalArgumentException("Task not found in the calendar.");
        }
    }

    public void addEvent(CustomEvent event) {
        events.add(event);
    }

    public List<CustomEvent> getEvents() {
        return events;
    }

    public List<CustomEvent> getEventsUntil(final LocalDateTime dateTime) {
        System.out.println("Filtering events until: " + dateTime);
    return events.stream()
            .filter(event -> event.getStartDate().isBefore(dateTime))
            .collect(Collectors.toList());
    }

    // Method to get past events until a specific date and time
    public List<CustomEvent> getPastEventsUntil(final LocalDateTime dateTime) {
        return events.stream()
                .filter(new Predicate<CustomEvent>() {
					public boolean test(CustomEvent event) {
						return event.getStartDate().isBefore(dateTime);
					}
				})
                .collect(Collectors.toList());
    }

    //Method to get an event between a specific startDate and a specific endDate
    public List<CustomEvent> getEventsBetween(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return events.stream()
                .filter(event -> isEventWithinRange(event, startDateTime, endDateTime))
                .collect(Collectors.toList());
    }
   //Returns true if and only if an event is between a range of a start date time and an endDateTime
    private boolean isEventWithinRange(CustomEvent event, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        LocalDateTime eventDateTime = event.getStartDate();
        if (eventDateTime == null) {
            return false;
        }
        return !eventDateTime.isBefore(startDateTime) && !eventDateTime.isAfter(endDateTime);
    }

    // Method to get incomplete tasks
    public List<CustomEvent> getIncompleteTasks() {
        //Get the time that is now with the Calendar library
        LocalDateTime now = TimeService.getTeller().now();
        //Return events that are instances of a Task with specific filter
        //e.g if the task is completed and the due date of the task has passed the current time
        return events.stream()
        .filter(event -> event instanceof Task)
        .map(event -> (Task) event)
        .filter(task -> !task.isCompleted() && (task.getDeadline() == null || task.getDeadline().isAfter(now)))
        .collect(Collectors.toList());
    }

    // Method to get overdue tasks
    public List<CustomEvent> getOverdueTasks() {
        LocalDateTime now = TimeService.getTeller().now();
        return events.stream()
        .filter(event -> event instanceof Task)
        .map(event -> (Task) event)
        .filter(task -> !task.isCompleted() && task.getDeadline().isBefore(now))
        .collect(Collectors.toList());
    }

    // Additional method to remove an event
    public void removeEvent(CustomEvent event) {
        events.remove(event);
    }

    // Additional method to check if the calendar has a specific event
    public boolean containsEvent(CustomEvent event) {
        return events.contains(event);
    }

    // Additional method to clear all events from the calendar
    public void clearCalendar() {
        events.clear();
    }

    public void addEvents(List<CustomEvent> newEvents) {
    }

    public void replaceEventAtIndex(CustomEvent updatedEvent , int index) {
        if (index >= 0 && index < events.size()) {
            events.set(index , updatedEvent);
        }else {
            throw new IllegalArgumentException("Invalid index:" + index);
        }
    }
}