package com.bookmarkapp.data.model;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "categories",
        indices = {@Index(value = "name", unique = true)})
public class Category {

    @PrimaryKey(autoGenerate = true)
    private long id;

    private String name;
    private String iconName;
    private int color;

    public Category(String name, String iconName, int color) {
        this.name = name;
        this.iconName = iconName;
        this.color = color;
    }

    // Getters
    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getIconName() {
        return iconName;
    }

    public int getColor() {
        return color;
    }

    // Setters
    public void setId(long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setIconName(String iconName) {
        this.iconName = iconName;
    }

    public void setColor(int color) {
        this.color = color;
    }

    // Predefined categories
    public static final String NEWS_ARTICLES = "News & Articles";
    public static final String SOCIAL_MEDIA = "Social Media";
    public static final String SHOPPING = "Shopping";
    public static final String ENTERTAINMENT = "Entertainment/Video";
    public static final String DEVELOPMENT = "Development/Tech";
    public static final String EDUCATION = "Education";
    public static final String FINANCE = "Finance";
    public static final String TRAVEL = "Travel";
    public static final String FOOD = "Food & Recipes";
    public static final String SPORTS = "Sports";
    public static final String OTHER = "Other";
}
