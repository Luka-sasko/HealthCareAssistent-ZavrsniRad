package com.example.healthcareassistent

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.text.isDigitsOnly
import androidx.core.view.isVisible
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import java.text.DecimalFormat
import kotlin.math.roundToInt


class BmiFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_bmi, container, false)

        val btnBMI=view.findViewById<Button>(R.id.btnBMI)
        val etWeight=view.findViewById<EditText>(R.id.etWeight)
        val etHeight=view.findViewById<EditText>(R.id.etHeight)
        val tvbmi=view.findViewById<TextView>(R.id.tvBMI)
        val Uid=arguments?.getString("uid")
        var bmi=0.0


        val db= FirebaseFirestore.getInstance()

        val docRef=db.collection("Users").document(Uid.toString())

        db.runTransaction { transaction ->
            val snapshot=transaction.get(docRef)
            val težina=snapshot.getString("težina")!!.toDouble()
            val visina=snapshot.getString("visina")!!.toDouble()
            bmi=((težina / (visina/100*visina/100))*100).roundToInt().toDouble()/100
            val data= hashMapOf("BMI" to bmi)
            transaction.set(docRef,data, SetOptions.merge())
        }

        val buttonprikazi=view.findViewById<Button>(R.id.button2)
        tvbmi.setVisibility(View.INVISIBLE)
        buttonprikazi.setOnClickListener {
            tvbmi.text=bmi.toString()
            if(tvbmi.isVisible){
                tvbmi.setVisibility(View.INVISIBLE)
                buttonprikazi.text="Prikaži"
            }
            else{
                tvbmi.setVisibility(View.VISIBLE)
                buttonprikazi.text="Sakrij"
            }
        }

        btnBMI.setOnClickListener {
            if (etHeight.text.isNotEmpty().and(etWeight.text.isNotEmpty())){
            val visina=etHeight.text.toString().toDouble()
            val tezina=etWeight.text.toString().toDouble()
            val izracunatiBMI=((tezina / (visina/100*visina/100))*100).roundToInt().toDouble()/100
            view.findViewById<TextView>(R.id.tvUneseniBmi).text=izracunatiBMI.toString()}
            else{
                Toast.makeText(context,"Unesite podatke!", Toast.LENGTH_SHORT).show()
            }
        }
        return view
    }

}