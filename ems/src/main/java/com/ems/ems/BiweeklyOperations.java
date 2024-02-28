package com.ems.ems;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import biweekly.Biweekly;
import biweekly.ICalendar;
import biweekly.component.VEvent;
import biweekly.component.VTodo;
import biweekly.property.DateDue;
import biweekly.property.DateEnd;
import biweekly.property.Status;
import gr.hua.dit.oop2.calendar.TimeService;
/**
 * This class handles operations related to iCalendar using the Biweekly library.
 * It includes methods to serialize custom calendar events to iCalendar format,
 * process iCalendar files, and various helper methods for converting between custom events
 * and iCalendar components.
 * Written by it21587
 */
public class BiweeklyOperations {

    //Helper method to convert Calendar objects to ical objects (VEvents , VTodos)
    public static ICalendar serializeCalendarToICalendar (CustomCalendar customCalendar) {
        ICalendar iCalendar = new ICalendar();
        for (CustomEvent event : customCalendar.getEvents()) {
            if (event instanceof Appointment) {
                iCalendar.addEvent(convertAppointmentToVEvent((Appointment) event));
            }else if (event instanceof Task) {
                iCalendar.addTodo(convertTaskToVtodo((Task) event));
            } else {
                iCalendar.addEvent(convertCustomEventToVEVent(event));
            }
        }
        return iCalendar;
    } 

    // Helper methods to convert your custom event types to biweekly components
    // convertAppointmentToVEvent, convertTaskToVTodo, convertCustomEventToVEvent

    //Convert an Appointment to a VEvent
    private static VEvent convertAppointmentToVEvent(Appointment appointment) {
        VEvent vEvent = new VEvent();

        //Set Summary and Description
        vEvent.setSummary(appointment.getTitle());
        vEvent.setDescription(appointment.getDescription());

        //Set the start date/time
        LocalDateTime startDate = appointment.getStartDate();
        if (appointment.getStartDate() != null) {
            Date start = Date.from(appointment.getStartDate().atZone(ZoneId.systemDefault()).toInstant());
            vEvent.setDateStart(start);
        }

        //Calculate and set the end date/time based on the duration
        Duration duration = appointment.getDuration();
        if(startDate != null && duration !=null) {
            LocalDateTime endDate = startDate.plus(duration);
            Date end = Date.from(endDate.atZone(ZoneId.systemDefault()).toInstant());
            vEvent.setDateEnd(end);
        }
        return vEvent;
    }

    //Convert a task to a VTodo
    private static VTodo convertTaskToVtodo(Task task) {
        VTodo vTodo = new VTodo();
        vTodo.setSummary(task.getTitle());
        vTodo.setDescription(task.getDescription());

        if(task.getDeadline() != null) {
            DateDue dateDue = new DateDue(Date.from(task.getDeadline().atZone(ZoneId.systemDefault()).toInstant()));
            vTodo.setDateDue(dateDue);
        }

        Status status = task.isCompleted() ? Status.completed() : Status.needsAction();
        vTodo.setStatus(status);

        return vTodo;
    }

    //Convert a CustomEvent to a VEvent.
    private static VEvent convertCustomEventToVEVent(CustomEvent event) {
        VEvent vEvent = new VEvent();
        vEvent.setSummary(event.getTitle());
        vEvent.setDescription(event.getDescription());

        if(event.getStartDate() != null) {
            //Convert LocalDateTime to Instant before using Date.from
            Instant startInstant = event.getStartDate().atZone(ZoneId.systemDefault()).toInstant();
            Date start = Date.from(startInstant);
            vEvent.setDateStart(start);
        }

        return vEvent;
    }

