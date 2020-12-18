package com.example.nk2

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Login : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)

        supportActionBar?.title = applicationContext.getString(R.string.login)
        val loginEmail = findViewById<EditText>(R.id.login_email)
        val loginPass = findViewById<EditText>(R.id.login_password)
        val btnlogin = findViewById<Button>(R.id.login_btn_login)

        val fAuth = FirebaseAuth.getInstance()

        if (fAuth.currentUser != null) {
            val ID = fAuth.currentUser!!.uid.toString()
            checkUserAccesLevel("$ID")
        }

        btnlogin.setOnClickListener {
            val email = loginEmail.getText().toString().trim()
            val password = loginPass.getText().toString().trim()

            if (TextUtils.isEmpty(email)) {
                loginEmail.setError("Email harus diisi")
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(password)) {
                loginPass.setError("Password harus diisi")
                return@setOnClickListener
            }
            if (password.length < 6) {
                loginPass.setError("Password harus lebih dari 6 karakter")
                return@setOnClickListener
            }

            //Database
            fAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(this, "User Berhasil Masuk", Toast.LENGTH_SHORT).show()
                    checkUserAccesLevel(it.getResult()!!.user!!.uid)
                } else {
                    Toast.makeText(this, "Email/Password Salah", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun checkUserAccesLevel(uid: String) {
        val fStore = FirebaseFirestore.getInstance()
        val df = fStore.collection("User").document(uid)

        df.get().addOnSuccessListener {
            if (it.getString("isAdmin") == "1") {
                startActivity(Intent(applicationContext, AdminBeranda::class.java))
            } else if (it.getString("isUser") == "1") {
                startActivity(Intent(applicationContext, MahasiswaBeranda::class.java))
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Akun tidak ada", Toast.LENGTH_SHORT).show()
        }
    }


    fun register(view: View) {
        val intent = Intent(applicationContext, Register::class.java)
        startActivity(intent)
        finish()
    }

}