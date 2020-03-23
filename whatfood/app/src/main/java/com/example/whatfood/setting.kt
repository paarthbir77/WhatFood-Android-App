package com.example.whatfood

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

var settingArray = arrayListOf<Boolean>()
var settingString = String()
val sharedfile = "settingsfile"
class setting : AppCompatActivity() {
    //private val sharedfile = "settingsfile"
   // val SWITCH1 = "switch1"
    val SWITCH = arrayListOf<String>("switch1","switch2","switch3","switch4","switch5","switch6","switch7" )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings)
        val btnSave = findViewById<Button>(R.id.save_settings)
        btnSave.setOnClickListener(View.OnClickListener {
            saveData()
            //var intent = Intent(this, MainAct::class.java).putExtra("setting", settingArray)
            //startActivity(intent)
            loadData()
            val sharedPreferences = getSharedPreferences(sharedfile, MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            settingString=""
            for (i in settingArray){
                //settingString += i.toString()
                if (i == true){
                    settingString+="1"
                }
                else{
                    settingString+="0"
                }
            }
            editor.putString("settingstring", settingString)
            editor.apply()
            editor.commit()
            Toast.makeText(applicationContext, "Saved", Toast.LENGTH_LONG).show()
        })

        //loadData()
        updateViews()
    }
    public fun saveData(){
        val sharedPreferences = getSharedPreferences(sharedfile, MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        //val sw1 = findViewById<Switch>(R.id.milk)
        //val sw2 = findViewById<Switch>(R.id.egg)
        val switches = arrayOf(findViewById<Switch>(R.id.milk), findViewById<Switch>(R.id.egg),findViewById<Switch>(R.id.peanut),findViewById<Switch>(R.id.fish),findViewById<Switch>(R.id.wheat),findViewById<Switch>(R.id.shellfish),findViewById<Switch>(R.id.nuts))
        //sw1.isChecked = sharedPreferences.getBoolean()
        //editor.putBoolean(SWITCH1, sw1.isChecked)
        //editor.putBoolean(SWITCH2, sw2.isChecked)
        var i=0
        for(sw in switches){
            editor.putBoolean(SWITCH[i], sw.isChecked)
            i+=1
        }
        //settingString=""
        //for (i in settingArray){
            //settingString += i.toString()
         //   if (i == true){
         //       settingString+="1"
        //    }
        //    else{
         //       settingString+="0"
         //   }
        //}
       // editor.putString("settingstring", settingString)
        editor.apply()
        editor.commit()
        //Toast.makeText(applicationContext, settingString, Toast.LENGTH_LONG).show()

    }
    public fun loadData(): ArrayList<Boolean> {
        val arrayList = arrayListOf<Boolean>()
        val sharedPreferences = getSharedPreferences(sharedfile, MODE_PRIVATE)
        for(switchstring in SWITCH){
            arrayList.add(sharedPreferences.getBoolean(switchstring, false))
        }
        //val switchOnOff1 = sharedPreferences.getBoolean(SWITCH1, false);
        //val switchOnOff2 = sharedPreferences.getBoolean(SWITCH2, false);
        //arrayList.add(switchOnOff1)
        //arrayList.add(switchOnOff2)
        settingArray = arrayList
        return arrayList
    }
    public fun updateViews(){
        val array = loadData()
        val switches = arrayOf(findViewById<Switch>(R.id.milk), findViewById<Switch>(R.id.egg),findViewById<Switch>(R.id.peanut),findViewById<Switch>(R.id.fish),findViewById<Switch>(R.id.wheat),findViewById<Switch>(R.id.shellfish),findViewById<Switch>(R.id.nuts))
        //val sw1 = findViewById<Switch>(R.id.milk)
        //val sw2 = findViewById<Switch>(R.id.egg)
        //sw1.setChecked(array[0])
        //sw2.setChecked(array[1])
        var i = 0
        for(sw in switches){
            sw.setChecked(array[i])
            i += 1
        }



    }
}
