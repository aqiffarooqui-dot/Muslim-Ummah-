package com.example.ui.util

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.abs

class CompassSensorManager(context: Context) : SensorEventListener {
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    private val rotationVectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
    private val accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private val magnetometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

    private val _azimuthFlow = MutableStateFlow(0f)
    val azimuthFlow: StateFlow<Float> = _azimuthFlow.asStateFlow()

    private val _isSensorAvailable = MutableStateFlow(true)
    val isSensorAvailable: StateFlow<Boolean> = _isSensorAvailable.asStateFlow()

    private val _accuracyFlow = MutableStateFlow(SensorManager.SENSOR_STATUS_ACCURACY_HIGH)
    val accuracyFlow: StateFlow<Int> = _accuracyFlow.asStateFlow()

    private var manualOffsetDegrees: Float = 0f

    private val rotationMatrix = FloatArray(9)
    private val orientationAngles = FloatArray(3)
    private val lastAccelerometer = FloatArray(3)
    private val lastMagnetometer = FloatArray(3)
    private var lastAccelerometerSet = false
    private var lastMagnetometerSet = false

    private var currentAzimuth = 0f

    fun setCalibrationOffset(offset: Float) {
        manualOffsetDegrees = offset
    }

    fun getCalibrationOffset(): Float = manualOffsetDegrees

    fun startListening() {
        val hasRotationVector = rotationVectorSensor != null &&
                sensorManager.registerListener(this, rotationVectorSensor, SensorManager.SENSOR_DELAY_UI)

        if (!hasRotationVector) {
            val hasAcc = accelerometerSensor != null &&
                    sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_UI)
            val hasMag = magnetometerSensor != null &&
                    sensorManager.registerListener(this, magnetometerSensor, SensorManager.SENSOR_DELAY_UI)
            _isSensorAvailable.value = hasAcc && hasMag
        } else {
            _isSensorAvailable.value = true
        }
    }

    fun stopListening() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent) {
        when (event.sensor.type) {
            Sensor.TYPE_ROTATION_VECTOR -> {
                SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)
                SensorManager.getOrientation(rotationMatrix, orientationAngles)
                val azimuthInRadians = orientationAngles[0]
                val azimuthInDegrees = Math.toDegrees(azimuthInRadians.toDouble()).toFloat()
                val normalizedAzimuth = (azimuthInDegrees + manualOffsetDegrees + 360f) % 360f
                updateSmoothAzimuth(normalizedAzimuth)
            }
            Sensor.TYPE_ACCELEROMETER -> {
                System.arraycopy(event.values, 0, lastAccelerometer, 0, event.values.size)
                lastAccelerometerSet = true
                computeOrientationFromSensors()
            }
            Sensor.TYPE_MAGNETIC_FIELD -> {
                System.arraycopy(event.values, 0, lastMagnetometer, 0, event.values.size)
                lastMagnetometerSet = true
                computeOrientationFromSensors()
            }
        }
    }

    private fun computeOrientationFromSensors() {
        if (lastAccelerometerSet && lastMagnetometerSet) {
            val success = SensorManager.getRotationMatrix(rotationMatrix, null, lastAccelerometer, lastMagnetometer)
            if (success) {
                SensorManager.getOrientation(rotationMatrix, orientationAngles)
                val azimuthInRadians = orientationAngles[0]
                val azimuthInDegrees = Math.toDegrees(azimuthInRadians.toDouble()).toFloat()
                val normalizedAzimuth = (azimuthInDegrees + manualOffsetDegrees + 360f) % 360f
                updateSmoothAzimuth(normalizedAzimuth)
            }
        }
    }

    private fun updateSmoothAzimuth(targetAzimuth: Float) {
        // Handle wrap-around near 0/360
        var diff = targetAzimuth - currentAzimuth
        while (diff < -180f) diff += 360f
        while (diff > 180f) diff -= 360f

        val alpha = 0.25f // Smoothing factor
        currentAzimuth += diff * alpha
        currentAzimuth = (currentAzimuth + 360f) % 360f
        _azimuthFlow.value = currentAzimuth
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        if (sensor?.type == Sensor.TYPE_MAGNETIC_FIELD || sensor?.type == Sensor.TYPE_ROTATION_VECTOR) {
            _accuracyFlow.value = accuracy
        }
    }
}
