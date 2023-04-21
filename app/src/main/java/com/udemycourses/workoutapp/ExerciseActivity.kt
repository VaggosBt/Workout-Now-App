package com.udemycourses.workoutapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.udemycourses.workoutapp.databinding.ActivityExerciseBinding

class ExerciseActivity : AppCompatActivity() {

    private var binding: ActivityExerciseBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExerciseBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setSupportActionBar(binding?.toolbarExercise)

        if(supportActionBar != null){
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }

        binding?.toolbarExercise?.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }


    }
}