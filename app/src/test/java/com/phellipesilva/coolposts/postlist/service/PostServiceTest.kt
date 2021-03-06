package com.phellipesilva.coolposts.postlist.service

import com.phellipesilva.coolposts.postlist.service.entity.PostRemoteEntity
import com.phellipesilva.coolposts.postlist.service.entity.UserRemoteEntity
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

class PostServiceTest {

    private lateinit var server: MockWebServer
    private lateinit var postService: PostService

    @Before
    fun `Set Up`() {
        server = MockWebServer()
        server.start(4040)

         postService = Retrofit.Builder()
            .baseUrl("http://localhost:4040")
            .client(OkHttpClient.Builder().build())
            .addConverterFactory(MoshiConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
            .create(PostService::class.java)
    }

    @After
    fun `Tear Down`() {
        server.shutdown()
    }

    @Test
    fun `Should serialize posts to remote entities when requesting posts from service`() {
        val json = ResourcesUtils.readJsonFromResources("json/posts_response.json")
        val mockResponse = MockResponse().setBody(json)
        server.enqueue(mockResponse)

        postService.fetchPosts().test()
            .assertNoErrors()
            .assertValue { it.size == 2 }
            .assertValue {
                it[0] == PostRemoteEntity(
                    userId = 1,
                    id = 1,
                    title = "sunt aut facere repellat provident occaecati excepturi optio reprehenderit",
                    body = "quia et suscipit\nsuscipit recusandae consequuntur expedita et cum\nreprehenderit molestiae ut ut quas totam\nnostrum rerum est autem sunt rem eveniet architecto"
                )
            }
            .assertValue {
                it[1] == PostRemoteEntity(
                    userId = 1,
                    id = 2,
                    title = "qui est esse",
                    body = "est rerum tempore vitae\nsequi sint nihil reprehenderit dolor beatae ea dolores neque\nfugiat blanditiis voluptate porro vel nihil molestiae ut reiciendis\nqui aperiam non debitis possimus qui neque nisi nulla"
                )
            }
            .assertComplete()
    }

    @Test
    fun `Should serialize users to remote entities when requesting users from service`() {
        val json = ResourcesUtils.readJsonFromResources("json/users_response.json")
        val mockResponse = MockResponse().setBody(json)
        server.enqueue(mockResponse)

        postService.fetchUsers().test()
            .assertNoErrors()
            .assertValue { it.size == 2 }
            .assertValue {
                it[0] == UserRemoteEntity(
                    id = 1,
                    name = "Leanne Graham"
                )
            }
            .assertValue {
                it[1] == UserRemoteEntity(
                    id = 2,
                    name = "Ervin Howell"
                )
            }
            .assertComplete()
    }
}