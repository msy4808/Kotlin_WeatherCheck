package com.moon.kotlin_weathercheck

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationRequest
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import java.util.jar.Manifest

class MainActivity : AppCompatActivity() {

    //현재 위치를 가져오기위한 변수
    private var mFusedLocationProviderClient: FusedLocationProviderClient?= null
    lateinit var mLastLocation: Location //위치 값을 가지고 있는 객체
    internal lateinit var mLocationRequest: com.google.android.gms.location.LocationRequest //위치 정보 요청의 매개변수를 저장
    private val REQUEST_PERMISSION_LOCATION = 10

    lateinit var text1:TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        text1 = findViewById<TextView>(R.id.text1)
        val btn = findViewById<Button>(R.id.location_btn)

        mLocationRequest = com.google.android.gms.location.LocationRequest.create().apply {
            priority = com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        btn.setOnClickListener {
            if(checkPermissionForLocation(this)){
                startLocationUpdates()
            }
        }
    }

    private fun startLocationUpdates() {
        //FusedLocationProviderClient의 인스턴스 생성
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        if (ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            return
        }
        //기기의 위치에 관한 저이 업데이트를 요청하는 메서드 실행
        //지정한 루퍼 스레드(Looper.myLooper())에서 콜백(mLocationCallback)으로 위치 업데이트 요청
        mFusedLocationProviderClient!!.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper())
    }

    private val mLocationCallback = object : LocationCallback(){
        override fun onLocationResult(p0: LocationResult) {
            //시스템에서 받은 location 정보를 onLocationChanged()로 전달
            p0.lastLocation
            onLocationChanged(p0.lastLocation)

        }
    }
    fun onLocationChanged(location: Location) {
        try {
            mLastLocation = location
            val geo = Geocoder(application.applicationContext)
            val address = geo.getFromLocation(mLastLocation.latitude, mLastLocation.longitude, 1)
            text1.text = "내위치 : ${address[0]}"

            intent.putExtra("위도", mLastLocation.latitude)
            intent.putExtra("경도", mLastLocation.longitude)
        }catch (e:Exception){
            Log.d("Error", e.stackTraceToString())
        }

    }

    //위치권한이 있는지 확인하는 메서드
    private fun checkPermissionForLocation(context: Context): Boolean {
        //Android 6.0이상에서 위치권한에 추가 런타임 권한이 필요
        return if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(context.checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                true
            }else{
                //권한이 없으면 권한 요청 알림 보내기. ActivityCompat 이 클래스에서 권한허가 다이얼로그 띄우기 가능
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_PERMISSION_LOCATION)
                false
            }
        }else{
            true
        }
    }

    //사용자에게 권한 요청 후 결과에 대한 처리 로직
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == REQUEST_PERMISSION_LOCATION) {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED) { //권한이 허가된 경우 처리
                startLocationUpdates()
            }else{
                Log.d("PE","onRequestPermissionsResult() _ 권한 허용 거부")
                Toast.makeText(this, "권한이 없습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}