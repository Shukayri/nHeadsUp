package com.example.newheadsup

import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Button
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import android.widget.TextView

import com.example.newheadsup.model.Celebrity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.random.Random


class MainActivity : AppCompatActivity() {

    lateinit var clStart: ConstraintLayout
    lateinit var clRotate: ConstraintLayout


    private lateinit var startButton: Button
    private lateinit var tvName: TextView
    private lateinit var tvTabooOne: TextView
    private lateinit var tvTabooTwo: TextView
    private lateinit var tvTabooThree: TextView

    private var apiInterface: APIInterface? = null
    private var timeRemaining = 60000L
    private var orientation: Int? = null

    private var celebList: ArrayList<Celebrity> = arrayListOf()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        tvName = findViewById(R.id.tvName)
        tvTabooOne = findViewById(R.id.tvTabooOne)
        tvTabooTwo = findViewById(R.id.tvTabooTwo)
        tvTabooThree = findViewById(R.id.tvTabooThree)

        startButton = findViewById(R.id.startButton)
        clStart = findViewById(R.id.clStart)
        clRotate = findViewById(R.id.clRotate)

        title = "Time:--"
        orientation = resources.configuration.orientation

        apiInterface = APIClient().getClient()?.create(APIInterface::class.java)


        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            getData()
        } else {

            if(timeRemaining != 60000L){
                clStart.isVisible = false
                clRotate.isVisible = true
            }

            startButton.setOnClickListener {
                clStart.isVisible = false
                clRotate.isVisible = true
                startTimer()
            }
        }
    }

    private fun getData(){
        apiInterface!!.getCelebs()?.enqueue(object : Callback<ArrayList<Celebrity>> {
            override fun onResponse(
                call: Call<ArrayList<Celebrity>>,
                response: Response<ArrayList<Celebrity>>
            ) {
                updateQuestion(response.body()!!)
            }

            override fun onFailure(call: Call<ArrayList<Celebrity>>, t: Throwable) {
                println("False")
                call.cancel()
            }
        })
    }


    private fun updateQuestion(list: ArrayList<Celebrity>) {
        val celeb = list[Random.nextInt(0,list.size)]
        if(celeb !in celebList)
            celebList.add(celeb)
        tvName.text = celeb.name
        tvTabooOne.text = celeb.taboo1
        tvTabooTwo.text = celeb.taboo2
        tvTabooThree.text = celeb.taboo3
    }

    private fun startTimer() {
        object : CountDownTimer(timeRemaining, 1000) {

            override fun onTick(millisUntilFinished: Long) {
                timeRemaining = millisUntilFinished
                title = "Time: ${timeRemaining / 1000}"
            }

            override fun onFinish() {
                title = "Done"
                if (orientation == Configuration.ORIENTATION_PORTRAIT)
                {
                    clRotate.isVisible = false
                    clStart.isVisible = true
                }
            }
        }.start()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putLong("time", timeRemaining)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        timeRemaining = savedInstanceState.getLong("time", 60000L)
        title = "Time: ${timeRemaining / 1000}"
        startTimer()
    }

}