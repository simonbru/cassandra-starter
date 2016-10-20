package ch.daplab.nosql.cassandra.doodle;

import ch.daplab.nosql.cassandra.doodle.domains.Poll;
import ch.daplab.nosql.cassandra.doodle.domains.Subscriber;
import ch.daplab.nosql.cassandra.doodle.services.PollService;
import ch.daplab.nosql.cassandra.doodle.services.impl.dummy.DummyPollServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;

import static spark.Spark.*;

/**
 * PollsController class will expose a series of RESTfull endpoints
 */
public class PollApp {

	private static final Logger logger = LoggerFactory.getLogger(PollApp.class);

	private static final ObjectMapper mapper = new ObjectMapper();

	public static void main(String args[]) {

		// TODO: set your own implementation here
		PollService pollService = new DummyPollServiceImpl();

        port(8080);

		get("/rest/polls/:pollId", (request, response) -> {

			String pollId = request.params(":pollId");

			Poll poll = null;

			/* validate poll Id parameter */
			if (isEmpty(pollId) || pollId.length() < 5) {
				String sMessage = "Error invoking getPoll - Invalid poll Id parameter";
				throw new IllegalStateException(sMessage);
			}

			try {
				poll = pollService.getPollById(pollId);
			} catch (Exception e) {
				String sMessage = "Error invoking getPoll. [%1$s]";
				throw new IllegalStateException(String.format(sMessage, e.toString()));
			}

			logger.debug("Returing Poll: " + poll.toString());

			return mapper.writeValueAsBytes(poll);
		});


		get("/rest/polls", (request, response) -> {

			try {
				List<Poll> polls = pollService.getAllPolls();
                return mapper.writeValueAsBytes(polls);
            } catch (Exception e) {
				String sMessage = "Error invoking getPolls. [%1$s]";
				throw new IllegalStateException(String.format(sMessage, e.toString()));
			}
		});

        post("/rest/polls", (request, response) -> {

            Poll createdPoll;

            try {
                createdPoll = mapper.readValue(request.bodyAsBytes(), Poll.class);
                createdPoll = pollService.createPoll(createdPoll);
            } catch (Exception e) {
                String sMessage = "Error creating new poll. [%1$s]";
                throw new IllegalStateException(String.format(sMessage, e.toString()));
            }

            response.status(201);
            response.header("Location", "/rest/polls/" + createdPoll.getId());

            return mapper.writeValueAsString(createdPoll);
        });

        put("/rest/polls/:pollId", (request, response) -> {

            String pollId = request.params(":pollId");

            /* validate poll Id parameter */
            if (isEmpty(pollId) || pollId.length() < 5) {
                String sMessage = "Error updating poll - Invalid poll Id parameter";
                throw new IllegalStateException(sMessage);
            }

            Subscriber createdSubscriber;
            Poll updatedPoll;

            try {
                createdSubscriber = mapper.readValue(request.bodyAsBytes(), Subscriber.class);

                updatedPoll = pollService.addSubscriber(pollId, createdSubscriber);
            } catch (Exception e) {
                String sMessage = "Error creating new poll. [%1$s]";
                throw new IllegalStateException(String.format(sMessage, e.toString()));
            }

            response.status(201);
            response.header("Location", "/rest/polls/" + updatedPoll.getId());

            return mapper.writeValueAsString(updatedPoll);

        });

        delete("/rest/polls/:pollId", (request, response) -> {

            String pollId = request.params(":pollId");

			/* validate poll Id parameter */
            if (isEmpty(pollId) || pollId.length() < 5) {
                String sMessage = "Error invoking getPoll - Invalid poll Id parameter";
                throw new IllegalStateException(sMessage);
            }

            try {
                pollService.deletePoll(pollId);
            } catch (Exception e) {
                String sMessage = "Error invoking getPoll. [%1$s]";
                throw new IllegalStateException(String.format(sMessage, e.toString()));
            }

            return "ok";
        });

		exception(Exception.class, (exception, request, response) -> {
            logger.error("Got an exception", exception);
			response.body("{\"error\": \"" + exception.getMessage() + "\"}");
			response.status(500);
		});

	}

	public static boolean isEmpty(String s_p) {
		return (null == s_p) || s_p.trim().length() == 0;
	}

}
