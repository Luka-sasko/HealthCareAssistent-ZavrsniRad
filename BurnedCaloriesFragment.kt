package com.example.healthcareassistent

import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

class BurnedCaloriesFragment : Fragment() {
    var trajanjeAktivnosti=0
    var potrošenoKalorija=0.0
    var koefPotrošnje=0.0
    var aktivnost=""
    private lateinit var progressBar: ProgressBar
    private lateinit var seekBar: SeekBar
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val tvtext="Potrošeno tijekom\n aktivnosti:\n"

        val Uid=arguments?.getString("uid")

        val view = inflater.inflate(R.layout.fragment_burned_calories, container, false)
        val etActivityDuration=view.findViewById<EditText>(R.id.etActivityDuration)
        val tvPotroseno=view.findViewById<TextView>(R.id.tvBurnedCaloriesEst)
        val btnSpremi=view.findViewById<ImageButton>(R.id.ibtnSaveBurnedCal)


        val date = SimpleDateFormat("dd MM yyyy")
        val currentDate = date.format(Date())

        val db=FirebaseFirestore.getInstance()
        val docRef=db.collection("Users").document(Uid.toString()).collection("BurnedCalories").document("podaci")
        docRef.get().addOnSuccessListener {

            if (it["Datum"] != currentDate) {
                docRef.set(hashMapOf("Datum" to currentDate), SetOptions.merge())
                docRef.set(hashMapOf("PotrošenoKalorija" to 0), SetOptions.merge())
                progressBar.progress=0

            }else{
                if(it["Cilj"]!=null) { seekBar.progress = it["Cilj"].toString().toInt();progressBar.max=seekBar.progress }else{ seekBar.progress=0 }
                progressBar.progress=it["PotrošenoKalorija"].toString().toDouble().roundToInt()
            }

        }

        progressBar=view.findViewById(R.id.progressBarBurnedCalories)
        seekBar=view.findViewById(R.id.seekBarGoalToBurn)

        seekBar.min=0
        seekBar.max=3000

        seekBar.setOnSeekBarChangeListener(object:SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, p2: Boolean) {
                view.findViewById<TextView>(R.id.tvGoalToBurn).text=progress.toString()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                Toast.makeText(context,"Uspješno postavljen novi dnevni cilj potrošnje kalorija: "+seekBar.progress.toString()+".",Toast.LENGTH_SHORT).show()
                docRef.set(hashMapOf("Cilj" to seekBar.progress), SetOptions.merge())
                progressBar.max=seekBar.progress
            }

        })


        val radioGroup=view.findViewById<RadioGroup>(R.id.radioGroup)
        var radioButton:RadioButton
        radioGroup.setOnCheckedChangeListener { radioGroup, i ->
            etActivityDuration.visibility=View.VISIBLE
            tvPotroseno.visibility=View.VISIBLE
            btnSpremi.visibility=View.VISIBLE
            radioButton=view.findViewById(radioGroup.checkedRadioButtonId)

            if (radioButton.text=="Hodanje"){koefPotrošnje=4.5}
            if (radioButton.text=="Trčanje"){koefPotrošnje=12.5}
            if (radioButton.text=="Plivanje"){koefPotrošnje=9.5}
            if (radioButton.text=="Planinarenje"){koefPotrošnje=13.5}
            if (radioButton.text=="Vožnja biciklom"){koefPotrošnje=10.0}

            aktivnost=radioButton.text.toString()
            Toast.makeText(context,"Odabrali ste aktivnost :"+radioButton.text+". "+
                "\nProsječna potrošnja vaše aktivnosti iznosi : "+(koefPotrošnje*60).toString()+"kcal/h."+
                "\nZa dodavanje vaše aktivnosti unesite njezino vrijeme trajanja i pritisnite gumb ispod teksta"
                ,Toast.LENGTH_LONG).show()
        }

        etActivityDuration.visibility=View.INVISIBLE
        tvPotroseno.visibility=View.INVISIBLE
        btnSpremi.visibility=View.INVISIBLE

        btnSpremi.setOnClickListener {
            if (etActivityDuration.text.isNotEmpty()) {
                docRef.set(hashMapOf("PotrošenoKalorija" to potrošenoKalorija), SetOptions.merge())
                Toast.makeText(context, "Aktivnost zabilježena: " + aktivnost + "." + "\nUkupno kalorija potrošeno: " + potrošenoKalorija.toString() + "!", Toast.LENGTH_LONG).show()
                etActivityDuration.visibility=View.INVISIBLE
                btnSpremi.visibility=View.INVISIBLE
                docRef.get().addOnSuccessListener {
                    progressBar.progress=it["PotrošenoKalorija"].toString().toDouble().roundToInt()
                }
            }else {
                Toast.makeText(context, "Unesite podatke za vrijeme trajanja aktivnosti!!", Toast.LENGTH_LONG).show()
            }
        }

        etActivityDuration.addTextChangedListener(object:TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (s.toString() != "") {
                    trajanjeAktivnosti = s.toString().toInt()
                    potrošenoKalorija = trajanjeAktivnosti * koefPotrošnje
                    tvPotroseno.text = tvtext + " " + potrošenoKalorija.toString()
                }
            }

        })





        return view
    }


}