package com.bookmarkapp.ui.components;

import com.bookmarkapp.ui.theme.AppTheme;

public class SearchBar {

    public interface SearchBarCallbacks {
        void onQueryChange(String query);
        void onSearch(String query);
        void onClear();
        void onBackClick();
    }

    // Search bar configuration
    public static final int HEIGHT = 56;
    public static final int CORNER_RADIUS = AppTheme.CORNER_EXTRA_LARGE;
    public static final int ICON_SIZE = 24;
    public static final int HORIZONTAL_PADDING = AppTheme.SPACING_MEDIUM;

    // Placeholder text
    public static final String PLACEHOLDER_TEXT = "Search bookmarks...";

    // Debounce delay for search (milliseconds)
    public static final long DEBOUNCE_DELAY_MS = 300;

    // Minimum query length to trigger search
    public static final int MIN_QUERY_LENGTH = 1;

    // Helper method to validate query
    public static boolean isValidQuery(String query) {
        return query != null && query.trim().length() >= MIN_QUERY_LENGTH;
    }

    // Helper method to clean query
    public static String cleanQuery(String query) {
        if (query == null) return "";
        return query.trim();
    }

    // Get search hint based on context
    public static String getSearchHint(boolean hasCategoryFilter, String categoryName) {
        if (hasCategoryFilter && categoryName != null) {
            return "Search in " + categoryName + "...";
        }
        return PLACEHOLDER_TEXT;
    }

    // Get "no results" message
    public static String getNoResultsMessage(String query) {
        if (query == null || query.isEmpty()) {
            return "Start typing to search";
        }
        return "No results for \"" + query + "\"";
    }

    // Format search result count
    public static String formatResultCount(int count, String query) {
        if (count == 0) {
            return "No results";
        } else if (count == 1) {
            return "1 result for \"" + query + "\"";
        } else {
            return count + " results for \"" + query + "\"";
        }
    }

    // Highlight matching text (returns indices)
    public static int[] findMatchIndices(String text, String query) {
        if (text == null || query == null || query.isEmpty()) {
            return new int[0];
        }

        String lowerText = text.toLowerCase();
        String lowerQuery = query.toLowerCase();

        int index = lowerText.indexOf(lowerQuery);
        if (index == -1) {
            return new int[0];
        }

        return new int[]{index, index + query.length()};
    }
}
