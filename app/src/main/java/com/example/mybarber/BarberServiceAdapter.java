package com.example.mybarber;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class BarberServiceAdapter extends RecyclerView.Adapter<BarberServiceAdapter.ViewHolder> {

    private List<BarberService> services;
    private List<Integer> selectedPositions; // Track selected positions
    private OnItemClickListener listener;

    public BarberServiceAdapter(List<BarberService> services) {
        this.services = services;
        this.selectedPositions = new ArrayList<>(); // Initialize the list
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setSelectedPositions(List<Integer> selectedPositions) {
        this.selectedPositions = selectedPositions;
        notifyDataSetChanged(); // Notify adapter to update views
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_barber_service, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BarberService service = services.get(position);
        holder.tvServiceName.setText(service.getName());
        holder.tvServicePrice.setText("Rs. " + service.getPrice()+"0"); // Set the service price
        holder.ivServiceIcon.setImageResource(service.getIcon());

        // Set the selected state based on the selected positions
        boolean isSelected = selectedPositions.contains(holder.getAdapterPosition());
        holder.itemView.setSelected(isSelected);

        // Customize the visual effect color
        int color = isSelected ? ContextCompat.getColor(holder.itemView.getContext(), R.color.selected_item_color) :
                ContextCompat.getColor(holder.itemView.getContext(), R.color.unselected_item_color);
        holder.itemView.setBackgroundColor(color);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onItemClick(holder.getAdapterPosition());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return services.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvServiceName;
        private TextView tvServicePrice; // Add TextView for service price
        private ImageView ivServiceIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvServiceName = itemView.findViewById(R.id.tv_service_name);
            tvServicePrice = itemView.findViewById(R.id.tv_service_price); // Initialize TextView for service price
            ivServiceIcon = itemView.findViewById(R.id.iv_service_icon);
        }
    }
}





