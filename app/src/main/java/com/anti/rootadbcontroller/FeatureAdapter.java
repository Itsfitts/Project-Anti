package com.anti.rootadbcontroller;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.anti.rootadbcontroller.models.FeatureItem;

import java.util.List;

/**
 * An adapter for the RecyclerView in MainActivity, responsible for displaying a list of features.
 * It binds the data from a list of {@link FeatureItem} objects to the views within each item of the RecyclerView.
 */
public class FeatureAdapter extends RecyclerView.Adapter<FeatureAdapter.FeatureViewHolder> {

    private List<FeatureItem> featureItems;
    private OnFeatureClickListener listener;

    /**
     * An interface for handling click events on features in the RecyclerView.
     */
    public interface OnFeatureClickListener {
        /**
         * Called when a feature item is clicked.
         * @param featureId The unique ID of the clicked feature.
         */
        void onFeatureClick(int featureId);
    }

    /**
     * Constructs a new FeatureAdapter.
     * @param featureItems The list of features to display.
     * @param listener The listener for click events.
     */
    public FeatureAdapter(List<FeatureItem> featureItems, OnFeatureClickListener listener) {
        this.featureItems = featureItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public FeatureViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_feature, parent, false);
        return new FeatureViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FeatureViewHolder holder, int position) {
        FeatureItem item = featureItems.get(position);
        holder.featureIcon.setImageResource(item.getIconResId());
        holder.featureTitle.setText(item.getTitle());
        holder.featureDescription.setText(item.getDescription());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onFeatureClick(item.getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return featureItems.size();
    }

    /**
     * A ViewHolder that holds the views for a single feature item in the RecyclerView.
     */
    static class FeatureViewHolder extends RecyclerView.ViewHolder {
        ImageView featureIcon;
        TextView featureTitle;
        TextView featureDescription;

        public FeatureViewHolder(@NonNull View itemView) {
            super(itemView);
            featureIcon = itemView.findViewById(R.id.featureIcon);
            featureTitle = itemView.findViewById(R.id.featureTitle);
            featureDescription = itemView.findViewById(R.id.featureDescription);
        }
    }
}
