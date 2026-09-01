package com.example.scamshield;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.scamshield.models.ScamModel;
import java.util.List;

public class ScamAdapter extends RecyclerView.Adapter<ScamAdapter.ViewHolder> {

    private Context context;
    private List<ScamModel> scamList;
    private OnScamClickListener listener;

    public interface OnScamClickListener {
        void onScamClick(ScamModel scam);
    }

    public ScamAdapter(Context context, List<ScamModel> scamList, OnScamClickListener listener) {
        this.context = context;
        this.scamList = scamList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_scam, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ScamModel scam = scamList.get(position);

        holder.tvPhone.setText("📞 " + scam.getPhoneNumber());
        holder.tvMessage.setText(scam.getMessage());
        holder.tvScamType.setText("⚠️ " + scam.getScamType());
        holder.tvDateTime.setText("🕐 " + scam.getDateTime());

        // Danger level color
        String type = scam.getScamType().toLowerCase();
        if (type.contains("high") || type.contains("prize") || type.contains("bank") || type.contains("money")) {
            holder.tvScamType.setTextColor(context.getResources().getColor(android.R.color.holo_red_dark));
        } else if (type.contains("medium") || type.contains("loan")) {
            holder.tvScamType.setTextColor(context.getResources().getColor(android.R.color.holo_orange_dark));
        } else {
            holder.tvScamType.setTextColor(context.getResources().getColor(android.R.color.holo_blue_dark));
        }

        if (scam.isReported()) {
            holder.tvStatus.setText("✅ Reported");
            holder.tvStatus.setTextColor(context.getResources().getColor(android.R.color.holo_green_dark));
        } else {
            holder.tvStatus.setText("⏳ Pending");
            holder.tvStatus.setTextColor(context.getResources().getColor(android.R.color.holo_orange_dark));
        }

        // Click listener
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onScamClick(scam);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return scamList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvPhone, tvMessage, tvScamType, tvDateTime, tvStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPhone = itemView.findViewById(R.id.tvPhone);
            tvMessage = itemView.findViewById(R.id.tvMessage);
            tvScamType = itemView.findViewById(R.id.tvScamType);
            tvDateTime = itemView.findViewById(R.id.tvDateTime);
            tvStatus = itemView.findViewById(R.id.tvStatus);
        }
    }
}