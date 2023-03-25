package com.example.spendy

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.spendy.databinding.ActivityMainBinding
import com.example.spendy.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)


        firebaseAuth = FirebaseAuth.getInstance()

        binding.alreadyRegistered.setOnClickListener{
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
        }

        binding.button.setOnClickListener{
            val email = binding.email.text.toString()
            val password = binding.password.text.toString()
            val repassword = binding.repassword.text.toString()

            if(email.isNotEmpty() && password.isNotEmpty() && repassword.isNotEmpty()){
                if(password == repassword){
                    firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener{
                        if(it.isSuccessful){
                            Toast.makeText(this,"User Created",Toast.LENGTH_SHORT).show()
                            val intent = Intent(this,MainActivity::class.java)
                            startActivity(intent)
                        }else{
                            Toast.makeText(this,it.exception.toString(),Toast.LENGTH_SHORT).show()
                        }
                    }
                }else{
                    Toast.makeText(this,"Password is not matching",Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(this,"Please provide all the details",Toast.LENGTH_SHORT).show()
            }
        }
    }
}