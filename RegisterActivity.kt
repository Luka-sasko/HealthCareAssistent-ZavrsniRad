package com.example.healthcareassistent

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        findViewById<TextView>(R.id.tv_login).setOnClickListener {
            startActivity(Intent(this@RegisterActivity,LoginActivity::class.java))
            finish()
        }

        val regist=findViewById<Button>(R.id.btn_register)
        regist.setOnClickListener {
            when{
                TextUtils.isEmpty(findViewById<EditText>(R.id.et_register_email).text.toString().trim{it <= ' '}) ->
                {
                    Toast.makeText(this@RegisterActivity,"Unesite email vašu email adresu!",Toast.LENGTH_LONG).show()
                }
                TextUtils.isEmpty(findViewById<EditText>(R.id.et_register_password).text.toString().trim{it <= ' '}) ->
                {
                    Toast.makeText(this@RegisterActivity,"Unesite vašu lozinku!",Toast.LENGTH_SHORT).show()
                }
                TextUtils.isEmpty(findViewById<EditText>(R.id.et_register_password2).text.toString().trim{it <= ' '}) ->
                {
                    Toast.makeText(this@RegisterActivity,"Unesite vašu lozinku!",Toast.LENGTH_SHORT).show()
                }
                else -> {
                    val email: String =
                        findViewById<EditText>(R.id.et_register_email).text.toString()
                            .trim { it <= ' ' }
                    val password: String =
                        findViewById<EditText>(R.id.et_register_password).text.toString()
                            .trim { it <= ' ' }
                    val password2: String =
                        findViewById<EditText>(R.id.et_register_password2).text.toString()
                            .trim { it <= ' ' }
                    if (password == password2) {
                        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(
                                { task ->
                                    if (task.isSuccessful) {
                                        val firebaseUser: FirebaseUser = task.result!!.user!!
                                        Toast.makeText(this@RegisterActivity, "Uspješno ste se registrirali.", Toast.LENGTH_LONG).show()
                                        val db = Firebase.firestore
                                        val date = SimpleDateFormat("dd/M/yyyy")
                                        val currentDate = date.format(Date())

                                        val usertoAdd = User("Ime", "Prezime", email, "+385"
                                            , 0.0, 0.0, 0, currentDate)
                                        db.collection("Users").document(firebaseUser.uid).set(usertoAdd)

                                       val intent = Intent(this@RegisterActivity, MainActivity::class.java)
                                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                        intent.putExtra("user_id", firebaseUser.uid)
                                        intent.putExtra("email_id", email)
                                        startActivity(intent)
                                        finish()
                                    } else { Toast.makeText(this@RegisterActivity, task.exception!!.message.toString(), Toast.LENGTH_SHORT).show() }

                                })
                    }else {Toast.makeText(this@RegisterActivity, "Lozinke se ne podudaraju!", Toast.LENGTH_SHORT).show()}
                }
            }
        }
    }
}