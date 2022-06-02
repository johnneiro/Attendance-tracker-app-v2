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

public class ReportPageAdapter extends RecyclerView.Adapter<ReportPageAdapter.CardHolder> {
    private Context context;
    private ArrayList<ReportPageModel> reportPageModelArrayList;

    public ReportPageAdapter(Context context, ArrayList<ReportPageModel> reportPageModelArrayList) {
        this.context = context;
        this.reportPageModelArrayList = reportPageModelArrayList;
    }

    @NonNull
    @Override
    public CardHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cards_report, parent, false);
        return new ReportPageAdapter.CardHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportPageAdapter.CardHolder holder, int position) {
        ReportPageModel reportPageModel = reportPageModelArrayList.get(position);
        holder.setDetails(reportPageModel);

        if(position % 2 == 0)
            holder.cardView.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.green)));
        else
            holder.cardView.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.sky)));
    }

    @Override
    public int getItemCount() {
        return reportPageModelArrayList.size();
    }

    class CardHolder extends RecyclerView.ViewHolder{
        private TextView title, date, status, ticket;
        private CardView cardView;
        public CardHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.report_status_cardview);
            title = itemView.findViewById(R.id.report_card_title);
            date = itemView.findViewById(R.id.report_card_date_submitted);
            status = itemView.findViewById(R.id.report_card_status);
            ticket = itemView.findViewById(R.id.report_card_ticket);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), ReportDetails.class);
                    intent.putExtra("report_status", reportPageModelArrayList.get(getAdapterPosition()).getReportStatus());
                    intent.putExtra("report_title", reportPageModelArrayList.get(getAdapterPosition()).getReportTitle());
                    intent.putExtra("reportID", reportPageModelArrayList.get(getAdapterPosition()).getReportID());
                    intent.putExtra("report_documents", reportPageModelArrayList.get(getAdapterPosition()).getgDriveLink());
                    intent.putExtra("report_details", reportPageModelArrayList.get(getAdapterPosition()).getReportDetails());
                    intent.putExtra("date_submitted", reportPageModelArrayList.get(getAdapterPosition()).getDateReport());
                    intent.putExtra("ticket_no", reportPageModelArrayList.get(getAdapterPosition()).getTicketNo());
                    v.getContext().startActivity(intent);

                }
            });
        }

        void setDetails(ReportPageModel reportPageModel){
            title.setText(reportPageModel.getReportTitle());
            date.setText(reportPageModel.getDateReport());
            status.setText(reportPageModel.getReportStatus());
            ticket.setText(reportPageModel.getTicketNo());


        }
    }
}
