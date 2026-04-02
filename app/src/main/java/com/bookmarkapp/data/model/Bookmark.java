package com.bookmarkapp.data.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "bookmarks",
        foreignKeys = @ForeignKey(
                entity = Category.class,
                parentColumns = "id",
                childColumns = "categoryId",
                onDelete = ForeignKey.SET_NULL
        ),
        indices = {@Index("categoryId"), @Index("url")})
public class Bookmark {

    @PrimaryKey(autoGenerate = true)
    private long id;

    private String url;
    private String title;
    private String description;
    private String thumbnailUrl;
    private Long categoryId;
    private long createdAt;
    private boolean isFavorite;
    private String domain;
    private String notes;

    public Bookmark(String url, String title, String description, String thumbnailUrl,
                    Long categoryId, long createdAt, boolean isFavorite, String domain) {
        this.url = url;
        this.title = title;
        this.description = description;
        this.thumbnailUrl = thumbnailUrl;
        this.categoryId = categoryId;
        this.createdAt = createdAt;
        this.isFavorite = isFavorite;
        this.domain = domain;
        this.notes = null;
    }

    // Getters
    public long getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public String getDomain() {
        return domain;
    }

    public String getNotes() {
        return notes;
    }

    // Setters
    public void setId(long id) {
        this.id = id;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
