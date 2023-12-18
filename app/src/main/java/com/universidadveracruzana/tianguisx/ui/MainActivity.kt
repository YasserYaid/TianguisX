package com.universidadveracruzana.tianguisx.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.universidadveracruzana.tianguisx.R
import com.universidadveracruzana.tianguisx.databinding.ActivityMainBinding
import com.universidadveracruzana.tianguisx.ui.cu01.LoginActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        //setContentView(binding.root)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ///////////////////////////////////LISTENERS///////////////////////////////////////////////
        binding.AcceptEulaButton.setOnClickListener{
            if(binding.confirmEulaChekbox.isChecked){
                goToLogin()
            }else{
                Toast.makeText(this, applicationContext.getResources().getString(R.string.Code_Message_EULA), Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun goToLogin(){
        val intent = Intent(applicationContext, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    /*

    private fun getCurrentDateTimeString() : String{
        val date = Calendar.getInstance().time
        return SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date)
    }

    private fun getRandomUUIDString() : String {
        return UUID.randomUUID().toString().replace("-", "")
    }

     */

}