    // Process iCal file and print information about events and todo
    public static CustomCalendar processIcalFile(String filePath) {
        File icalFile = new File(filePath);
        CustomCalendar calendar = new CustomCalendar(); // Create a new Calendar instance

        try {

            // Parse iCal file
            ICalendar ical = Biweekly.parse(icalFile).first();

            // Retrieve events and todos from the iCal object
            //events can be simple events or appointments
            //todos are tasks
            List<VEvent> events = ical.getEvents();
            List<VTodo> todos = ical.getTodos();

            for (VEvent event : events) {
                if (isAppointment(event)) {
                    calendar.addEvent(convertVEventToAppointment(event));
                } else {
                    calendar.addEvent(convertVEventToEvent(event));
                }
            }

            // Display information about todos (VTODOs)
            for (VTodo todo : todos) {
                calendar.addEvent(convertVTodoToTask(todo));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

      return calendar;
    }

    // Check if an event is an appointment based on the criteria
    //Returns true or false in case of an event being an appointment
    private static boolean isAppointment(VEvent event) {
        return event.getDateStart() != null &&
                (event.getDateEnd() != null || (event.getDuration() != null && event.getDuration().getValue() != null));
    }

    // Helper method to convert Biweekly VEvent to the Calendar Event object
    private static CustomEvent convertVEventToEvent(VEvent vEvent) {
        String summary = vEvent.getSummary().getValue();
        String description = vEvent.getDescription() != null ? vEvent.getDescription().getValue() : "";

        LocalDateTime dateTime;
        if (vEvent.getDateStart() != null && vEvent.getDateStart().getValue() != null) {
            dateTime = vEvent.getDateStart().getValue().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        } else {
            // Set dateTime to null if there is no start date.
            dateTime = null;
        }
        return new CustomEvent(summary, description, dateTime);
    }

    // Helper method to convert Biweekly VEvent to the Calendar Appointment object
    private static Appointment convertVEventToAppointment(VEvent vEvent) {
        String summary = vEvent.getSummary().getValue();
        String description = vEvent.getDescription() != null ? vEvent.getDescription().getValue() : "";

        LocalDateTime startDate;
        if (vEvent.getDateStart() != null && vEvent.getDateStart().getValue() != null) {
            startDate = vEvent.getDateStart().getValue().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        } else {
            // Set start date to null if there is no start date.
            startDate = null;
        }

        Duration duration;
        if (vEvent.getDateEnd() != null && vEvent.getDateEnd().getValue() != null) {
            LocalDateTime endDate = vEvent.getDateEnd().getValue().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            duration = Duration.between(startDate, endDate);
        } else if (vEvent.getDuration() != null && vEvent.getDuration().getValue() != null) {
            duration = Duration.ofMillis(vEvent.getDuration().getValue().getMinutes());
        } else {
            // Set a default duration if needed
            duration = Duration.ofMinutes(60);
        }

        return new Appointment(summary, description, startDate, duration);
    }

    // Helper method to convert Biweekly VTodo to the Calendar Task object
    private static Task convertVTodoToTask(VTodo vTodo) {
        String summary = vTodo.getSummary().getValue();
        String description = vTodo.getDescription() != null ? vTodo.getDescription().getValue() : "";
        String statusValue =vTodo.getStatus() != null ? vTodo.getStatus().getValue().trim() : "";
        boolean isCompleted = false;
        
        
        LocalDateTime startDateTime;
        if(vTodo.getDateStart() != null && vTodo.getDateStart().getValue() !=null) {
            startDateTime = vTodo.getDateStart().getValue().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        }else{
            startDateTime = null;
        }
        LocalDateTime dueDateTime;
        if (vTodo.getDateDue() != null && vTodo.getDateDue().getValue() != null) {
          dueDateTime = vTodo.getDateDue().getValue().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        }else {
            dueDateTime = TimeService.getTeller().now();
        }
        if ("COMPLETED".equalsIgnoreCase(statusValue)) {
            isCompleted = true;
        }

        return new Task(summary, description, startDateTime,  dueDateTime, isCompleted);
    }

    /* ------------------------------------------Helper Methods for the second requirement of the project -------------------------------- */
    //Method to write to an ics file
    public static void writeIcalendarToIcsFile(String icalFilename , ICalendar ical) {
        try {
            String icsContent = Biweekly.write(ical).go();
            Path filePath = Paths.get(icalFilename);
            Files.write(filePath , icsContent.getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE , StandardOpenOption.APPEND);
            System.out.println("ICS file written succesfully at: " + filePath.toAbsolutePath());
        }catch (IOException e) {
            System.err.println("Error writing ICS file:" + e.getMessage());
        }
    }
    //Method to write a single VEvent to an .ics file
    public static void writeEventToIcsFile(String icalFileName , VEvent event) {
        ICalendar ical = new ICalendar(); //Create a new ICalendar object
        ical.addEvent(event); //Add the VEvent to the ICalendar
        //Call the existing method to write the ICalendar to the .ics file
        writeIcalendarToIcsFile(icalFileName , ical);
    }
    //Method to write a single VEvent to an .ics file
    public static void writeToDoToIcsFile(String icalFilename , VTodo todo) {
        ICalendar ical = new ICalendar(); // Create a new ICalendar object.
        ical.addTodo(todo); //Add the VTodo to the ICalendar

        //Call the existing method to write the ICalendar to the .ics file
        writeIcalendarToIcsFile(icalFilename, ical);
    }
    //Help method to create a VEVENT of type Event when user chooses so. 
    public static VEvent createEventFromUserInput(Scanner scanner) {
        VEvent event = new VEvent();
        System.out.println("Enter event details:");
        System.out.println("Title:");
        event.setSummary(scanner.nextLine());
        System.out.println("Description:");
        event.setDescription(scanner.nextLine());
         while (true) {
        try {
            System.out.print("Start Date and Time (YYYY-MM-DDTHH:mm): ");
            LocalDateTime startDate = LocalDateTime.parse(scanner.nextLine());
            Date startDateAsDate = Date.from(startDate.atZone(ZoneId.systemDefault()).toInstant());
            event.setDateStart(startDateAsDate);
            break; // Break out of the loop if parsing is successful
        } catch (DateTimeParseException e) {
            System.out.println("Invalid date and time format. Please enter again.");
        }
    }
        return event;
        
    }
    //Help method to create a VEVENT of type Appointment when user chooses so. 
    public static VEvent createAppointmentFromUserInput(Scanner scanner){
        VEvent appointment = new VEvent();
        System.out.println("Enter appointment details:");
        System.out.println("Title:");
        appointment.setSummary(scanner.nextLine());
        System.out.println("Description:");
        appointment.setDescription(scanner.nextLine());
        while (true) {
            try {
                System.out.print("Start date and Time (YYYY-MM-DDTHH:mm): ");
                LocalDateTime startDate = LocalDateTime.parse(scanner.nextLine());
                Date startDateAsDate = Date.from(startDate.atZone(ZoneId.systemDefault()).toInstant());
                appointment.setDateStart(startDateAsDate);
    
                System.out.print("End date and Time (YYYY-MM-DDTHH:mm): ");
                LocalDateTime endDate = LocalDateTime.parse(scanner.nextLine());
                Date endDateAsDate = Date.from(endDate.atZone(ZoneId.systemDefault()).toInstant());
                appointment.setDateEnd(new DateEnd(endDateAsDate)); // Set end date
    
                break; // Break out of the loop if parsing is successful
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date and time format. Please enter again.");
            }
        }
        return appointment;
    }

    //Help method to create a VEVENT of type Task when user chooses so.
    public static VTodo createTaskFromUserInput(Scanner scanner) {
        VTodo task = new VTodo();
        System.out.println("Enter task details:");
        System.out.println("Title:");
        task.setSummary(scanner.nextLine());
        System.out.print("Description:");
        task.setDescription(scanner.nextLine());
        while (true) {
            try {
                System.out.print("Due date and Time (YYYY-MM-DDTHH:mm): ");
                LocalDateTime dueDateTime = LocalDateTime.parse(scanner.nextLine());
                Date dueDateAsDate = Date.from(dueDateTime.atZone(ZoneId.systemDefault()).toInstant());
                task.setDateDue(new DateDue(dueDateAsDate)); // Set due date
    
                break; // Break out of the loop if parsing is successful
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date and time format. Please enter again.");
            }
        }
        task.setStatus(Status.inProgress());
        return task;


    }

    

    // Method to print events and tasks
    public static void printEventsAndTasks(CustomCalendar calendar) {
        List<CustomEvent> events = calendar.getEvents();
        for (CustomEvent event : events) {
            if (event instanceof Appointment) {
                Appointment appointment = (Appointment) event;
                System.out.println("Appointment Summary: " + appointment.getTitle());
                System.out.println("Appointment Start: " + appointment.getStartDate());
                System.out.println("Appointment Duration: " + appointment.getDuration());
                System.out.println("-----------------");
            } else if (event instanceof Task) {
                Task task = (Task) event;
                System.out.println("Task Summary: " + task.getTitle());
                System.out.println("Task Deadline: " + task.getDeadline());
                System.out.println("Task Completed: " + task.isCompleted());
                System.out.println("-------------");
            } else {
                System.out.println("Event Summary: " + event.getTitle());
                System.out.println("Event DateTime: " + event.getStartDate());
                System.out.println("-----------------");
            }
        }
    }
}