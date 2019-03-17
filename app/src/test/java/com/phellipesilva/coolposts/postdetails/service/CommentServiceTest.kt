package com.phellipesilva.coolposts.postdetails.service

import com.phellipesilva.coolposts.postdetails.data.Comment
import io.reactivex.observers.TestObserver
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory

class CommentServiceTest {

    private lateinit var server: MockWebServer
    private lateinit var commentService: CommentService

    @Before
    fun setUp() {
        server = MockWebServer()
        server.start(4040)
        server.url("/latest?base=EUR")

        commentService = Retrofit.Builder()
            .baseUrl("http://localhost:4040")
            .client(OkHttpClient.Builder().build())
            .addConverterFactory(MoshiConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
            .create(CommentService::class.java)
    }

    @After
    fun tearDown() {
        server.shutdown()
    }

    @Test
    fun shouldParseCommentsCorrectlyWhenRequestingCommentsOfSpecificPostFromService() {
        val testObserver = TestObserver<List<Comment>>()
        val json = readJsonFromResources("json/comments_response.json")
        val mockResponse = MockResponse().setBody(json)
        server.enqueue(mockResponse)

        val commentObservable = commentService.getComments(1)
        commentObservable.subscribe(testObserver)

        testObserver.assertNoErrors()
        testObserver.assertValue { it.size == 2 }
        testObserver.assertValue {
            it[0] == Comment(
                postId = 1,
                id = 1,
                name = "id labore ex et quam laborum",
                email = "Eliseo@gardner.biz",
                body = "laudantium enim quasi est quidem magnam voluptate ipsam eos\ntempora quo necessitatibus\ndolor quam autem quasi\nreiciendis et nam sapiente accusantium"
            )
        }
        testObserver.assertValue {
            it[1] == Comment(
                postId = 1,
                id = 2,
                name = "quo vero reiciendis velit similique earum",
                email = "Jayne_Kuhic@sydney.com",
                body = "est natus enim nihil est dolore omnis voluptatem numquam\net omnis occaecati quod ullam at\nvoluptatem error expedita pariatur\nnihil sint nostrum voluptatem reiciendis et"
            )
        }

    }

    private fun readJsonFromResources(filePath: String): String {
        return this.javaClass
            .classLoader
            ?.getResourceAsStream(filePath)
            ?.bufferedReader().use { it!!.readText() }
    }
}