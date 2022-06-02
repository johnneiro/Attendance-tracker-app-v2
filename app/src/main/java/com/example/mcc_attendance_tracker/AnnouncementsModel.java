package com.example.mcc_attendance_tracker;

public class AnnouncementsModel {
   private String title,date,time,details, speaker, status, fee, link;
   private int webID;
   private boolean hasParticipated;

   public AnnouncementsModel(int webID, String title, String date, String time, String details, String speaker, String status, String fee, String link, boolean hasParticipated){
       this.webID = webID;
       this.title = title;
       this.date = date;
       this.time = time;
       this.details = details;
       this.speaker = speaker;
       this.status = status;
       this.fee = fee;
       this.link = link;
       this.hasParticipated = hasParticipated;
   }

    public int getWebID() {
        return webID;
    }

    public boolean getHasParticipated() {
        return hasParticipated;
    }

    public String getDetails() { return details; }

    public String getTitle() {
        return title;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getSpeaker() {
        return speaker;
    }

    public String getStatus() {
        return status;
    }

    public String getFee() {
        return fee;
    }

    public String getLink() {
        return link;
    }
}
