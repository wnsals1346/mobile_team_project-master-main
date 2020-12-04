package com.example.mp_team_prj.Fragment

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter


import com.example.mp_team_prj.Post

import com.example.mp_team_prj.R
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageException
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.view_card.view.*


class sns_postAdapter(val postList : ArrayList<Post>): Adapter<sns_postAdapter.CustomViewHoler>(){







    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): sns_postAdapter.CustomViewHoler {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.view_card,parent,false) // 어뎁터와 연결될 액티비티의 컨텍스트를 들고온다.
    // view 는 뷰_카드에 있는 레이아웃 값을 들고옴


        return CustomViewHoler(view).apply {
            itemView.setOnClickListener{
                val curPos : Int = adapterPosition
                val post : Post = postList.get(curPos)

//                post.like = post.like

            }


        } //view 홀더로  값을 넣어서 해당 class 를 보내줌



        }

    override fun getItemCount(): Int {

      return postList.size

        }

    override fun onBindViewHolder(holder: sns_postAdapter.CustomViewHoler, position: Int) {

        try {
            downloadInMemory(postList.get(position).img.toString(), holder.img)
        }
        catch(e: StorageException){


            holder.img.setImageResource(R.drawable.user_)



        }

        holder.like.text = postList.get(position).like.toString()


        holder.title.text = postList.get(position).title
        holder.content.text = postList.get(position).content





    }

    class CustomViewHoler ( itemView : View) :RecyclerView.ViewHolder(itemView)  {

        val img = itemView.findViewById<ImageView>(R.id.image)
        val title = itemView.findViewById<TextView>(R.id.title)
        val content = itemView.findViewById<TextView>(R.id.content)
        val like = itemView.findViewById<TextView>(R.id.like)



    }




    private fun downloadInMemory(img : String , view : ImageView) {
        var fbStorage = FirebaseStorage.getInstance()
        var ref : StorageReference? = null

        var ONE_MEGABYTE: Long = 1024 * 1024
        if (img != "No_img"){

            print(img)


            ref = fbStorage.reference.child("images").child(img)!!
            ref.getBytes(ONE_MEGABYTE).addOnCompleteListener {

                println("이미지 확인 완료")
                print(img)

                view.setImageBitmap(byteArrayToBitmap(it.result!!))


            }

        }
        else{
        print(img)
        ref = fbStorage.reference.child("images").child("no_detail_img.gif")!!

        ref.getBytes(ONE_MEGABYTE).addOnCompleteListener {

            println("이미지 확인 완료")
            print(img)

            view.setImageBitmap(byteArrayToBitmap(it.result!!))


            }

        }




    }

    private fun byteArrayToBitmap(byteArry: ByteArray): Bitmap {
        var bitmap: Bitmap?=null
        bitmap = BitmapFactory.decodeByteArray(byteArry,0,byteArry.size)
        return bitmap
    }







}

