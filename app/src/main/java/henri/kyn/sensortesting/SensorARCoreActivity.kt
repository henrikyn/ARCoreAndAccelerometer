package henri.kyn.sensortesting

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ActivityInfo
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.ar.core.Config
import com.google.ar.core.Session
import henri.kyn.sensortesting.databinding.ActivitySensorArcoreBinding

class SensorARCoreActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var binding: ActivitySensorArcoreBinding

    private var mSensorManager: SensorManager? = null
    private var accelerometerSensor: Sensor? = null
    private var registered = false

    private var session: Session? = null

    private var withARCore = false

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySensorArcoreBinding.inflate(layoutInflater)
        setContentView(binding.root)

        withARCore = intent.getBooleanExtra("withARCore", false)
        val isPortrait = intent.getBooleanExtra("inPortrait", false)

        requestedOrientation =
            if (isPortrait) ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            else ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        mSensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometerSensor = mSensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        binding.finishButton.setOnClickListener {
            finish()
        }
    }

    override fun onResume() {
        super.onResume()


        if (withARCore && session == null) {
            createSession()
        }

        session?.resume()

        startSensorListener()
    }

    override fun onPause() {
        super.onPause()

        session?.pause()

        stopSensorListener()
    }

    override fun onDestroy() {
        super.onDestroy()

        session?.close()
        session = null
    }

    private fun createSession() {
        session = Session(this)
        val config = Config(session)
        session?.configure(config)
    }

    private fun startSensorListener() {
        if (registered || accelerometerSensor == null) {
            return
        }

        val sensorDelay = 100000 // 100ms in microseconds
        mSensorManager?.registerListener(this, accelerometerSensor, sensorDelay)
        registered = true
    }

    private fun stopSensorListener() {
        if (registered) {
            mSensorManager?.unregisterListener(this)
            registered = false
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        when (event?.sensor?.type) {
            Sensor.TYPE_ACCELEROMETER -> {
                val x = event.values[0]
                val y = event.values[1]
                val z = event.values[2]

                binding.sensorDataText.text = "x: $x\ny: $y\nz: $z"
            }
            else -> { } // Exhaustive else branch
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) { }
}