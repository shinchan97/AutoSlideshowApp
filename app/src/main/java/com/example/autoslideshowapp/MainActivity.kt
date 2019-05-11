package com.example.autoslideshowapp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.provider.MediaStore
import android.content.ContentUris
import android.database.Cursor
import android.os.Handler
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    private val PERMISSION_REQUEST_CODE = 10

    private var mTimer: Timer? = null

    private var mTimerSec = 0.0

    private var mHandler = Handler()

    private var cursor: Cursor?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // android 6.0 and later
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // check the permission status
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                // permission is already granted
                getContentInfo()
            } else {
                // not permitted yet, so display permission dialog
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), PERMISSION_REQUEST_CODE)
            }
            //android 5.0 or before
        } else {
            getContentInfo()
        }

        forward_button.setOnClickListener {
            forward()
        }

        back_button.setOnClickListener {
            back()
        }

        play_button.setOnClickListener {
            PlayAndPause()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (cursor != null) {
            cursor!!.close()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode){
            PERMISSION_REQUEST_CODE ->
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getContentInfo()
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), PERMISSION_REQUEST_CODE)
                        Toast.makeText(this, "The access to your Photo has been denied.", Toast.LENGTH_LONG).show()
                        Toast.makeText(this, "Please give a permission.", Toast.LENGTH_LONG).show()
                    }
                }
        }
    }

    private fun getContentInfo(){
        // grab the image data
        val resolver = contentResolver
        cursor = resolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            null, //item(s)
            null, //filter condition
            null, //parameter for filter
            null) //sort

        if (cursor!!.moveToFirst()) {
            // retrieve ID from the index
            val fieldIndex = cursor!!.getColumnIndex(MediaStore.Images.Media._ID)
            val id = cursor!!.getLong(fieldIndex)
            val imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)

            imageView.setImageURI(imageUri)
        }
    }

    private fun back() {


            if (cursor!!.moveToPrevious()) {
                val fieldIndex = cursor!!.getColumnIndex(MediaStore.Images.Media._ID)
                val id = cursor!!.getLong(fieldIndex)
                val imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)

                imageView.setImageURI(imageUri)
            } else {
                cursor!!.moveToLast()
                val fieldIndex = cursor!!.getColumnIndex(MediaStore.Images.Media._ID)
                val id = cursor!!.getLong(fieldIndex)
                val imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)

                imageView.setImageURI(imageUri)
            }
    }

    private fun forward() {

            if (cursor!!.moveToNext()) {
                val fieldIndex = cursor!!.getColumnIndex(MediaStore.Images.Media._ID)
                val id = cursor!!.getLong(fieldIndex)
                val imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)

                imageView.setImageURI(imageUri)
            } else {
                cursor!!.moveToFirst()
                val fieldIndex = cursor!!.getColumnIndex(MediaStore.Images.Media._ID)
                val id = cursor!!.getLong(fieldIndex)
                val imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)

                imageView.setImageURI(imageUri)
            }
    }

    private fun PlayAndPause() {

            if (mTimer == null) {
                mTimer = Timer()
                play_button.text = "Pause"
                Toast.makeText(this, "the button display has switched to pause", Toast.LENGTH_LONG).show()

                mTimer!!.schedule(object : TimerTask() {
                    override fun run() {
                        mTimerSec += 0.1
                        mHandler.post() {
                            forward()
                        }
                    }
                }, 2000, 2000)
            } else if (mTimer != null) {
                mTimer!!.cancel()
                mTimer = null
                play_button.text = "Play"
                Toast.makeText(this, "the button has switched back", Toast.LENGTH_LONG).show()
            }
    }
}
