package com.example.smarthomegesturecontrol

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity(), OnItemSelectedListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val Drop_Down_Ges = findViewById<Spinner?>(R.id.GestureList)
        Drop_Down_Ges.onItemSelectedListener = this
        val adapt_Ges = ArrayAdapter(this@MainActivity, android.R.layout.simple_list_item_1,
                resources.getStringArray(R.array.smart_home_gestures))
        adapt_Ges.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        Drop_Down_Ges.adapter = adapt_Ges
    }


    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val Item_Spinner = parent?.getItemAtPosition(position).toString()
        if (Item_Spinner != "Select a Gesture") {
            val Expert_Video_Play = Intent(this@MainActivity, Play_Demo_Activity::class.java)
            Expert_Video_Play.putExtra("gesture_name", Item_Spinner)
            startActivity(Expert_Video_Play)
        }
    }

    override fun onNothingSelected(arg0: AdapterView<*>?) {

    }
}