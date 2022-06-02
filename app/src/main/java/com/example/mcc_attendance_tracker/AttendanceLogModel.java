package com.example.mcc_attendance_tracker;

public class AttendanceLogModel {
   private String date,day, timeIn, timeOut, remarks;

    public AttendanceLogModel(String date, String day, String timeIn, String timeOut, String remarks) {
        this.date = date;
        this.day = day;
        this.timeIn = timeIn;
        this.timeOut = timeOut;
        this.remarks = remarks;
    }

    public String getDate() {
        return date;
    }

    public String getDay() {
        return day;
    }

    public String getTimeIn() {
        return timeIn;
    }

    public String getTimeOut() {
        return timeOut;
    }

    public String getRemarks() {
        return remarks;
    }

}
