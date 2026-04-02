package com.bookmarkapp.ui.components;

import com.bookmarkapp.data.model.Bookmark;
import com.bookmarkapp.ui.theme.AppTheme;

public class BookmarkCard {

    public interface BookmarkCardCallbacks {
        void onClick(Bookmark bookmark);
        void onLongClick(Bookmark bookmark);
        void onFavoriteClick(Bookmark bookmark);
        void onShareClick(Bookmark bookmark);
        void onDeleteClick(Bookmark bookmark);
    }

    public static final int CARD_HEIGHT = 180;
    public static final int IMAGE_HEIGHT = 100;
    public static final int CORNER_RADIUS = 12;

    // Helper method to format domain for display
    public static String formatDomain(String domain) {
        if (domain == null || domain.isEmpty()) {
            return "";
        }
        // Remove www. prefix if present
        if (domain.startsWith("www.")) {
            domain = domain.substring(4);
        }
        // Truncate if too long
        if (domain.length() > 25) {
            return domain.substring(0, 22) + "...";
        }
        return domain;
    }

    // Helper method to format title for display
    public static String formatTitle(String title, int maxLines) {
        if (title == null || title.isEmpty()) {
            return "Untitled";
        }
        return title;
    }

    // Helper method to get category color
    public static int getCategoryColor(int categoryIndex) {
        if (categoryIndex < 0 || categoryIndex >= AppTheme.CATEGORY_COLORS.length) {
            return AppTheme.CATEGORY_COLORS[AppTheme.CATEGORY_COLORS.length - 1];
        }
        return AppTheme.CATEGORY_COLORS[categoryIndex];
    }

    // Helper to determine if we should show placeholder
    public static boolean shouldShowPlaceholder(Bookmark bookmark) {
        return bookmark.getThumbnailUrl() == null || bookmark.getThumbnailUrl().isEmpty();
    }

    // Get placeholder text (first letter of domain)
    public static String getPlaceholderText(Bookmark bookmark) {
        String domain = bookmark.getDomain();
        if (domain == null || domain.isEmpty()) {
            return "?";
        }
        return domain.substring(0, 1).toUpperCase();
    }

    // Get background color for placeholder based on domain
    public static int getPlaceholderColor(Bookmark bookmark) {
        String domain = bookmark.getDomain();
        if (domain == null) {
            return AppTheme.CATEGORY_COLORS[0];
        }
        int index = Math.abs(domain.hashCode()) % AppTheme.CATEGORY_COLORS.length;
        return AppTheme.CATEGORY_COLORS[index];
    }

    // Format timestamp for display
    public static String formatTimestamp(long timestamp) {
        long now = System.currentTimeMillis();
        long diff = now - timestamp;

        long seconds = diff / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;

        if (days > 0) {
            return days + (days == 1 ? " day ago" : " days ago");
        } else if (hours > 0) {
            return hours + (hours == 1 ? " hour ago" : " hours ago");
        } else if (minutes > 0) {
            return minutes + (minutes == 1 ? " min ago" : " mins ago");
        } else {
            return "Just now";
        }
    }
}
