package com.example.leey_.gamebacklog;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface GameDAO {

    @Query("SELECT * FROM game")
    public List<Game> getAllGames();

    @Insert
    public void insertGames(Game Games);

    @Delete
    public void deleteGames(Game Games);

    @Update
    public void updateGames(Game Games);
}
