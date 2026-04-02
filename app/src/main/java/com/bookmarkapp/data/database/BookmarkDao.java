package com.bookmarkapp.data.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.bookmarkapp.data.model.Bookmark;

import java.util.List;

@Dao
public interface BookmarkDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Bookmark bookmark);

    @Update
    void update(Bookmark bookmark);

    @Delete
    void delete(Bookmark bookmark);

    @Query("DELETE FROM bookmarks WHERE id = :id")
    void deleteById(long id);

    @Query("SELECT * FROM bookmarks ORDER BY createdAt DESC")
    LiveData<List<Bookmark>> getAllBookmarks();

    @Query("SELECT * FROM bookmarks ORDER BY createdAt DESC")
    List<Bookmark> getAllBookmarksSync();

    @Query("SELECT * FROM bookmarks WHERE id = :id")
    LiveData<Bookmark> getBookmarkById(long id);

    @Query("SELECT * FROM bookmarks WHERE id = :id")
    Bookmark getBookmarkByIdSync(long id);

    @Query("SELECT * FROM bookmarks WHERE categoryId = :categoryId ORDER BY createdAt DESC")
    LiveData<List<Bookmark>> getBookmarksByCategory(long categoryId);

    @Query("SELECT * FROM bookmarks WHERE isFavorite = 1 ORDER BY createdAt DESC")
    LiveData<List<Bookmark>> getFavoriteBookmarks();

    @Query("SELECT * FROM bookmarks WHERE " +
            "title LIKE '%' || :query || '%' OR " +
            "description LIKE '%' || :query || '%' OR " +
            "url LIKE '%' || :query || '%' OR " +
            "notes LIKE '%' || :query || '%' " +
            "ORDER BY createdAt DESC")
    LiveData<List<Bookmark>> searchBookmarks(String query);

    @Query("SELECT * FROM bookmarks WHERE " +
            "(title LIKE '%' || :query || '%' OR " +
            "description LIKE '%' || :query || '%' OR " +
            "url LIKE '%' || :query || '%' OR " +
            "notes LIKE '%' || :query || '%') AND " +
            "categoryId = :categoryId " +
            "ORDER BY createdAt DESC")
    LiveData<List<Bookmark>> searchBookmarksInCategory(String query, long categoryId);

    @Query("UPDATE bookmarks SET isFavorite = :isFavorite WHERE id = :id")
    void updateFavoriteStatus(long id, boolean isFavorite);

    @Query("SELECT * FROM bookmarks WHERE url = :url LIMIT 1")
    Bookmark getBookmarkByUrl(String url);

    @Query("SELECT COUNT(*) FROM bookmarks")
    LiveData<Integer> getBookmarkCount();

    @Query("SELECT COUNT(*) FROM bookmarks WHERE categoryId = :categoryId")
    LiveData<Integer> getBookmarkCountByCategory(long categoryId);

    @Query("DELETE FROM bookmarks WHERE id IN (:ids)")
    void deleteByIds(List<Long> ids);

    @Query("UPDATE bookmarks SET notes = :notes WHERE id = :id")
    void updateNotes(long id, String notes);
}
