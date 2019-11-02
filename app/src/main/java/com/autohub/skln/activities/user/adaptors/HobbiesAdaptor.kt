package com.autohub.skln.activities.user.adaptors

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.autohub.skln.R
import com.autohub.skln.databinding.HobbiesRowBinding
import com.autohub.skln.listeners.ItemClickListener
import com.autohub.skln.models.AcadmicsData
import com.autohub.skln.models.HobbiesData


class HobbiesAdaptor(var context: Context, var mItemClickListener: ItemClickListener<AcadmicsData>)
    : RecyclerView.Adapter<HobbiesAdaptor.Holder>() {

    private var hobbiesDatalist: List<HobbiesData> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val hobiesRowBinding: HobbiesRowBinding =
                DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context),
                        R.layout.hobbies_row, parent, false
                )


        return Holder(hobiesRowBinding)
    }


    override fun onBindViewHolder(holder: Holder, position: Int) {

        holder.hobiesRowBinding.setItemClickListener(mItemClickListener)
        hobbiesDatalist[position].let {
            with(holder.hobiesRowBinding)
            {
                hobbiesData = it
                img.setImageResource(it.icon)
                txt.setText(it.hobbyName)

                val unwrappedDrawable = ContextCompat.getDrawable(context, com.autohub.skln.R.drawable.acadmic_rowbg_drawable)
                val wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable!!)
                DrawableCompat.setTint(wrappedDrawable, context.resources.getColor(it.color)/* it.color*/)
                rr.setBackground(unwrappedDrawable)
            }
        }

    }

    override fun getItemCount(): Int {
        return hobbiesDatalist.size
    }

    fun setData(hobbiesDatalist: List<HobbiesData>) {
        this.hobbiesDatalist = hobbiesDatalist
        notifyDataSetChanged()
    }

    inner class Holder(var hobiesRowBinding: HobbiesRowBinding) :
            RecyclerView.ViewHolder(hobiesRowBinding.root) {
        fun bind(obj: Any) {
            with(hobiesRowBinding)
            {

            }
        }
    }


}