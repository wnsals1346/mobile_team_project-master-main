package com.example.mp_team_prj.Fragment


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.example.mp_team_prj.Database.AppDatabase_user
import com.example.mp_team_prj.MainActivity

import com.example.mp_team_prj.R
import com.example.mp_team_prj.databinding.FragmentMyPageBinding

/**
 * A simple [Fragment] subclass.
 */
class MyPageFragment : Fragment() {


    lateinit var binding : FragmentMyPageBinding



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {




        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_my_page,container,false)

        var db = (activity as MainActivity).Userdb!!

        binding.MaxVelocity.text = db.userDao().maxSpeed().toString()

        binding.MaxVelocity.text = db.userDao().avgSpeed().toString()

        binding.totalDistance.text = db.userDao().total_distance().toString()

        binding.exerciseTime.text = db.userDao().total_time().toString()





        // Inflate the layout for this fragment
        return binding.root

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // 사용할 메소드는 여기다 선언하기



    }



}
