package com.example.mcc_attendance_tracker;

public class LeaveStatusModel {
    String reason, details, dateStart, dateEnd, status;
    int leaveID;

    public LeaveStatusModel(String reason, String details, String dateStart, String dateEnd, String status, int leaveID) {
        this.reason = reason;
        this.details = details;
        this.dateStart = dateStart;
        this.dateEnd = dateEnd;
        this.status = status;
        this.leaveID = leaveID;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getDateStart() {
        return dateStart;
    }

    public void setDateStart(String dateStart) {
        this.dateStart = dateStart;
    }

    public String getDateEnd() {
        return dateEnd;
    }

    public void setDateEnd(String dateEnd) {
        this.dateEnd = dateEnd;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getLeaveID() {
        return leaveID;
    }

    public void setLeaveID(int leaveID) {
        this.leaveID = leaveID;
    }
}
