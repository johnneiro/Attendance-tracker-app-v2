package com.example.mcc_attendance_tracker;

public class ProjectStatusModel {
    private String taskName, dateSubmitted, fileFormat, gDrive, dateAssigned, status;
    private int projectID;


    public ProjectStatusModel(String taskName, String dateSubmitted, String fileFormat, int projectID, String gDrive, String dateAssigned, String status) {
        this.taskName = taskName;
        this.dateSubmitted = dateSubmitted;
        this.fileFormat = fileFormat;
        this.projectID = projectID;
        this.gDrive = gDrive;
        this.dateAssigned = dateAssigned;
        this.status = status;
    }

    public String getStatus() {
        return status;
    }


    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getDateSubmitted() {
        return dateSubmitted;
    }

    public void setDateSubmitted(String dateSubmitted) {
        this.dateSubmitted = dateSubmitted;
    }

    public String getFileFormat() {
        return fileFormat;
    }

    public void setFileFormat(String fileFormat) {
        this.fileFormat = fileFormat;
    }

    public int getProjectID() {
        return projectID;
    }

    public void setProjectID(int projectID) {
        this.projectID = projectID;
    }

    public String getgDrive() {
        return gDrive;
    }

    public void setgDrive(String gDrive) {
        this.gDrive = gDrive;
    }

    public String getDateAssigned() {
        return dateAssigned;
    }

    public void setDateAssigned(String dateAssigned) {
        this.dateAssigned = dateAssigned;
    }
}
