package com.udemycourses.workoutapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Toast
import com.udemycourses.workoutapp.databinding.ActivityExerciseBinding

class ExerciseActivity : AppCompatActivity() {

    private var binding: ActivityExerciseBinding? = null

    private var timer: CountDownTimer? = null
    private var progress = 0

    private var totalRestTime : Long = 10000
    private var totalExerciseTime : Long = 30000

    private var exerciseList : ArrayList<ExerciseModel>? = null
    private var currentExercisePosition = -1 //When start, we'll start increasing this value by 1 after each exercise, so first value will be 0, which is arrayList's first element


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExerciseBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setSupportActionBar(binding?.toolbarExercise)

        if(supportActionBar != null){
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }

        exerciseList = Constants.defaultExerciseList()

        binding?.toolbarExercise?.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        setupRestView()

    }

    private fun setupRestView(){
        if(timer != null) {
            timer?.cancel()
            progress = 0
        }

        binding?.flRestProgressBar?.visibility = View.VISIBLE
        binding?.tvTitle?.visibility = View.VISIBLE
        binding?.tvUpcomingExerciseLabel?.visibility = View.VISIBLE
        binding?.tvUpcomingExerciseName?.visibility = View.VISIBLE

        binding?.flExerciseProgressBar?.visibility = View.INVISIBLE
        binding?.tvExerciseName?.visibility = View.INVISIBLE
        binding?.ivImage?.visibility = View.INVISIBLE


        binding?.tvUpcomingExerciseName?.text = exerciseList!![currentExercisePosition+1].getName()

        setRestProgressBar()
    }

    private fun setRestProgressBar(){
        binding?.restProgressBar?.progress = progress
        timer = object : CountDownTimer(totalRestTime, 1000){
            override fun onTick(millisUntilFinished: Long) {

                progress++
                val fullProgress = (totalRestTime / 1000).toInt()
                binding?.restProgressBar?.progress = fullProgress - progress
                binding?.tvTimer?.text = (fullProgress-progress).toString()

            }
            override fun onFinish() {
                Toast.makeText(this@ExerciseActivity,"Here now we will start the exercise",Toast.LENGTH_SHORT).show()
                currentExercisePosition++
                setupExerciseView()
            }
        }.start()
    }

    private fun setupExerciseView(){
        if(timer != null) {
            timer?.cancel()
            progress = 0
        }
        binding?.flRestProgressBar?.visibility = View.INVISIBLE
        binding?.tvTitle?.visibility = View.INVISIBLE
        binding?.tvUpcomingExerciseLabel?.visibility = View.INVISIBLE
        binding?.tvUpcomingExerciseName?.visibility = View.INVISIBLE

        binding?.flExerciseProgressBar?.visibility = View.VISIBLE
        binding?.tvExerciseName?.visibility = View.VISIBLE
        binding?.ivImage?.visibility = View.VISIBLE

        binding?.ivImage?.setImageResource(exerciseList!![currentExercisePosition].getImage())
        binding?.tvExerciseName?.text = exerciseList!![currentExercisePosition].getName()

        setupExerciseProgressBar()
    }

    private fun setupExerciseProgressBar(){
        binding?.exerciseProgressBar?.progress = progress
        timer = object : CountDownTimer(totalExerciseTime, 1000){
            override fun onTick(millisUntilFinished: Long) {
               progress++
                val fullProgress = (totalExerciseTime / 1000).toInt()
                binding?.exerciseProgressBar?.progress = fullProgress - progress
                binding?.tvExerciseTimer?.text = (fullProgress-progress).toString()
            }

            override fun onFinish() {
                if(currentExercisePosition<exerciseList?.size!! - 1 ){
                    Toast.makeText(this@ExerciseActivity,"Here now we will take some rest before next exercise",Toast.LENGTH_SHORT).show()
                    setupRestView()
                }else{
                    Toast.makeText(this@ExerciseActivity,"Congratulations, you 've completed the exercise session.",Toast.LENGTH_SHORT).show()
                }
            }

        }.start()

    }



    override fun onDestroy() {
        super.onDestroy()

        if(timer != null){
            timer?.cancel()
            progress = 0
        }
        binding = null
    }

}