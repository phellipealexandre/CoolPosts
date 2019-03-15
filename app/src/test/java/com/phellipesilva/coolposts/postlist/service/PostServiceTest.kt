package com.phellipesilva.coolposts.postlist.service

import com.phellipesilva.coolposts.postlist.service.entities.CommentRemoteEntity
import com.phellipesilva.coolposts.postlist.service.entities.PostRemoteEntity
import com.phellipesilva.coolposts.postlist.service.entities.UserRemoteEntity
import okhttp3.OkHttpClient
import org.junit.Before
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import io.reactivex.observers.TestObserver
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Test

class PostServiceTest {

    private lateinit var server: MockWebServer
    private lateinit var postService: PostService

    @Before
    fun setUp() {
        server = MockWebServer()
        server.start(4040)
        server.url("/latest?base=EUR")

         postService = Retrofit.Builder()
            .baseUrl("http://localhost:4040")
            .client(OkHttpClient.Builder().build())
            .addConverterFactory(MoshiConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
            .create(PostService::class.java)
    }

    @After
    fun tearDown() {
        server.shutdown()
    }

    @Test
    fun shouldParsePostsCorrectlyWhenRequestingAllPostsFromService() {
        val testObserver = TestObserver<List<PostRemoteEntity>>()
        val json = readJsonFromResources("json/posts_response.json")
        val mockResponse = MockResponse().setBody(json)
        server.enqueue(mockResponse)

        val postsObservable = postService.getPosts()
        postsObservable.subscribe(testObserver)

        testObserver.assertNoErrors()
        testObserver.assertValue { it.size == 2 }
        testObserver.assertValue {
            it[0] == PostRemoteEntity(
                userId = 1,
                id = 1,
                title = "sunt aut facere repellat provident occaecati excepturi optio reprehenderit",
                body = "quia et suscipit\nsuscipit recusandae consequuntur expedita et cum\nreprehenderit molestiae ut ut quas totam\nnostrum rerum est autem sunt rem eveniet architecto"
            )
        }
        testObserver.assertValue {
            it[1] == PostRemoteEntity(
                userId = 1,
                id = 2,
                title = "qui est esse",
                body = "est rerum tempore vitae\nsequi sint nihil reprehenderit dolor beatae ea dolores neque\nfugiat blanditiis voluptate porro vel nihil molestiae ut reiciendis\nqui aperiam non debitis possimus qui neque nisi nulla"
            )
        }
    }

    @Test
    fun shouldParseUsersCorrectlyWhenRequestingAllUsersFromService() {
        val testObserver = TestObserver<List<UserRemoteEntity>>()
        val json = readJsonFromResources("json/users_response.json")
        val mockResponse = MockResponse().setBody(json)
        server.enqueue(mockResponse)

        val userObservable = postService.getUsers()
        userObservable.subscribe(testObserver)

        testObserver.assertNoErrors()
        testObserver.assertValue { it.size == 2 }
        testObserver.assertValue {
            it[0] == UserRemoteEntity(
                id = 1,
                name = "Leanne Graham",
                website = "hildegard.org"
            )
        }
        testObserver.assertValue {
            it[1] == UserRemoteEntity(
                id = 2,
                name = "Ervin Howell",
                website = "anastasia.net"
            )
        }
    }

    @Test
    fun shouldParseCommentsCorrectlyWhenRequestingCommentsOfSpecificPostFromService() {
        val testObserver = TestObserver<List<CommentRemoteEntity>>()
        val json = readJsonFromResources("json/comments_response.json")
        val mockResponse = MockResponse().setBody(json)
        server.enqueue(mockResponse)

        val commentObservable = postService.getComments(1)
        commentObservable.subscribe(testObserver)

        testObserver.assertNoErrors()
        testObserver.assertValue { it.size == 2 }
        testObserver.assertValue {
            it[0] == CommentRemoteEntity(
                postId = 1,
                id = 1,
                name = "id labore ex et quam laborum",
                email = "Eliseo@gardner.biz",
                body = "laudantium enim quasi est quidem magnam voluptate ipsam eos\ntempora quo necessitatibus\ndolor quam autem quasi\nreiciendis et nam sapiente accusantium"
            )
        }
        testObserver.assertValue {
            it[1] == CommentRemoteEntity(
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