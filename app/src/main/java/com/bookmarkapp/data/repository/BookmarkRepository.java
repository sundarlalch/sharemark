package com.bookmarkapp.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.bookmarkapp.data.database.AppDatabase;
import com.bookmarkapp.data.database.BookmarkDao;
import com.bookmarkapp.data.database.CategoryDao;
import com.bookmarkapp.data.model.Bookmark;
import com.bookmarkapp.data.model.Category;

import java.util.List;
import java.util.concurrent.ExecutorService;

public class BookmarkRepository {

    private final BookmarkDao bookmarkDao;
    private final CategoryDao categoryDao;
    private final ExecutorService executorService;
    private final LiveData<List<Bookmark>> allBookmarks;
    private final LiveData<List<Category>> allCategories;

    public BookmarkRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        bookmarkDao = db.bookmarkDao();
        categoryDao = db.categoryDao();
        executorService = AppDatabase.databaseWriteExecutor;
        allBookmarks = bookmarkDao.getAllBookmarks();
        allCategories = categoryDao.getAllCategories();
    }

    // Bookmark operations
    public LiveData<List<Bookmark>> getAllBookmarks() {
        return allBookmarks;
    }

    public LiveData<Bookmark> getBookmarkById(long id) {
        return bookmarkDao.getBookmarkById(id);
    }

    public LiveData<List<Bookmark>> getBookmarksByCategory(long categoryId) {
        return bookmarkDao.getBookmarksByCategory(categoryId);
    }

    public LiveData<List<Bookmark>> getFavoriteBookmarks() {
        return bookmarkDao.getFavoriteBookmarks();
    }

    public LiveData<List<Bookmark>> searchBookmarks(String query) {
        return bookmarkDao.searchBookmarks(query);
    }

    public LiveData<List<Bookmark>> searchBookmarksInCategory(String query, long categoryId) {
        return bookmarkDao.searchBookmarksInCategory(query, categoryId);
    }

    public void insert(Bookmark bookmark) {
        executorService.execute(() -> bookmarkDao.insert(bookmark));
    }

    public void insert(Bookmark bookmark, OnInsertCompleteListener listener) {
        executorService.execute(() -> {
            long id = bookmarkDao.insert(bookmark);
            if (listener != null) {
                listener.onInsertComplete(id);
            }
        });
    }

    public void update(Bookmark bookmark) {
        executorService.execute(() -> bookmarkDao.update(bookmark));
    }

    public void delete(Bookmark bookmark) {
        executorService.execute(() -> bookmarkDao.delete(bookmark));
    }

    public void deleteById(long id) {
        executorService.execute(() -> bookmarkDao.deleteById(id));
    }

    public void deleteByIds(List<Long> ids) {
        executorService.execute(() -> bookmarkDao.deleteByIds(ids));
    }

    public void updateFavoriteStatus(long id, boolean isFavorite) {
        executorService.execute(() -> bookmarkDao.updateFavoriteStatus(id, isFavorite));
    }

    public void updateNotes(long id, String notes) {
        executorService.execute(() -> bookmarkDao.updateNotes(id, notes));
    }

    public void getBookmarkByUrl(String url, OnBookmarkFoundListener listener) {
        executorService.execute(() -> {
            Bookmark bookmark = bookmarkDao.getBookmarkByUrl(url);
            if (listener != null) {
                listener.onBookmarkFound(bookmark);
            }
        });
    }

    public LiveData<Integer> getBookmarkCount() {
        return bookmarkDao.getBookmarkCount();
    }

    // Category operations
    public LiveData<List<Category>> getAllCategories() {
        return allCategories;
    }

    public LiveData<Category> getCategoryById(long id) {
        return categoryDao.getCategoryById(id);
    }

    public void getCategoryByName(String name, OnCategoryFoundListener listener) {
        executorService.execute(() -> {
            Category category = categoryDao.getCategoryByName(name);
            if (listener != null) {
                listener.onCategoryFound(category);
            }
        });
    }

    public void insert(Category category) {
        executorService.execute(() -> categoryDao.insert(category));
    }

    public void update(Category category) {
        executorService.execute(() -> categoryDao.update(category));
    }

    public void delete(Category category) {
        executorService.execute(() -> categoryDao.delete(category));
    }

    public List<Category> getAllCategoriesSync() {
        return categoryDao.getAllCategoriesSync();
    }

    // Callback interfaces
    public interface OnInsertCompleteListener {
        void onInsertComplete(long id);
    }

    public interface OnBookmarkFoundListener {
        void onBookmarkFound(Bookmark bookmark);
    }

    public interface OnCategoryFoundListener {
        void onCategoryFound(Category category);
    }
}
