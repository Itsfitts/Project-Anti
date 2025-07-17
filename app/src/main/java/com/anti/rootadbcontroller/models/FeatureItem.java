package com.anti.rootadbcontroller.models;

/**
 * Represents a feature item displayed in the main RecyclerView. This class holds the data
 * for a single feature, including its ID, title, description, and icon.
 */
public class FeatureItem {
    private int id;
    private String title;
    private String description;
    private int iconResId;

    /**
     * Constructs a new FeatureItem.
     * @param id The unique identifier for the feature.
     * @param title The title of the feature.
     * @param description A brief description of what the feature does.
     * @param iconResId The resource ID for the feature's icon.
     */
    public FeatureItem(int id, String title, String description, int iconResId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.iconResId = iconResId;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getIconResId() {
        return iconResId;
    }
}
