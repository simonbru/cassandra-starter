package ch.daplab.nosql.cassandra.doodle.web.rest

import java.io.IOException
import java.util.ArrayList
import java.util.Arrays

import ch.daplab.nosql.cassandra.doodle.domains.Poll
import ch.daplab.nosql.cassandra.doodle.domains.Subscriber
import com.fasterxml.jackson.databind.ObjectMapper

import okhttp3.*
import org.junit.Assert
import org.junit.Test


/**
 * Please start [ch.daplab.nosql.cassandra.doodle.PollApp] before launching this test.
 */
class RESTBasicTest {

    @Test
    @Throws(IOException::class)
    fun createPollTest() {
        val okHttpClient = OkHttpClient()

        //
        //        Create a new poll
        //
        val poll = Poll()
        // poll.setId(null); id is returned by the REST API, not set manually.
        poll.email = "email@address.com"

        poll.label = "Afterwork"
        poll.maxChoices = 1

        @SuppressWarnings("serial")
        val choices = object : ArrayList<String>() {
            init {
                add("Monday")
                add("Tuesday")
                add("Friday")
            }
        }
        poll.choices = choices

        // JSON serialization.
        val mapper = ObjectMapper()

        val requestBody = RequestBody.create(JSON, mapper.writeValueAsString(poll))

        val request = Request.Builder().url(URL).post(requestBody).build()

        val response = okHttpClient.newCall(request).execute()

        Assert.assertEquals(201, response.code().toLong())
        Assert.assertNotNull(response.header("Location"))
        println(response.header("Location"))

        val receivedPoll = mapper.readValue(response.body().bytes(), Poll::class.java)
        Assert.assertNotNull(receivedPoll.id)


        //
        //        Retrieve the newly created poll
        //
        val request2 = Request.Builder().url(URL + "/" + receivedPoll.id).get().build()
        val response2 = okHttpClient.newCall(request2).execute()
        Assert.assertEquals(200, response2.code().toLong())
        val receivedPoll2 = mapper.readValue(response2.body().bytes(), Poll::class.java)

        Assert.assertEquals(receivedPoll.id, receivedPoll2.id)


        //
        //        Add a subscriber
        //
        val s = Subscriber()
        s.label = "Benoit"
        s.choices = Arrays.asList("Monday", "Tuesday")

        val subcriber = RequestBody.create(JSON, mapper.writeValueAsString(s))

        val request3 = Request.Builder().url(URL + "/" + receivedPoll.id).put(subcriber).build()
        val response3 = okHttpClient.newCall(request3).execute()
        Assert.assertEquals(201, response3.code().toLong())
        val receivedPoll3 = mapper.readValue(response3.body().bytes(), Poll::class.java)
        Assert.assertEquals(receivedPoll.id, receivedPoll3.id)
        Assert.assertEquals(1, receivedPoll3.subscribers!!.size.toLong())


        //
        //        Delete the poll
        //
        val request4 = Request.Builder().url(URL + "/" + receivedPoll.id).delete().build()
        val response4 = okHttpClient.newCall(request4).execute()
        Assert.assertEquals(200, response4.code().toLong())

    }

    companion object {

        val JSON = MediaType.parse("application/json; charset=utf-8")

        val URL = "http://localhost:8080/rest/polls"
    }

}
