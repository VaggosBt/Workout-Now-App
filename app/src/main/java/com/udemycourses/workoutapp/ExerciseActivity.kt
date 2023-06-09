package com.udemycourses.workoutapp

import android.app.Dialog
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.udemycourses.workoutapp.databinding.ActivityExerciseBinding
import com.udemycourses.workoutapp.databinding.DialogCustomBackConfirmationBinding
import java.util.*
import kotlin.collections.ArrayList

class ExerciseActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private var binding: ActivityExerciseBinding? = null

    private var timer: CountDownTimer? = null
    private var progress = 0

    private var totalRestTime : Long = 10 // (in seconds)
    private var totalExerciseTime : Long = 30 // - ( in seconds)

    private var exerciseList : ArrayList<ExerciseModel>? = null
    private var currentExercisePosition = -1 //When start, we'll start increasing this value by 1 after each exercise, so first value will be 0, which is arrayList's first element

    private var tts : TextToSpeech? = null
    private var player : MediaPlayer? = null

    private var exerciseAdapter : ExerciseStatusAdapter? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExerciseBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setSupportActionBar(binding?.toolbarExercise)

        if(supportActionBar != null){
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.title = "Workout Now"
        }

        binding?.toolbarExercise?.setNavigationOnClickListener {
            //onBackPressedDispatcher.onBackPressed()
            customDialogForBackButton()
        }

        exerciseList = Constants.defaultExerciseList()



        tts = TextToSpeech(this,this )

        // Delay the first call to speakOutTwoPhrases by 500 milliseconds in order the tts to fully initialize
        Handler().postDelayed({
            speakOutTwoPhrases(
                binding?.tvUpcomingExerciseLabel?.text.toString().substring(0, binding?.tvUpcomingExerciseLabel?.text.toString().length - 1),
                binding?.tvUpcomingExerciseName?.text.toString()
            )
        }, 500)

        setupRestView()
        setupExerciseStatusRecyclerView()

        //Manually highlighting just the first item of the recycler view as the upcoming and selected exercise, as its not been highlighted somewhere else before that point
        exerciseList!![currentExercisePosition+1].setIsSelected(true)
        exerciseAdapter!!.notifyDataSetChanged()

    }

    private fun customDialogForBackButton() {




            if(timer != null){
                timer!!.cancel()
            }

        Handler().postDelayed({
        val customDialog  = Dialog(this)
        val dialogBinding = DialogCustomBackConfirmationBinding.inflate(layoutInflater)
        customDialog.setContentView(dialogBinding.root)

        customDialog.setCanceledOnTouchOutside(false)



        dialogBinding.yesBtn.setOnClickListener {
            this@ExerciseActivity.finish()
            customDialog.dismiss()
        }
        dialogBinding.noBtn.setOnClickListener {
            customDialog.dismiss()

            if(binding?.flRestProgressBar?.visibility == View.VISIBLE){
                resumeTimer(binding?.restProgressBar, binding?.tvTimer, totalRestTime,false)
            }else{
                resumeTimer(binding?.exerciseProgressBar, binding?.tvExerciseTimer, totalExerciseTime,true)
            }

        }
        customDialog.show()
        }, 200)
    }

    private fun resumeTimer(progressBar: ProgressBar?, timerTextView : TextView?, totalTime: Long, isExerciseTimer: Boolean) {
        //TODO("Fully implement")

       progressBar?.progress = progress



        timer = object : CountDownTimer((totalTime * 1000), 1000) {
            override fun onTick(millisUntilFinished: Long) {

                progress++
                Log.e("PROGRESS_RESUME", progress.toString())
                val fullProgress = ((totalTime*1000) / 1000).toInt()
                progressBar?.progress = fullProgress - progress
                timerTextView?.text = (fullProgress - progress).toString()
                if(fullProgress-progress <= 0){
                    onFinish()
                }

            }

            override fun onFinish() {
                //Making the necessary controls and actions if the finished timer is a finished exercise
                if(isExerciseTimer){

                    //setupExerciseView()   //TODO (SEE IF THIS LINE OF CODE IS REDUNDANT)

                    if (currentExercisePosition < exerciseList?.size!! - 1) {

                        //Notify the Recycler View adapter that this exercise has finished so that is not selected and to change the corresponding item's background to the one for completed exercises
                        exerciseList!![currentExercisePosition].setIsSelected(false)
                        exerciseList!![currentExercisePosition].setIsCompleted(true)
                        exerciseAdapter!!.notifyDataSetChanged()

                        speakOutTwoPhrases(
                            "Please take a rest for the next ten seconds",
                            "Very good! Keep it up!"
                        )

                        //Notify the Recycler View adapter that the next exercise item is being selected so to change its background accordingly to highlight it
                        exerciseList!![currentExercisePosition+1].setIsSelected(true)
                        exerciseAdapter!!.notifyDataSetChanged()

                        setupRestView()

                    } else {
                        finish()
                        val intent = Intent(this@ExerciseActivity, FinishScreenActivity::class.java)
                        startActivity(intent)
                    }

                }else{ //Making the necessary actions if the finished timer is a finished rest time period

                    currentExercisePosition++
                    setupExerciseView()

                }
            }
        }.start()



    }

    override fun onBackPressed() {
        customDialogForBackButton()
    }

    private fun setupExerciseStatusRecyclerView(){
        binding?.rvExerciseStatus?.layoutManager = LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL, false)
        exerciseAdapter = ExerciseStatusAdapter(exerciseList!!)
        binding?.rvExerciseStatus?.adapter = exerciseAdapter
    }

    private fun setupRestView() {
        if (timer != null) {
            timer?.cancel()
            progress = 0
        }

        try {

            if (player != null) {
                player!!.stop()
            }
            val soundURI =
                Uri.parse("android.resource://com.udemycourses.workoutapp/" + R.raw.rest_music)
            player = MediaPlayer.create(applicationContext, soundURI)
            player?.isLooping = false
            player?.start()

        } catch (e: Exception) {
            e.printStackTrace()
        }

        player?.setOnCompletionListener {
            player?.start()
        }

            binding?.flRestProgressBar?.visibility = View.VISIBLE
            binding?.tvTitle?.visibility = View.VISIBLE
            binding?.tvUpcomingExerciseLabel?.visibility = View.VISIBLE
            binding?.tvUpcomingExerciseName?.visibility = View.VISIBLE

            binding?.flExerciseProgressBar?.visibility = View.INVISIBLE
            binding?.tvExerciseName?.visibility = View.INVISIBLE
            binding?.ivImage?.visibility = View.INVISIBLE


            binding?.tvUpcomingExerciseName?.text =
                exerciseList!![currentExercisePosition + 1].getName()

            speakOutTwoPhrases(
                binding?.tvUpcomingExerciseLabel?.text.toString()
                    .substring(0, binding?.tvUpcomingExerciseLabel?.text.toString().length - 1),
                binding?.tvUpcomingExerciseName?.text.toString()
            )

            setRestProgressBar()
        }

        private fun setRestProgressBar() {
            binding?.restProgressBar?.progress = progress



            timer = object : CountDownTimer(totalRestTime * 1000, 1000) {
                override fun onTick(millisUntilFinished: Long) {

                    progress++
                    Log.e("PROGRESS_REST", progress.toString())
                    val fullProgress = ((totalRestTime*1000) / 1000).toInt()
                    binding?.restProgressBar?.progress = fullProgress - progress
                    binding?.tvTimer?.text = (fullProgress - progress).toString()

                }

                override fun onFinish() {
                    currentExercisePosition++


                    setupExerciseView()
                }
            }.start()
        }

        private fun setupExerciseView() {
            if (timer != null) {
                timer?.cancel()
                progress = 0
            }

            try {

                if (player != null) {
                    player!!.stop()
                }
                val soundURI =
                    Uri.parse("android.resource://com.udemycourses.workoutapp/" + R.raw.exercise_music)
                player = MediaPlayer.create(applicationContext, soundURI)
                player?.isLooping = false
                player?.start()

            } catch (e: Exception) {
                e.printStackTrace()
            }

            player?.setOnCompletionListener {
                player?.start()
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

        private fun setupExerciseProgressBar() {
            binding?.exerciseProgressBar?.progress = progress
            timer = object : CountDownTimer(totalExerciseTime * 1000, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    progress++
                    Log.e("PROGRESS_EXERCISE", progress.toString())
                    val fullProgress = ((totalExerciseTime * 1000) / 1000).toInt()
                    binding?.exerciseProgressBar?.progress = fullProgress - progress
                    binding?.tvExerciseTimer?.text = (fullProgress - progress).toString()

                    if (binding?.tvExerciseTimer?.text == "10") {
                        speakOut("Ten seconds remaining!")
                    }
                }

                override fun onFinish() {


                    //setupExerciseView()   //TODO (SEE IF THIS LINE OF CODE IS REDUNDANT)

                    if (currentExercisePosition < exerciseList?.size!! - 1) {

                        //Notify the Recycler View adapter that this exercise has finished so that is not selected and to change the corresponding item's background to the one for completed exercises
                        exerciseList!![currentExercisePosition].setIsSelected(false)
                        exerciseList!![currentExercisePosition].setIsCompleted(true)
                        exerciseAdapter!!.notifyDataSetChanged()

                        speakOutTwoPhrases(
                            "Please take a rest for the next ten seconds",
                            "Very good! Keep it up!"
                        )

                        //Notify the Recycler View adapter that the next exercise item is being selected so to change its background accordingly to highlight it
                        exerciseList!![currentExercisePosition+1].setIsSelected(true)
                        exerciseAdapter!!.notifyDataSetChanged()

                        setupRestView()
                    } else {
                        finish()
                        val intent = Intent(this@ExerciseActivity, FinishScreenActivity::class.java)
                        startActivity(intent)
                    }
                }

            }.start()

        }

        //overridden method for textToSpeech feature
        override fun onInit(status: Int) {
            if (status == TextToSpeech.SUCCESS) {
                var result = tts?.setLanguage(Locale.US)

                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e("TTS", "The language specified in not supported")
                }
            } else {
                Log.e("TTS", "Initialization failed!")
            }
        }

        private fun speakOut(text: String) {
            tts!!.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")
        }

        private fun speakOutTwoPhrases(phrase1: String, phrase2: String) {
            val utteranceText = "$phrase1, $phrase2"
            tts!!.speak(phrase1, TextToSpeech.QUEUE_FLUSH, null, utteranceText)
            tts!!.playSilentUtterance(50, TextToSpeech.QUEUE_ADD, null)
            tts!!.speak(phrase2, TextToSpeech.QUEUE_ADD, null, utteranceText)
        }



        override fun onDestroy() {
            super.onDestroy()

            if (timer != null) {
                timer?.cancel()
                progress = 0
            }
            binding = null

            if (tts != null) {
                tts!!.stop()
                tts!!.shutdown()
            }

            if (player != null) {
                player!!.stop()
                player!!.release()
            }
        }



}