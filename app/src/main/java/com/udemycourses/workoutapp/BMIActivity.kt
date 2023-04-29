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



class BMIActivity : AppCompatActivity() {

    companion object {
        private const val METRIC_UNITS_VIEW = "METRIC_UNIT_VIEW" // Metric Unit View
        private const val US_UNITS_VIEW = "US_UNIT_VIEW" // US Unit View
    }

    private var binding : ActivityBmiBinding? = null
    private var currentVisibleView: String = METRIC_UNITS_VIEW // A variable to hold a value to make a selected view visible

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

        makeVisibleMetricUnitsView()

        binding?.rgUnits?.setOnCheckedChangeListener { _, checkedId: Int ->

            if(checkedId == R.id.rbMetricUnits){
                makeVisibleMetricUnitsView()
            }else{
                makeVisibleUSView()
            }
        }

        binding?.btnCalculateUnits?.setOnClickListener {
            calculateUnits()
        }

    }

    private fun makeVisibleMetricUnitsView(){
        currentVisibleView = METRIC_UNITS_VIEW
        binding?.tilMetricUnitHeight?.visibility = View.VISIBLE
        binding?.etMetricUnitHeight?.hint = "WEIGHT (in kg)"
        binding?.tilMetricUnitHeightFeet?.visibility = View.GONE
        binding?.tilMetricUnitHeightInch?.visibility = View.GONE

        binding?.etMetricUnitHeight?.text!!.clear()
        binding?.etMetricUnitWeight?.text!!.clear()

        binding?.llDisplayBMIResult?.visibility = View.INVISIBLE

    }

    private fun makeVisibleUSView(){
        currentVisibleView = US_UNITS_VIEW
        binding?.tilMetricUnitHeight?.visibility = View.INVISIBLE
        binding?.etMetricUnitHeight?.hint = "WEIGHT (in pounds)"
        binding?.tilMetricUnitHeightFeet?.visibility = View.VISIBLE
        binding?.tilMetricUnitHeightInch?.visibility = View.VISIBLE

        binding?.etMetricUnitHeightFeet?.text!!.clear()
        binding?.etMetricUnitHeightInch?.text!!.clear()
        binding?.etMetricUnitWeight?.text!!.clear()

        binding?.llDisplayBMIResult?.visibility = View.INVISIBLE

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

    private fun calculateUnits(){
        if(currentVisibleView == METRIC_UNITS_VIEW){
            if(validateMetricUnits()){
                val weightValue : Float = binding?.etMetricUnitWeight?.text.toString().toFloat()
                val heightValue : Float = binding?.etMetricUnitHeight?.text.toString().toFloat() / 100

                val bmi = weightValue / (heightValue*heightValue)

                displayBMIResult(bmi)

            }else{
                Toast.makeText(this@BMIActivity,
                    "Please enter valid values", Toast.LENGTH_SHORT).show()
            }
        }else{
            if(validateUSUnits()){
                val usUnitHeightValueFeet : String = binding?.etMetricUnitHeightFeet?.text.toString()
                val usUnitHeightValueInch: String = binding?.etMetricUnitHeightInch?.text.toString()
                val usUnitWeightValue: Float = binding?.etMetricUnitWeight?.text.toString().toFloat()

                // Here the Height Feet and Inch values are merged and multiplied by 12 for converting it to inches.
                val heightValue = usUnitHeightValueInch.toFloat() + usUnitHeightValueFeet.toFloat() * 12

                val bmi = 703 * (usUnitWeightValue / (heightValue * heightValue))

                displayBMIResult(bmi)

            }else{
                Toast.makeText(this@BMIActivity,
                    "Please enter valid values", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun validateUSUnits() : Boolean{
        var isValid = true;

        when{
            binding?.etMetricUnitWeight?.text.toString().isEmpty() -> {
                isValid = false
            }
            binding?.etMetricUnitHeightFeet?.text.toString().isEmpty() -> {
            isValid = false
            }
            binding?.etMetricUnitHeightInch?.text.toString().isEmpty() -> {
                isValid = false
            }

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