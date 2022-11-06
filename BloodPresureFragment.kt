package com.example.healthcareassistent

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CalendarView
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import java.text.SimpleDateFormat
import java.util.*
import java.time.LocalDateTime
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

private lateinit var adapter: MyAdapter
private lateinit var recyclerView: RecyclerView
private lateinit var bpArrayList: ArrayList<BloodPresureData>

class BloodPresureFragment : Fragment() {
    val month=SimpleDateFormat("MM")
    val currentMonth=month.format(Date())

    private lateinit var Uid:String
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_blood_presure, container, false)
        val tlak = view.findViewById<EditText>(R.id.etBloodPresure).text
        val btnSpremi = view.findViewById<Button>(R.id.saveNew)
        val btnPovijest = view.findViewById<Button>(R.id.showHistory)
        val recyclerViewPovijest = view.findViewById<RecyclerView>(R.id.recyclerViewHistory)
        var markedDate = ""
        val calendar = view.findViewById<CalendarView>(R.id.calendarView)
        calendar.setOnDateChangeListener { calendarView, i, i2, i3 ->

            var dan:String
            var mjesec:String
            var godina:String

            if(i3<10){ dan="0"+i3.toString() }
            else{ dan=i3.toString() }

            if(i2+1<10){ mjesec="0"+(i2+1).toString() }
            else{ mjesec=(i2+1).toString() }

            godina=i.toString()

            markedDate=dan+"/"+mjesec+"/"+godina


        }

        Uid = arguments?.getString("uid").toString()
        val db = FirebaseFirestore.getInstance()
        val docRef = db.collection("Users").document(Uid.toString())


        btnSpremi.setOnClickListener {
            if (tlak.isNotEmpty()) {
                val date = SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss")
                val currentDate = date.format(Date())
                val BPdoc = docRef.collection("BloodPresure").document(currentDate.toString())
                if (markedDate == "") {
                    markedDate = SimpleDateFormat("dd/MM/yyyy").format(Date())
                }
                val inputdate = hashMapOf("Datum" to markedDate)
                val inputtlak = hashMapOf("Tlak" to tlak.toString())
                BPdoc.set(inputdate, SetOptions.merge())
                BPdoc.set(inputtlak, SetOptions.merge())
                readData()
                Toast.makeText(context, "Spremljeno!\n ${markedDate}: ˘${tlak}", Toast.LENGTH_SHORT)
                    .show()
            }else{
                Toast.makeText(context,"Unesite podatke!",Toast.LENGTH_SHORT).show()
            }
        }





        val layoutManager = LinearLayoutManager(context)
        recyclerView = recyclerViewPovijest
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)
        readData()

        recyclerView.visibility=View.INVISIBLE

        btnPovijest.setOnClickListener {
            if (recyclerView.visibility == View.VISIBLE) {
                recyclerView.visibility = View.INVISIBLE
            } else {
                recyclerView.visibility = View.VISIBLE
            }
            readData()
        }

        return view
    }
    private fun readData(){
        bpArrayList = arrayListOf<BloodPresureData>()
        val db = FirebaseFirestore.getInstance()
        val docRef = db.collection("Users").document(Uid.toString())
        docRef.collection("BloodPresure").orderBy("Datum",Query.Direction.DESCENDING).limit(30).get()
            .addOnSuccessListener {
                for (data in it) {
                    val info = BloodPresureData("", "")
                    info.Datum = data.get("Datum").toString()
                    info.Value = data.get("Tlak").toString()
                    bpArrayList.add(info)
                    adapter = MyAdapter(bpArrayList)
                    recyclerView.adapter = adapter
                }
            }
    }

}