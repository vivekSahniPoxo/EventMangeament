package com.example.eventmangeament

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.eventmangeament.databinding.ActivitySettingsBinding
import com.example.eventmangeament.utils.Cons
import com.example.eventmangeament.utils.SharePref


class SettingsActivity : AppCompatActivity() {
    lateinit var binding: ActivitySettingsBinding
    lateinit var sharePref: SharePref
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharePref = SharePref()


        binding.buttonSubmitUrl.setOnClickListener {
             sharePref.clearAll()
//            if (sharePref.getData(Cons.BASE_URL)?.isNotEmpty() == true){
//                sharePref.clearAll()
//            }else {
                var UpdateBaseUrl = binding.baseUrlConfig.text.toString().trim()
                Cons.BASE_URL = "http://$UpdateBaseUrl"
                binding.ipconfigForm.visibility = View.GONE
                 sharePref.saveData("baseUrl",Cons.BASE_URL)
                Toast.makeText(this@SettingsActivity, Cons.BASE_URL, Toast.LENGTH_SHORT).show()
            //}
        }

        binding.ipconfig.setOnClickListener {
            binding.ipconfigForm.visibility = View.VISIBLE

        }
    }
}