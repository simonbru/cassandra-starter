package ch.daplab.nosql.cassandra.doodle.services.impl.dummy;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import ch.daplab.nosql.cassandra.doodle.domains.Poll;
import ch.daplab.nosql.cassandra.doodle.domains.Subscriber;
import ch.daplab.nosql.cassandra.doodle.services.PollService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DummyPollServiceImpl implements PollService {

	private static final Logger logger_c = LoggerFactory.getLogger(DummyPollServiceImpl.class);

	private final Map<String, Poll> polls = new ConcurrentHashMap<>();


	@Override
	public Poll getPollById(String pollId) {
		return polls.get(pollId);
	}

	@Override
	public List<Poll> getAllPolls() {
		return new ArrayList<>(polls.values());
	}

	@Override
	public Poll createPoll(Poll poll) {
        String uuid = UUID.randomUUID().toString();
		polls.put(uuid, poll);
        poll.setId(uuid);
		return poll;
	}

	@Override
	public Poll addSubscriber(String pollId, Subscriber subscriber) {
		Poll poll = polls.get(pollId);
		poll.getSubscribers().add(subscriber);
		return poll;
	}

	@Override
	public void deletePoll(String pollId) {
		polls.remove(pollId);
	}

}
