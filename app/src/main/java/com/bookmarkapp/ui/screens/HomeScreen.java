package com.bookmarkapp.ui.screens;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.browser.customtabs.CustomTabsIntent;

import com.bookmarkapp.data.model.Bookmark;
import com.bookmarkapp.data.model.Category;
import com.bookmarkapp.ui.components.BookmarkCard;
import com.bookmarkapp.ui.components.BookmarkGrid;
import com.bookmarkapp.ui.theme.AppTheme;

import java.util.List;

public class HomeScreen {

    public interface HomeScreenCallbacks {
        void onBookmarkClick(Bookmark bookmark);
        void onBookmarkLongClick(Bookmark bookmark);
        void onFavoriteClick(Bookmark bookmark);
        void onShareClick(Bookmark bookmark);
        void onDeleteClick(Bookmark bookmark);
        void onCategorySelect(Category category);
        void onSearchClick();
        void onAddBookmarkClick();
        void onRefresh();
    }

    // Screen state
    public enum ScreenState {
        LOADING,
        CONTENT,
        EMPTY,
        ERROR
    }

    // Tab configuration
    public static final String TAB_ALL = "All";
    public static final String TAB_FAVORITES = "Favorites";

    // FAB configuration
    public static final int FAB_SIZE = 56;
    public static final int FAB_MARGIN = AppTheme.SPACING_MEDIUM;

    // Top bar configuration
    public static final int TOP_BAR_HEIGHT = 64;
    public static final String APP_TITLE = "Bookmarks";

    // Open bookmark in browser
    public static void openBookmark(Context context, Bookmark bookmark) {
        if (bookmark == null || bookmark.getUrl() == null) return;

        try {
            // Try Chrome Custom Tabs first
            CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
            builder.setShowTitle(true);
            builder.setShareState(CustomTabsIntent.SHARE_STATE_ON);

            CustomTabsIntent customTabsIntent = builder.build();
            customTabsIntent.launchUrl(context, Uri.parse(bookmark.getUrl()));
        } catch (Exception e) {
            // Fall back to regular browser intent
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(bookmark.getUrl()));
            browserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(browserIntent);
        }
    }

    // Share bookmark
    public static void shareBookmark(Context context, Bookmark bookmark) {
        if (bookmark == null) return;

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");

        String title = bookmark.getTitle() != null ? bookmark.getTitle() : bookmark.getUrl();
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, title);
        shareIntent.putExtra(Intent.EXTRA_TEXT, bookmark.getUrl());

        context.startActivity(Intent.createChooser(shareIntent, "Share bookmark"));
    }

    // Get screen state based on data
    public static ScreenState getScreenState(List<Bookmark> bookmarks, boolean isLoading, String error) {
        if (isLoading) {
            return ScreenState.LOADING;
        }
        if (error != null && !error.isEmpty()) {
            return ScreenState.ERROR;
        }
        if (bookmarks == null || bookmarks.isEmpty()) {
            return ScreenState.EMPTY;
        }
        return ScreenState.CONTENT;
    }

    // Get category chip text
    public static String getCategoryChipText(Category category, int bookmarkCount) {
        if (category == null) return TAB_ALL;

        if (bookmarkCount > 0) {
            return category.getName() + " (" + bookmarkCount + ")";
        }
        return category.getName();
    }

    // Get empty state configuration
    public static class EmptyStateConfig {
        public final String title;
        public final String subtitle;
        public final String actionText;
        public final boolean showAction;

        public EmptyStateConfig(String title, String subtitle, String actionText, boolean showAction) {
            this.title = title;
            this.subtitle = subtitle;
            this.actionText = actionText;
            this.showAction = showAction;
        }
    }

    public static EmptyStateConfig getEmptyStateConfig(boolean hasFilter, boolean isFavoritesTab) {
        if (isFavoritesTab) {
            return new EmptyStateConfig(
                    "No favorites yet",
                    "Tap the heart icon on a bookmark to add it to favorites",
                    null,
                    false
            );
        }

        if (hasFilter) {
            return new EmptyStateConfig(
                    "No bookmarks in this category",
                    "Bookmarks will appear here when they're categorized",
                    "View all",
                    true
            );
        }

        return new EmptyStateConfig(
                "No bookmarks yet",
                "Share a URL from any app to save it here",
                "Add bookmark",
                true
        );
    }

    // Error state configuration
    public static class ErrorStateConfig {
        public final String title;
        public final String message;
        public final String actionText;

        public ErrorStateConfig(String title, String message, String actionText) {
            this.title = title;
            this.message = message;
            this.actionText = actionText;
        }
    }

    public static ErrorStateConfig getErrorStateConfig(String error) {
        return new ErrorStateConfig(
                "Something went wrong",
                error != null ? error : "An unexpected error occurred",
                "Retry"
        );
    }
}
