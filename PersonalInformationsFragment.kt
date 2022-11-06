package com.example.healthcareassistent


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


private lateinit var comunicator: PersonalInformationsComunicator

class PersonalInformationsFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_personal_informations, container, false)
        val button = view.findViewById<Button>(R.id.button_edit_personal_informations)

        comunicator=activity as PersonalInformationsComunicator
        button.setOnClickListener {

            comunicator.changeEditingFragment()
        }

        val db=Firebase.firestore
        val Uid=arguments?.getString("uid")
        val ime=view.findViewById<TextView>(R.id.TVUserName)
        val prezime=view.findViewById<TextView>(R.id.TVUserSurname)
        val email=view.findViewById<TextView>(R.id.TVUserEmail)
        val kontakt=view.findViewById<TextView>(R.id.TVUserContact)
        val visina=view.findViewById<TextView>(R.id.TVUserHeight)
        val tezina=view.findViewById<TextView>(R.id.TVUserWeight)

        db.collection("Users").document(Uid.toString()).get().addOnSuccessListener {
            val data=it.data
            ime.text=data?.get("ime").toString()
            prezime.text=data?.get("prezime").toString()
            kontakt.text=data?.get("kontakt").toString()
            email.text=data?.get("email").toString()
            visina.text=data?.get("visina").toString()
            tezina.text=data?.get("težina").toString()


        }







        return view
    }

}

