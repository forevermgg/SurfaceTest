package com.mgg.surface


import android.R.attr
import android.R.attr.height
import android.R.attr.width
import android.os.Bundle
import android.util.Log
import android.util.Size
import android.view.Display
import android.view.Surface
import android.view.SurfaceHolder
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.mgg.surface.databinding.ActivityMainBinding
import java.util.Locale
import java.util.concurrent.ExecutionException


class MainActivity : AppCompatActivity(), SurfaceHolder.Callback2 {

    private lateinit var binding: ActivityMainBinding
    private var renderer: OpenGLRenderer? = null
    private var imageProcessingUtilTest:ImageProcessingUtilTest? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Example of a call to a native method
        binding.sampleText.text = stringFromJNI()

        // binding.testSurfaceView.
        val surfaceHolder=binding.testSurfaceView.holder

        surfaceHolder.addCallback(this)
    }

    /**
     * A native method that is implemented by the 'surface' native library,
     * which is packaged with this application.
     */
    external fun stringFromJNI(): String

    external fun nativeGetSurfaceFormat(surface: Surface): Int

    companion object {
        // Used to load the 'surface' library on application startup.
        init {
            System.loadLibrary("surface")
        }
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        renderer = OpenGLRenderer()
        val fpsRecorder = FpsRecorder(25)
        imageProcessingUtilTest = ImageProcessingUtilTest()
        renderer?.setFrameUpdateListener(
            ContextCompat.getMainExecutor(this)
        ) { timestamp: Long? ->
            val fps: Double? = timestamp?.let { fpsRecorder.recordTimestamp(it) }
            fps?.let {
                binding.sampleText.text = getString(
                    R.string.fps_counter_template,
                    if (java.lang.Double.isNaN(it) || java.lang.Double.isInfinite(it)) "---" else String.format(
                        Locale.US,
                        "%.0f", fps
                    )
                )
            }
        }
        val surface = holder.surface
        val format = nativeGetSurfaceFormat(surface)
        Log.e("mgg", "nativeGetSurfaceFormat = $format")
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        val surface = holder.surface
        renderer?.attachOutputSurface(
            surface, Size(width, height),
            Surfaces.toSurfaceRotationDegrees(binding.testSurfaceView.display.rotation)
        )
        /*renderer?.execFunction {
            imageProcessingUtilTest?.writeJpegToSurface_returnsTheSameImage(surface)
        }*/
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        try {
            val detachFuture = renderer?.detachOutputSurface()
            detachFuture?.get()
        } catch (e: ExecutionException) {
            Log.e(
                "mgg", "An error occurred while waiting for surface to detach from "
                        + "the renderer", e.cause
            )
        } catch (e: InterruptedException) {
            Log.e(
                "mgg", "Interrupted while waiting for surface to detach from the "
                        + "renderer."
            )
            Thread.currentThread().interrupt() // Restore the interrupted status
        } finally {
            renderer?.shutdown()
        }
    }

    override fun surfaceRedrawNeeded(holder: SurfaceHolder) {
        val surfaceViewDisplay: Display = binding.testSurfaceView.display
        renderer?.invalidateSurface(
            Surfaces.toSurfaceRotationDegrees(Surface.ROTATION_90)
        )
    }
}