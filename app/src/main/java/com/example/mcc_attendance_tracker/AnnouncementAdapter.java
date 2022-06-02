package com.example.mcc_attendance_tracker;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AnnouncementAdapter extends RecyclerView.Adapter<AnnouncementAdapter.CardHolder> {

    private Context context;
    private ArrayList<AnnouncementsModel> announcementsModels;


    public AnnouncementAdapter(Context context, ArrayList<AnnouncementsModel> announcementsModels) {
        this.context = context;
        this.announcementsModels = announcementsModels;
    }



    @NonNull
    @Override
    public CardHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.dashboard_announcement_card, parent, false);

        return new CardHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardHolder holder, int position) {
        AnnouncementsModel announcementsModel = announcementsModels.get(position);
        holder.setDetails(announcementsModel);

        if(position % 2 == 0)
            holder.announcementCards.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.green)));
        else
            holder.announcementCards.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.sky)));

    }

    @Override
    public int getItemCount() {
        return announcementsModels.size();
    }

    class CardHolder extends RecyclerView.ViewHolder{
        private TextView txtAnnouncementTitle, txtAnnouncementDate, txtAnnouncementTime, txtAnnouncementStatus, txtAnnouncementSpeaker;
        private CardView announcementCards;


        CardHolder(View v){
            super(v);

            txtAnnouncementTitle = v.findViewById(R.id.dashboard_announcement_card_title);
            txtAnnouncementDate = v.findViewById(R.id.dashboard_announcement_card_date);
            txtAnnouncementTime = v.findViewById(R.id.dashboard_announcement_card_time);
            txtAnnouncementStatus = v.findViewById(R.id.dashboard_announcement_card_status);
            txtAnnouncementSpeaker = v.findViewById(R.id.dashboard_announcement_card_speaker);
            announcementCards = v.findViewById(R.id.dashboard_announcement_cardview);

            txtAnnouncementTime.setTextColor(context.getResources().getColor(R.color.white));
            txtAnnouncementDate.setTextColor(context.getResources().getColor(R.color.white));
            txtAnnouncementTitle.setTextColor(context.getResources().getColor(R.color.white));
            txtAnnouncementSpeaker.setTextColor(context.getResources().getColor(R.color.white));
            txtAnnouncementStatus.setTextColor(context.getResources().getColor(R.color.white));

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(view.getContext(),AnnouncementsDetails.class);
                    i.putExtra("hasParticipated", announcementsModels.get(getAdapterPosition()).getHasParticipated());
                    i.putExtra("webinarID", announcementsModels.get(getAdapterPosition()).getWebID());
                    i.putExtra("title", announcementsModels.get(getAdapterPosition()).getTitle());
                    i.putExtra("meeting_date", announcementsModels.get(getAdapterPosition()).getDate());
                    i.putExtra("details", announcementsModels.get(getAdapterPosition()).getDetails());
                    i.putExtra("meeting_time", announcementsModels.get(getAdapterPosition()).getTime());
                    i.putExtra("meeting_link", announcementsModels.get(getAdapterPosition()).getLink());
                    i.putExtra("speaker", announcementsModels.get(getAdapterPosition()).getSpeaker());
                    i.putExtra("register_fee", announcementsModels.get(getAdapterPosition()).getFee());
                    i.putExtra("status", announcementsModels.get(getAdapterPosition()).getStatus());
                    view.getContext().startActivity(i);
                }
            });


        }

        void setDetails(AnnouncementsModel announcementsModel){
            txtAnnouncementTitle.setText(announcementsModel.getTitle());
            txtAnnouncementDate.setText(announcementsModel.getDate());
            txtAnnouncementTime.setText(announcementsModel.getTime());
            txtAnnouncementSpeaker.setText(announcementsModel.getSpeaker());
            txtAnnouncementStatus.setText(announcementsModel.getStatus());
        }
    }
}
