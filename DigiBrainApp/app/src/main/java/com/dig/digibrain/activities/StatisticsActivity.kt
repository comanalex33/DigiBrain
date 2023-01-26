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
import java.text.DecimalFormat
import kotlin.math.roundToInt

class StatisticsActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private lateinit var binding: ActivityStatisticsBinding
    private lateinit var sessionManager: SessionManager
    private lateinit var viewModel: StatisticsViewModel

    var reports: List<QuizReportModel>? = null

    private var totalScore: Float? = null
    private var maximumScore: Float? = null
    private var subjectCategories = mutableListOf<String>()
    private var subjectCategoryIds = mutableListOf(0L)

    private var multipleChoiceTotal: Int? = null
    private var multipleChoiceScore: Float? = null
    private var wordsGapTotal: Int? = null
    private var wordsGapScore: Float? = null
    private var trueFalseTotal: Int? = null
    private var trueFalseScore: Float? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStatisticsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(applicationContext)
        setupViewModel()

        getUserReports()

        subjectCategories.add(resources.getString(R.string.all))
        val adapter = ArrayAdapter(this, R.layout.spinner_item, subjectCategories)

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.subjectSpinner.adapter = adapter
        binding.subjectSpinner.onItemSelectedListener = this

        val defaultPosition = adapter.getPosition(resources.getString(R.string.all))
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
                                reports?.apply {
                                    getSubjects(this)
                                }

                                totalScore = getTotalScore().toFloat()
                                maximumScore = getMaximumScore().toFloat()

                                multipleChoiceTotal = getTotalQuestions("MultipleChoice", 0L)
                                multipleChoiceScore = getTotalScore("MultipleChoice", 0L).toFloat()
                                wordsGapTotal = getTotalQuestions("WordsGap", 0L)
                                wordsGapScore = getTotalScore("WordsGap", 0L).toFloat()
                                trueFalseTotal = getTotalQuestions("TrueFalse", 0L)
                                trueFalseScore = getTotalScore("TrueFalse", 0L).toFloat()
                                setupCharts()
                            }
                            Status.ERROR -> {}
                            Status.LOADING -> {}
                        }
                    }
                }
        }
    }

    private fun getSubjects(reports: List<QuizReportModel>) {
        val subjectIds = mutableListOf<Long>()
        for(report in reports) {
            if(!subjectIds.contains(report.subjectId))
                subjectIds.add(report.subjectId)
        }
        viewModel.getSubjectsForIds(subjectIds)
            .observe(this) {
                it.let { resource ->
                    when(resource.status) {
                        Status.SUCCESS -> {
                            resource.data?.apply {
                                for(subject in resource.data) {
                                    subjectCategories.add(subject.name)
                                    subjectCategoryIds.add(subject.id)
                                }

                            }
                        }
                        Status.ERROR -> {}
                        Status.LOADING -> {}
                    }
                }
            }
    }

    private fun setupCharts() {
        setUpDesignPie(binding.pieChart)
        setUpDesignPie(binding.multipleChoicePieChart)
        setUpDesignPie(binding.wordsGapPieChart)
        setUpDesignPie(binding.trueFalsePieChart)

        updateChart(binding.pieChart, binding.pieChartNoContent, totalScore!!, maximumScore!! - totalScore!!)
        updateChart(binding.multipleChoicePieChart, binding.multipleChoiceNoData, multipleChoiceScore!!, multipleChoiceTotal!!.toFloat() - multipleChoiceScore!!)
        updateChart(binding.wordsGapPieChart, binding.wordsGapNoData, wordsGapScore!!, wordsGapTotal!!.toFloat() - wordsGapScore!!)
        updateChart(binding.trueFalsePieChart, binding.trueFalseNoData, trueFalseScore!!, trueFalseTotal!!.toFloat() - trueFalseScore!!)

        binding.multipleChoiceTotalValue.text = multipleChoiceTotal.toString()
        binding.multipleChoiceScoreValue.text = multipleChoiceScore.toString()
        binding.wordsGapTotalValue.text = wordsGapTotal.toString()
        binding.wordsGapScoreValue.text = wordsGapScore.toString()
        binding.trueFalseTotalValue.text = trueFalseTotal.toString()
        binding.trueFalseScoreValue.text = trueFalseScore.toString()
    }

    private fun setUpDesignPie(pie: PieChart) {
        pie.apply {
            setUsePercentValues(true)
            description.isEnabled = false
            setExtraOffsets(5f, 10f, 5f, 5f)
            dragDecelerationFrictionCoef = 0.95f
            isDrawHoleEnabled = true
            setHoleColor(resources.getColor(R.color.blue_light))
            setTransparentCircleColor(resources.getColor(R.color.blue_light))
            setTransparentCircleAlpha(110)
            holeRadius = 58f
            transparentCircleRadius = 61f
            setDrawCenterText(true)
            rotationAngle = 0f
            isRotationEnabled = true
            isHighlightPerTapEnabled = true
            animateY(1400, Easing.EaseInOutQuad)
            legend.isEnabled = false
            setEntryLabelColor(resources.getColor(R.color.blue_light))
            setEntryLabelTextSize(7f)
        }
    }

    private fun getTotalScore(): Double {
        var score = 0.0
        if(reports != null)
            for(report in reports!!) {
                score += report.score
            }
        return score
    }

    private fun getTotalScore(subjectId: Long): Double {
        var score = 0.0
        if(reports != null)
            for(report in reports!!) {
                if(report.subjectId == subjectId)
                    score += report.score
            }
        return score
    }

    private fun getMaximumScore(): Double {
        var score = 0.0
        if(reports != null)
            for(report in reports!!) {
                score += report.numberOfQuestions
            }
        return score
    }

    private fun getMaximumScore(subjectId: Long): Double {
        var score = 0.0
        if(reports != null)
            for(report in reports!!) {
                if(report.subjectId == subjectId)
                    score += report.numberOfQuestions
            }
        return score
    }

    private fun getTotalQuestions(type: String, subjectId: Long): Int {
        var count = 0
        if(reports != null)
            for(report in reports!!) {
                if(report.quizType == type) {
                    if(subjectId == 0L)
                        count += report.numberOfQuestions
                    else if(report.subjectId == subjectId)
                        count += report.numberOfQuestions
                }
            }
        return count
    }

    private fun getTotalScore(type: String, subjectId: Long): Double {
        var score = 0.0
        if(reports != null)
            for(report in reports!!) {
                if(report.quizType == type) {
                    if(subjectId == 0L)
                        score += report.score
                    else if(report.subjectId == subjectId)
                        score += report.score
                }
            }
        return score
    }

    private fun updateChart(pie: PieChart, noData: View, value1: Float, value2: Float) {
        if(value1 + value2 == 0f) {
            pie.visibility = View.GONE
            noData.visibility = View.VISIBLE
        } else {
            pie.visibility = View.VISIBLE
            noData.visibility = View.GONE
        }

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
        data.setValueTextColor(Color.WHITE)
        pie.data = data

        pie.highlightValues(null)
        pie.invalidate()
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        if(p2 == 0) {
            totalScore = getTotalScore().toFloat()
            maximumScore = getMaximumScore().toFloat()
        } else {
            totalScore = getTotalScore(subjectCategoryIds[p2]).toFloat()
            maximumScore = getMaximumScore(subjectCategoryIds[p2]).toFloat()
        }
        multipleChoiceTotal = getTotalQuestions("MultipleChoice", subjectCategoryIds[p2])
        multipleChoiceScore = getTotalScore("MultipleChoice", subjectCategoryIds[p2]).toFloat()
        wordsGapTotal = getTotalQuestions("WordsGap", subjectCategoryIds[p2])
        wordsGapScore = getTotalScore("WordsGap", subjectCategoryIds[p2]).toFloat()
        trueFalseTotal = getTotalQuestions("TrueFalse", subjectCategoryIds[p2])
        trueFalseScore = getTotalScore("TrueFalse", subjectCategoryIds[p2]).toFloat()

        binding.multipleChoiceTotalValue.text = multipleChoiceTotal.toString()
        binding.multipleChoiceScoreValue.text = multipleChoiceScore.toString()
        binding.wordsGapTotalValue.text = wordsGapTotal.toString()
        binding.wordsGapScoreValue.text = wordsGapScore.toString()
        binding.trueFalseTotalValue.text = trueFalseTotal.toString()
        binding.trueFalseScoreValue.text = trueFalseScore.toString()

        updateChart(binding.pieChart, binding.pieChartNoContent, totalScore!!, maximumScore!! - totalScore!!)
        updateChart(binding.multipleChoicePieChart, binding.multipleChoiceNoData, multipleChoiceScore!!, multipleChoiceTotal!!.toFloat() - multipleChoiceScore!!)
        updateChart(binding.wordsGapPieChart, binding.wordsGapNoData, wordsGapScore!!, wordsGapTotal!!.toFloat() - wordsGapScore!!)
        updateChart(binding.trueFalsePieChart, binding.trueFalseNoData, trueFalseScore!!, trueFalseTotal!!.toFloat() - trueFalseScore!!)
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {

    }
}