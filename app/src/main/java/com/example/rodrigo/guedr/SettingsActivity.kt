package com.example.rodrigo.guedr

import android.app.Activity
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.RadioGroup


class SettingsActivity: AppCompatActivity() {

    var radioGroup: RadioGroup? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        // Esto sería un equivalente a una clase anónima en Kotlin
//        findViewById<View>(R.id.ok_button).setOnClickListener(object: View.OnClickListener {
//            override  fun onClick(v: View?){
//                // Aquí iría el código de aceptar
//                acceptSettings()
//            }
//        })


        // Esto es una closure indicando v (una View) y no devuelve nada
//        findViewById<View>(R.id.ok_button).setOnClickListener { v ->
//            acceptSettings()
//        }

        findViewById<View>(R.id.ok_button).setOnClickListener { acceptSettings() }
        findViewById<View>(R.id.cancel_button).setOnClickListener { cancelSettings() }

        radioGroup = findViewById(R.id.units_rg)
    }


    private fun cancelSettings(){
        setResult(Activity.RESULT_CANCELED)
        // Finalizamos esta actividad, regresando a la anterior
        finish()
    }

    private fun acceptSettings(){
        setResult(Activity.RESULT_OK)
        // Finalizamos esta actividad, regresando a la anterior
        finish()
    }

}