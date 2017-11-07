package com.example.rodrigo.guedr.activity

import android.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v13.app.FragmentPagerAdapter

import android.support.v4.view.ViewPager
import android.view.Menu
import android.view.MenuItem
import android.widget.Toolbar
import com.example.rodrigo.guedr.R
import com.example.rodrigo.guedr.fragment.ForecastFragment
import com.example.rodrigo.guedr.model.Cities
import kotlinx.android.synthetic.main.activity_city_pager.*

class CityPagerActivity : AppCompatActivity() {

    val pager by lazy { findViewById<ViewPager>(R.id.view_pager) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_city_pager)

        // Configuramos la ToolBar
        val toolbar = findViewById<android.support.v7.widget.Toolbar>(R.id.toolbar)
        toolbar.setLogo(R.mipmap.ic_launcher)
        setSupportActionBar(toolbar)

        val cities = Cities()
        val adapter = object : FragmentPagerAdapter(fragmentManager) {
            override fun getItem(position: Int) = ForecastFragment.newInstance(cities[position])

            override fun getCount() = cities.count

            override fun getPageTitle(position: Int) = cities[position].name
        }

        pager.adapter = adapter
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.pager, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.previous -> {
                pager.currentItem = pager.currentItem - 1
                true
            }
            R.id.next -> {
                pager.currentItem++
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
