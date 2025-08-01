package com.anti.rootadbcontroller.models

/**
 * Represents a feature item displayed in the UI. This class holds the data
 * for a single feature, including its ID, title, description, icon, and detailed explanation.
 */
data class FeatureItem(
    val id: Int,
    val title: String,
    val description: String,
    val iconResId: Int,
    val detailedExplanation: String = "",
)
