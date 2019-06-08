package dev.dextra.newsapp

import android.content.Intent
import android.net.Uri
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasAction
import androidx.test.espresso.intent.matcher.IntentMatchers.hasData
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import dev.dextra.newsapp.api.model.ArticlesResponse
import dev.dextra.newsapp.api.model.Source
import dev.dextra.newsapp.base.BaseInstrumentedTest
import dev.dextra.newsapp.base.TestSuite
import dev.dextra.newsapp.feature.news.NEWS_ACTIVITY_SOURCE
import dev.dextra.newsapp.feature.news.NewsActivity
import dev.dextra.newsapp.utils.JsonUtils
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.CoreMatchers.not
import org.junit.After
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NewsActivityInstrumentedTest : BaseInstrumentedTest() {

    private val emptyResponse: ArticlesResponse = ArticlesResponse(emptyList(), "ok", 0)

    //Source baseado na variavel da classe SourcesActivityInstrumentedTest
    private val source: Source = Source(
        "cat",
        "BR",
        "Test Brazil Description",
        "1234",
        "PT",
        "Test Brazil",
        "http://www.google.com.br"
    )

    @get:Rule
    val activityRule = ActivityTestRule(NewsActivity::class.java, false, false)

    private fun startActivity(source: Source?) {
        activityRule.launchActivity(Intent().apply {
            putExtra(NEWS_ACTIVITY_SOURCE, source)
        })
        Intents.init()
    }

    @Test
    fun testNews() {
        // inicia activity com o source
        startActivity(source)

        // mostra animacao de loading
        waitLoading()

        // verifica se a 'lista de noticias' esta visivel e se as telas de 'erro' e 'vazio' estao ocultas
        onView(withId(R.id.news_list)).check(matches(isDisplayed()))
        onView(withId(R.id.error_state)).check(matches(not(isDisplayed())))
        onView(withId(R.id.empty_state)).check(matches(not(isDisplayed())))

        // verifica se exite um filho contento o texto 'Test Brazil'
        onView(withChild(withText("Test Brazil"))).check(matches(isDisplayed()))
    }

    @Test
    fun testEmptyNews() {
        // atribui 'resposta vazia' para a requisicao
        TestSuite.mock(TestConstants.newsURL).body(JsonUtils.toJson(emptyResponse)).apply()

        // inicia activity com o source
        startActivity(source)

        // mostra animacao de loading
        waitLoading()

        // verifica se a 'lista de noticias' e tela 'erro' estao ocultas e se a tela de 'vazio' esta visivel
        onView(withId(R.id.news_list)).check(matches(not(isDisplayed())))
        onView(withId(R.id.error_state)).check(matches(not(isDisplayed())))
        onView(withId(R.id.empty_state)).check(matches(isDisplayed()))
    }

    @Test
    fun testErrorNews() {
        // atribui erro de conexao para a requisicao
        TestSuite.mock(TestConstants.newsURL).throwConnectionError().apply()

        // inicia activity com o source
        startActivity(source)

        // mostra animacao de loading
        waitLoading()

        // verifica se a 'lista de noticias' e tela de 'vazio' estao ocultas e se a tela de 'erro' esta visivel
        onView(withId(R.id.news_list)).check(matches(not(isDisplayed())))
        onView(withId(R.id.error_state)).check(matches(isDisplayed()))
        onView(withId(R.id.empty_state)).check(matches(not(isDisplayed())))
    }

    @Test
    fun testErrorNewsRetryClick() {
        // atribui erro de conexao para a requisicao
        TestSuite.mock(TestConstants.newsURL).throwConnectionError().apply()

        // inicia activity com o source
        startActivity(source)

        //mostra animacao de loading
        waitLoading()

        // verifica se a 'lista de noticias' e tela de 'vazio' estao ocultas e se a tela de 'erro' esta visivel
        onView(withId(R.id.news_list)).check(matches(not(isDisplayed())))
        onView(withId(R.id.error_state)).check(matches(isDisplayed()))
        onView(withId(R.id.empty_state)).check(matches(not(isDisplayed())))

        // limpa a propriedade de erro da conexao
        TestSuite.clearEndpointMocks()

        // 'clica' em retry
        onView(withId(R.id.error_state_retry)).perform(click())

        // mostra animacao de loading
        waitLoading()

        // verifica se a 'lista de noticias' esta visivel e se as telas de 'erro' e 'vazio' estao ocultas
        onView(withId(R.id.news_list)).check(matches(isDisplayed()))
        onView(withId(R.id.error_state)).check(matches(not(isDisplayed())))
        onView(withId(R.id.empty_state)).check(matches(not(isDisplayed())))
    }

    @Test
    fun testOpenArticle() {
        //inicia activity
        startActivity(source)

        // mostra animacao de loading
        waitLoading()

        // 'clica' na view que tem o texto abaixo (retirado do arquivo everything.json)
        onView(withText("Agency suspends Iowa prison guard over positive news article")).perform(click())

        // verifica o redirecionamento da url com base nas configuracoes de action e uri (url retirada do arquivo everything.json)
        intended(
            allOf(
                hasAction(Intent.ACTION_VIEW),
                hasData(
                    Uri.parse(
                        "https://abcnews.go.com/US/wireStory/agency-suspends-iowa-prison-guard-positive-news-article-62093289"
                    )
                )
            )
        )
    }

    @After
    fun clearTest() {
        Intents.release()
    }

}