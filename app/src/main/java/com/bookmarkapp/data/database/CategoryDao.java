package com.bookmarkapp.data.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.bookmarkapp.data.model.Category;

import java.util.List;

@Dao
public interface CategoryDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insert(Category category);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAll(List<Category> categories);

    @Update
    void update(Category category);

    @Delete
    void delete(Category category);

    @Query("DELETE FROM categories WHERE id = :id")
    void deleteById(long id);

    @Query("SELECT * FROM categories ORDER BY name ASC")
    LiveData<List<Category>> getAllCategories();

    @Query("SELECT * FROM categories ORDER BY name ASC")
    List<Category> getAllCategoriesSync();

    @Query("SELECT * FROM categories WHERE id = :id")
    LiveData<Category> getCategoryById(long id);

    @Query("SELECT * FROM categories WHERE id = :id")
    Category getCategoryByIdSync(long id);

    @Query("SELECT * FROM categories WHERE name = :name LIMIT 1")
    Category getCategoryByName(String name);

    @Query("SELECT * FROM categories WHERE name = :name LIMIT 1")
    LiveData<Category> getCategoryByNameLive(String name);

    @Query("SELECT COUNT(*) FROM categories")
    LiveData<Integer> getCategoryCount();
}
