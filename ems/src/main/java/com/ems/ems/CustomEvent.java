package com.ems.ems;
import java.time.Duration;
import java.time.LocalDateTime;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

// Base class for all events
class CustomEvent {
    private final StringProperty title = new SimpleStringProperty();
    private final StringProperty description = new SimpleStringProperty();
    private LocalDateTime startDate;

    // Event Constructor
    public CustomEvent(String title, String description, LocalDateTime startDate){
        this.title.set(title);
        this.description.set(description);
        this.startDate = startDate;
    }

    // Getter methods
    public StringProperty titleProperty() {
        return title;
    }

    public String getTitle() {
        return title.get();
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    public String getDescription() {
        return description.get();
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    // Setter methods
    public void setTitle(String newTitle) {
        title.set(newTitle);
    }

    public void setDescription(String newDescription) {
        description.set(newDescription);
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }
}

// Appointment class
final class Appointment extends CustomEvent {
    private Duration duration;

    public Appointment(String title, String description, LocalDateTime startDate, Duration duration) {
        super(title, description, startDate);
        this.duration = duration;
    }

    // Getter methods
    public Duration getDuration() {
        return duration;
    }

    // Setter methods
    public void setDuration(Duration duration) {
        this.duration = duration;
    }
}

// Task class
final class Task extends CustomEvent {
    private LocalDateTime deadline;
    private boolean isCompleted;

    public Task(String title, String description, LocalDateTime startDate, LocalDateTime deadline, boolean isCompleted) {
        super(title, description, startDate);
        this.deadline = deadline;
        this.isCompleted = isCompleted;
    }

    // Getter methods
    public LocalDateTime getDeadline() {
        return deadline;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    // Setter Methods
    public void setDeadline(LocalDateTime deadline) {
        this.deadline = deadline;
    }

    public void setCompleted(boolean isCompleted) {
        this.isCompleted = isCompleted;
    }
}