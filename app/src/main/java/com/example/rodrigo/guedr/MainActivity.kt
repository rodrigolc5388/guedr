package com.example.rodrigo.guedr

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button

class MainActivity : AppCompatActivity(), View.OnClickListener {

    val TAG = MainActivity::class.java.canonicalName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.stone_button).setOnClickListener(this)
        findViewById<Button>(R.id.donkey_button).setOnClickListener(this)

        Log.v(TAG, "He pasado por onCreate")

        if (savedInstanceState != null) {
            Log.v(TAG,  "saedInstanceState no es null y clave vale: ${savedInstanceState.getString("clave")}")
        }
        else {
            Log.v(TAG, "savedInstanceState ES null")
        }
    }

    override fun onClick(v: View?) {
        Log.v(TAG, "Hemos pasado por onClick")
//        if (v == stoneButton){
//            Log.v(TAG, "Han pulsado el botón piedra")
//        }
//        else {
//            Log.v(TAG, "Han pulsado el botón burro")
//        }


//        if (v != null) {
//            if (v.id == R.id.stone_button) {
//                Log.v(TAG, "Han pulsado el botón piedra")
//            } else if (v.id == R.id.donkey_button){
//                Log.v(TAG, "Han pulsado el botón burro")
//            }
//        }


//        when (v?.id) {
//            R.id.stone_button -> {
//                val a = 5
//                val b = 7
//                val c = a + b
//                Log.v(TAG, "Han pulsado el botón piedra")
//            }
//            R.id.donkey_button -> Log.v(TAG, "Han pulsado el botón burro")
//            else -> Log.v(TAG, "No sé qué me han pulsado")
//        }

        // La forma más "Kotlin" de hacer esto
        Log.v(TAG, when (v?.id) {
            R.id.stone_button -> "Han pulsado el botón piedra"
            R.id.donkey_button -> "Han pulsado el botón burro"
            else -> "No sé qué me han pulsado"
        })
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        Log.v(TAG, "He pasado por onSaveInstanceState")

//        if (outState != null) {
//            outState.putString("clave", "valor")
//        }

        outState?.putString("clave", "valor")
    }
}
