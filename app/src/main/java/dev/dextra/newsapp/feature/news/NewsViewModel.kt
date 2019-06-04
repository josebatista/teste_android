package dev.dextra.newsapp.feature.news

import androidx.lifecycle.MutableLiveData
import dev.dextra.newsapp.api.model.Article
import dev.dextra.newsapp.api.model.Source
import dev.dextra.newsapp.api.repository.NewsRepository
import dev.dextra.newsapp.base.BaseViewModel
import dev.dextra.newsapp.base.NetworkState


class NewsViewModel(
    private val newsRepository: NewsRepository
) : BaseViewModel() {

    val articles = MutableLiveData<List<Article>>()
    val networkState = MutableLiveData<NetworkState>()

    private var source: Source? = null

    fun configureSource(source: Source) {
        this.source = source
    }

    fun loadNews() {
        networkState.postValue(NetworkState.RUNNING)
        source?.let {
            addDisposable(
                newsRepository.getEverything(source?.id).subscribe({ response ->
                    articles.postValue(response.articles)
                    if (response.articles.isNotEmpty()) {
                        networkState.postValue(NetworkState.SUCCESS)
                    } else {
                        networkState.postValue(NetworkState.ERROR)
                    }
                },
                    {
                        networkState.postValue(NetworkState.ERROR)
                    })
            )
        } ?: throw IllegalArgumentException("Please configure a source")
    }
}
