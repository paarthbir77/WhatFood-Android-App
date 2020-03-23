package com.example.whatfood

import android.Manifest
import android.app.ActionBar
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class MainAct : AppCompatActivity() {
    var mCapture: Button? = null
    var imageView: ImageView? = null
    var image_uri: Uri? = null
    private val mInputSize = 256
    private val mModelPath = "model.tflite"
    private val mLabelPath = "labels_food.txt"
    private lateinit var classifier: Classifier
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        imageView = findViewById(R.id.iv1)
        mCapture = findViewById(R.id.b1)
        initClassifier()
        val actionbarlayout = this.layoutInflater.inflate(R.layout.custom_toolbar, null)
        val action = this.supportActionBar
        if (action != null) {
            action.setDisplayShowCustomEnabled(true)
        }
        action!!.setCustomView(actionbarlayout)


        //button click action
        //mCapture.setOnClickListener(buttonlistener)
        findViewById<Button>(R.id.b1).setOnClickListener(buttonlistener)
        //pred(imageView!!)

    }

    private val buttonlistener = View.OnClickListener {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED || checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                val permission = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                requestPermissions(permission, PERMISSION_CODE)
            } else {
                openCamera()
            }
        } else {
            openCamera()
        }
    }

    private fun openCamera() {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "New Picture")
        values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera")
        image_uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        //Camera intent
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri)
        startActivityForResult(cameraIntent, IMAGE_CAPTURE_CODE)
    }

    fun onRequestPermissionResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSION_CODE -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openCamera()
                } else {
                    Toast.makeText(this, "No Permission", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    private fun initClassifier() {
        classifier = Classifier(assets, mModelPath, mLabelPath, mInputSize)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) { //called when image was captured from camera
        if (resultCode == Activity.RESULT_OK) { //set the image captured to our ImageView
//Toast.makeText(this, "Display Pic", Toast.LENGTH_SHORT).show();
            imageView!!.setImageURI(null)
            imageView!!.setImageURI(image_uri)
            imageView!!.rotation = 90f
            //val bitmap = ((view as ImageView).drawable as BitmapDrawable).bitmap
            pred(imageView!!)

        }

    }

    fun pred(view: ImageView){
        val bitmap = ((view as ImageView).drawable as BitmapDrawable).bitmap
        val result = classifier.recognizeImage(bitmap)
        //runOnUiThread { Toast.makeText(this, result.get(0).title, Toast.LENGTH_SHORT).show() }
        var size = result.size
        //runOnUiThread { Toast.makeText(this, size.toString(), Toast.LENGTH_SHORT).show() }
        if (size==0){
            runOnUiThread { Toast.makeText(this, "No item found", Toast.LENGTH_SHORT).show() }
        }
        else{
            runOnUiThread { Toast.makeText(this, result.get(0).title, Toast.LENGTH_SHORT).show() }
        }

    }
    companion object {
        private const val PERMISSION_CODE = 1000
        private const val IMAGE_CAPTURE_CODE = 1001
    }

}