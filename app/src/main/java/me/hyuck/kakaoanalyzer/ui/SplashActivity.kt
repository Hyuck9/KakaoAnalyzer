package me.hyuck.kakaoanalyzer.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import me.hyuck.kakaoanalyzer.R
import me.hyuck.kakaoanalyzer.util.PermissionUtils

class SplashActivity : AppCompatActivity() {

    companion object {
        const val REQUEST_STORAGE = 101 // 저장소 권한 요청 CODE
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Handler().postDelayed({
            if (PermissionUtils.storagePermissionCheck(this@SplashActivity)) {
                nextActivity()
            } else {
                ActivityCompat.requestPermissions(
                    this@SplashActivity,
                    arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ),
                    REQUEST_STORAGE
                )
            }
        }, 1000)
    }

    private fun nextActivity() {
        val intent = Intent(this@SplashActivity, MainActivity::class.java)
        intent.flags =
            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
        overridePendingTransition(R.anim.fadein, R.anim.fadeout)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_STORAGE) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) { // 권한 거부
                finish()
            } else { // 권한 승낙
                nextActivity()
            }
        }
    }
}
