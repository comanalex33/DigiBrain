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
import com.dig.digibrain.databinding.CardQuizTypeBinding
import com.dig.digibrain.interfaces.IQuizTypeChanged
import com.dig.digibrain.models.QuizTypeModel

class QuizTypeAdapter(var context: Context, var listener: IQuizTypeChanged, var currentTypePosition: QuizTypeModel?, private var arrayList: List<QuizTypeModel>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var binding: CardQuizTypeBinding
    private var selectedPosition: Int = -1
    private var updatePosition: Boolean = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        binding = CardQuizTypeBinding.inflate(LayoutInflater.from(context), parent, false)
        return QuizTypeViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val quizTypeItem: QuizTypeModel = arrayList[position]

        (holder as QuizTypeViewHolder).initializeUIComponents(quizTypeItem)

        if(!updatePosition) {
            currentTypePosition?.apply {
                if(holder.quizTypeName.text == this.name)
                    selectedPosition = holder.adapterPosition
            }
        }

        holder.card.setOnClickListener {
            listener.changeQuizType(quizTypeItem)

            selectedPosition = holder.adapterPosition
            updatePosition = true
            notifyDataSetChanged()
        }

        if(selectedPosition == position) {
            holder.card.backgroundTintList = AppCompatResources.getColorStateList(
                context,
                R.color.blue_light
            )
        } else {
            holder.card.backgroundTintList = AppCompatResources.getColorStateList(
                context,
                R.color.gray
            )
        }
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    inner class QuizTypeViewHolder(myView: View): RecyclerView.ViewHolder(myView) {

        var card: CardView = myView.findViewById(R.id.type_card_view)
        var quizTypeName: TextView = myView.findViewById(R.id.type_text)
        var quizTypeImage: ImageView = myView.findViewById(R.id.type_image)
//        var typeName: String = ""

        fun initializeUIComponents(model: QuizTypeModel) {
            quizTypeName.text = model.name
            quizTypeImage.setImageResource(model.iconId)
//            typeName = model.name
        }
    }
}