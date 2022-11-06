package com.example.healthcareassistent

import android.media.Image
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.getField
import com.google.firebase.ktx.Firebase
import org.w3c.dom.Text
import kotlin.math.roundToInt

class FluidIntakeFragment : Fragment() {
    var količina_vode=0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {



        val view = inflater.inflate(R.layout.fragment_fluid_intake, container, false)
        val db= FirebaseFirestore.getInstance()
        val Uid=arguments?.getString("uid")
        val tv_brojcasa=view.findViewById<TextView>(R.id.broj_casa_vode)

        val casa1=view.findViewById<ImageView>(R.id.casa1)
        val casa2=view.findViewById<ImageView>(R.id.casa2)
        val casa3=view.findViewById<ImageView>(R.id.casa3)
        val casa4=view.findViewById<ImageView>(R.id.casa4)
        val casa5=view.findViewById<ImageView>(R.id.casa5)
        val btn_plus=view.findViewById<Button>(R.id.add_glass)
        val btn_minus=view.findViewById<Button>(R.id.reduce_glass)

        fun updateVisibility(){
            if (količina_vode > 1) { casa1.setVisibility(View.VISIBLE) }
            if (količina_vode > 3) { casa2.setVisibility(View.VISIBLE) }
            if (količina_vode > 5) { casa3.setVisibility(View.VISIBLE) }
            if (količina_vode > 7) { casa4.setVisibility(View.VISIBLE) }
            if (količina_vode > 9) { casa5.setVisibility(View.VISIBLE) }

            if (količina_vode<2){ casa1.setVisibility(View.INVISIBLE)}
            if (količina_vode<4){ casa2.setVisibility(View.INVISIBLE)}
            if (količina_vode<6){ casa3.setVisibility(View.INVISIBLE)}
            if (količina_vode<8){ casa4.setVisibility(View.INVISIBLE)}
            if (količina_vode<10){ casa5.setVisibility(View.INVISIBLE)}
            return
        }

        val dbRef=db.collection("Users").document(Uid.toString())
        db.runTransaction { transaction ->
            val snapshot = transaction.get(dbRef)
            količina_vode = snapshot.getDouble("kolicinaVoda")!!.toInt()
            tv_brojcasa.text = količina_vode.toString()
            var brojcasa = 10 - količina_vode
            if (količina_vode > 10) {
                brojcasa = 10
            }
            view.findViewById<TextView>(R.id.preporuka).text = "Preporučena vrijednost koju još trebate unjeti je " + brojcasa.toString() + " čaša."
        }



        btn_plus.setOnClickListener {
            količina_vode=količina_vode+1

            db.runTransaction {  transaction->
                val snapshot=transaction.get(dbRef)
                količina_vode=snapshot.getDouble("kolicinaVoda")!!.toInt()+1
                transaction.update(dbRef,"kolicinaVoda",količina_vode)
                }

            updateVisibility()

            var brojcasa=10-količina_vode
            if (brojcasa<0){
                brojcasa=0
            }

            tv_brojcasa.text=količina_vode.toString()
            view.findViewById<TextView>(R.id.preporuka).text="Preporučena vrijednost koju još trebate unjeti je "+brojcasa.toString()+" čaša."
            Thread.sleep(200)

        }


        btn_minus.setOnClickListener {
            količina_vode=količina_vode-1
            var brojcasa=10-količina_vode
            if (količina_vode<1){
                brojcasa=10
                količina_vode=0
            }
            if(brojcasa<0){brojcasa=0}

            updateVisibility()

            db.runTransaction {  transaction->
                val snapshot=transaction.get(dbRef)
                količina_vode=snapshot.getDouble("kolicinaVoda")!!.toInt()-1
                if (količina_vode<1){ količina_vode=0}
                transaction.update(dbRef,"kolicinaVoda",količina_vode)
            }

            tv_brojcasa.text=količina_vode.toString()
            view.findViewById<TextView>(R.id.preporuka).text="Preporučena vrijednost koju još trebate unjeti je "+brojcasa.toString()+" čaša."
            Thread.sleep(200)
        }











        return view
    }


}