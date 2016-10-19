package ch.daplab.nosql.cassandra.doodle.services;

import ch.daplab.nosql.cassandra.doodle.domains.Poll;
import ch.daplab.nosql.cassandra.doodle.domains.Subscriber;

import java.util.List;


public interface PollService {

	public Poll getPollById(String pollId);

	public List<Poll> getAllPolls();

	public Poll createPoll(Poll poll);

	public Poll addSubscriber(String pollId, Subscriber subscriber);

	public void deletePoll(String pollId);
	
}
