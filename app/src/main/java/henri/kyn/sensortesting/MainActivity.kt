package henri.kyn.sensortesting

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.ar.core.ArCoreApk
import henri.kyn.sensortesting.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.portraitButton.setOnClickListener {
            startSensorARCoreActivity(inPortrait = true)
        }

        binding.landscapeButton.setOnClickListener {
            startSensorARCoreActivity(inPortrait = false)
        }
    }

    private fun startSensorARCoreActivity(inPortrait: Boolean) {
        if (!cameraPermissionIsGranted()) {
            requestPermissionCameraPermission()
            return
        }
        if (!arCoreIsInstalled()) {
            return
        }

        val activityIntent = Intent(baseContext, SensorARCoreActivity::class.java)
        activityIntent.putExtra("withARCore", binding.withARCoreSwitch.isChecked)
        activityIntent.putExtra("inPortrait", inPortrait)
        startActivity(activityIntent)
    }

    private fun cameraPermissionIsGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissionCameraPermission() {
        if (!shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
            // Permission can be requested, request it
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 123)
        } else {
            // Permission can't be requested, open settings
            startActivity(
                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", packageName, null)
                }
            )
            Toast.makeText(baseContext, "Please grant permission", Toast.LENGTH_SHORT).show()
        }
    }

    private fun arCoreIsInstalled(): Boolean =
        ArCoreApk.getInstance().requestInstall(this, true) == ArCoreApk.InstallStatus.INSTALLED
}
