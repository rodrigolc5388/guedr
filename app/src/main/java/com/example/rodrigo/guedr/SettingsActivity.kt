package com.example.rodrigo.guedr

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.RadioGroup


class SettingsActivity: AppCompatActivity() {

    companion object {
        val EXTRA_UNITS = "EXTRA_UNITS"

        fun intent(context: Context, units: Int): Intent {
            val intent = Intent(context, SettingsActivity::class.java)
            intent.putExtra(EXTRA_UNITS, units)
            return intent
        }
    }

    val radioGroup by lazy { findViewById<RadioGroup>(R.id.units_rg) }

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

        val radioSelected = intent.getIntExtra(EXTRA_UNITS, R.id.celsius_rb)
        radioGroup.check((radioSelected))
    }


    private fun cancelSettings(){
        setResult(Activity.RESULT_CANCELED)
        // Finalizamos esta actividad, regresando a la anterior
        finish()
    }

    private fun acceptSettings(){
        val returnIntent = Intent()
        returnIntent.putExtra(EXTRA_UNITS, radioGroup.checkedRadioButtonId)
        setResult(Activity.RESULT_OK, returnIntent)
        // Finalizamos esta actividad, regresando a la anterior
        finish()
    }

}