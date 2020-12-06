package com.example.a117429464_ca2;

public class AssignmentModel {


    private int id;
    private String title;
    private String importance;
    private String dueDate;
    private String description;

    public AssignmentModel(int id, String title, String importance, String dueDate, String description, boolean completed) {
        this.id = id;
        this.title = title;
        this.importance = importance;
        this.dueDate = dueDate;
        this.description = description;
        this.completed = completed;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setImportance(String importance) {
        this.importance = importance;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    private boolean completed;

    public String getTitle() {
        return title;
    }

    public String getImportance() {
        return importance;
    }

    public String getDueDate() {
        return dueDate;
    }

    public String getDescription() {
        return description;
    }

    public boolean isCompleted() {
        return completed;
    }


}
