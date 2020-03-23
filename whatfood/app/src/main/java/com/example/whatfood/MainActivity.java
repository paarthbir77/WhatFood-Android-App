package com.example.whatfood;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageView;
import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import com.example.whatfood.Classifier;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final int PERMISSION_CODE = 1000;
    private static final int IMAGE_CAPTURE_CODE = 1001;
    Button mCapture;
    ImageView imageView;
    Uri image_uri;
    private final int mInputSize = 256;
    private final String mModelPath = "model.tflite";
    private final String mLabelPath = "labels_food.txt";
    private Classifier classifier;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = findViewById(R.id.iv1);
        mCapture = findViewById(R.id.b1);
        initClassifier();
        //button click action
        mCapture.setOnClickListener(buttonlistener);

    }
    private View.OnClickListener buttonlistener = new View.OnClickListener(){
        public void onClick(View v){
          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
              if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED || checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
                  String[] permission = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                  requestPermissions(permission, PERMISSION_CODE);
              }
              else {
                  openCamera();
              }
          }
          else {
              openCamera();
          }
        }
    };

    private void openCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera");
        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        //Camera intent
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(cameraIntent, IMAGE_CAPTURE_CODE);
    }

    public void onRequestPermissionResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        switch (requestCode){
            case PERMISSION_CODE: {
                if (grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    openCamera();
                }
                else {
                    Toast.makeText(this, "No Permission", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void initClassifier(){
        classifier = new Classifier( getAssets(),mModelPath, mLabelPath, mInputSize);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //called when image was captured from camera

        if (resultCode == RESULT_OK){
            //set the image captured to our ImageView
            //Toast.makeText(this, "Display Pic", Toast.LENGTH_SHORT).show();
            imageView.setImageURI(null);
            imageView.setImageURI(image_uri);
            imageView.setRotation(90f);
            //Matrix matrix = new Matrix();
            //imageView.setScaleType(ImageView.ScaleType.MATRIX);   //required
            //matrix.postRotate(180f, imageView.getWidth() / 2, imageView.getHeight() / 2);
            //imageView.setImageMatrix(matrix);
            Bitmap bitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
            //imageView.setImageBitmap(bitmap);
            List<Classifier.Recognition> result = classifier.recognizeImage(bitmap);
            //runOnUiThread(Toast.makeText(this, result.get(0).getTitle(), Toast.LENGTH_SHORT).show());
            int size = result.size();
            String s = Integer.toString(size);
            Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
        }
    }
}
