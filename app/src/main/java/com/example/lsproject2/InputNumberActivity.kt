package com.example.lsproject2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.example.lsproject2.databinding.ActivityInputNumberBinding
import com.example.lsproject2.model.FindIdModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class InputNumberActivity : AppCompatActivity() {
    lateinit var firestore : FirebaseFirestore
    lateinit var auth : FirebaseAuth
    lateinit var binding : ActivityInputNumberBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_input_number)
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        binding.apply.setOnClickListener {
            savePhoneNumber()
        }
    }
    fun savePhoneNumber(){
        var findIdModel = FindIdModel()
        findIdModel.id = auth.currentUser?.email
        findIdModel.phoneNumber = binding.edittextPhonenumber.text.toString()

        firestore.collection("findids").document().set(findIdModel).addOnCompleteListener {
                task ->
            if(task.isSuccessful){
                finish()
                auth.currentUser?.sendEmailVerification()
                startActivity(Intent(this,LoginActivity::class.java))
            }
        }
    }
}