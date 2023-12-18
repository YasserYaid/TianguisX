package com.universidadveracruzana.tianguisx.ui.cu10

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.squareup.picasso.Picasso
import com.universidadveracruzana.tianguisx.R
import com.universidadveracruzana.tianguisx.databinding.ActivityShowBuyerInformationCardBinding
import com.universidadveracruzana.tianguisx.databinding.ActivityShowSellerInformationCardBinding
import com.universidadveracruzana.tianguisx.entities.Buyer
import com.universidadveracruzana.tianguisx.entities.Seller

class ShowBuyerInformationCardActivity : AppCompatActivity() {

    private lateinit var binding : ActivityShowBuyerInformationCardBinding
    var currentSeller : Seller? = null
    var consultBuyer : Buyer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShowBuyerInformationCardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        showBuyerInformationCardGUIConfig()
    }

    fun showBuyerInformationCardGUIConfig(){
        ////////////////START ////////////////////////////
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            currentSeller = intent.getSerializableExtra("currentSeller", Seller::class.java)
            consultBuyer = intent.getSerializableExtra("consultBuyer", Buyer::class.java)
        }
        else {
            currentSeller = intent.getSerializableExtra("currentSeller") as Seller
            consultBuyer = intent.getSerializableExtra("consultBuyer") as Buyer
        }

        Picasso.get().load(consultBuyer?.profileImageURL).into(binding.ui10IvImageProfile)
        binding.ui10EtName.text = " " + consultBuyer?.name + " " + consultBuyer?.lastName
        binding.ui10EtCorreo.text = " " + consultBuyer?.email
        binding.ui10EtPhone.text = " " + consultBuyer?.phoneNumber
        binding.ui10EtCity.text = " " + consultBuyer?.city
        binding.ui10EtState.text = " " + consultBuyer?.state
        binding.ui10EtAddress.text = consultBuyer?.streetAddress + " " + consultBuyer?.numberAddress
        binding.ui10EtAdditionalAddressInformation.text = " " + consultBuyer?.descriptionAddress
        binding.ui10EtFirstDay.text = " " + consultBuyer?.initialReceptionDay
        binding.ui10EtLastDay.text = " " + consultBuyer?.finalReceptionDay
        binding.ui10EtFirstHour.text = " " + consultBuyer?.initialReceptionHour
        binding.ui10EtLastHour.text = " " + consultBuyer?.finalReceptionHour
    }
}