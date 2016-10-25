package ch.daplab.nosql.cassandra.doodle

import ch.daplab.nosql.cassandra.doodle.domains.Poll
import ch.daplab.nosql.cassandra.doodle.domains.Subscriber
import ch.daplab.nosql.cassandra.doodle.services.PollService
import ch.daplab.nosql.cassandra.doodle.services.impl.cassandra.CassandraPollServiceImpl
import ch.daplab.nosql.cassandra.doodle.services.impl.dummy.DummyPollServiceImpl
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import spark.Spark.*

/**
 * PollsController class will expose a series of RESTfull endpoints
 */
object PollApp {

    private val logger = LoggerFactory.getLogger(PollApp::class.java!!)

    private val mapper = ObjectMapper()

    @JvmStatic fun main(args: Array<String>) {

        // TODO: set your own implementation here
//        val pollService = DummyPollServiceImpl()
        val pollService = CassandraPollServiceImpl()

        port(8080)

        get("/rest/polls/:pollId") { request, response ->

            val pollId = request.params(":pollId")

            var poll: Poll? = null

            /* validate poll Id parameter */
            if (isEmpty(pollId) || pollId.length < 5) {
                val sMessage = "Error invoking getPoll - Invalid poll Id parameter"
                throw IllegalStateException(sMessage)
            }

            try {
                poll = pollService.getPollById(pollId)
            } catch (e: Exception) {
                val sMessage = "Error invoking getPoll. [%1\$s]"
                throw IllegalStateException(String.format(sMessage, e.toString()))
            }

            logger.debug("Returing Poll: " + poll!!.toString())

            mapper.writeValueAsBytes(poll)
        }


        get("/rest/polls") { request, response ->

            try {
                val polls = pollService.allPolls
                return@get mapper.writeValueAsBytes(polls)
            } catch (e: Exception) {
                val sMessage = "Error invoking getPolls. [%1\$s]"
                throw IllegalStateException(String.format(sMessage, e.toString()))
            }
        }

        post("/rest/polls") { request, response ->

            var createdPoll: Poll

            try {
                createdPoll = mapper.readValue<Poll>(request.bodyAsBytes(), Poll::class.java!!)
                createdPoll = pollService.createPoll(createdPoll)
            } catch (e: Exception) {
                val sMessage = "Error creating new poll. [%1\$s]"
                throw IllegalStateException(String.format(sMessage, e.toString()))
            }

            response.status(201)
            response.header("Location", "/rest/polls/" + createdPoll.id)

            mapper.writeValueAsString(createdPoll)
        }

        put("/rest/polls/:pollId") { request, response ->

            val pollId = request.params(":pollId")

            /* validate poll Id parameter */
            if (isEmpty(pollId) || pollId.length < 5) {
                val sMessage = "Error updating poll - Invalid poll Id parameter"
                throw IllegalStateException(sMessage)
            }

            val createdSubscriber: Subscriber
            val updatedPoll: Poll

            try {
                createdSubscriber = mapper.readValue<Subscriber>(request.bodyAsBytes(), Subscriber::class.java!!)

                updatedPoll = pollService.addSubscriber(pollId, createdSubscriber)
            } catch (e: Exception) {
                val sMessage = "Error creating new poll. [%1\$s]"
                throw IllegalStateException(String.format(sMessage, e.toString()))
            }

            response.status(201)
            response.header("Location", "/rest/polls/" + updatedPoll.id)

            mapper.writeValueAsString(updatedPoll)

        }

        delete("/rest/polls/:pollId") { request, response ->

            val pollId = request.params(":pollId")

            /* validate poll Id parameter */
            if (isEmpty(pollId) || pollId.length < 5) {
                val sMessage = "Error invoking getPoll - Invalid poll Id parameter"
                throw IllegalStateException(sMessage)
            }

            try {
                pollService.deletePoll(pollId)
            } catch (e: Exception) {
                val sMessage = "Error invoking getPoll. [%1\$s]"
                throw IllegalStateException(String.format(sMessage, e.toString()))
            }

            "ok"
        }

        exception(Exception::class.java) { exception, request, response ->
            logger.error("Got an exception", exception)
            response.body("{\"error\": \"" + exception.message + "\"}")
            response.status(500)
        }

    }

    fun isEmpty(s_p: String?): Boolean {
        return null == s_p || s_p.trim { it <= ' ' }.length == 0
    }

}
