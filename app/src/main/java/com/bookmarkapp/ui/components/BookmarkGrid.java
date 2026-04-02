package com.bookmarkapp.ui.components;

import com.bookmarkapp.data.model.Bookmark;
import com.bookmarkapp.ui.theme.AppTheme;

import java.util.List;

public class BookmarkGrid {

    public interface GridCallbacks {
        void onBookmarkClick(Bookmark bookmark);
        void onBookmarkLongClick(Bookmark bookmark);
        void onFavoriteClick(Bookmark bookmark);
        void onShareClick(Bookmark bookmark);
        void onDeleteClick(Bookmark bookmark);
        void onRefresh();
    }

    // Grid configuration
    public static final int COLUMNS_PHONE_PORTRAIT = 2;
    public static final int COLUMNS_PHONE_LANDSCAPE = 3;
    public static final int COLUMNS_TABLET_PORTRAIT = 3;
    public static final int COLUMNS_TABLET_LANDSCAPE = 4;

    // Grid spacing
    public static final int GRID_SPACING = AppTheme.SPACING_SMALL;
    public static final int GRID_PADDING = AppTheme.SPACING_MEDIUM;

    // Calculate columns based on screen width
    public static int calculateColumns(int screenWidthDp) {
        if (screenWidthDp < 600) {
            return COLUMNS_PHONE_PORTRAIT;
        } else if (screenWidthDp < 840) {
            return COLUMNS_TABLET_PORTRAIT;
        } else {
            return COLUMNS_TABLET_LANDSCAPE;
        }
    }

    // Check if grid is empty
    public static boolean isEmpty(List<Bookmark> bookmarks) {
        return bookmarks == null || bookmarks.isEmpty();
    }

    // Get empty state message
    public static String getEmptyStateMessage(boolean hasFilter) {
        if (hasFilter) {
            return "No bookmarks found in this category";
        }
        return "No bookmarks yet. Share a URL to get started!";
    }

    // Get empty state for search
    public static String getSearchEmptyStateMessage(String query) {
        if (query == null || query.isEmpty()) {
            return "Enter a search term to find bookmarks";
        }
        return "No bookmarks found for \"" + query + "\"";
    }

    // Sort bookmarks by different criteria
    public static void sortByDateDescending(List<Bookmark> bookmarks) {
        if (bookmarks != null) {
            bookmarks.sort((a, b) -> Long.compare(b.getCreatedAt(), a.getCreatedAt()));
        }
    }

    public static void sortByDateAscending(List<Bookmark> bookmarks) {
        if (bookmarks != null) {
            bookmarks.sort((a, b) -> Long.compare(a.getCreatedAt(), b.getCreatedAt()));
        }
    }

    public static void sortByTitleAscending(List<Bookmark> bookmarks) {
        if (bookmarks != null) {
            bookmarks.sort((a, b) -> {
                String titleA = a.getTitle() != null ? a.getTitle() : "";
                String titleB = b.getTitle() != null ? b.getTitle() : "";
                return titleA.compareToIgnoreCase(titleB);
            });
        }
    }

    public static void sortByTitleDescending(List<Bookmark> bookmarks) {
        if (bookmarks != null) {
            bookmarks.sort((a, b) -> {
                String titleA = a.getTitle() != null ? a.getTitle() : "";
                String titleB = b.getTitle() != null ? b.getTitle() : "";
                return titleB.compareToIgnoreCase(titleA);
            });
        }
    }

    // Filter bookmarks by favorites
    public static List<Bookmark> filterFavorites(List<Bookmark> bookmarks) {
        if (bookmarks == null) return null;

        return bookmarks.stream()
                .filter(Bookmark::isFavorite)
                .collect(java.util.stream.Collectors.toList());
    }

    // Get bookmark count text
    public static String getBookmarkCountText(int count) {
        if (count == 0) {
            return "No bookmarks";
        } else if (count == 1) {
            return "1 bookmark";
        } else {
            return count + " bookmarks";
        }
    }
}
