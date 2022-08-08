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

public class UniDocStatusAdapter extends RecyclerView.Adapter<UniDocStatusAdapter.CardHolder> {
    private Context context;
    private ArrayList<UniDocStatusModel> unidocStatusModelArrayList;

    public UniDocStatusAdapter(Context context, ArrayList<UniDocStatusModel> unidocStatusModelArrayList) {
        this.context = context;
        this.unidocStatusModelArrayList = unidocStatusModelArrayList;
    }

    @NonNull
    @Override
    public CardHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cards_university_documents, parent, false);
        return new CardHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardHolder holder, int position) {
        UniDocStatusModel unidocStatusModel = unidocStatusModelArrayList.get(position);
        holder.setDetails(unidocStatusModel);

        if(position % 2 == 0)
            holder.cardView.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.green)));
        else
            holder.cardView.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.sky)));
    }

    @Override
    public int getItemCount() {
        return unidocStatusModelArrayList.size();
    }

    class CardHolder extends RecyclerView.ViewHolder{
        private TextView title, date, format, status;
        private CardView cardView;
        private int documentID;
        private String dateAssigned;
        public CardHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.uni_status_cardview);
            title = itemView.findViewById(R.id.university_documents_card_title);
            date = itemView.findViewById(R.id.university_documents_card_date_submitted);
            format = itemView.findViewById(R.id.university_documents_card_document_type);
            status = itemView.findViewById(R.id.university_documents_card_status);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), UniDocDetails.class);
                    intent.putExtra("document_title", unidocStatusModelArrayList.get(getAdapterPosition()).getDocumentTitle());
                    intent.putExtra("document_id", unidocStatusModelArrayList.get(getAdapterPosition()).getDocumentID());
                    intent.putExtra("coordinator_name", unidocStatusModelArrayList.get(getAdapterPosition()).getCoordinatorName());
                    intent.putExtra("coordinator_email", unidocStatusModelArrayList.get(getAdapterPosition()).getCoordinatorEmail());
                    intent.putExtra("file_format", unidocStatusModelArrayList.get(getAdapterPosition()).getFileFormat());
                    intent.putExtra("date_submitted", unidocStatusModelArrayList.get(getAdapterPosition()).getDeadLine());
                    intent.putExtra("deadline", unidocStatusModelArrayList.get(getAdapterPosition()).getDateSubmitted());
                    intent.putExtra("gdrive_link", unidocStatusModelArrayList.get(getAdapterPosition()).getgDrive());
                    intent.putExtra("status", unidocStatusModelArrayList.get(getAdapterPosition()).getStatus());
                    v.getContext().startActivity(intent);

                }
            });
        }

        void setDetails(UniDocStatusModel unidocStatusModel){
            title.setText(unidocStatusModel.getDocumentTitle());
            date.setText(unidocStatusModel.getDateSubmitted());
            format.setText(unidocStatusModel.getFileFormat());
            status.setText(unidocStatusModel.getStatus());

        }
    }
}


