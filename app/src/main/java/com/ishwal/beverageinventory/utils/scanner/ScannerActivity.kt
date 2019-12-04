package com.ishwal.beverageinventory.utils.scanner

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.media.RingtoneManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.zxing.Result
import com.ishwal.beverageinventory.R
import kotlinx.android.synthetic.main.activity_scanner.*
import me.dm7.barcodescanner.zxing.ZXingScannerView
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions


class ScannerActivity : AppCompatActivity(), ZXingScannerView.ResultHandler, EasyPermissions.PermissionCallbacks, EasyPermissions.RationaleCallbacks {

    override fun onRationaleDenied(requestCode: Int) {
        onBackPressed()
    }

    override fun onRationaleAccepted(requestCode: Int) {
    }


    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        onBackPressed()
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        mScannerView.setResultHandler(this) // Register ourselves as a handler for scan results.
        mScannerView.startCamera()
    }

    companion object {
        const val RC_CAMERA: Int = 101
    }

    private lateinit var mScannerView: ZXingScannerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mScannerView = ZXingScannerView(this)   // Programmatically initialize the scanner view
        setContentView(R.layout.activity_scanner)

        content_frame.addView(mScannerView)
    }

    public override fun onResume() {
        super.onResume()
        startCamera()       // Start camera on resume
    }

    @AfterPermissionGranted(RC_CAMERA)
    private fun startCamera() {
        val perms = arrayOf(Manifest.permission.CAMERA)
        if (EasyPermissions.hasPermissions(this, *perms)) {
            // Already have permission, do the thing
            // ...
            mScannerView.setResultHandler(this) // Register ourselves as a handler for scan results.
            mScannerView.startCamera()
        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(this, getString(R.string.camera_permission_description),
                    RC_CAMERA, perms[0])
        }
    }

    public override fun onPause() {
        super.onPause()
        mScannerView.stopCamera()           // Stop camera on pause
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun handleResult(rawResult: Result?) {

        try {
            val notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val r = RingtoneManager.getRingtone(applicationContext, notification)
            r.play()
        } catch (e: Exception) {
        }

        if (rawResult != null) {
            val result = ScanResultModel(rawResult.text, intent.getIntExtra("requestCode", 0))
            val returnIntent = Intent()
            returnIntent.putExtra("barcodeData", result)
            setResult(Activity.RESULT_OK, returnIntent)
            finish()
        } else {
            val returnIntent = Intent()
            setResult(Activity.RESULT_CANCELED, returnIntent)
            finish()
        }
    }

}
