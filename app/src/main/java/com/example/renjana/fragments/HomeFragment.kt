package com.example.renjana.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.lifecycle.lifecycleScope
import com.example.renjana.ChatActivity
import com.example.renjana.PreChatActivity
import com.example.renjana.PreCheckoutActivity
import com.example.renjana.R
import com.google.android.material.snackbar.Snackbar
import com.synnapps.carouselview.CarouselView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class HomeFragment : Fragment() {

    var promoImage = intArrayOf(
        R.drawable.promo_1,
        R.drawable.promo_2,
        R.drawable.promo_3,
        R.drawable.promo_4,
        R.drawable.promo_5
    )

    var text = arrayOf(
        "Promo 1",
        "Promo 2",
        "Promo 3",
        "Promo 4",
        "Promo 5"
    )

    lateinit var carouselView: CarouselView
    lateinit var btnBookPro: Button
    lateinit var btnBookCendekia: Button
    lateinit var btnBookRencana: Button


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        val view:View = inflater.inflate(R.layout.fragment_home, container, false)

        btnBookPro = view.findViewById(R.id.btnR_Pro)
        btnBookCendekia = view.findViewById(R.id.btnR_Cendekia)
        btnBookRencana = view.findViewById(R.id.btnR_Rencana)

        carouselView = view.findViewById(R.id.carouselView)
        carouselView.pageCount = text.size
        carouselView.setImageListener { position, imageView ->
            imageView.setImageResource(promoImage[position])
        }
        carouselView.setImageClickListener { position ->
            view.let {
                Snackbar.make(it, text[position], Snackbar.LENGTH_SHORT).show()
            }
        }

        btnBookPro.setOnClickListener { intentToCheck(1) }

        btnBookCendekia.setOnClickListener { intentToCheck(2) }

        btnBookRencana.setOnClickListener {
            //intentToCheck(3)
            val intent = Intent(context, PreChatActivity::class.java)
            context?.startActivity(intent)
        }

        return view
    }

    private fun intentToCheck(i: Int) {
        val type:String = when (i) {
            1 -> {
                "Renjana Pro"
            }
            2 -> {
                "Renjana Cendekia"
            }
            else -> {
                "Rencana Renjana"
            }
        }
        /*lifecycleScope.launch {
            while (true){
                delay(1000L)
                Log.d("HomeFragment :", type)
            }
        }*/

        val intent = Intent(context, PreCheckoutActivity::class.java)
        intent.putExtra("type", i)
        context?.startActivity(intent)
    }

}