package com.phellipesilva.coolposts.postdetails.data.service

import com.phellipesilva.coolposts.postdetails.data.entity.CommentEntity
import com.phellipesilva.coolposts.utils.ResourcesUtils
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
    fun `Set Up`() {
        server = MockWebServer()
        server.start(4040)

        commentService = Retrofit.Builder()
            .baseUrl("http://localhost:4040")
            .client(OkHttpClient.Builder().build())
            .addConverterFactory(MoshiConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
            .create(CommentService::class.java)
    }

    @After
    fun `Tear Down`() {
        server.shutdown()
    }

    @Test
    fun `Should serialize comments when requesting comments from service`() {
        val json = ResourcesUtils.readJsonFromResources("json/comments_response.json")
        val mockResponse = MockResponse().setBody(json)
        server.enqueue(mockResponse)

        commentService.fetchCommentsFromPost(1).test()
            .assertNoErrors()
            .assertValue { it.size == 2 }
            .assertValue {
                it[0] == CommentEntity(
                    id = 1,
                    postId = 1,
                    name = "id labore ex et quam laborum",
                    email = "Eliseo@gardner.biz",
                    body = "laudantium enim quasi est quidem magnam voluptate ipsam eos\ntempora quo necessitatibus\ndolor quam autem quasi\nreiciendis et nam sapiente accusantium"
                )
            }
            .assertValue {
                it[1] == CommentEntity(
                    id = 2,
                    postId = 1,
                    name = "quo vero reiciendis velit similique earum",
                    email = "Jayne_Kuhic@sydney.com",
                    body = "est natus enim nihil est dolore omnis voluptatem numquam\net omnis occaecati quod ullam at\nvoluptatem error expedita pariatur\nnihil sint nostrum voluptatem reiciendis et"
                )
            }
    }
}