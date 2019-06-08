package dev.dextra.newsapp.feature.news

import dev.dextra.newsapp.TestConstants
import dev.dextra.newsapp.api.model.ArticlesResponse
import dev.dextra.newsapp.api.model.Source
import dev.dextra.newsapp.base.BaseTest
import dev.dextra.newsapp.base.NetworkState
import dev.dextra.newsapp.base.TestSuite
import dev.dextra.newsapp.utils.JsonUtils
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.koin.test.get

class NewsViewModelTest : BaseTest() {

    private val emptyResponse = ArticlesResponse(emptyList(), "ok", 0)

    private lateinit var viewModel: NewsViewModel
    private lateinit var source: Source

    @Before
    fun setupTest() {
        viewModel = TestSuite.get()

        //Source baseado em um dos sources presentes no sources.json
        source = Source(
            "general",
            "br",
            "Descubra a seção brasileira da Blasting News...",
            "blasting-news-br",
            "pt",
            "Blasting News (BR)",
            "http://br.blastingnews.com"
        )
    }

    @Test
    fun testGetNews() {
        viewModel.configureSource(source)

        viewModel.loadNews()

        assert(viewModel.articles.value?.size == 11) // quantidade presente no arquivo everything.json
        assertEquals(NetworkState.SUCCESS, viewModel.networkState.value)

        viewModel.onCleared()
        assert(viewModel.getDisposables().isEmpty())
    }

    @Test
    fun testEmptyNews() {
        TestSuite.mock(TestConstants.newsURL).body(JsonUtils.toJson(emptyResponse)).apply()

        viewModel.configureSource(source)
        viewModel.loadNews()

        assert(viewModel.articles.value?.size == 0)
        assertEquals(NetworkState.EMPTY, viewModel.networkState.value)
    }

    @Test
    fun testGetMoreNews() {
        viewModel.configureSource(source)

        viewModel.loadNews()
        assert(viewModel.articles.value?.size == 11) // quantidade presente no arquivo everything.json
        assertEquals(NetworkState.SUCCESS, viewModel.networkState.value)

        viewModel.loadMore(2)
        assert(viewModel.articles.value?.size == 22) // quantidade presente no arquivo everything.json
        assertEquals(NetworkState.SUCCESS, viewModel.networkState.value)

        TestSuite.mock(TestConstants.newsURL).body(JsonUtils.toJson(emptyResponse)).apply() // Testar resposta vazia

        viewModel.loadMore(3)
        assert(viewModel.articles.value?.size == 22) // quantidade presente no arquivo everything.json
        assertEquals(NetworkState.SUCCESS, viewModel.networkState.value)

        viewModel.onCleared()
        assert(viewModel.getDisposables().isEmpty())
    }

    @Test(expected = IllegalArgumentException::class)
    fun testWithOutSource() {
        viewModel.loadNews()
    }

    @Test
    fun testErrorSources() {
        TestSuite.mock(TestConstants.newsURL).throwConnectionError().apply()

        viewModel.configureSource(source)

        viewModel.loadNews()

        assert(viewModel.articles.value == null)
        assertEquals(NetworkState.ERROR, viewModel.networkState.value)
    }

}