package com.bookmarkapp.ui.theme;

public class AppTheme {

    // Color definitions (as ARGB integers)
    public static final int PRIMARY = 0xFF6750A4;
    public static final int ON_PRIMARY = 0xFFFFFFFF;
    public static final int PRIMARY_CONTAINER = 0xFFEADDFF;
    public static final int ON_PRIMARY_CONTAINER = 0xFF21005D;

    public static final int SECONDARY = 0xFF625B71;
    public static final int ON_SECONDARY = 0xFFFFFFFF;
    public static final int SECONDARY_CONTAINER = 0xFFE8DEF8;
    public static final int ON_SECONDARY_CONTAINER = 0xFF1D192B;

    public static final int TERTIARY = 0xFF7D5260;
    public static final int ON_TERTIARY = 0xFFFFFFFF;
    public static final int TERTIARY_CONTAINER = 0xFFFFD8E4;
    public static final int ON_TERTIARY_CONTAINER = 0xFF31111D;

    public static final int ERROR = 0xFFB3261E;
    public static final int ON_ERROR = 0xFFFFFFFF;
    public static final int ERROR_CONTAINER = 0xFFF9DEDC;
    public static final int ON_ERROR_CONTAINER = 0xFF410E0B;

    public static final int BACKGROUND = 0xFFFFFBFE;
    public static final int ON_BACKGROUND = 0xFF1C1B1F;
    public static final int SURFACE = 0xFFFFFBFE;
    public static final int ON_SURFACE = 0xFF1C1B1F;
    public static final int SURFACE_VARIANT = 0xFFE7E0EC;
    public static final int ON_SURFACE_VARIANT = 0xFF49454F;

    public static final int OUTLINE = 0xFF79747E;
    public static final int OUTLINE_VARIANT = 0xFFCAC4D0;

    // Category colors
    public static final int[] CATEGORY_COLORS = {
            0xFF2196F3, // Blue - News & Articles
            0xFF9C27B0, // Purple - Social Media
            0xFFFF9800, // Orange - Shopping
            0xFFE91E63, // Pink - Entertainment
            0xFF4CAF50, // Green - Development
            0xFF3F51B5, // Indigo - Education
            0xFF009688, // Teal - Finance
            0xFF00BCD4, // Cyan - Travel
            0xFFFF5722, // Deep Orange - Food
            0xFF8BC34A, // Light Green - Sports
            0xFF607D8B  // Blue Grey - Other
    };

    // Spacing values in dp
    public static final int SPACING_EXTRA_SMALL = 4;
    public static final int SPACING_SMALL = 8;
    public static final int SPACING_MEDIUM = 16;
    public static final int SPACING_LARGE = 24;
    public static final int SPACING_EXTRA_LARGE = 32;

    // Corner radius values in dp
    public static final int CORNER_SMALL = 8;
    public static final int CORNER_MEDIUM = 12;
    public static final int CORNER_LARGE = 16;
    public static final int CORNER_EXTRA_LARGE = 28;

    // Grid configuration
    public static final int GRID_COLUMNS_PHONE = 2;
    public static final int GRID_COLUMNS_TABLET = 3;
    public static final int GRID_COLUMNS_DESKTOP = 4;

    // Thumbnail sizes in dp
    public static final int THUMBNAIL_SIZE_SMALL = 48;
    public static final int THUMBNAIL_SIZE_MEDIUM = 80;
    public static final int THUMBNAIL_SIZE_LARGE = 120;

    // Card dimensions in dp
    public static final int CARD_MIN_HEIGHT = 160;
    public static final int CARD_IMAGE_HEIGHT = 100;

    private AppTheme() {
        // Private constructor to prevent instantiation
    }
}
