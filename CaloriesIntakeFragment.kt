package com.example.healthcareassistent

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

class CaloriesIntakeFragment : Fragment() {

    private lateinit var comunicator: CaloriesIntakeComunicator

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_calories_intake, container, false)

        val Uid=arguments?.getString("uid")

        val btnMeso=view.findViewById<ImageButton>(R.id.ibtnMeat)
        val btnPrilog=view.findViewById<ImageButton>(R.id.ibtnCarbs)
        val btnVocePovrce=view.findViewById<ImageButton>(R.id.ibtnFruitVegetable)
        val btnDesert=view.findViewById<ImageButton>(R.id.ibtnSweets)
        val btnMlijecniProizvodi=view.findViewById<ImageButton>(R.id.ibtnMlijecniProizvodi)
        val btnPića=view.findViewById<ImageButton>(R.id.ibtnDrinks)

        val tvProgress=view.findViewById<TextView>(R.id.tvProgress)
        val etCilj=view.findViewById<EditText>(R.id.etGoal)
        val tvUneseno=view.findViewById<TextView>(R.id.tvCalories)
        val progressBar=view.findViewById<ProgressBar>(R.id.progressBarCalories)
        val ibtnReset=view.findViewById<ImageButton>(R.id.ibtnReset)

        val db=FirebaseFirestore.getInstance().collection("Users").document(Uid.toString())
        val docRef=db.collection("Calories")
        val date = SimpleDateFormat("d M yyyy")
        val currentDate = date.format(Date())

        val etUGHCilj=view.findViewById<EditText>(R.id.etUGHCilj)
        val etMastiCilj=view.findViewById<EditText>(R.id.etMastiCilj)
        val etProteiniCIlj=view.findViewById<EditText>(R.id.etProteiniCilj)
        val tvUnesenoUGH=view.findViewById<TextView>(R.id.tvUnesenoUGH)
        val tvUnesenoProteini=view.findViewById<TextView>(R.id.tvUnesenoProteini)
        val tvUnesenoMasti=view.findViewById<TextView>(R.id.tvUnesenoMasti)


        comunicator=activity as CaloriesIntakeComunicator
        ibtnReset.setOnClickListener {
            comunicator.refreshFragment()
        }

        docRef.document("podaci").get().addOnSuccessListener {
            if (it["Datum"]!=currentDate){
                updateInfo(Uid.toString(),tvUneseno.text.toString())
                tvUneseno.text="0"

                docRef.document("podaci").set(hashMapOf("UneseneKalorijeJucer" to it["UneseneKalorije"]), SetOptions.merge())
                docRef.document("podaci").set(hashMapOf("UGHJucer" to it["UGH"]), SetOptions.merge())
                docRef.document("podaci").set(hashMapOf("MastiJucer" to it["Masti"]), SetOptions.merge())
                docRef.document("podaci").set(hashMapOf("ProteiniJucer" to it["Proteini"]), SetOptions.merge())

                docRef.document("podaci").set(hashMapOf("UneseneKalorije" to 0), SetOptions.merge())
                docRef.document("podaci").set(hashMapOf("UGH" to 0.0), SetOptions.merge())
                docRef.document("podaci").set(hashMapOf("Masti" to 0.0), SetOptions.merge())
                docRef.document("podaci").set(hashMapOf("Proteini" to 0.0), SetOptions.merge())

                etCilj.hint=it["Cilj"].toString()
                etMastiCilj.hint=it["MastiCilj"].toString()
                etProteiniCIlj.hint=it["ProteiniCilj"].toString()
                etUGHCilj.hint=it["UGHCilj"].toString()
                tvProgress.text=tvUneseno.text.toString() + " / "+etCilj.hint.toString()+" kcal"

            }
            else{

                if(it["Cilj"]!=null) {
                    etCilj.hint = it["Cilj"].toString()
                }else{
                    etCilj.hint = "0"
                }

                if(it["UGHCilj"]!=null) {
                    etUGHCilj.hint = it["UGHCilj"].toString()
                }else{
                    etUGHCilj.hint = "0"
                }

                if(it["ProteiniCilj"]!=null) {
                    etProteiniCIlj.hint = it["ProteiniCilj"].toString()
                }else{
                    etProteiniCIlj.hint = "0"
                }

                if(it["MastiCilj"]!=null) {
                    etMastiCilj.hint = it["MastiCilj"].toString()
                }else{
                    etMastiCilj.hint = "0"
                }

                tvProgress.text=tvUneseno.text.toString() + " / "+etCilj.hint.toString()+" kcal"
                progressBar.max=etCilj.hint.toString().toInt()
                progressBar.progress=tvUneseno.text.toString().toInt()

                tvUneseno.text=it["UneseneKalorije"].toString()
                tvUnesenoMasti.text="Uneseno: "+it["Masti"].toString().toDouble().roundToInt().toString()
                tvUnesenoProteini.text="Uneseno: "+it["Proteini"].toString().toDouble().roundToInt().toString()
                tvUnesenoUGH.text="Uneseno: "+it["UGH"].toString().toDouble().roundToInt().toString()

                tvProgress.text=tvUneseno.text.toString() + " / "+etCilj.hint.toString()+" kcal"
                progressBar.max=etCilj.hint.toString().toInt()
                progressBar.progress=tvUneseno.text.toString().toDouble().roundToInt()

            }
        }

        val CaloriesDoc = docRef.document("podaci")


        etCilj.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {

                var inputCilj= hashMapOf("Cilj" to 0)

                if (etCilj.text.isNotEmpty()){
                    val noviCilj=etCilj.text.toString().toInt()
                    inputCilj= hashMapOf("Cilj" to noviCilj )
                }
                CaloriesDoc.set(inputCilj, SetOptions.merge())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        })

        etUGHCilj.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {

                var inputUGHCilj= hashMapOf("UGHCilj" to 0)

                if (etUGHCilj.text.isNotEmpty()){
                    val noviCilj=etUGHCilj.text.toString().toInt()
                    inputUGHCilj= hashMapOf("UGHCilj" to noviCilj )
                }
                CaloriesDoc.set(inputUGHCilj, SetOptions.merge())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        })

        etProteiniCIlj.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {

                var inputProteiniCilj= hashMapOf("ProteiniCilj" to 0)

                if (etProteiniCIlj.text.isNotEmpty()){
                    val noviCilj=etProteiniCIlj.text.toString().toInt()
                    inputProteiniCilj= hashMapOf("ProteiniCilj" to noviCilj )
                }
                CaloriesDoc.set(inputProteiniCilj, SetOptions.merge())
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        })

        etMastiCilj.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {

                var inputMastiCilj= hashMapOf("MastiCilj" to 0)

                if (etMastiCilj.text.isNotEmpty()){
                    val noviCilj=etMastiCilj.text.toString().toInt()
                    inputMastiCilj= hashMapOf("MastiCilj" to noviCilj )
                }else{
                    val noviCilj=etMastiCilj.hint.toString().toInt()
                    inputMastiCilj= hashMapOf("MastiCilj" to noviCilj )
                }

                CaloriesDoc.set(inputMastiCilj, SetOptions.merge())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        })



        btnMeso.setOnClickListener {
            activity?.let {
                val intent=Intent(it,CaloriesIntakeActivity::class.java)
                intent.putExtra("tip","Meso")
                intent.putExtra("user_id",Uid)
                it.startActivity(intent)
            }
        }
        btnPrilog.setOnClickListener {
            activity?.let {
                val intent=Intent(it,CaloriesIntakeActivity::class.java)
                intent.putExtra("tip","Prilozi")
                intent.putExtra("user_id",Uid)
                it.startActivity(intent)
            }
        }
        btnVocePovrce.setOnClickListener {
            activity?.let {
                val intent=Intent(it,CaloriesIntakeActivity::class.java)
                intent.putExtra("tip","VoćeiPovrće")
                intent.putExtra("user_id",Uid)
                it.startActivity(intent)
            }
        }
        btnDesert.setOnClickListener {
            activity?.let {
                val intent=Intent(it,CaloriesIntakeActivity::class.java)
                intent.putExtra("tip","Desert")
                intent.putExtra("user_id",Uid)
                it.startActivity(intent)
            }
        }
        btnPića.setOnClickListener {
            activity?.let {
                val intent=Intent(it,CaloriesIntakeActivity::class.java)
                intent.putExtra("tip","Piće")
                intent.putExtra("user_id",Uid)
                it.startActivity(intent)
            }
        }
        btnMlijecniProizvodi.setOnClickListener {
            activity?.let {
                val intent=Intent(it,CaloriesIntakeActivity::class.java)
                intent.putExtra("tip","MlijecniProizvodi")
                intent.putExtra("user_id",Uid)
                it.startActivity(intent)
            }
        }







        return view
    }

    private fun updateInfo(Uid:String,tvUneseno:String) {
        val db=FirebaseFirestore.getInstance().collection("Users").document(Uid.toString())
        val docRef=db.collection("Calories")
        val date = SimpleDateFormat("d M yyyy")
        val currentDate = date.format(Date())

        docRef.document("podaci").set(hashMapOf("Datum" to currentDate), SetOptions.merge())
    }


}