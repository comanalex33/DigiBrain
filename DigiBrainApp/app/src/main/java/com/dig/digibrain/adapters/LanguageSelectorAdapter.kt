package com.dig.digibrain.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.dig.digibrain.R
import com.dig.digibrain.databinding.CardSelectLanguageBinding
import com.dig.digibrain.interfaces.ILanguageChanged
import com.dig.digibrain.models.LanguageModel

class LanguageSelectorAdapter(
    var context: Context,
    var currentLanguageCode: String?,
    private var arrayList: List<LanguageModel>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var binding: CardSelectLanguageBinding
    private lateinit var listener: ILanguageChanged

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        binding = CardSelectLanguageBinding.inflate(LayoutInflater.from(context), parent, false)
        return LanguageViewHolder(binding.root)
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val languageModel = arrayList[position]

        (holder as LanguageViewHolder).initializeUIComponents(languageModel)

        currentLanguageCode?.apply {
            if(languageModel.code == currentLanguageCode) {
                holder.card.backgroundTintList = AppCompatResources.getColorStateList(
                    context,
                    R.color.blue_light
                )
            }
        }

        holder.card.setOnClickListener {
            listener.changeLanguage(languageModel.code)
        }
    }

    fun addListener(listener: ILanguageChanged) {
        this.listener = listener
    }

    inner class LanguageViewHolder(myView: View): RecyclerView.ViewHolder(myView) {

        var card: CardView = myView.findViewById(R.id.select_language_card)
        var languageIcon: ImageView = myView.findViewById(R.id.language_icon)
        var languageName: TextView = myView.findViewById(R.id.language_name)

        fun initializeUIComponents(model: LanguageModel) {
            when(model.code) {
                "ro" -> languageIcon.setImageResource(R.drawable.flag_of_romania)
                "en" -> languageIcon.setImageResource(R.drawable.flag_of_england)
            }
            languageName.text = model.name
        }
    }

}