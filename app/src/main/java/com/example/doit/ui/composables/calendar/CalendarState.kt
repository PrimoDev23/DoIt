package com.example.doit.ui.composables.calendar

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import java.time.DayOfWeek
import java.time.LocalDate

@OptIn(ExperimentalFoundationApi::class)
@Stable
class CalendarState(
    initialSelectedDate: LocalDate
) {

    var selectedDate by mutableStateOf(initialSelectedDate)
        private set

    var displayMonth: LocalDate by mutableStateOf(initialSelectedDate.withDayOfMonth(1))
        private set

    internal val pagerState = CalendarPagerState(3)

    internal val months by derivedStateOf {
        (0 until 3).map {
            val offset = it - 1L
            val month = displayMonth.plusMonths(offset)

            getDatesForMonth(month)
        }
    }

    fun updateSelectedDate(date: LocalDate) {
        selectedDate = date
    }

    suspend fun onSettledPageChanged(page: Int) {
        if (page == 1) {
            return
        }

        val offset = -1L + page
        displayMonth = displayMonth.plusMonths(offset)

        pagerState.scrollToPage(1)
    }

    private fun getDatesForMonth(date: LocalDate): List<List<LocalDate?>> {
        var currentDate = date.withDayOfMonth(1)
        val lengthOfMonth = date.lengthOfMonth()
        val endDate = date.withDayOfMonth(lengthOfMonth)

        val month = mutableListOf<List<LocalDate?>>()
        var week = mutableListOf<LocalDate?>()

        do {
            week.add(currentDate)

            if (currentDate.dayOfWeek == DayOfWeek.SUNDAY) {
                if (week.size < 7) {
                    val diff = 7 - week.size

                    repeat(diff) {
                        week.add(0, null)
                    }
                }

                month.add(week)
                week = mutableListOf()
            }

            currentDate = currentDate.plusDays(1)
        } while (currentDate <= endDate)

        if (week.isNotEmpty()) {
            if (week.size < 7) {
                val diff = 7 - week.size

                repeat(diff) {
                    week.add(null)
                }
            }

            month.add(week)
        }

        return month
    }

    companion object {
        val Saver = listSaver(
            save = {
                listOf(
                    it.selectedDate
                )
            },
            restore = {
                CalendarState(
                    initialSelectedDate = it[0] as LocalDate
                )
            }
        )
    }
}

@ExperimentalFoundationApi
class CalendarPagerState(
    override val pageCount: Int
) : PagerState(initialPage = 1)

@Composable
fun rememberCalendarState(initialSelectedDate: LocalDate = LocalDate.now()): CalendarState {
    return rememberSaveable(saver = CalendarState.Saver) {
        CalendarState(initialSelectedDate = initialSelectedDate)
    }
}