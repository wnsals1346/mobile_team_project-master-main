package com.example.mp_team_prj.Database

import androidx.room.*
@Dao
interface userDao{
    @Query("SELECT * FROM user_info")
    fun getAll() : List<User>

    @Query("SELECT AVG(SPEED) FROM user_info")
    fun avgSpeed() : Int

    @Query("SELECT MAX(SPEED) FROM user_info")
    fun maxSpeed() : Int

    @Query("SELECT SUM(TIME) FROM user_info")
    fun total_time() : Float



    @Query("SELECT SUM(DISTANCE) FROM user_info")
    fun total_distance() : Float



    @Insert
    fun insert( user : User)
    @Update
    fun update( user: User)
    @Delete
    fun delete( user: User )

}