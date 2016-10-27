package ch.daplab.nosql.cassandra.doodle

import ch.daplab.nosql.cassandra.doodle.domains.Poll
import ch.daplab.nosql.cassandra.doodle.domains.Subscriber
import ch.daplab.nosql.cassandra.doodle.services.impl.CassandraHelpers
import ch.daplab.nosql.cassandra.doodle.services.impl.cassandraOM.CassandraOMPollServiceImpl
import ch.daplab.nosql.cassandra.doodle.services.impl.dummy.DummyPollServiceImpl
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import org.slf4j.LoggerFactory
import spark.Spark.*
import java.util.*

/**
 * PollsController class will expose a series of RESTfull endpoints
 */
object PollApp {

    private val logger = LoggerFactory.getLogger(PollApp::class.java)

    private val mapper = ObjectMapper()

    @JvmStatic fun main(args: Array<String>) {

        // TODO: set your own implementation here
//        val pollService = DummyPollServiceImpl()
        val cassandraSession = CassandraHelpers.getLocalhostConnection()
//        val pollService = CassandraPollServiceImpl(cassandraSession)
        val pollService = CassandraOMPollServiceImpl(cassandraSession)

        port(8080)

        get("/rest/polls/:pollId") { request, response ->

            val pollId = request.params(":pollId")
            validatePollId(pollId)

            val poll = tryPollService { pollService.getPollById(pollId) }

            logger.debug("Returning Poll: " + poll?.toString())

            if (poll == null) {
                halt(404, "")
            } else {
                mapper.writeValueAsBytes(poll)
            }
        }

        get("/rest/polls") { request, response ->
            val polls = tryPollService { pollService.allPolls }
            mapper.writeValueAsBytes(polls)
        }

        post("/rest/polls") { request, response ->

            val createdPoll = tryPollService {
                val receivedPoll = mapper.readValue<Poll>(request.bodyAsBytes(), Poll::class.java)
                pollService.createPoll(receivedPoll)
            }

            response.status(201)
            response.header("Location", "/rest/polls/" + createdPoll.id)

            mapper.writeValueAsString(createdPoll)
        }

        put("/rest/polls/:pollId") { request, response ->

            val pollId = request.params(":pollId")
            validatePollId(pollId)


            val updatedPoll = tryPollService {
                val createdSubscriber = mapper.readValue<Subscriber>(
                        request.bodyAsBytes(), Subscriber::class.java
                )
                pollService.addSubscriber(pollId, createdSubscriber)
            }

            response.status(201)
            response.header("Location", "/rest/polls/" + updatedPoll.id)

            mapper.writeValueAsString(updatedPoll)
        }

        delete("/rest/polls/:pollId") { request, response ->

            val pollId = request.params(":pollId")
            validatePollId(pollId)

            tryPollService { pollService.deletePoll(pollId) }

            "ok"
        }

        exception(Exception::class.java) { exception, request, response ->
            logger.error("Got an exception", exception)
            val jsonBody = JsonNodeFactory.instance
                    .objectNode().put("error", exception.message).toString()
            response.body(jsonBody)
            response.status(500)
        }

    }

    private fun validatePollId(pollId: String) {
        if (isEmpty(pollId) || pollId.length < 5) {
            val sMessage = "Error - Invalid poll Id parameter"
            throw IllegalStateException(sMessage)
        }
    }

    private inline fun <T> tryPollService(body: () -> T): T {
        try {
            return body()
        } catch (e: Exception) {
            throw IllegalStateException("Error invoking PollService. [$e]")
        }
    }

    fun isEmpty(s_p: String?): Boolean {
        return null == s_p || s_p.trim().length == 0
    }

}
