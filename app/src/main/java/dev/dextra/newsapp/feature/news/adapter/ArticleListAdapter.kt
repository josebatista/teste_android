package dev.dextra.newsapp.feature.news.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import dev.dextra.newsapp.ICON_LOCATOR_URL
import dev.dextra.newsapp.R
import dev.dextra.newsapp.api.model.Article
import dev.dextra.newsapp.feature.news.NewsActivity
import kotlinx.android.synthetic.main.item_article.view.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ArticleListAdapter(private val listener: NewsActivity) :
    RecyclerView.Adapter<ArticleListAdapter.ArticleListAdapterViewHolder>() {

    private val dateFormat = SimpleDateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.SHORT)
    private val parseFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())

    private val dataSet: ArrayList<Article> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleListAdapterViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_article, parent, false)
        return ArticleListAdapterViewHolder(view)
    }

    override fun getItemCount(): Int = dataSet.size

    override fun onBindViewHolder(holder: ArticleListAdapterViewHolder, position: Int) {
        val article = dataSet[position]

        holder.view.setOnClickListener { listener.onClick(article) }

        holder.view.article_name.text = article.title
        holder.view.article_description.text = article.description
        holder.view.article_author.text = article.author
        holder.view.article_date.text = dateFormat.format(parseFormat.parse(article.publishedAt))

        val image = article.urlToImage ?: ICON_LOCATOR_URL.format(article.url)

        Picasso.get()
            .load(image)
            .fit()
            .placeholder(R.drawable.placeholder_image)
            .error(R.drawable.placeholder_image)
            .into(holder.view.article_image)

    }

    fun add(articles: List<Article>) {
        dataSet.addAll(articles)
        notifyDataSetChanged()
    }

    fun clear() {
        dataSet.clear()
    }

    class ArticleListAdapterViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    interface ArticleListAdapterItemClick {
        fun onClick(article: Article)
    }

}