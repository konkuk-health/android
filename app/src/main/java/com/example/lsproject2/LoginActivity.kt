package com.example.lsproject2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.lsproject2.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser


class LoginActivity : AppCompatActivity() {
    lateinit var binding: ActivityLoginBinding
    var auth : FirebaseAuth? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        test()
        binding.emailLoginButton.setOnClickListener {
            signinAndSignup()
        }
    }

    fun signinAndSignup() {
        auth?.createUserWithEmailAndPassword(binding.edittextId.text.toString(), binding.edittextPassword.text.toString())?.addOnCompleteListener {
                task ->
            if (task.isSuccessful) {
                moveMainPage(task.result.user)
                //아이디 만들기
            } else if(!task.exception?.message.isNullOrEmpty()) {
                //에러메시지만 출력
                Toast.makeText(this, task.exception?.message, Toast.LENGTH_LONG).show()
            } else {
                //로그인 화면으로
                signinEmail()
            }
        }
    }

    fun signinEmail() {
        auth?.signInWithEmailAndPassword(binding.edittextId.text.toString(), binding.edittextPassword.text.toString())?.addOnCompleteListener {
                task ->
            if (task.isSuccessful) {
                //로그인 성공
                moveMainPage(task.result.user)
            } else {
                // 에러 메시지 출력
                Toast.makeText(this, task.exception?.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    fun moveMainPage (user:FirebaseUser?) {
        if (user != null) {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }
    fun test() {
        binding.googleLoginButton.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }
}