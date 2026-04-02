package com.bookmarkapp.data.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.bookmarkapp.data.model.Bookmark;
import com.bookmarkapp.data.model.Category;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Bookmark.class, Category.class}, version = 2, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract BookmarkDao bookmarkDao();
    public abstract CategoryDao categoryDao();

    private static volatile AppDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    // Migration from version 1 to 2: Add notes column
    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE bookmarks ADD COLUMN notes TEXT");
        }
    };

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    "bookmark_database"
                            )
                            .addMigrations(MIGRATION_1_2)
                            .addCallback(sRoomDatabaseCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static final RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);

            databaseWriteExecutor.execute(() -> {
                CategoryDao categoryDao = INSTANCE.categoryDao();
                List<Category> defaultCategories = createDefaultCategories();
                categoryDao.insertAll(defaultCategories);
            });
        }
    };

    private static List<Category> createDefaultCategories() {
        List<Category> categories = new ArrayList<>();

        categories.add(new Category(Category.NEWS_ARTICLES, "article", 0xFF2196F3));
        categories.add(new Category(Category.SOCIAL_MEDIA, "people", 0xFF9C27B0));
        categories.add(new Category(Category.SHOPPING, "shopping_cart", 0xFFFF9800));
        categories.add(new Category(Category.ENTERTAINMENT, "movie", 0xFFE91E63));
        categories.add(new Category(Category.DEVELOPMENT, "code", 0xFF4CAF50));
        categories.add(new Category(Category.EDUCATION, "school", 0xFF3F51B5));
        categories.add(new Category(Category.FINANCE, "account_balance", 0xFF009688));
        categories.add(new Category(Category.TRAVEL, "flight", 0xFF00BCD4));
        categories.add(new Category(Category.FOOD, "restaurant", 0xFFFF5722));
        categories.add(new Category(Category.SPORTS, "sports_soccer", 0xFF8BC34A));
        categories.add(new Category(Category.OTHER, "bookmark", 0xFF607D8B));

        return categories;
    }
}
