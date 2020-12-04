package com.example.mp_team_prj

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.database.sqlite.SQLiteConstraintException
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.location.Location
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.WindowManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentTransaction

import com.example.mp_team_prj.Database.AppDatabase_user
import com.example.mp_team_prj.Database.User
import com.example.mp_team_prj.Fragment.MyPageFragment
import com.example.mp_team_prj.Fragment.SNSFragment
import com.example.mp_team_prj.databinding.ActivityMainBinding
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.bottomnavigation.BottomNavigationView

import com.google.maps.android.SphericalUtil
import java.util.*
import kotlin.concurrent.timer
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener,
    OnMapReadyCallback {

    val permissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )
    val PERM_FLAG = 99
    private lateinit var mMap: GoogleMap
    private val polylineOptionR = PolylineOptions().width(5f).color(Color.RED)
    private val polylineOptionB = PolylineOptions().width(5f).color(Color.BLUE)
    val pattern = listOf(Dot(), Gap(20F), Dash(30F), Gap(20F))
    private lateinit var startLocation: Location
    lateinit var binding: ActivityMainBinding
    var userVelocity: Float = 0.0f
    var userDistance: Float = 0.0f
    var userTime: Float = 0.0f
    var dist = 0.0f
    private var time = 0
    private var timerTask: Timer? = null

    // 사용자의 현재위치 호출
    lateinit var fusedLocationClient: FusedLocationProviderClient
    lateinit var locationCallback: LocationCallback
    public final var Userdb: AppDatabase_user? = null    //데이터베이스를 못받아옴 .
    // 유저 아이디 받음 .
    var user_id: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Userdb = AppDatabase_user.getInstance(this)!!

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT    // 세로 모드 고정.
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)   // 화면 꺼지지 않게.
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        if (isPermitted()) {
            startProcess()
        } else {
            ActivityCompat.requestPermissions(this, permissions, PERM_FLAG)
        }

        // 허락 받기
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            1
        )

        if (intent.hasExtra("uid")) {
            user_id = intent.getStringExtra("uid")
        }

        binding.bottomNavigation.setOnNavigationItemSelectedListener(this)
    }

    fun isPermitted(): Boolean {
        for (perm in permissions) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    perm
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }
        return true
    }

    fun startProcess() {
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        setUpdateLocationListener()
        binding.MylocCheckBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.MydestCheckBox.isChecked = false
            }
        }
        binding.MydestCheckBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.MylocCheckBox.isChecked = false
            }
        }
        binding.DestButton.setOnClickListener {
            resetLocation()
            setDestLocationListener()
            start()
        }
        binding.EndButton.setOnClickListener {
            try {
                Userdb!!.userDao().insert(
                    User(
                        speed = userVelocity.toInt(),
                        time = userTime,
                        distance = userDistance
                    )
                )

            } catch (e: SQLiteConstraintException) {
                Log.i("Exist data ", "이미 데이터가 존재합니다. ")

            }
            resetLocation()
            resetTime()
            setUpdateLocationListener()


        }
    }

    @SuppressLint("MissingPermission")
    fun setUpdateLocationListener() {
        val descriptor = getDescriptorFromDrawable(R.drawable.defaultmarker)
        val locationRequest = LocationRequest.create()
        startLocation = Location("")
        locationRequest.run {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 1000
        }

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult?.let {//null이 아닐때 실행
                    for ((i, location) in it.locations.withIndex()) {
                        Log.d("위치", "$i ${location.latitude}, ${location.longitude}")
                        setLastLocation(location, descriptor)
                        startLocation.latitude = location.latitude
                        startLocation.longitude = location.longitude
                        if (binding.MylocCheckBox.isChecked()) {
                            // TODO : CheckBox is checked.
                            locationCamera(location)
                        } else {
                            // TODO : CheckBox is unchecked.
                        }
                    }
                }
            }
        }
        // 로케이션 요청함수 호출
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.myLooper()
        )
    }

    @SuppressLint("MissingPermission")
    fun setDestLocationListener() {
        val descriptor = getDescriptorFromDrawable(R.drawable.destmarker)
        val descriptorD = getDescriptorFromDrawable(R.drawable.destmarker)
        val locationRequest = LocationRequest.create()
        val randomLocationX = (-30000..30000).random() / 100000f
        val randomLocationY = (-30000..30000).random() / 100000f
        val myDestination = LatLng(
            startLocation.latitude + randomLocationX,
            startLocation.longitude + randomLocationY
        )
        locationRequest.run {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 1000
        }

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult?.let {//null이 아닐때 실행
                    //val locations = locationResult?.lastLocation
                    for ((i, location) in it.locations.withIndex()) {
                        Log.d("위치", "$i ${location.latitude}, ${location.longitude}")
                        setDestLocation(location, myDestination, descriptor, descriptorD)
                        if (binding.MylocCheckBox.isChecked()) {
                            // TODO : CheckBox is checked.
                            locationCamera(location)
                            drawLine(location)
                        } else if (binding.MydestCheckBox.isChecked()) {
                            // TODO : CheckBox is unchecked.
                            destinationCamera(myDestination)
                            drawLine(location)
                        } else {
                            drawLine(location)
                        }
                    }
                }
            }
        }
        // 로케이션 요청함수 호출
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.myLooper()
        )
    }

    fun setLastLocation(location: Location, descriptor: BitmapDescriptor) {
        val myLocation = LatLng(location.latitude, location.longitude)
        mMap.clear()
        mMap.addMarker(MarkerOptions().position(myLocation).title("현재 위치").icon(descriptor))
    }

    fun setDestLocation(
        location: Location,
        destination: LatLng,
        descriptor: BitmapDescriptor,
        descriptorD: BitmapDescriptor
    ) {
        val myLocation = LatLng(location.latitude, location.longitude)
        Speedometer(location, destination)
        mMap.clear()
        mMap.addMarker(MarkerOptions().position(myLocation).title("현재 위치").icon(descriptor))
        mMap.addMarker(MarkerOptions().position(destination).title("목적지").icon(descriptorD))
        mMap.addPolyline(
            PolylineOptions().add(myLocation, destination).pattern(pattern).width(12f)
                .color(Color.DKGRAY)
        )
    }

    fun locationCamera(location: Location) {
        val myLocation = LatLng(location.latitude, location.longitude)
        mMap.moveCamera(
            CameraUpdateFactory.newCameraPosition(
                CameraPosition.Builder().target(myLocation).zoom(17f).build()
            )
        )
    }

    fun destinationCamera(destination: LatLng) {
        mMap.moveCamera(
            CameraUpdateFactory.newCameraPosition(
                CameraPosition.Builder().target(destination).zoom(16f).build()
            )
        )
    }

    fun drawLine(location: Location) {
        val myLocation = LatLng(location.latitude, location.longitude)
        polylineOptionR.add(myLocation)
        mMap.addPolyline(polylineOptionR)
    }

    fun drawLine2(location: Location) {
        val myLocation = LatLng(location.latitude, location.longitude)
        polylineOptionB.add(myLocation)
        mMap.addPolyline(polylineOptionB)
    }

    fun Speedometer(location: Location, destination: LatLng) {
        dist = dist + location.speed //미터로나옴
        userDistance = ((dist / 1000) * 100).roundToInt() / 100f
        binding.Mydistance.text = userDistance.toString()

        binding.MyVelocity.text = ((location.speed * 3.6 * 100).roundToInt() / 100f).toString()

        userVelocity = ((dist / (time / 100)) * 3.6 * 100).roundToInt() / 100f
        binding.avgvelocity.text = userVelocity.toString()

        val myLocation = LatLng(location.latitude, location.longitude)
        binding.RemDistance.text = (((SphericalUtil.computeDistanceBetween(
            myLocation,
            destination
        ) / 1000) * 100).roundToInt() / 100f).toString()
    }

    fun resetLocation() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    private fun start() {

        timerTask?.cancel()
        timerTask = timer(period = 10) {       // timer interval 10 ms
            time++
            val hour = time / 360000
            val min = (time / 6000) % 60
            val sec = (time / 100) % 60
            val milli = time % 100

            runOnUiThread {
                binding.hour.text = "$hour"
                binding.min.text = "$min"
                binding.sec.text = "$sec"
                binding.millisec.text = "$milli"
            }
        }
    }

    private fun resetTime() {
        timerTask?.cancel()
        userTime = time / 360000f
        time = 0
        dist = 0.0f
        binding.hour.text = "00"
        binding.min.text = "00"
        binding.sec.text = "00"
        binding.millisec.text = "00"
        binding.MyVelocity.text = "00"
        binding.Mydistance.text = "00"
        binding.avgvelocity.text = "00"
        binding.RemDistance.text = "00"
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERM_FLAG -> {
                var check = true
                for (grant in grantResults) {
                    if (grant != PackageManager.PERMISSION_GRANTED) {
                        check = false
                        break
                    }
                }
                if (check) {
                    startProcess()
                } else {
                    Toast.makeText(this, "어플을 이용하시기 위해서 권한을 승인해 주세요.", Toast.LENGTH_LONG).show()
                    finish()
                }
            }
        }
    }

    fun getDescriptorFromDrawable(drawableID: Int): BitmapDescriptor {
        var bitmapDrawable: BitmapDrawable
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            bitmapDrawable = getDrawable(drawableID) as BitmapDrawable
        } else {
            bitmapDrawable = resources.getDrawable(drawableID) as BitmapDrawable
        }
        val scaledBitmap = Bitmap.createScaledBitmap(bitmapDrawable.bitmap, 100, 100, false)
        return BitmapDescriptorFactory.fromBitmap(scaledBitmap)
    }


    override fun onNavigationItemSelected(p0: MenuItem): Boolean {
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        when (p0.itemId) {
            R.id.drive -> {
                binding.fragmentContainer.visibility=INVISIBLE
            }
            R.id.AR -> {
                val intent = Intent(this, UploadActivity::class.java)
                var user_id = user_id
                intent.putExtra("uid", user_id)
                startActivity(intent)
            }
            R.id.SNS -> {
                val fragmentC = SNSFragment()
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragmentC).commit()
                binding.fragmentContainer.visibility=VISIBLE
            }
            R.id.user_info -> {
                val fragmentD = MyPageFragment()
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragmentD).commit()
                binding.fragmentContainer.visibility=VISIBLE
            }
        }
        transaction.addToBackStack(null)
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        transaction.commit()
        return true
    }
}