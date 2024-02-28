package com.ems.ems;

import java.util.List;
import java.util.Scanner;

/*
 * This interface defines the contract for classes responsible for managing events.
 */
public interface EventManager {
    /*
     * Displays events based on the specified option and the provided iCal filename.
     * 
      @param option        The option specifying the type of events to display.
       @param icalFilename  The filename of the iCal file containing event data.
     */
    List <CustomEvent> getFilteredEvents(String option);

   /*
     * Updates the calendar based on the provided iCal filename.
     * 
     * @param icalFilename  The filename of the iCal file to update the calendar from.
     */
    void updateCalendar(String icalFilename , Scanner scanner);
}