package com.universidadveracruzana.tianguisx.ui

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.universidadveracruzana.tianguisx.databinding.ActivityBuyerMainMenuBinding
import com.universidadveracruzana.tianguisx.entities.Buyer
import com.universidadveracruzana.tianguisx.ui.cu01.LoginActivity
import com.universidadveracruzana.tianguisx.ui.cu14.ShowBuyerProfileDetailsActivity
import com.universidadveracruzana.tianguisx.ui.cu16.ScanQRCodeActivity
import com.universidadveracruzana.tianguisx.ui.cu17.ShowSelectedProductsListActivity
import com.universidadveracruzana.tianguisx.ui.cu20.ExploreProductsActivity
import com.universidadveracruzana.tianguisx.ui.cu21.ChangeSettingsActivity

class BuyerMainMenuActivity : AppCompatActivity() {

    private lateinit var binding : ActivityBuyerMainMenuBinding
    private var currentBuyer : Buyer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBuyerMainMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)
        buyerMainMenuConfig()
    }

    private fun buyerMainMenuConfig(){
        ///////////////////START/////////////////////////////////////////////////
        currentBuyer = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            intent.getSerializableExtra("currentBuyer", Buyer::class.java)
        }
        else{
            intent.getSerializableExtra("currentBuyer") as Buyer
        }

        binding.buyerMainMenuTvName.text = currentBuyer?.name

        //////////////////////////////LISTENERS/////////////////////////////////
        binding.buyerMainMenuBtnProfile.setOnClickListener{
            goToProfile()
        }
        binding.buyerMainMenuBtnScanQr.setOnClickListener{
            goToScanQR()
        }
        binding.buyerMainMenuBtnSelectedProducts.setOnClickListener {
            goToSelectedProducts()
        }
        binding.buyerMainMenuBtnSearch.setOnClickListener {
            goToProductSearh()
        }
        /*
        binding.buyerMainMenuBtnSettings.setOnClickListener {
            goToSettings()
        }
         */
        binding.buyerMainMenuBtnAbout.setOnClickListener {
            goToAboutUs()
        }
        binding.buyerMainMenuBtnLogout.setOnClickListener {
            goToLogin()
        }
    }

    private fun goToProfile(){
        val intent = Intent(applicationContext, ShowBuyerProfileDetailsActivity::class.java)
        intent.putExtra("currentBuyer", currentBuyer)
        startActivity(intent)
    }

    private fun goToScanQR(){
        val intent = Intent(applicationContext, ScanQRCodeActivity::class.java)
        intent.putExtra("currentBuyer", currentBuyer)
        startActivity(intent)
    }

    private fun goToSelectedProducts(){
        val intent = Intent(applicationContext, ShowSelectedProductsListActivity::class.java)
        intent.putExtra("currentBuyer", currentBuyer)
        startActivity(intent)
    }

    private fun goToProductSearh(){
        val intent = Intent(applicationContext, ExploreProductsActivity::class.java)
        intent.putExtra("currentBuyer", currentBuyer)
        startActivity(intent)
    }

    private fun goToSettings(){
        val intent = Intent(applicationContext, ChangeSettingsActivity::class.java)
        intent.putExtra("currentBuyer", currentBuyer)
        startActivity(intent)
    }

    private fun goToAboutUs(){
        val intent = Intent(applicationContext, AboutUsActivity::class.java)
        intent.putExtra("currentBuyer", currentBuyer)
        startActivity(intent)
    }

    private fun goToLogin(){
        val intent = Intent(applicationContext, LoginActivity::class.java)
        intent.putExtra("currentBuyer", currentBuyer)
        startActivity(intent)
        finish()
    }

}