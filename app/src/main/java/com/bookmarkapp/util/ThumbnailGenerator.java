package com.bookmarkapp.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;

public class ThumbnailGenerator {

    private final Context context;
    private final File cacheDir;

    private static final int THUMBNAIL_SIZE = 200;
    private static final int CORNER_RADIUS = 16;

    // Material Design colors for placeholder thumbnails
    private static final int[] COLORS = {
            0xFF2196F3, // Blue
            0xFF4CAF50, // Green
            0xFFFF9800, // Orange
            0xFF9C27B0, // Purple
            0xFFE91E63, // Pink
            0xFF00BCD4, // Cyan
            0xFF795548, // Brown
            0xFF607D8B  // Blue Grey
    };

    public ThumbnailGenerator(Context context) {
        this.context = context;
        this.cacheDir = new File(context.getCacheDir(), "thumbnails");
        if (!cacheDir.exists()) {
            cacheDir.mkdirs();
        }
    }

    public String generatePlaceholderThumbnail(String domain) {
        if (domain == null || domain.isEmpty()) {
            domain = "?";
        }

        // Get first letter of domain
        String letter = domain.substring(0, 1).toUpperCase(Locale.ROOT);

        // Generate consistent color based on domain
        int colorIndex = Math.abs(domain.hashCode()) % COLORS.length;
        int color = COLORS[colorIndex];

        // Create bitmap
        Bitmap bitmap = Bitmap.createBitmap(THUMBNAIL_SIZE, THUMBNAIL_SIZE, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        // Draw rounded rectangle background
        Paint bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bgPaint.setColor(color);
        RectF rect = new RectF(0, 0, THUMBNAIL_SIZE, THUMBNAIL_SIZE);
        canvas.drawRoundRect(rect, CORNER_RADIUS, CORNER_RADIUS, bgPaint);

        // Draw letter
        Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(THUMBNAIL_SIZE * 0.5f);
        textPaint.setTextAlign(Paint.Align.CENTER);

        // Center text vertically
        Rect textBounds = new Rect();
        textPaint.getTextBounds(letter, 0, 1, textBounds);
        float x = THUMBNAIL_SIZE / 2f;
        float y = (THUMBNAIL_SIZE + textBounds.height()) / 2f;
        canvas.drawText(letter, x, y, textPaint);

        // Save to cache
        String filename = "placeholder_" + domain.hashCode() + ".png";
        File file = new File(cacheDir, filename);

        try (FileOutputStream out = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
        } catch (IOException e) {
            return null;
        } finally {
            bitmap.recycle();
        }

        return file.getAbsolutePath();
    }

    public String getCachedThumbnailPath(String url) {
        if (url == null) return null;
        String filename = "thumb_" + url.hashCode() + ".png";
        File file = new File(cacheDir, filename);
        return file.exists() ? file.getAbsolutePath() : null;
    }

    public void clearCache() {
        if (cacheDir.exists()) {
            File[] files = cacheDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    file.delete();
                }
            }
        }
    }

    public long getCacheSize() {
        long size = 0;
        if (cacheDir.exists()) {
            File[] files = cacheDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    size += file.length();
                }
            }
        }
        return size;
    }
}
