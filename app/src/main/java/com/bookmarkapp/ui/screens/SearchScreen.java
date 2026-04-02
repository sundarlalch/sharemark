package com.bookmarkapp.ui.screens;

import android.content.Context;

import com.bookmarkapp.data.model.Bookmark;
import com.bookmarkapp.data.model.Category;
import com.bookmarkapp.ui.components.SearchBar;
import com.bookmarkapp.ui.theme.AppTheme;
import com.bookmarkapp.viewmodel.SearchViewModel;

import java.util.List;

public class SearchScreen {

    public interface SearchScreenCallbacks {
        void onBackClick();
        void onQueryChange(String query);
        void onSearch(String query);
        void onClearSearch();
        void onBookmarkClick(Bookmark bookmark);
        void onBookmarkLongClick(Bookmark bookmark);
        void onFavoriteClick(Bookmark bookmark);
        void onShareClick(Bookmark bookmark);
        void onDeleteClick(Bookmark bookmark);
        void onFilterSelect(Category category);
        void onClearFilter();
        void onSortChange(SearchViewModel.SortOrder sortOrder);
    }

    // Screen state
    public enum ScreenState {
        INITIAL,      // No search yet
        SEARCHING,    // Currently searching
        RESULTS,      // Has results
        NO_RESULTS,   // Search completed, no results
        ERROR         // Error occurred
    }

    // Configuration
    public static final int FILTER_CHIP_HEIGHT = 32;
    public static final int FILTER_SECTION_HEIGHT = 48;
    public static final int RESULT_ITEM_HEIGHT = 72;

    // Recent searches limit
    public static final int MAX_RECENT_SEARCHES = 10;

    // Get screen state
    public static ScreenState getScreenState(
            String query,
            List<Bookmark> results,
            boolean isSearching,
            String error
    ) {
        if (error != null && !error.isEmpty()) {
            return ScreenState.ERROR;
        }
        if (isSearching) {
            return ScreenState.SEARCHING;
        }
        if (query == null || query.trim().isEmpty()) {
            return ScreenState.INITIAL;
        }
        if (results == null || results.isEmpty()) {
            return ScreenState.NO_RESULTS;
        }
        return ScreenState.RESULTS;
    }

    // Get state message
    public static String getStateMessage(ScreenState state, String query) {
        switch (state) {
            case INITIAL:
                return "Search for bookmarks by title, URL, or description";
            case SEARCHING:
                return "Searching...";
            case NO_RESULTS:
                return "No bookmarks found for \"" + query + "\"";
            case ERROR:
                return "An error occurred while searching";
            default:
                return "";
        }
    }

    // Format result count
    public static String formatResultCount(int count) {
        if (count == 0) {
            return "No results";
        } else if (count == 1) {
            return "1 result";
        } else {
            return count + " results";
        }
    }

    // Get sort order display name
    public static String getSortOrderDisplayName(SearchViewModel.SortOrder sortOrder) {
        if (sortOrder == null) return "Date (newest)";

        switch (sortOrder) {
            case DATE_DESC:
                return "Date (newest)";
            case DATE_ASC:
                return "Date (oldest)";
            case TITLE_ASC:
                return "Title (A-Z)";
            case TITLE_DESC:
                return "Title (Z-A)";
            default:
                return "Date (newest)";
        }
    }

    // Get filter display text
    public static String getFilterDisplayText(Category category) {
        if (category == null) {
            return "All categories";
        }
        return category.getName();
    }

    // Check if has active filters
    public static boolean hasActiveFilters(Long categoryId, SearchViewModel.SortOrder sortOrder) {
        boolean hasCategory = categoryId != null && categoryId > 0;
        boolean hasNonDefaultSort = sortOrder != null && sortOrder != SearchViewModel.SortOrder.DATE_DESC;
        return hasCategory || hasNonDefaultSort;
    }

    // Recent search item
    public static class RecentSearch {
        public final String query;
        public final long timestamp;

        public RecentSearch(String query, long timestamp) {
            this.query = query;
            this.timestamp = timestamp;
        }
    }

    // Highlight configuration for search results
    public static class HighlightConfig {
        public final int startIndex;
        public final int endIndex;
        public final int highlightColor;

        public HighlightConfig(int startIndex, int endIndex, int highlightColor) {
            this.startIndex = startIndex;
            this.endIndex = endIndex;
            this.highlightColor = highlightColor;
        }
    }

    // Find highlight positions in text
    public static HighlightConfig findHighlight(String text, String query) {
        if (text == null || query == null || query.isEmpty()) {
            return null;
        }

        String lowerText = text.toLowerCase();
        String lowerQuery = query.toLowerCase();

        int index = lowerText.indexOf(lowerQuery);
        if (index == -1) {
            return null;
        }

        // Use a yellow highlight color
        return new HighlightConfig(index, index + query.length(), 0xFFFFEB3B);
    }

    // Open search keyboard helper
    public static void requestFocus(Context context) {
        // This would be called to show keyboard when search screen opens
        // Implementation depends on the Compose focus system
    }
}
