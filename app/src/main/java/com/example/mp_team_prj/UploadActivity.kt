package com.example.mp_team_prj

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.databinding.DataBindingUtil
import androidx.room.Room

import com.example.mp_team_prj.databinding.ActivityUploadBinding
import com.google.android.material.internal.ContextUtils.getActivity


import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

import kotlinx.android.synthetic.main.activity_upload.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.jar.Manifest
import java.util.zip.Inflater

class UploadActivity : AppCompatActivity() {
    private var viewUpoload: View? = null
    var pickImageFromAlbum = 0
    var uriPhoto: Uri? = null
    var photo_name: String? = null
    private lateinit var binding: ActivityUploadBinding
    private var fbStorage = FirebaseStorage.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContentView(R.layout.activity_upload)


        binding = DataBindingUtil.setContentView(this, R.layout.activity_upload)

        // firestore 에 저장하는 방식으로 가야함 , 왜 ? room 은 local 이기 때문

        var user_id = intent.getStringExtra("uid")


        var btn = findViewById<Button>(R.id.upload_btn)


        val intent = Intent(this, MainActivity::class.java)

        btn.setOnClickListener {


            var title = findViewById<EditText>(R.id.title).text.toString()


            var content = findViewById<EditText>(R.id.content).text.toString()




            if (title.length == 0) {
                title = "no Title here"
            }

            if (content.length == 0) {
                title = "no Content here"
            }


            val database = FirebaseDatabase.getInstance()

            val myRaf = database.getReference()

            //realtime database


            val dataInput = Post(user_id.toString(), title, content, img = photo_name)



            myRaf.child("post").push().setValue(dataInput)



            intent.putExtra("uid", user_id)

            startActivity(intent)


            //db.post_Dao().insert(Post(title,content))


        }
        var img_btn = findViewById<Button>(R.id.img_btn)

        img_btn.setOnClickListener {
            var photoPickerIntent = Intent(Intent.ACTION_PICK)
            photoPickerIntent.type = "image/*"
            startActivityForResult(photoPickerIntent, pickImageFromAlbum)

        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)




        if (requestCode == pickImageFromAlbum) {
            if (resultCode == Activity.RESULT_OK) {
                uriPhoto = data?.data
                binding.img.setImageURI(uriPhoto)
            }
            if (checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {

try {
    ImageUpload(binding.root)

}catch (e : KotlinNullPointerException){
    println("뒤로가기 실행")
}
            } else {

            }

        }

    }


    private fun ImageUpload(view: View) {

        println("이미지 업로드 됐나용?")

        var timestamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())

        var imgFileName = "IMAGE_" + timestamp + "_.png"

        photo_name = imgFileName

        println(uriPhoto)

        var storageRef = fbStorage!!.reference.child("images").child(imgFileName)


        var a = storageRef?.putFile(uriPhoto!!)

        Thread.sleep(1000L)
        a?.addOnCompleteListener {


            Toast.makeText(view.context, "이미지가 업로드 되었습니다.", Toast.LENGTH_SHORT).show()


        }
    }
}



