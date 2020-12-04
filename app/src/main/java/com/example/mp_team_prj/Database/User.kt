package com.example.mp_team_prj.Database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_info")
class User(@PrimaryKey(autoGenerate = true) val id : Long = 0 ,

           @ColumnInfo(name = "SPEED")
           var speed : Int ,

           var time : Float ,

           var distance : Float


           )