package com.ems.ems;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import biweekly.component.VEvent;
import biweekly.component.VTodo;
import gr.hua.dit.oop2.calendar.*;

//Written by it21587 and it219103
public class EventManagerImpl implements EventManager {
    private CustomCalendar calendar;
    //Method to get CustomCalendar object
    public CustomCalendar getCalendar() {
        return this.calendar;
    }

    // Constructor
    public EventManagerImpl(CustomCalendar calendar, TimeService timeService) {
        //Initialize the event with the provided calendar and time service
        this.calendar = calendar;
    }
    //Default constructor
    public EventManagerImpl() {
		// TODO Auto-generated constructor stub
	}

    public void resetCalendar() {
        //Initialize the calendar to a new empty CustomCalendar.
        this.calendar = new CustomCalendar();
        
    }

    public void setCalendar(CustomCalendar newCalendar) {
        this.calendar = newCalendar;
    }

	// Method to get filtered events based on the option
    public List<CustomEvent> getFilteredEvents(String option) {
        switch (option) {
            case "All" :
                return getAllEvents();
            case "End of day":
                return getEventsUntilEndOfDay();
            case "End of week":
                return getEventsUntilEndOfWeek();
            case "End of month":
                return getEventsUntilEndOfMonth();
            case "Past day":
                return getPastEventsUntilBeginningOfDay();
            case "Past week":
                return getPastEventsUntilBeginningOfWeek();
            case "Past month":
                return getPastEventsUntilBeginningOfMonth();
            case "To dos":
                return getIncompleteTasks();
            case "Due tasks":
                return getOverdueTasks();
            default:
                // Handle invalid option
                return List.of();
        }
    }

   // Method to get the initial list of events for the calendar
   public List<CustomEvent> getInitialEvents() {
    if (calendar != null) {
        return calendar.getEvents();
    }
    return new ArrayList<>(); // Return an empty list if calendar is null
}


 // Implement methods update events . For the second question
    
    public void updateCalendar(String icalFilename , Scanner scanner) {
        List<VEvent> newEvents = new ArrayList<>();
        List<VTodo> newTasks = new ArrayList<>();
        //Check if the specified ICS file exists 
        boolean fileExists = Files.exists(Paths.get(icalFilename));
        if (fileExists) {
            // If the file exists, load existing events
            loadExistingEvents(icalFilename);
        }

    // Ask the user to input new events
    System.out.println("Enter new events. Type 'exit' to finish.");
    String userInput="";
    do {
        System.out.println("Choose the type of event (1: Event, 2: Appointment, 3: Task):");
        int eventType = scanner.nextInt();
        scanner.nextLine(); // Consume the newline character

        VEvent newEvent=null;
        VEvent newAppointment=null;
        VTodo newTask=null;

        switch (eventType) {
            case 1:
                newEvent = BiweeklyOperations.createEventFromUserInput(scanner);
                newEvents.add(newEvent);
                break;
            case 2:
                newAppointment = BiweeklyOperations.createAppointmentFromUserInput(scanner);
                newEvents.add(newAppointment);
                break;
            case 3:
                newTask = BiweeklyOperations.createTaskFromUserInput(scanner);
                newTasks.add(newTask);
                break;
            default:
                System.out.println("Invalid event type. Please choose 1, 2, or 3.");
                continue;
        }
        
        //Ask if the user wants to input more events
        System.out.println("Do you want to add more events? (yes/no)");
        userInput = scanner.nextLine().toLowerCase();
    }while (!userInput.equals("no"));

    //Save the new Events and tasks to the ICS file
    for (VEvent event : newEvents) {
        BiweeklyOperations.writeEventToIcsFile(icalFilename , event); 
    }

    for (VTodo task : newTasks) {
        BiweeklyOperations.writeToDoToIcsFile(icalFilename, task);
    }
    
}

