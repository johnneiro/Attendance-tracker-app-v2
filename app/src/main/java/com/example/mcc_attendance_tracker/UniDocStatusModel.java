package com.example.mcc_attendance_tracker;

public class UniDocStatusModel {
    private String documentTitle, coordinatorName, coordinatorEmail, dateSubmitted, fileFormat, gDrive, deadLine, status;
    private int documentID;


    public UniDocStatusModel (String documentTitle, String coordinatorName, String coordinatorEmail, String dateSubmitted, String fileFormat, int documentID, String gDrive, String deadLine, String status) {
        this.documentTitle = documentTitle;
        this.coordinatorName = coordinatorName;
        this.coordinatorEmail = coordinatorEmail;
        this.dateSubmitted = dateSubmitted;
        this.fileFormat = fileFormat;
        this.documentID = documentID;
        this.gDrive = gDrive;
        this.deadLine = deadLine;
        this.status = status;
    }

    public String getStatus() {
        return status;
    }


    public String getDocumentTitle() {return documentTitle;}

    public void setDocumentTitle(String documentTitle) {
        this.documentTitle = documentTitle;
    }

    public String getCoordinatorName() {
        return coordinatorName;
    }

    public void setCoordinatorName(String coordinatorName) {this.coordinatorName = coordinatorName;}

    public String getCoordinatorEmail() {
        return coordinatorEmail;
    }

    public void setCoordinatorEmail(String coordinatorEmail) {this.coordinatorEmail = coordinatorEmail;}

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

    public int getDocumentID() {
        return documentID;
    }

    public void setDocumentID(int documentID) {
        this.documentID = documentID;
    }

    public String getgDrive() {
        return gDrive;
    }

    public void setgDrive(String gDrive) {
        this.gDrive = gDrive;
    }

    public String getDeadLine() {
        return deadLine;
    }

    public void setDeadLine(String deadLine) {
        this.deadLine = deadLine;
    }
}
