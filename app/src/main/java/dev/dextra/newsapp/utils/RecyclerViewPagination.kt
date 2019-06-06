package dev.dextra.newsapp.utils

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class RecyclerViewPagination(private val listener: RecyclerViewPaginationScrollListener) :
    RecyclerView.OnScrollListener() {

    private var currentPage = 1

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        val viewManager = recyclerView.layoutManager

        if (viewManager is LinearLayoutManager) {
            val totalItemCount = viewManager.itemCount
            val lastView = viewManager.findLastCompletelyVisibleItemPosition()

            if (totalItemCount == (lastView + 1)) {
                currentPage++
                listener.loadMore(currentPage)
            }
        }
    }

    interface RecyclerViewPaginationScrollListener {
        fun loadMore(currentPage: Int)
    }
}