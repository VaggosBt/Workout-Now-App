package com.udemycourses.workoutapp

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.Toast
import com.udemycourses.workoutapp.databinding.ActivityBmiBinding
import com.udemycourses.workoutapp.databinding.DialogCustomBackConfirmationBinding
import java.math.BigDecimal
import java.math.RoundingMode

private var binding : ActivityBmiBinding? = null

class BMIActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBmiBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        //setup the toolbar
        setSupportActionBar(binding?.toolbarBMIActivity)

        if(supportActionBar != null){
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.title = "Calculate BMI"
        }

        binding?.toolbarBMIActivity?.setNavigationOnClickListener {
            //onBackPressedDispatcher.onBackPressed()
            onBackPressedDispatcher.onBackPressed()
        }

        binding?.btnCalculateUnits?.setOnClickListener {
            if(validateMetricUnits()){
                val weightValue : Float = binding?.etMetricUnitWeight?.text.toString().toFloat()
                val heightValue : Float = binding?.etMetricUnitHeight?.text.toString().toFloat() / 100

                val bmi = weightValue / (heightValue*heightValue)

                displayBMIResult(bmi)

            }else{
                Toast.makeText(this@BMIActivity,
                    "Please enter valid values", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun displayBMIResult(bmi: Float){

        var bmiLabel : String
        val bmiDescription : String

        if(bmi.compareTo(15f) <= 0 ){
            bmiLabel = "Very severely underweight"
            bmiDescription = "Oops! You really need to take better care of yourself! Eat more frequently maybe!"
        }else if(bmi.compareTo(15f) > 0 && bmi.compareTo(16f) <= 0 ){
            bmiLabel = "Severely underweight"
            bmiDescription = "Oops! You really need to take better care of yourself! Eat more frequently maybe!!"
        }else if(bmi.compareTo(16f) > 0 && bmi.compareTo(18.5f) <=0 ){
            bmiLabel = "Underweight"
            bmiDescription = "Oops! You really need to take better care of yourself! Eat more frequently maybe!"
        }else if(bmi.compareTo(18.5f) > 0 && bmi.compareTo(25f) <= 0 ){
            bmiLabel = "Normal"
            bmiDescription = "Congratulations! You are in a good shape!"
        }else if(bmi.compareTo(25f) > 0 && bmi.compareTo(30f) <= 0 ) {
            bmiLabel = "Overweight"
            bmiDescription = "Oops! You really need to take better care of yourself! Start Workout more frequently maybe!"
        }else if(bmi.compareTo(30f) > 0 && bmi.compareTo(35f) <= 0 ) {
            bmiLabel = "Obese Class | (Moderately obese)"
            bmiDescription = "You are in a very dangerous condition! Act now!"
        }else if(bmi.compareTo(35f) > 0 && bmi.compareTo(40f) <= 0 ){
            bmiLabel = "Obese Class || (Severely obese)"
            bmiDescription = "You are in a very dangerous condition! Act now!"
        }else{
            bmiLabel = "Obese Class ||| (Very Severely obese)"
            bmiDescription = "You are in a very dangerous condition! Act now!"
        }

        val bmiValue = BigDecimal(bmi.toDouble()).setScale(2,RoundingMode.HALF_EVEN).toString()
        binding?.llDisplayBMIResult?.visibility = View.VISIBLE
        binding?.tvBMIValue?.text = bmiValue
        binding?.tvBMIType?.text = bmiLabel
        binding?.tvBMIDescription?.text = bmiDescription

    }

    private fun validateMetricUnits() : Boolean{
        var isValid = true;

        if(binding?.etMetricUnitWeight?.text.toString().isEmpty()){
            isValid = false
        }else if(binding?.etMetricUnitHeight?.text.toString().isEmpty()){
            isValid = false
        }
        return isValid
    }

    override fun onDestroy() {
        super.onDestroy()

        if(binding != null ) {
            binding = null
        }
    }

}