package com.example.gift_for_apelsinka.activity.main.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.example.gift_for_apelsinka.R
import com.example.gift_for_apelsinka.activity.main.model.Statement


class StatementViewPageAdapter(context: Context, list : List<Statement>) : PagerAdapter() {
    private var ctx : Context = context
    private var statementList = list
    private lateinit var layoutInflater : LayoutInflater

    override fun getCount(): Int {
        return statementList.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        layoutInflater = LayoutInflater.from(ctx)
        val view = layoutInflater.inflate(R.layout.field_of_statement, container, false)

        view.findViewById<TextView>(R.id.body_statement).text = statementList[position].body
        view.findViewById<TextView>(R.id.author_statement).text = statementList[position].author

        container.addView(view, 0)
        return view
    }

    override fun destroyItem(container: View, position: Int, `object`: Any) {
        (container as ViewPager).removeView(`object` as View?)
    }
}