package com.example.whatfood

import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import java.io.FileReader


class results : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_results)
        var textView = findViewById<TextView>(R.id.result_tv)
        var textresult = findViewById<TextView>(R.id.result_tv2)
        var imageView = findViewById<ImageView>(R.id.iv2)
        val bundle = intent.extras
        val message = bundle.getString("result")

        val filename = bundle.getString("image")
        val instream = this.openFileInput(filename)
        val bitmap = BitmapFactory.decodeStream(instream)

        instream.close()
        imageView.setImageBitmap(bitmap)
        imageView!!.rotation = 90f

        textView.setText(message)

        val settings = getSharedPreferences(sharedfile, MODE_PRIVATE)
        val str = settings.getString("settingstring", "0000000")
        val arrayList = arrayListOf<String>("milk", "Egg","Peanut", "Fish", "Wheat","shellfish","nuts")
        var stri = ""


        var i=0
        csvReader().open(assets.open("allergies.csv")) {
            readAllAsSequence().forEach { row ->
                if (row[0] == message){
                    for (allergen in str){                     // str is setting string eg. 1011001
                        if(allergen=='1' && row[i+1]=="1"){
                            stri+="May contain "+arrayList[i]+" based product. Confirm with chef.\n"
                        }
                        i+=1
                    }
                }
            }
        }
        if(stri==""){
         stri = "Bon Appetit! None of your allergies will be activated."
        }

        textresult.setText(stri)
    }

}
