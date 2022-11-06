package com.example.healthcareassistent

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.android.awaitFrame
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.launch
import kotlin.math.ceil


private lateinit var comunicator: PersonalInformationsComunicator


class EditPersonalInformationsragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_edit_personal_informationsragment, container, false)
        val button = view.findViewById<Button>(R.id.button_save_personal_informations)

        val ime=view.findViewById<TextView>(R.id.etUserName)
        val prezime=view.findViewById<TextView>(R.id.etUserSurname)
        val email=view.findViewById<TextView>(R.id.tvUserEmail)
        val kontakt=view.findViewById<TextView>(R.id.etUserContact)
        val visina=view.findViewById<TextView>(R.id.etUserHeight)
        val tezina=view.findViewById<TextView>(R.id.etUserWeight)

        val Uid=arguments?.getString("uid")
        val db=FirebaseFirestore.getInstance()

        email.setOnClickListener {

           Toast.makeText(context,"Nije moguće promjeniti email adresu!",Toast.LENGTH_SHORT).show()

        }

        db.collection("Users").document(Uid.toString()).get().addOnSuccessListener {
            val data = it.data
            ime.text = data?.get("ime").toString()
            prezime.text = data?.get("prezime").toString()
            kontakt.text = data?.get("kontakt").toString()
            email.text = data?.get("email").toString()
            visina.text = data?.get("visina").toString()
            tezina.text = data?.get("težina").toString()
        }





        comunicator=activity as PersonalInformationsComunicator
        button.setOnClickListener {
            val docRef = db.collection("Users").document(Uid.toString())
            db.runTransaction { transaction->
                transaction.update(docRef,"ime",ime.text.toString())
                transaction.update(docRef,"prezime",prezime.text.toString())
                transaction.update(docRef,"kontakt",kontakt.text.toString())
                transaction.update(docRef,"težina",tezina.text.toString())
                transaction.update(docRef,"visina",visina.text.toString())
            }
            Thread.sleep(900)
            comunicator.changeFragment()
        }


        return view
    }
}


