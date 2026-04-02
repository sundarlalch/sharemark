package com.bookmarkapp.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.bookmarkapp.data.model.Bookmark;
import com.bookmarkapp.data.model.Category;
import com.bookmarkapp.data.repository.BookmarkRepository;
import com.bookmarkapp.ml.WebPageAnalyzer;

import java.util.List;

public class BookmarkViewModel extends AndroidViewModel {

    private final BookmarkRepository repository;
    private final WebPageAnalyzer webPageAnalyzer;

    private final LiveData<List<Bookmark>> allBookmarks;
    private final LiveData<List<Category>> allCategories;
    private final MutableLiveData<Long> selectedCategoryId = new MutableLiveData<>(null);
    private final LiveData<List<Bookmark>> filteredBookmarks;

    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Bookmark> recentlyAddedBookmark = new MutableLiveData<>();

    public BookmarkViewModel(@NonNull Application application) {
        super(application);
        repository = new BookmarkRepository(application);
        webPageAnalyzer = new WebPageAnalyzer(application);

        allBookmarks = repository.getAllBookmarks();
        allCategories = repository.getAllCategories();

        // Filter bookmarks based on selected category
        filteredBookmarks = Transformations.switchMap(selectedCategoryId, categoryId -> {
            if (categoryId == null || categoryId == 0) {
                return repository.getAllBookmarks();
            } else {
                return repository.getBookmarksByCategory(categoryId);
            }
        });
    }

    // Getters for LiveData
    public LiveData<List<Bookmark>> getAllBookmarks() {
        return allBookmarks;
    }

    public LiveData<List<Category>> getAllCategories() {
        return allCategories;
    }

    public LiveData<List<Bookmark>> getFilteredBookmarks() {
        return filteredBookmarks;
    }

    public LiveData<List<Bookmark>> getFavoriteBookmarks() {
        return repository.getFavoriteBookmarks();
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<Bookmark> getRecentlyAddedBookmark() {
        return recentlyAddedBookmark;
    }

    public LiveData<Integer> getBookmarkCount() {
        return repository.getBookmarkCount();
    }

    // Category selection
    public void selectCategory(Long categoryId) {
        selectedCategoryId.setValue(categoryId);
    }

    public Long getSelectedCategoryId() {
        return selectedCategoryId.getValue();
    }

    // Bookmark operations
    public void addBookmarkFromUrl(String url) {
        isLoading.setValue(true);

        webPageAnalyzer.analyzeUrl(url, new WebPageAnalyzer.AnalysisCallback() {
            @Override
            public void onAnalysisComplete(Bookmark bookmark, String categoryName) {
                // Find category ID
                repository.getCategoryByName(categoryName, category -> {
                    if (category != null) {
                        bookmark.setCategoryId(category.getId());
                    }

                    // Insert bookmark
                    repository.insert(bookmark, id -> {
                        bookmark.setId(id);
                        recentlyAddedBookmark.postValue(bookmark);
                        isLoading.postValue(false);
                    });
                });
            }

            @Override
            public void onAnalysisError(Exception e) {
                errorMessage.postValue("Failed to analyze URL: " + e.getMessage());
                isLoading.postValue(false);
            }
        });
    }

    public void addBookmark(Bookmark bookmark) {
        repository.insert(bookmark);
    }

    public void updateBookmark(Bookmark bookmark) {
        repository.update(bookmark);
    }

    public void deleteBookmark(Bookmark bookmark) {
        repository.delete(bookmark);
    }

    public void deleteBookmarkById(long id) {
        repository.deleteById(id);
    }

    public void deleteBookmarksByIds(List<Long> ids) {
        repository.deleteByIds(ids);
    }

    public void updateBookmarkNotes(long id, String notes) {
        repository.updateNotes(id, notes);
    }

    public void toggleFavorite(Bookmark bookmark) {
        repository.updateFavoriteStatus(bookmark.getId(), !bookmark.isFavorite());
    }

    public void toggleFavorite(long id, boolean currentStatus) {
        repository.updateFavoriteStatus(id, !currentStatus);
    }

    // Category operations
    public void addCategory(Category category) {
        repository.insert(category);
    }

    public void updateCategory(Category category) {
        repository.update(category);
    }

    public void deleteCategory(Category category) {
        repository.delete(category);
    }

    public void clearError() {
        errorMessage.setValue(null);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        webPageAnalyzer.shutdown();
    }
}