    //Helper method to load Existing Event
    private void loadExistingEvents(String icalFilename) {
        try{
            //Read the existing calendar from the ICS file
            CustomCalendar existingCalendar = BiweeklyOperations.processIcalFile(icalFilename);
            //Set the loaded calendar as the current calendar
            this.calendar = existingCalendar;
        }catch (Exception e) {
            System.err.println("Error reading file:" + e.getMessage());
        } 
    }

    //------------ Helper methods for displayEvents based on different options ------------//
    //Written by it219103
    //Method to get All Events in the Calendar
    private List<CustomEvent> getAllEvents(){
        return calendar.getEvents();
    }

    //Method to get all events until the End of this day
    private List<CustomEvent> getEventsUntilEndOfDay() {
    // Use the loaded events directly
    //Use of TimeService from org.hua.dit.oop2 Calendar
    LocalDateTime now = TimeService.getTeller().now();
    LocalDateTime startOfDay = now.toLocalDate().atStartOfDay();
    LocalDateTime endOfDay = now.withHour(23).withMinute(59).withSecond(59);
    return calendar.getEventsBetween(startOfDay, endOfDay);
    }
    
    //Method to get all events until the end of the week
    private List<CustomEvent> getEventsUntilEndOfWeek() {
     // Use the loaded events directly
    //Use of TimeService from org.hua.dit.oop2 Calendar
    LocalDateTime now = TimeService.getTeller().now();
    LocalDateTime startOfWeek = now.with(DayOfWeek.MONDAY).truncatedTo(ChronoUnit.DAYS);
    LocalDateTime endOfWeek = startOfWeek.plusDays(6).with(LocalTime.MAX); //Calculate end of the week (Sunday)
    return calendar.getEventsBetween(startOfWeek, endOfWeek);

        
    }
    
    //Method to get all events until the of this month
    private  List<CustomEvent> getEventsUntilEndOfMonth(){
        LocalDateTime now = TimeService.getTeller().now();
        LocalDateTime endOfMonth = now.with(TemporalAdjusters.lastDayOfMonth()).with(LocalTime.MAX);
        return calendar.getEventsBetween(now, endOfMonth);
        
    }
    //Method to get all events until the beginning of the day
    private List<CustomEvent> getPastEventsUntilBeginningOfDay() {
        LocalDateTime now = TimeService.getTeller().now();
        LocalDateTime startOfPreviousDay = now.minusDays(1).truncatedTo(ChronoUnit.DAYS);
        LocalDateTime endOfPreviousDay = startOfPreviousDay.plusDays(1).minusNanos(1);
        return calendar.getEventsBetween(startOfPreviousDay, endOfPreviousDay);
    }
    //Method to get all past events until the beginning of the week 
    private List<CustomEvent> getPastEventsUntilBeginningOfWeek() {
        LocalDateTime now = TimeService.getTeller().now();
        // Find the start of the current week
        LocalDateTime startOfCurrentWeek = now.with(DayOfWeek.MONDAY).truncatedTo(ChronoUnit.DAYS);
        // Calculate the start of the previous week
        LocalDateTime startOfPreviousWeek = startOfCurrentWeek.minusWeeks(1);
        return calendar.getEventsBetween(startOfPreviousWeek , now);
        
    }
    //Method to all past events until the beginning of the month
    private List<CustomEvent> getPastEventsUntilBeginningOfMonth() {
        LocalDateTime now = TimeService.getTeller().now();
         // Calculate the start of the previous month
        LocalDateTime startOfPreviousMonth = now.minusMonths(1).withDayOfMonth(1).with(LocalTime.MIN);
        return calendar.getEventsBetween(startOfPreviousMonth, now);
        
    }
    //Method to get incomplete tasks
    private List<CustomEvent> getIncompleteTasks() {
       return calendar.getIncompleteTasks();
    }
    //Method to get overdue tasks
    private List<CustomEvent> getOverdueTasks(){
         return calendar.getOverdueTasks();
    }

}
    

    
