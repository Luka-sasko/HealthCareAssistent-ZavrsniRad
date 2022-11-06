package com.example.healthcareassistent

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.*
import androidx.navigation.NavController
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import kotlin.math.roundToInt


class CaloriesIntakeActivity : AppCompatActivity() {

    lateinit var listView: ListView
    var food:ArrayList<String> = ArrayList()
    var selectedFood:ArrayList<String> = ArrayList()
    var arrayAdapter: ArrayAdapter<String>?= null
    lateinit var etSearch: EditText
    var koef=1.0
    lateinit var ukupno:String

    var UGH=0.0
    var Masti=0.0
    var Proteini=0.0
    var Kalorije=0





    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calories_intake)
        val userID=intent.getStringExtra("user_id")
        val TipHrane=intent.getStringExtra("tip")
        val db=FirebaseFirestore.getInstance()
        val docRef=db.collection("Food")
        val etGramaza=findViewById<EditText>(R.id.etGramaza)
        val btnDodaj=findViewById<Button>(R.id.buttonAdd)
        val btnPonisti=findViewById<Button>(R.id.buttonRemove)
        btnDodaj.visibility= View.INVISIBLE
        btnPonisti.visibility= View.INVISIBLE
        docRef.whereEqualTo("Tip",TipHrane.toString()).orderBy("Ime",Query.Direction.ASCENDING).get().addOnSuccessListener {
            for (item in it){
                food.add(item["Ime"].toString())
            }
            listView=findViewById(R.id.foodListView)
            etSearch=findViewById(R.id.etSearch)
            arrayAdapter= ArrayAdapter(this,android.R.layout.simple_list_item_1,android.R.id.text1,food)
            listView.adapter=arrayAdapter


            etSearch.addTextChangedListener(object :TextWatcher {
                override fun afterTextChanged(s: Editable?) {}

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {arrayAdapter!!.filter.filter(s)}
            })

            etGramaza.addTextChangedListener(object :TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    if (etGramaza.text.isNotEmpty()) {
                        koef = ((etGramaza.text.toString().toDouble() / 100) * 100).roundToInt().toDouble() / 100
                    }
                    else{koef=1.0}
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            })


            listView.setOnItemClickListener { adapterView, view, i, l ->
                docRef.whereEqualTo("Tip",TipHrane.toString()).whereEqualTo("Ime",food[i]).get().addOnSuccessListener {
                    for (item in it) {
                        UGH=UGH+item["UGH"].toString().toDouble()*koef
                        Masti=Masti+item["Masti"].toString().toDouble()*koef
                        Proteini=Proteini+item["Proteini"].toString().toDouble()*koef
                        Kalorije=(Kalorije.toDouble()+item["Kalorije"].toString().toDouble()*koef).roundToInt()
                        ukupno= "Ugljikohidrata: "+UGH.toString()+ "\nMasti: "+Masti.toString()+ "\nProteina: "+Proteini.toString()+ ". \nUkupno kalorija: "+Kalorije.toString()+"!"
                        Toast.makeText(this,"Trenutno za dodati:\n"+ukupno+"\nUkoliko želite dodati odabrene proizvode pritisnite gumb Dodaj u gorenjem desnom kutu!\nUkoliko želite poništiti izabrane namirnice pritisnite gumb Poništi!",Toast.LENGTH_SHORT).show()
                        btnDodaj.visibility= View.VISIBLE
                        btnPonisti.visibility= View.VISIBLE
                    }
                }
                selectedFood.add(food[i])

            }

            btnDodaj.setOnClickListener {
                var ukupnoUGH:Double
                var ukupnoMasti:Double
                var ukupnoKalorija:Int
                var ukupnoProteini:Double

                val doc=db.collection("Users").document(userID.toString()).collection("Calories").document("podaci")
                doc.get().addOnSuccessListener {
                    ukupnoKalorija=it["UneseneKalorije"].toString().toInt()
                    ukupnoMasti=it["Masti"].toString().toDouble()
                    ukupnoProteini=it["Proteini"].toString().toDouble()
                    ukupnoUGH=it["UGH"].toString().toDouble()
                    doc.set(hashMapOf("UneseneKalorije" to Kalorije+ukupnoKalorija), SetOptions.merge())
                    doc.set(hashMapOf("UGH" to (UGH+ukupnoUGH).roundToInt()), SetOptions.merge())
                    doc.set(hashMapOf("Masti" to (Masti+ukupnoMasti).roundToInt()), SetOptions.merge())
                    doc.set(hashMapOf("Proteini" to (Proteini+ukupnoProteini).roundToInt()), SetOptions.merge())
                    ukupno ="Ugljikohidrata: " + UGH.toString() + "\nMasti: " + Masti.toString() + "\nProteina: " + Proteini.toString() + ". \nUkupno kalorija: " + Kalorije.toString() + "!"
                    Toast.makeText(this,"Uspješno ste dodali odabrane proizvode.\nUkupno dodano: "+ukupno, Toast.LENGTH_SHORT).show()
                    Toast.makeText(this,"Trenutno uneseno danas:\n"+"Ugljikohidrata: "+(UGH+ukupnoUGH).toString()+ "\nMasti: "+(Masti+ukupnoMasti).toString()+ "\nProteina: "+(Proteini+ukupnoProteini).toString()+ ". \nUkupno kalorija: "+(Kalorije+ukupnoKalorija).toString()+"!",Toast.LENGTH_LONG).show()
                    UGH=0.0
                    Masti=0.0
                    Kalorije=0
                    Proteini=0.0
                }

            }

            btnPonisti.setOnClickListener {
                UGH=0.0
                Masti=0.0
                Kalorije=0
                Proteini=0.0
                ukupno ="Ugljikohidrata: " + UGH.toString() + "\nMasti: " + Masti.toString() + "\nProteina: " + Proteini.toString() + ". \nUkupno kalorija: " + Kalorije.toString() + "!"
                Toast.makeText(this,"Uspješno ste obrisali odabrane proizvode.\nTrenutno za dodati:\n"+ukupno, Toast.LENGTH_SHORT).show()
            }








        }
    }
}



