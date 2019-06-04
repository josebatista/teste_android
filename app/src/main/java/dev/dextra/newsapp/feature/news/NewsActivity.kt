package dev.dextra.newsapp.feature.news

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputLayout
import dev.dextra.newsapp.R
import dev.dextra.newsapp.api.model.Article
import dev.dextra.newsapp.api.model.Source
import dev.dextra.newsapp.base.BaseListActivity
import dev.dextra.newsapp.feature.news.adapter.ArticleListAdapter
import kotlinx.android.synthetic.main.activity_news.*
import kotlinx.android.synthetic.main.activity_sources.*
import org.koin.android.ext.android.inject


const val NEWS_ACTIVITY_SOURCE = "NEWS_ACTIVITY_SOURCE"

class NewsActivity : BaseListActivity() {

    override val emptyStateTitle: Int = R.string.empty_state_title_news
    override val emptyStateSubTitle: Int = R.string.empty_state_subtitle_news
    override val errorStateTitle: Int = R.string.error_state_title_news
    override val errorStateSubTitle: Int = R.string.error_state_subtitle_news
    override val mainList: View
        get() = news_list

    private val newsViewModel: NewsViewModel by inject()

    private var viewAdapter: ArticleListAdapter = ArticleListAdapter(this, this, emptyList())
    private var viewManager: RecyclerView.LayoutManager = GridLayoutManager(this, 1)

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_news)

        (intent?.extras?.getSerializable(NEWS_ACTIVITY_SOURCE) as Source).let { source ->
            title = source.name

            loadNews(source)
        }

        super.onCreate(savedInstanceState)

    }

    private fun loadNews(source: Source) {
        newsViewModel.articles.observe(this, Observer {

        })

        newsViewModel.networkState.observe(this, networkStateObserver)

        newsViewModel.configureSource(source)
        newsViewModel.loadNews()
    }

    fun onClick(article: Article) {
        val i = Intent(Intent.ACTION_VIEW)
        i.data = Uri.parse(article.url)
        startActivity(i)
    }

//    private var loading: Dialog? = null
//
//    fun showLoading() {
//        if (loading == null) {
//            loading = Dialog(this)
//            loading?.apply {
//                requestWindowFeature(Window.FEATURE_NO_TITLE)
//                window.setBackgroundDrawableResource(android.R.color.transparent)
//                setContentView(R.layout.dialog_loading)
//            }
//        }
//        loading?.show()
//    }
//
//    fun hideLoading() {
//        loading?.dismiss()
//    }

    fun showData(articles: List<Article>) {
        val viewAdapter = ArticleListAdapter(this@NewsActivity, this@NewsActivity, articles)
        news_list.adapter = viewAdapter
    }

    override fun setupPortrait() {
        setListColumns(1)
        sources_filters.orientation = LinearLayout.VERTICAL
        configureFilterLayoutParams(country_select_layout, ViewGroup.LayoutParams.MATCH_PARENT, 0f)
        configureFilterLayoutParams(category_select_layout, ViewGroup.LayoutParams.MATCH_PARENT, 0f)
    }

    override fun setupLandscape() {
        setListColumns(2)
        sources_filters.orientation = LinearLayout.HORIZONTAL
        configureFilterLayoutParams(country_select_layout, 0, 1f)
        configureFilterLayoutParams(category_select_layout, 0, 1f)
    }

    private fun configureFilterLayoutParams(textInput: TextInputLayout, width: Int, weight: Float) {
        val layoutParams = textInput.layoutParams
        if (layoutParams is LinearLayout.LayoutParams) {
            layoutParams.width = width
            layoutParams.weight = weight
        }
    }

    private fun setListColumns(columns: Int) {
        val layoutManager = sources_list.layoutManager
        if (layoutManager is GridLayoutManager) {
            layoutManager.spanCount = columns
            viewAdapter.notifyDataSetChanged()
        }
    }

    override fun executeRetry() {
        newsViewModel.loadNews()
    }
}
