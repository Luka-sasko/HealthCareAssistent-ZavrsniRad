package com.example.healthcareassistent

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.androidplot.xy.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import java.text.FieldPosition
import java.text.Format
import java.text.ParsePosition
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.math.log

class KolicinaSnaFragment : Fragment() {
    private lateinit var povijest:List<String>
    var time="0h 0min"
    var sleepTime=0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view= inflater.inflate(R.layout.fragment_kolicina_sna, container, false)

        val Uid=arguments?.getString("uid")

        val seekBar=view.findViewById<SeekBar>(R.id.seekBar)
        val tvKolicinaSna=view.findViewById<TextView>(R.id.tvSleepTimeValue)
        val btnDodaj=view.findViewById<ImageButton>(R.id.ibtnAddSleepTime)

        val db=FirebaseFirestore.getInstance().collection("Users").document(Uid.toString())
        val docRef=db.collection("SleepTime")

        val date = SimpleDateFormat("dd MM yyyy")
        val day = SimpleDateFormat("EEE")
        val month=SimpleDateFormat("MM")
        val currentDay=day.format(Date())
        val currentDate = date.format(Date())
        val currentMonth=month.format(Date())


        seekBar.setOnSeekBarChangeListener(object :SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, p2: Boolean) {
                sleepTime=progress
                var sati=progress.div(60)
                var minuta=progress-sati*60
                time=sati.toString()+"h "+minuta.toString()+"min"
                tvKolicinaSna.text=time

            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                Toast.makeText(context,"Pritisnite gumb "+" kako bih zabilježili podatke!",Toast.LENGTH_SHORT).show()
                btnDodaj.visibility=View.VISIBLE
            }

        })
        btnDodaj.setOnClickListener {
            val sleepData:SleepData
            val Day=currentDay
            val Datum=currentDate
            val Month=currentMonth
            val VrijemeSpavanja= sleepTime
            val vrijemeSpavanja=time
            if (VrijemeSpavanja!=0){

                sleepData= SleepData(Day,Month,Datum,VrijemeSpavanja,vrijemeSpavanja)
                docRef.add(sleepData)

                Toast.makeText(context, "Uspješno dodana vrijednost trajanja sna: " + time + "!", Toast.LENGTH_SHORT).show()
            }else {
                Toast.makeText(context, "Unesite podatke!", Toast.LENGTH_SHORT).show()

            }
            btnDodaj.visibility=View.INVISIBLE

        }

        val progressBar1=view.findViewById<ProgressBar>(R.id.progressBar6)
        val progressBar2=view.findViewById<ProgressBar>(R.id.progressBar1)
        val progressBar3=view.findViewById<ProgressBar>(R.id.progressBar3)
        val progressBar4=view.findViewById<ProgressBar>(R.id.progressBar4)
        val progressBar5=view.findViewById<ProgressBar>(R.id.progressBar5)
        val progressBar6=view.findViewById<ProgressBar>(R.id.progressBar2)
        val progressBar7=view.findViewById<ProgressBar>(R.id.progressBar7)

        val tvday1=view.findViewById<TextView>(R.id.tvDay1)
        val tvday2=view.findViewById<TextView>(R.id.tvDay2)
        val tvday3=view.findViewById<TextView>(R.id.tvDay3)
        val tvday4=view.findViewById<TextView>(R.id.tvDay4)
        val tvday5=view.findViewById<TextView>(R.id.tvDay5)
        val tvday6=view.findViewById<TextView>(R.id.tvDay6)
        val tvday7=view.findViewById<TextView>(R.id.tvDay7)

        var sleepTimeList:ArrayList<Int>
        var DayList:ArrayList<String>
        var maxST = 960
        var index=0
        docRef.orderBy("date",Query.Direction.DESCENDING).limit(7).get().addOnSuccessListener {
            sleepTimeList = ArrayList()
            DayList= ArrayList()
            for (data in it) {
                sleepTimeList.add(data["sleepTime"].toString().toInt())
                DayList.add(data["dan"].toString())
            }
            while (sleepTimeList.size != 7) {
                sleepTimeList.add(0)
                DayList.add(" ")
            }

            maxST= sleepTimeList.get(0)
            while (index<7){
                if (maxST<sleepTimeList.get(index)){
                    maxST=sleepTimeList.get(index)

                }
                index++
            }
            progressBar1.max = maxST
            progressBar2.max = maxST
            progressBar3.max = maxST
            progressBar4.max = maxST
            progressBar5.max = maxST
            progressBar6.max = maxST
            progressBar7.max = maxST

            progressBar1.progress = sleepTimeList.get(0)
            progressBar2.progress = sleepTimeList.get(1)
            progressBar3.progress = sleepTimeList.get(2)
            progressBar4.progress = sleepTimeList.get(3)
            progressBar5.progress = sleepTimeList.get(4)
            progressBar6.progress = sleepTimeList.get(5)
            progressBar7.progress = sleepTimeList.get(6)

            tvday1.text=DayList.get(0)
            tvday2.text=DayList.get(1)
            tvday3.text=DayList.get(2)
            tvday4.text=DayList.get(3)
            tvday5.text=DayList.get(4)
            tvday6.text=DayList.get(5)
            tvday7.text=DayList.get(6)
            }




        return view
    }
}


