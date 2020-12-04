package com.example.mp_team_prj.Database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

import com.example.mp_team_prj.MainActivity


@Database(entities = [User::class],version = 1)
abstract class AppDatabase_user: RoomDatabase() {
    abstract fun userDao(): userDao

    companion object {
        var instance: AppDatabase_user? = null

        @Synchronized
        fun getInstance(context: MainActivity): AppDatabase_user? {
            if (instance == null) {
                instance = Room.databaseBuilder(context.applicationContext, AppDatabase_user::class.java, "user.db").allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build()
            }
            return instance
        }


    }


}




