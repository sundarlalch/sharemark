package com.bookmarkapp.ui.screens;

import com.bookmarkapp.data.model.Category;
import com.bookmarkapp.ui.theme.AppTheme;

import java.util.List;

public class CategoryScreen {

    public interface CategoryScreenCallbacks {
        void onBackClick();
        void onCategoryClick(Category category);
        void onCategoryLongClick(Category category);
        void onAddCategoryClick();
        void onEditCategory(Category category);
        void onDeleteCategory(Category category);
        void onColorSelect(Category category, int color);
    }

    // Configuration
    public static final int CATEGORY_ITEM_HEIGHT = 64;
    public static final int COLOR_PICKER_ITEM_SIZE = 48;
    public static final int ICON_SIZE = 24;

    // Screen title
    public static final String SCREEN_TITLE = "Categories";
    public static final String ADD_CATEGORY_TITLE = "Add Category";
    public static final String EDIT_CATEGORY_TITLE = "Edit Category";

    // Validation
    public static final int MIN_CATEGORY_NAME_LENGTH = 1;
    public static final int MAX_CATEGORY_NAME_LENGTH = 50;

    // Available colors for categories
    public static final int[] AVAILABLE_COLORS = AppTheme.CATEGORY_COLORS;

    // Available icons for categories
    public static final String[] AVAILABLE_ICONS = {
            "bookmark",
            "article",
            "people",
            "shopping_cart",
            "movie",
            "code",
            "school",
            "account_balance",
            "flight",
            "restaurant",
            "sports_soccer",
            "music_note",
            "photo",
            "work",
            "home",
            "star"
    };

    // Validate category name
    public static ValidationResult validateCategoryName(String name, List<Category> existingCategories) {
        if (name == null || name.trim().isEmpty()) {
            return new ValidationResult(false, "Category name cannot be empty");
        }

        String trimmedName = name.trim();

        if (trimmedName.length() < MIN_CATEGORY_NAME_LENGTH) {
            return new ValidationResult(false, "Category name is too short");
        }

        if (trimmedName.length() > MAX_CATEGORY_NAME_LENGTH) {
            return new ValidationResult(false, "Category name is too long (max " + MAX_CATEGORY_NAME_LENGTH + " characters)");
        }

        // Check for duplicates
        if (existingCategories != null) {
            for (Category category : existingCategories) {
                if (category.getName().equalsIgnoreCase(trimmedName)) {
                    return new ValidationResult(false, "A category with this name already exists");
                }
            }
        }

        return new ValidationResult(true, null);
    }

    // Validation result
    public static class ValidationResult {
        public final boolean isValid;
        public final String errorMessage;

        public ValidationResult(boolean isValid, String errorMessage) {
            this.isValid = isValid;
            this.errorMessage = errorMessage;
        }
    }

    // Check if category can be deleted
    public static boolean canDeleteCategory(Category category) {
        // System categories (predefined ones) shouldn't be deleted
        return category != null && !isSystemCategory(category.getName());
    }

    // Check if this is a system category
    public static boolean isSystemCategory(String categoryName) {
        return categoryName != null && (
                categoryName.equals(Category.NEWS_ARTICLES) ||
                categoryName.equals(Category.SOCIAL_MEDIA) ||
                categoryName.equals(Category.SHOPPING) ||
                categoryName.equals(Category.ENTERTAINMENT) ||
                categoryName.equals(Category.DEVELOPMENT) ||
                categoryName.equals(Category.EDUCATION) ||
                categoryName.equals(Category.FINANCE) ||
                categoryName.equals(Category.TRAVEL) ||
                categoryName.equals(Category.FOOD) ||
                categoryName.equals(Category.SPORTS) ||
                categoryName.equals(Category.OTHER)
        );
    }

    // Get icon for category
    public static String getIconForCategory(Category category) {
        if (category == null || category.getIconName() == null) {
            return "bookmark";
        }
        return category.getIconName();
    }

    // Get color for category
    public static int getColorForCategory(Category category) {
        if (category == null) {
            return AVAILABLE_COLORS[0];
        }
        return category.getColor();
    }

    // Get default color for new category
    public static int getDefaultColor() {
        return AVAILABLE_COLORS[0];
    }

    // Get default icon for new category
    public static String getDefaultIcon() {
        return "bookmark";
    }

    // Format bookmark count
    public static String formatBookmarkCount(int count) {
        if (count == 0) {
            return "No bookmarks";
        } else if (count == 1) {
            return "1 bookmark";
        } else {
            return count + " bookmarks";
        }
    }

    // Dialog configuration
    public static class DialogConfig {
        public final String title;
        public final String positiveButton;
        public final String negativeButton;
        public final boolean showDelete;

        public DialogConfig(String title, String positiveButton, String negativeButton, boolean showDelete) {
            this.title = title;
            this.positiveButton = positiveButton;
            this.negativeButton = negativeButton;
            this.showDelete = showDelete;
        }
    }

    public static DialogConfig getAddDialogConfig() {
        return new DialogConfig(ADD_CATEGORY_TITLE, "Add", "Cancel", false);
    }

    public static DialogConfig getEditDialogConfig() {
        return new DialogConfig(EDIT_CATEGORY_TITLE, "Save", "Cancel", true);
    }

    // Delete confirmation
    public static String getDeleteConfirmationMessage(Category category, int bookmarkCount) {
        if (bookmarkCount > 0) {
            return "Are you sure you want to delete \"" + category.getName() + "\"? " +
                    "The " + bookmarkCount + " bookmark(s) in this category will be moved to \"Other\".";
        }
        return "Are you sure you want to delete \"" + category.getName() + "\"?";
    }
}
