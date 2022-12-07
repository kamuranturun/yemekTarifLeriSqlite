package com.kamuran.yemektariflerikitabi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.navigation.Navigation

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuinfalter = menuInflater
        menuinfalter.inflate(R.menu.yemek_ekle_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.yemek_ekle_item) {
            val action = ListFragmentDirections.actionListFragmentToTarifFragment("menudengeldim",0)
            Navigation.findNavController(this, R.id.fragment).navigate(action)
        }
        return super.onOptionsItemSelected(item)
    }
}