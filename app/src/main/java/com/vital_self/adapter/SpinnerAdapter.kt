package com.vital_self.adapter

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.TextView
import com.vital_self.R

class SpinnerAdapter(
    private val mContext: Context,
    private val mLayoutResourceId: Int,
    cities: ArrayList<String>,
) :
    ArrayAdapter<String>(mContext, mLayoutResourceId, cities) {
    private val list: MutableList<String> = ArrayList(cities)
    private var finalList: List<String> = ArrayList(cities)


    override fun getCount(): Int {
        return list.size
    }
    override fun getItem(position: Int): String {
        return list[position]
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        if (convertView == null){
            val inflater = (mContext as Activity).layoutInflater
            convertView = inflater.inflate(R.layout.spinner_item, parent, false)
        }

        try {
            val city: String = getItem(position)
            val cityAutoCompleteView = convertView!!.findViewById<View>(R.id.spinner_item_text) as TextView
            cityAutoCompleteView.setText(city)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return convertView!!
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filterResults = FilterResults()
                if (constraint != null) {
                    val citySuggestion: MutableList<String> = ArrayList()
                    for (city in finalList) {
                        if (city.toLowerCase().startsWith(constraint.toString().toLowerCase())
                        ) {
                            citySuggestion.add(city)
                        }
                    }
                    filterResults.values = citySuggestion
                    filterResults.count = citySuggestion.size
                }
                return filterResults
            }
            override fun publishResults(
                constraint: CharSequence?,
                results: FilterResults
            ) {
                Log.d("TAG", "publishResults: ${results.values} and constraint $constraint ")
                     if (constraint == null) {
                        list.addAll(finalList)
                        notifyDataSetInvalidated()
                    }
                }
            }
        }
    }