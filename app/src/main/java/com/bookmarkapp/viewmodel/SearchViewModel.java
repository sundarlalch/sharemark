package com.bookmarkapp.viewmodel;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.bookmarkapp.data.model.Bookmark;
import com.bookmarkapp.data.model.Category;
import com.bookmarkapp.data.repository.BookmarkRepository;

import java.util.Collections;
import java.util.List;

public class SearchViewModel extends AndroidViewModel {

    private final BookmarkRepository repository;
    private final Handler searchHandler;
    private Runnable pendingSearch;

    private static final long DEBOUNCE_DELAY_MS = 300;

    private final MutableLiveData<String> searchQuery = new MutableLiveData<>("");
    private final MutableLiveData<Long> filterCategoryId = new MutableLiveData<>();
    private final MutableLiveData<SortOrder> sortOrder = new MutableLiveData<>(SortOrder.DATE_DESC);
    private final MutableLiveData<Boolean> isSearching = new MutableLiveData<>(false);

    private final LiveData<List<Bookmark>> searchResults;
    private final LiveData<List<Category>> allCategories;

    public enum SortOrder {
        DATE_DESC,
        DATE_ASC,
        TITLE_ASC,
        TITLE_DESC
    }

    public SearchViewModel(@NonNull Application application) {
        super(application);
        repository = new BookmarkRepository(application);
        searchHandler = new Handler(Looper.getMainLooper());
        allCategories = repository.getAllCategories();

        // Create search results that respond to query changes
        searchResults = Transformations.switchMap(searchQuery, query -> {
            if (query == null || query.trim().isEmpty()) {
                return new MutableLiveData<>(Collections.emptyList());
            }

            Long categoryId = filterCategoryId.getValue();
            if (categoryId != null && categoryId > 0) {
                return repository.searchBookmarksInCategory(query.trim(), categoryId);
            } else {
                return repository.searchBookmarks(query.trim());
            }
        });
    }

    // Getters
    public LiveData<List<Bookmark>> getSearchResults() {
        return searchResults;
    }

    public LiveData<List<Category>> getAllCategories() {
        return allCategories;
    }

    public LiveData<String> getSearchQuery() {
        return searchQuery;
    }

    public LiveData<Long> getFilterCategoryId() {
        return filterCategoryId;
    }

    public LiveData<SortOrder> getSortOrder() {
        return sortOrder;
    }

    public LiveData<Boolean> getIsSearching() {
        return isSearching;
    }

    // Search operations
    public void search(String query) {
        // Cancel any pending search
        if (pendingSearch != null) {
            searchHandler.removeCallbacks(pendingSearch);
        }

        // Debounce search
        pendingSearch = () -> {
            isSearching.setValue(true);
            searchQuery.setValue(query);
            isSearching.setValue(false);
        };

        searchHandler.postDelayed(pendingSearch, DEBOUNCE_DELAY_MS);
    }

    public void searchImmediate(String query) {
        if (pendingSearch != null) {
            searchHandler.removeCallbacks(pendingSearch);
        }
        isSearching.setValue(true);
        searchQuery.setValue(query);
        isSearching.setValue(false);
    }

    public void clearSearch() {
        if (pendingSearch != null) {
            searchHandler.removeCallbacks(pendingSearch);
        }
        searchQuery.setValue("");
    }

    // Filter operations
    public void setFilterCategory(Long categoryId) {
        filterCategoryId.setValue(categoryId);
        // Re-trigger search with new filter
        String currentQuery = searchQuery.getValue();
        if (currentQuery != null && !currentQuery.isEmpty()) {
            searchQuery.setValue(currentQuery);
        }
    }

    public void clearFilter() {
        filterCategoryId.setValue(null);
        // Re-trigger search without filter
        String currentQuery = searchQuery.getValue();
        if (currentQuery != null && !currentQuery.isEmpty()) {
            searchQuery.setValue(currentQuery);
        }
    }

    // Sort operations
    public void setSortOrder(SortOrder order) {
        sortOrder.setValue(order);
    }

    public void toggleSortOrder() {
        SortOrder current = sortOrder.getValue();
        if (current == null) current = SortOrder.DATE_DESC;

        switch (current) {
            case DATE_DESC:
                sortOrder.setValue(SortOrder.DATE_ASC);
                break;
            case DATE_ASC:
                sortOrder.setValue(SortOrder.TITLE_ASC);
                break;
            case TITLE_ASC:
                sortOrder.setValue(SortOrder.TITLE_DESC);
                break;
            case TITLE_DESC:
                sortOrder.setValue(SortOrder.DATE_DESC);
                break;
        }
    }

    // Bookmark operations (for quick actions from search results)
    public void deleteBookmark(Bookmark bookmark) {
        repository.delete(bookmark);
    }

    public void toggleFavorite(Bookmark bookmark) {
        repository.updateFavoriteStatus(bookmark.getId(), !bookmark.isFavorite());
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (pendingSearch != null) {
            searchHandler.removeCallbacks(pendingSearch);
        }
    }
}
