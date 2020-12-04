package com.example.mp_team_prj

import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase


val fb_db = FirebaseDatabase.getInstance().reference



data class Post(var user_id : String = "no_id" , var title : String ="no_title", var content : String ="no content", var img : String? = "No_img", var like : Int  = 0   ){

}






