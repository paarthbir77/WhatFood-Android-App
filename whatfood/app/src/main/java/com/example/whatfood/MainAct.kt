package com.example.whatfood

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.ByteArrayOutputStream


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

        //val bundle = intent.extras
        //val message = bundle.getArrayList("setting")



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

        val filename = "bitmap.png"
        val bytes = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        var stream = this.openFileOutput(filename, Context.MODE_PRIVATE)
        stream.write(bytes.toByteArray())
        stream.close()

        //bitmap.recycle()


        //runOnUiThread { Toast.makeText(this, size.toString(), Toast.LENGTH_SHORT).show() }
        if (size==0){
            runOnUiThread { Toast.makeText(this, "No item found", Toast.LENGTH_SHORT).show() }
            //val intent = Intent(this, results::class.java).putExtra("result", "chicken curry")
            //intent.putExtra("image", filename)
            //startActivity(intent)
        }
        else{
            //runOnUiThread { Toast.makeText(this, result.get(0).title, Toast.LENGTH_SHORT).show() }
            val intent = Intent(this, results::class.java).putExtra("result", result.get(0).title)
            intent.putExtra("image", filename)
            startActivity(intent)
        }

    }
    companion object {
        private const val PERMISSION_CODE = 1000
        private const val IMAGE_CAPTURE_CODE = 1001
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.action_settings -> {
                //Toast.makeText(applicationContext, "click on setting", Toast.LENGTH_LONG).show()
                intent = Intent(this, setting::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    }



