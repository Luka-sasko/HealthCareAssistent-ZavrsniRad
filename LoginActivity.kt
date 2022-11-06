package com.example.healthcareassistent

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        findViewById<TextView>(R.id.tv_register).setOnClickListener{
            startActivity(Intent(this@LoginActivity,RegisterActivity::class.java))
            finish()
        }

        val login=findViewById<Button>(R.id.login)

                login.setOnClickListener {
                    when
                    {
                        TextUtils.isEmpty(findViewById<EditText>(R.id.et_login_email).text.toString().trim { it <= ' ' }) ->
                        { Toast.makeText( this@LoginActivity, "Molim Vas, unesite email!", Toast.LENGTH_SHORT ).show() }
                        TextUtils.isEmpty(
                            findViewById<EditText>(R.id.et_login_password).text.toString()
                                .trim { it <= ' ' }) -> {
                            Toast.makeText(
                                this@LoginActivity,
                                "Unesite vašu lozinku!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        else -> {
                            val email: String =
                                findViewById<EditText>(R.id.et_login_email).text.toString()
                                    .trim { it <= ' ' }
                            val password: String =
                                findViewById<EditText>(R.id.et_login_password).text.toString()
                                    .trim { it <= ' ' }

                            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                                .addOnCompleteListener({ task ->
                                    if (task.isSuccessful) {
                                        Toast.makeText(
                                            applicationContext, "Uspješno ste se prijavili.",
                                            Toast.LENGTH_LONG
                                        ).show()

                                        val intent =
                                            Intent(this@LoginActivity, MainActivity::class.java)
                                        intent.flags =
                                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                        intent.putExtra("user_id", FirebaseAuth.getInstance().currentUser!!.uid)
                                        intent.putExtra("email_id", email)
                                        startActivity(intent)
                                        finish()
                                    } else {
                                        Toast.makeText(
                                            this@LoginActivity, task.exception!!.message.toString(),
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                })
                        }
                    }
                }
            }
        }