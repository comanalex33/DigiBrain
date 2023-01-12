package com.dig.digibrain.activities

import android.graphics.Color
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.dig.digibrain.R
import com.dig.digibrain.databinding.ActivityStatisticsBinding
import com.dig.digibrain.models.quiz.QuizReportModel
import com.dig.digibrain.services.SessionManager
import com.dig.digibrain.services.server.ApiClient
import com.dig.digibrain.utils.Status
import com.dig.digibrain.viewModels.StatisticsViewModel
import com.dig.digibrain.viewModels.ViewModelFactory
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter

class StatisticsActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private lateinit var binding: ActivityStatisticsBinding
    private lateinit var sessionManager: SessionManager
    private lateinit var viewModel: StatisticsViewModel

    var reports: List<QuizReportModel>? = null

    private var totalScore: Float? = null
    private var maximumScore: Float? = null

    private var multipleChoiceTotal: Int? = null
    private var multipleChoiceScore: Float? = null
    private var wordsGapTotal: Int? = null
    private var wordsGapScore: Float? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStatisticsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(applicationContext)
        setupViewModel()

        getUserReports()

        val adapter = ArrayAdapter(this, R.layout.spinner_item, listOf("Default"))
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.subjectSpinner.adapter = adapter
        binding.subjectSpinner.onItemSelectedListener = this

        val defaultPosition = adapter.getPosition("Default")
        binding.subjectSpinner.setSelection(defaultPosition)

        binding.backArrow.setOnClickListener {
            finish()
        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(ApiClient.getService())
        )[StatisticsViewModel::class.java]
    }

    private fun getUserReports() {
        val username = sessionManager.getUserName()

        if(username != null) {
            viewModel.getUserReports(username)
                .observe(this) {
                    it.let { resource ->
                        when (resource.status) {
                            Status.SUCCESS -> {
                                reports = resource.data
                                totalScore = getTotalScore().toFloat()
                                maximumScore = getMaximumScore().toFloat()

                                multipleChoiceTotal = getTotalQuestions("MultipleChoice")
                                multipleChoiceScore = getTotalScore("MultipleChoice").toFloat()
                                wordsGapTotal = getTotalQuestions("WordsGap")
                                wordsGapScore = getTotalScore("WordsGap").toFloat()
                                setupCharts()

                                if(resource.data!!.isNotEmpty())
                                    setUpVisibility()
                            }
                            Status.ERROR -> {}
                            Status.LOADING -> {}
                        }
                    }
                }
        }
    }

    private fun setUpVisibility() {
        binding.pieChartNoContent.visibility = View.GONE
        binding.multipleChoiceNoData.visibility = View.GONE
        binding.wordsGapNoData.visibility = View.GONE

        binding.pieChart.visibility = View.VISIBLE
        binding.multipleChoicePieChart.visibility = View.VISIBLE
        binding.wordsGapPieChart.visibility = View.VISIBLE
    }

    private fun setupCharts() {
        setUpDesignPie(binding.pieChart)
        setUpDesignPie(binding.multipleChoicePieChart)
        setUpDesignPie(binding.wordsGapPieChart)

        updateChart(binding.pieChart, totalScore!!, maximumScore!! - totalScore!!)
        updateChart(binding.multipleChoicePieChart, multipleChoiceScore!!, multipleChoiceTotal!!.toFloat() - multipleChoiceScore!!)
        updateChart(binding.wordsGapPieChart, wordsGapScore!!, wordsGapTotal!!.toFloat() - wordsGapScore!!)

        binding.multipleChoiceTotalValue.text = multipleChoiceTotal.toString()
        binding.multipleChoiceScoreValue.text = multipleChoiceScore.toString()
        binding.wordsGapTotalValue.text = wordsGapTotal.toString()
        binding.wordsGapScoreValue.text = wordsGapScore.toString()
    }

    private fun setUpDesignPie(pie: PieChart) {
        pie.apply {
            setUsePercentValues(true)
            description.isEnabled = false
            setExtraOffsets(5f, 10f, 5f, 5f)
            dragDecelerationFrictionCoef = 0.95f
            isDrawHoleEnabled = true
            setHoleColor(Color.WHITE)
            setTransparentCircleColor(Color.WHITE)
            setTransparentCircleAlpha(110)
            holeRadius = 58f
            transparentCircleRadius = 61f
            setDrawCenterText(true)
            rotationAngle = 0f
            isRotationEnabled = true
            isHighlightPerTapEnabled = true
            animateY(1400, Easing.EaseInOutQuad)
            legend.isEnabled = false
            setEntryLabelColor(Color.WHITE)
            setEntryLabelTextSize(7f)
        }
    }

    private fun getTotalScore(): Double {
        var score = 0.0
        for(report in reports!!) {
            score += report.score
        }
        return score
    }

    private fun getMaximumScore(): Double {
        var score = 0.0
        for(report in reports!!) {
            score += report.numberOfQuestions
        }
        return score
    }

    private fun getTotalQuestions(type: String): Int {
        var count = 0
        for(report in reports!!) {
            if(report.quizType == type) {
                count += report.numberOfQuestions
            }
        }
        return count
    }

    private fun getTotalScore(type: String): Double {
        var score = 0.0
        for(report in reports!!) {
            if(report.quizType == type) {
                score += report.score
            }
        }
        return score
    }

    private fun updateChart(pie: PieChart, value1: Float, value2: Float) {
        val entries = ArrayList<PieEntry>()
        entries.add(PieEntry(value1))
        entries.add(PieEntry(value2))

        val dataSet = PieDataSet(entries, "Mobile OS")

        val colors = mutableListOf<Int>()
        colors.add(resources.getColor(R.color.green))
        colors.add(resources.getColor(R.color.red))
        dataSet.colors = colors

        val data = PieData(dataSet)
        data.setValueFormatter(PercentFormatter())
        data.setValueTextSize(15f)
        data.setValueTypeface(Typeface.DEFAULT_BOLD)
        data.setValueTextColor(Color.BLACK)
        pie.data = data

        pie.highlightValues(null)
        pie.invalidate()
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

    }

    override fun onNothingSelected(p0: AdapterView<*>?) {

    }
}