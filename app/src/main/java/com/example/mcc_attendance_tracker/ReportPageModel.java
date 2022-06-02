package com.example.mcc_attendance_tracker;

public class ReportPageModel {
    private String reportTitle, reportDetails, gDriveLink, dateReport, reportStatus, ticketNo;
    private int reportID;

    public ReportPageModel(String reportTitle, String reportDetails, String gDriveLink, String dateReport, String reportStatus, String ticketNo, int reportID) {
        this.reportTitle = reportTitle;
        this.reportDetails = reportDetails;
        this.gDriveLink = gDriveLink;
        this.dateReport = dateReport;
        this.reportStatus = reportStatus;
        this.ticketNo = ticketNo;
        this.reportID = reportID;
    }

    public String getReportTitle() {
        return reportTitle;
    }

    public String getReportDetails() {
        return reportDetails;
    }

    public String getgDriveLink() {
        return gDriveLink;
    }

    public String getDateReport() {
        return dateReport;
    }

    public String getReportStatus() {
        return reportStatus;
    }

    public String getTicketNo() {
        return ticketNo;
    }

    public int getReportID() {
        return reportID;
    }
}
