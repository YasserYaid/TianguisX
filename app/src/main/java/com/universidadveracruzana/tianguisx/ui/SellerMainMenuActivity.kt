package com.universidadveracruzana.tianguisx.ui

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.universidadveracruzana.tianguisx.databinding.ActivitySellerMainMenuBinding
import com.universidadveracruzana.tianguisx.entities.Seller
import com.universidadveracruzana.tianguisx.ui.cu01.LoginActivity
import com.universidadveracruzana.tianguisx.ui.cu04.RegisterProductActivity
import com.universidadveracruzana.tianguisx.ui.cu05.CheckInventoryActivity
import com.universidadveracruzana.tianguisx.ui.cu09.ShowBuyersListBoxActivity
import com.universidadveracruzana.tianguisx.ui.cu11.ShowSellerProfileDetailsActivity
import com.universidadveracruzana.tianguisx.ui.cu21.ChangeSettingsActivity

class SellerMainMenuActivity : AppCompatActivity() {

    private lateinit var binding : ActivitySellerMainMenuBinding
    private var currentSeller : Seller? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySellerMainMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sellerMainMenuGUIConfig()
    }

    private fun sellerMainMenuGUIConfig(){
        ///////////////////////START////////////////////////
        currentSeller = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            intent.getSerializableExtra("currentSeller", Seller::class.java)
        }else{
            intent.getSerializableExtra("currentSeller") as Seller
        }

        binding.sellerMainMenuTvName.text = currentSeller?.name

        /////////////////////LISTENERS///////////////////////
        binding.sellerMainMenuBtnProfile.setOnClickListener{
            goToProfile()
        }
        binding.sellerMainMenuBtnInventory.setOnClickListener{
            goToInventory()
        }
        binding.sellerMainMenuBtnMyBuyers.setOnClickListener {
            goToMyBuyers()
        }
        /*
        binding.sellerMainMenuBtnSettings.setOnClickListener {
            goToSettings()
        }
         */
        binding.sellerMainMenuBtnAboutUs.setOnClickListener {
            goToAboutUs()
        }
        binding.sellerMainMenuBtnLogout.setOnClickListener {
            goToLogin()
        }
    }

    private fun goToProfile(){
        val intent = Intent(applicationContext, ShowSellerProfileDetailsActivity::class.java)
        intent.putExtra("currentSeller", currentSeller)
        startActivity(intent)
    }

    private fun goToInventory(){
        val intent = Intent(applicationContext, CheckInventoryActivity::class.java)
        intent.putExtra("currentSeller", currentSeller)
        startActivity(intent)
    }

    private fun goToMyBuyers(){
        val intent = Intent(applicationContext, ShowBuyersListBoxActivity::class.java)
        intent.putExtra("currentSeller", currentSeller)
        startActivity(intent)
    }

    private fun goToSettings(){
        val intent = Intent(applicationContext, ChangeSettingsActivity::class.java)
        intent.putExtra("currentSeller", currentSeller)
        startActivity(intent)
    }

    private fun goToAboutUs(){
        val intent = Intent(applicationContext, AboutUsActivity::class.java)
        intent.putExtra("currentSeller", currentSeller)
        startActivity(intent)
    }

    private fun goToLogin(){
        val intent = Intent(applicationContext, LoginActivity::class.java)
        intent.putExtra("currentSeller", currentSeller)
        startActivity(intent)
        finish()
    }

}