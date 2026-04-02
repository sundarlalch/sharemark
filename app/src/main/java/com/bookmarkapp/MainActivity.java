package com.bookmarkapp;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bookmarkapp.data.model.Bookmark;
import com.bookmarkapp.data.model.Category;
import com.bookmarkapp.ui.theme.AppTheme;
import com.bookmarkapp.viewmodel.BookmarkViewModel;
import com.bookmarkapp.viewmodel.SearchViewModel;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private BookmarkViewModel bookmarkViewModel;
    private SearchViewModel searchViewModel;
    private RecyclerView recyclerView;
    private BookmarkAdapter adapter;
    private ChipGroup categoryChipGroup;
    private View emptyState;
    private View loadingState;
    private EditText searchEditText;
    private FloatingActionButton fab;
    private FloatingActionButton deleteFab;

    private ActionMode actionMode;
    private boolean isSelectionMode = false;
    private boolean isSearchActive = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize ViewModels
        bookmarkViewModel = new ViewModelProvider(this).get(BookmarkViewModel.class);
        searchViewModel = new ViewModelProvider(this).get(SearchViewModel.class);

        // Setup views
        setupToolbar();
        setupRecyclerView();
        setupCategoryChips();
        setupFab();
        setupSearch();
        observeData();

        // Trigger initial load
        bookmarkViewModel.selectCategory(null);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.app_name);
        }
    }

    private void setupRecyclerView() {
        recyclerView = findViewById(R.id.recyclerView);
        emptyState = findViewById(R.id.emptyState);
        loadingState = findViewById(R.id.loadingState);

        int columns = getResources().getConfiguration().screenWidthDp < 600 ? 2 : 3;
        recyclerView.setLayoutManager(new GridLayoutManager(this, columns));

        adapter = new BookmarkAdapter(
                new ArrayList<>(),
                this::onBookmarkClick,
                this::onBookmarkLongClick,
                this::onFavoriteClick,
                this::onSelectionChanged
        );
        recyclerView.setAdapter(adapter);
    }

    private void setupCategoryChips() {
        categoryChipGroup = findViewById(R.id.categoryChipGroup);

        // Add "All" chip
        Chip allChip = new Chip(this);
        allChip.setText(R.string.tab_all);
        allChip.setCheckable(true);
        allChip.setChecked(true);
        allChip.setTag("all");
        allChip.setOnClickListener(v -> {
            showingFavorites = false;
            bookmarkViewModel.selectCategory(null);
            searchViewModel.setFilterCategory(null);
            uncheckAllChipsExcept(v);
        });
        categoryChipGroup.addView(allChip);

        // Add "Favorites" chip
        Chip favoritesChip = new Chip(this);
        favoritesChip.setText(R.string.tab_favorites);
        favoritesChip.setCheckable(true);
        favoritesChip.setTag("favorites");
        favoritesChip.setOnClickListener(v -> {
            showingFavorites = true;
            uncheckAllChipsExcept(v);
        });
        categoryChipGroup.addView(favoritesChip);
    }

    private boolean showingFavorites = false;

    private void uncheckAllChipsExcept(View selected) {
        for (int i = 0; i < categoryChipGroup.getChildCount(); i++) {
            Chip c = (Chip) categoryChipGroup.getChildAt(i);
            c.setChecked(c == selected);
        }
    }

    private void setupFab() {
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(v -> {
            Toast.makeText(this, R.string.add_bookmark_hint, Toast.LENGTH_LONG).show();
        });

        deleteFab = findViewById(R.id.deleteFab);
        if (deleteFab != null) {
            deleteFab.setOnClickListener(v -> deleteSelectedBookmarks());
        }
    }

    private void setupSearch() {
        searchEditText = findViewById(R.id.searchEditText);
        if (searchEditText != null) {
            searchEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    String query = s.toString().trim();
                    if (query.isEmpty()) {
                        isSearchActive = false;
                        // Trigger category filter refresh
                        bookmarkViewModel.selectCategory(bookmarkViewModel.getSelectedCategoryId());
                    } else {
                        isSearchActive = true;
                        searchViewModel.search(query);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });
        }
    }

    private void observeData() {
        // Observe filtered bookmarks (responds to category selection)
        bookmarkViewModel.getFilteredBookmarks().observe(this, bookmarks -> {
            if (!isSearchActive && !showingFavorites) {
                updateBookmarkList(bookmarks);
            }
        });

        // Observe favorite bookmarks
        bookmarkViewModel.getFavoriteBookmarks().observe(this, bookmarks -> {
            if (!isSearchActive && showingFavorites) {
                updateBookmarkList(bookmarks);
            }
        });

        // Observe categories
        bookmarkViewModel.getAllCategories().observe(this, categories -> {
            if (categories != null) {
                updateCategoryChips(categories);
            }
        });

        // Observe loading state
        bookmarkViewModel.getIsLoading().observe(this, isLoading -> {
            if (isLoading != null && isLoading) {
                loadingState.setVisibility(View.VISIBLE);
            } else {
                loadingState.setVisibility(View.GONE);
            }
        });

        // Observe search results
        searchViewModel.getSearchResults().observe(this, bookmarks -> {
            if (isSearchActive && bookmarks != null) {
                updateBookmarkList(bookmarks);
            }
        });
    }

    private void updateBookmarkList(List<Bookmark> bookmarks) {
        if (bookmarks != null && !bookmarks.isEmpty()) {
            adapter.updateBookmarks(bookmarks);
            recyclerView.setVisibility(View.VISIBLE);
            emptyState.setVisibility(View.GONE);
        } else {
            adapter.updateBookmarks(new ArrayList<>());
            recyclerView.setVisibility(View.GONE);
            emptyState.setVisibility(View.VISIBLE);
        }
        loadingState.setVisibility(View.GONE);
    }

    private void updateCategoryChips(List<Category> categories) {
        // Remove existing category chips (keep "All" chip)
        while (categoryChipGroup.getChildCount() > 1) {
            categoryChipGroup.removeViewAt(1);
        }

        // Add category chips
        for (Category category : categories) {
            Chip chip = new Chip(this);
            chip.setText(category.getName());
            chip.setCheckable(true);
            chip.setTag(category.getId());
            chip.setOnClickListener(v -> {
                Long categoryId = (Long) v.getTag();
                showingFavorites = false;
                bookmarkViewModel.selectCategory(categoryId);
                searchViewModel.setFilterCategory(categoryId);
                uncheckAllChipsExcept(v);
            });
            categoryChipGroup.addView(chip);
        }
    }

    private void onBookmarkClick(Bookmark bookmark) {
        if (isSelectionMode) {
            adapter.toggleSelection(bookmark.getId());
        } else {
            openBookmark(bookmark);
        }
    }

    private void onBookmarkLongClick(Bookmark bookmark) {
        showBookmarkOptionsDialog(bookmark);
    }

    private void openBookmark(Bookmark bookmark) {
        if (bookmark == null || bookmark.getUrl() == null) return;

        try {
            CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
            builder.setShowTitle(true);
            CustomTabsIntent customTabsIntent = builder.build();
            customTabsIntent.launchUrl(this, Uri.parse(bookmark.getUrl()));
        } catch (Exception e) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(bookmark.getUrl()));
            startActivity(browserIntent);
        }
    }

    private void onFavoriteClick(Bookmark bookmark) {
        bookmarkViewModel.toggleFavorite(bookmark);
    }

    private void onSelectionChanged(int selectedCount) {
        if (selectedCount > 0) {
            if (!isSelectionMode) {
                enterSelectionMode();
            }
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(selectedCount + " selected");
            }
        } else {
            exitSelectionMode();
        }
    }

    private void enterSelectionMode() {
        isSelectionMode = true;
        fab.setVisibility(View.GONE);
        if (deleteFab != null) {
            deleteFab.setVisibility(View.VISIBLE);
        }
    }

    private void exitSelectionMode() {
        isSelectionMode = false;
        adapter.clearSelection();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.app_name);
        }
        fab.setVisibility(View.VISIBLE);
        if (deleteFab != null) {
            deleteFab.setVisibility(View.GONE);
        }
    }

    private void deleteSelectedBookmarks() {
        Set<Long> selectedIds = adapter.getSelectedIds();
        if (selectedIds.isEmpty()) return;

        new AlertDialog.Builder(this)
                .setTitle("Delete Bookmarks")
                .setMessage("Delete " + selectedIds.size() + " selected bookmark(s)?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    bookmarkViewModel.deleteBookmarksByIds(new ArrayList<>(selectedIds));
                    exitSelectionMode();
                    Toast.makeText(this, "Bookmarks deleted", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showBookmarkOptionsDialog(Bookmark bookmark) {
        String[] options = {"Open", "Edit Notes", "Share", "Delete"};

        new AlertDialog.Builder(this)
                .setTitle(bookmark.getTitle() != null ? bookmark.getTitle() : bookmark.getUrl())
                .setItems(options, (dialog, which) -> {
                    switch (which) {
                        case 0: // Open
                            openBookmark(bookmark);
                            break;
                        case 1: // Edit Notes
                            showEditNotesDialog(bookmark);
                            break;
                        case 2: // Share
                            shareBookmark(bookmark);
                            break;
                        case 3: // Delete
                            confirmDeleteBookmark(bookmark);
                            break;
                    }
                })
                .show();
    }

    private void showEditNotesDialog(Bookmark bookmark) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_edit_notes, null);
        EditText notesEditText = dialogView.findViewById(R.id.notesEditText);

        if (bookmark.getNotes() != null) {
            notesEditText.setText(bookmark.getNotes());
        }

        new AlertDialog.Builder(this)
                .setTitle("Edit Notes")
                .setView(dialogView)
                .setPositiveButton("Save", (dialog, which) -> {
                    String notes = notesEditText.getText().toString().trim();
                    bookmarkViewModel.updateBookmarkNotes(bookmark.getId(), notes);
                    Toast.makeText(this, "Notes saved", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void shareBookmark(Bookmark bookmark) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        String shareText = bookmark.getUrl();
        if (bookmark.getTitle() != null && !bookmark.getTitle().isEmpty()) {
            shareText = bookmark.getTitle() + "\n" + bookmark.getUrl();
        }
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
        startActivity(Intent.createChooser(shareIntent, "Share bookmark"));
    }

    private void confirmDeleteBookmark(Bookmark bookmark) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Bookmark")
                .setMessage("Are you sure you want to delete this bookmark?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    bookmarkViewModel.deleteBookmark(bookmark);
                    Toast.makeText(this, "Bookmark deleted", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_search) {
            View searchContainer = findViewById(R.id.searchContainer);
            if (searchContainer != null) {
                boolean isVisible = searchContainer.getVisibility() == View.VISIBLE;
                if (isVisible) {
                    // Hiding search - clear the search
                    searchContainer.setVisibility(View.GONE);
                    if (searchEditText != null) {
                        searchEditText.setText("");
                    }
                    isSearchActive = false;
                    // Refresh the filtered view
                    bookmarkViewModel.selectCategory(bookmarkViewModel.getSelectedCategoryId());
                } else {
                    // Showing search
                    searchContainer.setVisibility(View.VISIBLE);
                    searchEditText.requestFocus();
                }
            }
            return true;
        } else if (item.getItemId() == R.id.action_select_all) {
            adapter.selectAll();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (isSelectionMode) {
            exitSelectionMode();
        } else {
            super.onBackPressed();
        }
    }

    // Bookmark Adapter with selection support
    static class BookmarkAdapter extends RecyclerView.Adapter<BookmarkAdapter.ViewHolder> {
        private List<Bookmark> bookmarks;
        private final Set<Long> selectedIds = new HashSet<>();
        private final OnBookmarkClickListener clickListener;
        private final OnBookmarkLongClickListener longClickListener;
        private final OnFavoriteClickListener favoriteListener;
        private final OnSelectionChangedListener selectionListener;

        interface OnBookmarkClickListener {
            void onClick(Bookmark bookmark);
        }

        interface OnBookmarkLongClickListener {
            void onLongClick(Bookmark bookmark);
        }

        interface OnFavoriteClickListener {
            void onClick(Bookmark bookmark);
        }

        interface OnSelectionChangedListener {
            void onSelectionChanged(int selectedCount);
        }

        BookmarkAdapter(List<Bookmark> bookmarks,
                        OnBookmarkClickListener clickListener,
                        OnBookmarkLongClickListener longClickListener,
                        OnFavoriteClickListener favoriteListener,
                        OnSelectionChangedListener selectionListener) {
            this.bookmarks = bookmarks;
            this.clickListener = clickListener;
            this.longClickListener = longClickListener;
            this.favoriteListener = favoriteListener;
            this.selectionListener = selectionListener;
        }

        void updateBookmarks(List<Bookmark> newBookmarks) {
            this.bookmarks = newBookmarks;
            notifyDataSetChanged();
        }

        void toggleSelection(long id) {
            if (selectedIds.contains(id)) {
                selectedIds.remove(id);
            } else {
                selectedIds.add(id);
            }
            notifyDataSetChanged();
            selectionListener.onSelectionChanged(selectedIds.size());
        }

        void selectAll() {
            selectedIds.clear();
            for (Bookmark bookmark : bookmarks) {
                selectedIds.add(bookmark.getId());
            }
            notifyDataSetChanged();
            selectionListener.onSelectionChanged(selectedIds.size());
        }

        void clearSelection() {
            selectedIds.clear();
            notifyDataSetChanged();
        }

        Set<Long> getSelectedIds() {
            return new HashSet<>(selectedIds);
        }

        boolean isSelected(long id) {
            return selectedIds.contains(id);
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_bookmark, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Bookmark bookmark = bookmarks.get(position);
            holder.bind(bookmark, clickListener, longClickListener, favoriteListener, isSelected(bookmark.getId()));
        }

        @Override
        public int getItemCount() {
            return bookmarks.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            private final ImageView thumbnailImage;
            private final TextView titleText;
            private final TextView domainText;
            private final TextView notesText;
            private final ImageView favoriteButton;
            private final View placeholderView;
            private final TextView placeholderText;
            private final View selectionOverlay;
            private final ImageView checkIcon;

            ViewHolder(View itemView) {
                super(itemView);
                thumbnailImage = itemView.findViewById(R.id.thumbnailImage);
                titleText = itemView.findViewById(R.id.titleText);
                domainText = itemView.findViewById(R.id.domainText);
                notesText = itemView.findViewById(R.id.notesText);
                favoriteButton = itemView.findViewById(R.id.favoriteButton);
                placeholderView = itemView.findViewById(R.id.placeholderView);
                placeholderText = itemView.findViewById(R.id.placeholderText);
                selectionOverlay = itemView.findViewById(R.id.selectionOverlay);
                checkIcon = itemView.findViewById(R.id.checkIcon);
            }

            void bind(Bookmark bookmark,
                      OnBookmarkClickListener clickListener,
                      OnBookmarkLongClickListener longClickListener,
                      OnFavoriteClickListener favoriteListener,
                      boolean isSelected) {
                // Title
                String title = bookmark.getTitle();
                titleText.setText(title != null && !title.isEmpty() ? title : "Untitled");

                // Domain
                String domain = bookmark.getDomain();
                domainText.setText(domain != null ? domain : "");

                // Notes
                if (notesText != null) {
                    String notes = bookmark.getNotes();
                    if (notes != null && !notes.isEmpty()) {
                        notesText.setVisibility(View.VISIBLE);
                        notesText.setText(notes);
                    } else {
                        notesText.setVisibility(View.GONE);
                    }
                }

                // Thumbnail
                String thumbnailUrl = bookmark.getThumbnailUrl();
                if (thumbnailUrl != null && !thumbnailUrl.isEmpty()) {
                    thumbnailImage.setVisibility(View.VISIBLE);
                    placeholderView.setVisibility(View.GONE);

                    Glide.with(itemView.getContext())
                            .load(thumbnailUrl)
                            .apply(RequestOptions.bitmapTransform(new RoundedCorners(16)))
                            .placeholder(android.R.color.darker_gray)
                            .into(thumbnailImage);
                } else {
                    thumbnailImage.setVisibility(View.GONE);
                    placeholderView.setVisibility(View.VISIBLE);

                    // Set placeholder text
                    String letter = domain != null && !domain.isEmpty()
                            ? domain.substring(0, 1).toUpperCase()
                            : "?";
                    placeholderText.setText(letter);

                    // Set placeholder color
                    int colorIndex = domain != null
                            ? Math.abs(domain.hashCode()) % AppTheme.CATEGORY_COLORS.length
                            : 0;
                    placeholderView.setBackgroundColor(AppTheme.CATEGORY_COLORS[colorIndex]);
                }

                // Selection state
                if (selectionOverlay != null) {
                    selectionOverlay.setVisibility(isSelected ? View.VISIBLE : View.GONE);
                }
                if (checkIcon != null) {
                    checkIcon.setVisibility(isSelected ? View.VISIBLE : View.GONE);
                }

                // Favorite button
                favoriteButton.setImageResource(bookmark.isFavorite()
                        ? android.R.drawable.btn_star_big_on
                        : android.R.drawable.btn_star_big_off);
                favoriteButton.setOnClickListener(v -> favoriteListener.onClick(bookmark));

                // Item click
                itemView.setOnClickListener(v -> clickListener.onClick(bookmark));
                itemView.setOnLongClickListener(v -> {
                    longClickListener.onLongClick(bookmark);
                    return true;
                });
            }
        }
    }
}
