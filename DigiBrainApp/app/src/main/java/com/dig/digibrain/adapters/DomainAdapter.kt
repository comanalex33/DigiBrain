package com.dig.digibrain.adapters

import android.app.Application
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.dig.digibrain.App
import com.dig.digibrain.R
import com.dig.digibrain.databinding.CardDomainBinding
import com.dig.digibrain.interfaces.IDomainChanged
import com.dig.digibrain.models.subject.DomainModel

class DomainAdapter(var context: Context, var application: Application, var listener: IDomainChanged, var currentDomain: DomainModel?, private var arrayList: List<DomainModel>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var binding: CardDomainBinding
    private var selectedPosition: Int = -1
    private var updatePosition: Boolean = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        binding = CardDomainBinding.inflate(LayoutInflater.from(context), parent, false)
        return DomainViewHolder(binding.root)
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val domainItem: DomainModel = arrayList[position]

        (holder as DomainViewHolder).initializeUIComponents(domainItem)

        if(!updatePosition) {
            currentDomain?.apply {
                if (holder.domainId == this.id) {
                    selectedPosition = holder.adapterPosition
                }
            }
        }

        holder.card.setOnClickListener {
            listener.disableErrorMessage()
            listener.changeDomain(domainItem)

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

    inner class DomainViewHolder(myView: View): RecyclerView.ViewHolder(myView) {

        var card: CardView = myView.findViewById(R.id.domain_card_view)
        var domainText: TextView = myView.findViewById(R.id.domain_text)
        var domainImage: ImageView = myView.findViewById(R.id.domain_image)
        var domainId: Long = 0

        fun initializeUIComponents(model: DomainModel) {
            domainText.text = model.name
            domainId = model.id

            var iconAvailable = false
            val iconPack = (application as App).iconPack
            if(iconPack != null) {
                if(iconPack.getIcon(model.iconId) != null) {
                    domainImage.background = iconPack.getIcon(model.iconId)!!.drawable
                    iconAvailable = true
                }
            }
            if(!iconAvailable) {
                // TODO - set default image
            }
        }
    }
}