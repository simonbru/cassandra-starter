package ch.daplab.nosql.cassandra.doodle

import ch.daplab.nosql.cassandra.doodle.domains.impl.DataPoll
import ch.daplab.nosql.cassandra.doodle.domains.impl.DataSubscriber
import ch.daplab.nosql.cassandra.doodle.services.impl.cassandraOM.CassandraOMPollServiceImpl
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import org.slf4j.LoggerFactory
import spark.Spark.*

/**
 * PollsController class will expose a series of RESTfull endpoints
 */
object PollApp {

    private val logger = LoggerFactory.getLogger(PollApp::class.java)

    private val mapper = ObjectMapper()

    @JvmStatic fun main(args: Array<String>) {

        // TODO: set your own implementation here
//        val pollService = DummyPollServiceImpl()
        val cassandraSession = CassandraConnectionProvider.getLocalhostConnection()
//        val pollService = CassandraPollServiceImpl(cassandraSession)
        val pollService = CassandraOMPollServiceImpl(cassandraSession)

        port(8080)

        get("/rest/polls/:pollId") { request, response ->

            val pollId = request.params(":pollId")

            /* validate dataPoll Id parameter */
            if (isEmpty(pollId) || pollId.length < 5) {
                val sMessage = "Error invoking getPoll - Invalid dataPoll Id parameter"
                throw IllegalStateException(sMessage)
            }

            val dataPoll: DataPoll?
            try {
                dataPoll = pollService.getPollById(pollId)
            } catch (e: Exception) {
                throw IllegalStateException("Error invoking getPoll. [$e]")
            }

            logger.debug("Returning DataPoll: " + dataPoll?.toString())

            if (dataPoll == null) {
                response.status(404)
                ""
            } else {
                mapper.writeValueAsBytes(dataPoll)
            }
        }


        get("/rest/polls") { request, response ->
            try {
                val polls = pollService.allDataPolls
                return@get mapper.writeValueAsBytes(polls)
            } catch (e: Exception) {
                throw IllegalStateException("Error invoking getPolls. [$e]")
            }
        }

        post("/rest/polls") { request, response ->

            val createdDataPoll: DataPoll
            try {
                val receivedPoll = mapper.readValue<DataPoll>(request.bodyAsBytes(), DataPoll::class.java)
                createdDataPoll = pollService.createPoll(receivedPoll)
            } catch (e: Exception) {
                throw IllegalStateException("Error creating new poll. [$e]")
            }

            response.status(201)
            response.header("Location", "/rest/polls/" + createdDataPoll.id)

            mapper.writeValueAsString(createdDataPoll)
        }

        put("/rest/polls/:pollId") { request, response ->

            val pollId = request.params(":pollId")

            /* validate poll Id parameter */
            if (isEmpty(pollId) || pollId.length < 5) {
                val sMessage = "Error updating poll - Invalid poll Id parameter"
                throw IllegalStateException(sMessage)
            }

            val updatedDataPoll: DataPoll
            try {
                val createdSubscriber = mapper.readValue<DataSubscriber>(request.bodyAsBytes(), DataSubscriber::class.java)

                updatedDataPoll = pollService.addSubscriber(pollId, createdSubscriber)
            } catch (e: Exception) {
                throw IllegalStateException("Error creating new poll. [$e]")
            }

            response.status(201)
            response.header("Location", "/rest/polls/" + updatedDataPoll.id)

            mapper.writeValueAsString(updatedDataPoll)
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
                throw IllegalStateException("Error invoking getPoll. [$e]")
            }

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

    fun isEmpty(s_p: String?): Boolean {
        return null == s_p || s_p.trim().length == 0
    }

}
