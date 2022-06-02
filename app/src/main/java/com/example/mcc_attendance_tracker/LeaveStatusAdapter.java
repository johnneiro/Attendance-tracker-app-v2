package com.example.mcc_attendance_tracker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class LeaveStatusAdapter extends RecyclerView.Adapter<LeaveStatusAdapter.CardHolder> {
    private ArrayList<LeaveStatusModel> leaveStatusModelArrayList;
    private Context context;


    public LeaveStatusAdapter(Context context, ArrayList<LeaveStatusModel> leaveStatusModelArrayList) {
        this.context = context;
        this.leaveStatusModelArrayList = leaveStatusModelArrayList;
    }

    @NonNull
    @Override
    public CardHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cards_leave_status, parent, false);
        return new LeaveStatusAdapter.CardHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LeaveStatusAdapter.CardHolder holder, int position) {
        LeaveStatusModel leaveStatusModel = leaveStatusModelArrayList.get(position);
        holder.setDetails(leaveStatusModel);

        if(position % 2 == 0)
            holder.cardView.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.green)));
        else
            holder.cardView.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.sky)));

    }

    @Override
    public int getItemCount() {
        return leaveStatusModelArrayList.size();
    }

    class CardHolder extends RecyclerView.ViewHolder{
        private TextView title, date, status;
        private CardView cardView;
        public CardHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.leave_status_cardview);
            title = itemView.findViewById(R.id.leave_status_card_title_text);
            date = itemView.findViewById(R.id.leave_status_card_date_text);
            status = itemView.findViewById(R.id.leave_status_card_status_text);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext().getApplicationContext(), LeaveDetails.class);
                    intent.putExtra("leaveID", leaveStatusModelArrayList.get(getAdapterPosition()).getLeaveID());
                    intent.putExtra("leave_reason", leaveStatusModelArrayList.get(getAdapterPosition()).getReason());
                    intent.putExtra("leave_details", leaveStatusModelArrayList.get(getAdapterPosition()).getDetails());
                    intent.putExtra("date_start", leaveStatusModelArrayList.get(getAdapterPosition()).getDateStart());
                    intent.putExtra("date_end", leaveStatusModelArrayList.get(getAdapterPosition()).getDateEnd());
                    intent.putExtra("leave_status", leaveStatusModelArrayList.get(getAdapterPosition()).getStatus());
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    v.getContext().startActivity(intent);

                }
            });
        }

        void setDetails(LeaveStatusModel leaveStatusModel){
            title.setText(leaveStatusModel.getReason());
            date.setText(leaveStatusModel.getDateStart() + " - " + leaveStatusModel.getDateEnd());
            status.setText(leaveStatusModel.getStatus());


        }
    }
}
