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

public class TeamMonitoringAdapter extends RecyclerView.Adapter<TeamMonitoringAdapter.CardHolder> {
    private Context context;
    private ArrayList<ProjectStatusModel> teamMonitoringModelArrayList;

    public TeamMonitoringAdapter(Context context, ArrayList<ProjectStatusModel> teamMonitoringModelArrayList) {
        this.context = context;
        this.teamMonitoringModelArrayList = teamMonitoringModelArrayList;
    }

    @NonNull
    @Override
    public CardHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cards_project_status, parent, false);
        return new CardHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardHolder holder, int position) {
        ProjectStatusModel projectStatusModel = teamMonitoringModelArrayList.get(position);
        holder.setDetails(projectStatusModel);

        if(position % 2 == 0)
            holder.cardView.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.green)));
        else
            holder.cardView.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.sky)));
    }

    @Override
    public int getItemCount() {
        return teamMonitoringModelArrayList.size();
    }

    class CardHolder extends RecyclerView.ViewHolder{
        private TextView title, date, format, status;
        private CardView cardView;
        private int projectID;
        private String dateAssigned;
        public CardHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.project_status_cardview);
            title = itemView.findViewById(R.id.project_status_card_title);
            date = itemView.findViewById(R.id.project_status_card_date_submitted);
            format = itemView.findViewById(R.id.project_status_card_document_type);
            status = itemView.findViewById(R.id.project_status_card_status);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), ProjectDetails.class);
                    intent.putExtra("task_name", teamMonitoringModelArrayList.get(getAdapterPosition()).getTaskName());
                    intent.putExtra("projectID", teamMonitoringModelArrayList.get(getAdapterPosition()).getProjectID());
                    intent.putExtra("format", teamMonitoringModelArrayList.get(getAdapterPosition()).getFileFormat());
                    intent.putExtra("date_assigned", teamMonitoringModelArrayList.get(getAdapterPosition()).getDateAssigned());
                    intent.putExtra("date_submitted", teamMonitoringModelArrayList.get(getAdapterPosition()).getDateSubmitted());
                    intent.putExtra("gdrive", teamMonitoringModelArrayList.get(getAdapterPosition()).getgDrive());
                    intent.putExtra("status", teamMonitoringModelArrayList.get(getAdapterPosition()).getStatus());
                    v.getContext().startActivity(intent);

                }
            });
        }

        void setDetails(ProjectStatusModel projectStatusModel){
            title.setText(projectStatusModel.getTaskName());
            date.setText(projectStatusModel.getDateSubmitted());
            format.setText(projectStatusModel.getFileFormat());
            status.setText(projectStatusModel.getStatus());


        }
    }
}


