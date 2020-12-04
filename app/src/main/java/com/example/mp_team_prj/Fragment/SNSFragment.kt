package com.example.mp_team_prj.Fragment


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager

import com.example.mp_team_prj.Post

import com.example.mp_team_prj.R
import com.example.mp_team_prj.databinding.FragmentSnBinding
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.fragment_sn.*





class SNSFragment : Fragment() {

    private lateinit  var binding : FragmentSnBinding
    private lateinit var database:DatabaseReference
    var post_list = arrayListOf<Post>()

    private var fbStorage = FirebaseStorage.getInstance()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment


        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_sn,container,false)






         //



        return binding.root
    }



    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


        rv_post.layoutManager = LinearLayoutManager(context , LinearLayoutManager.VERTICAL , false)

        rv_post.setHasFixedSize(true)
        database = FirebaseDatabase.getInstance().getReference().child("post")


        database.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {

               println("+++++++++++++++t실패했어용++++++++++++++++++++++++++++++++")
                }

            override fun onDataChange(snapshot: DataSnapshot) {
                println("돌아가나? ")




                for (data in snapshot.children ){
                    val modelResult = data.getValue(Post::class.java)!!
                    println(modelResult.title)

                     val find = arrayListOf<Post>(modelResult)
                    find.addAll(post_list)
                    post_list = find


                    }

                rv_post.adapter = sns_postAdapter(post_list)



            }
        })





    }

}




