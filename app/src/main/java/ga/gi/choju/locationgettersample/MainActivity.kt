package ga.gi.choju.locationgettersample

import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity(), LocationListener {
    private val TAG = "MainActivity"
    private lateinit var locationManager: LocationManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            registerForActivityResult(ActivityResultContracts.RequestPermission()){
                    isGranted: Boolean ->
                if (isGranted){
                    Log.i(TAG, "onCreate: アクセス許可")
                    getLocation()
                } else {
                    Log.i(TAG, "onCreate: アクセス拒否")
                    findViewById<TextView>(R.id.locate).text = "アクセスが拒否されました"
                }
            }.launch(android.Manifest.permission.ACCESS_COARSE_LOCATION)
        }else{
            getLocation()
        }
    }

//    override fun onResume() {
//        super.onResume()
//
//        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
//            val restartIntent = Intent(this, MainActivity::class.java)
//            finish()
//            startActivity(restartIntent)
//        }
//    }

    private fun getLocation(){
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager

        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }

        if(!locationManager.isProviderEnabled(LOCATION_SERVICE)){
            Log.i(TAG, "getLocation: 失敗")
            Snackbar
                .make(findViewById(R.id.main), "「位置情報」がオフのようです", Snackbar.LENGTH_LONG)
                .setAction("設定"){
                    startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                }
                .show()
        } else {
            Log.i(TAG, "getLocation: 成功")
        }

        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1000F, this)
    }

    override fun onLocationChanged(location: Location) {
        val lat = location.latitude
        val lon = location.longitude

        findViewById<TextView>(R.id.locate).text = "緯度: $lat, 軽度: $lon"
    }
}