package com.vital_self.utils

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.style.ForegroundColorSpan
import androidx.core.content.ContextCompat
import com.vital_self.R
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.prolificinteractive.materialcalendarview.MaterialCalendarView


class OtherMonthDecorator(
    private val context: Context,
    private val calendarView: MaterialCalendarView
) : DayViewDecorator {
    override fun shouldDecorate(day: CalendarDay): Boolean {
        return day.month != calendarView.currentDate.month
                || day.year != calendarView.currentDate.year
    }

    override fun decorate(view: DayViewFacade) {
        view.addSpan(
            ForegroundColorSpan(
                ContextCompat.getColor(
                    context, R.color.other_months_dates
                )
            )
        )
    }
}

class TodayDecorator(private val context: Context, private val calendarView: MaterialCalendarView) :
    DayViewDecorator {

    override fun shouldDecorate(day: CalendarDay?): Boolean {
        return day == CalendarDay.today()
    }

    override fun decorate(view: DayViewFacade?) {
        val drawable: Drawable? =
            ContextCompat.getDrawable(context, R.drawable.calendar_todays_date)
        view?.setBackgroundDrawable(drawable!!)
        view?.addSpan(
            ForegroundColorSpan(
                ContextCompat.getColor(context, R.color.white_both_theme)
            )
        )
    }
}

class DaysDecorator(private val context: Context, private val calendarView: MaterialCalendarView) :
    DayViewDecorator {

    override fun shouldDecorate(day: CalendarDay?): Boolean {
        return day != CalendarDay.today()
                && day?.day != calendarView.currentDate.day
                && day?.month == calendarView.currentDate.month
                && day.year == calendarView.currentDate.year
    }

    override fun decorate(view: DayViewFacade?) {
        view?.addSpan(
            ForegroundColorSpan(
                ContextCompat.getColor(context, R.color.primary_text_color)
            )
        )
    }
}