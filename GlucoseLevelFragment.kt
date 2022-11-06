package com.example.healthcareassistent

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.text.isDigitsOnly
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

private lateinit var adapter: MyAdapter
private lateinit var recyclerView: RecyclerView
private lateinit var bpArrayList: ArrayList<BloodPresureData>

class GlucoseLevelFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
         val view = inflater.inflate(R.layout.fragment_glucose_level, container, false)

        val tvProsjek=view.findViewById<TextView>(R.id.tvGlucoseAverage)
        val etTrenutna=view.findViewById<EditText>(R.id.etGlucoseLevel)
        val btnSpremi=view.findViewById<Button>(R.id.saveGlucose)
        val btnProsjek=view.findViewById<Button>(R.id.calcAverage)
        val Uid = arguments?.getString("uid")
        val db=FirebaseFirestore.getInstance()
        val docRef=db.collection("Users").document(Uid.toString()).collection("GlucoseLevel")

        fun calcAverage() {
            docRef.orderBy("Datum").limit(300).get().addOnSuccessListener {
                var suma = 0.0
                var n = 0.0
                for (data in it) {
                    suma = suma + data["Šećer"].toString().toDouble()
                    n = n + 1.0
                }
                tvProsjek.text=" "+((((suma/n)*10).roundToInt()).toDouble()/10).toString()+" "
            }
        }

        btnProsjek.setOnClickListener {
            calcAverage()
        }

        btnSpremi.setOnClickListener {
            if(etTrenutna.text.isNotEmpty()){
                val date = SimpleDateFormat("d M yyyy HH:mm:ss")
                val currentDate = date.format(Date())
                val GlucoseDoc = docRef.document(currentDate.toString())
                val secer = etTrenutna.text.toString()
                val inputsecer = hashMapOf("Šećer" to secer)
                val inputdate = hashMapOf("Datum" to currentDate)
                GlucoseDoc.set(inputsecer, SetOptions.merge())
                GlucoseDoc.set(inputdate, SetOptions.merge())
                calcAverage()
                readData(Uid.toString())
            }

            else{
                Toast.makeText(context,"Unesite podatke!",Toast.LENGTH_SHORT).show()
            }
        }


        val layoutManager = LinearLayoutManager(context)
        recyclerView = view.findViewById(R.id.recyclerViewGlucoseHistory)
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)
        recyclerView.visibility=View.INVISIBLE


        val btnPovijest=view.findViewById<Button>(R.id.btnPovijest)
        btnPovijest.setOnClickListener {
            if (recyclerView.visibility==View.VISIBLE){
                recyclerView.visibility=View.INVISIBLE}
            else{
                recyclerView.visibility=View.VISIBLE
            }
            readData(Uid.toString())

            }

        return view
    }

    private fun readData(Uid:String){
        val db=FirebaseFirestore.getInstance()
        val docRef=db.collection("Users").document(Uid).collection("GlucoseLevel")
        bpArrayList = arrayListOf<BloodPresureData>()
        docRef.orderBy("Datum",Query.Direction.DESCENDING).limit(30).get().addOnSuccessListener {
            for (data in it) {
                val info = BloodPresureData("", "")
                info.Datum = data.get("Datum").toString()
                info.Value = data.get("Šećer").toString()
                bpArrayList.add(info)
            }
            adapter = MyAdapter(bpArrayList)
            recyclerView.adapter = adapter
        }
    }
}