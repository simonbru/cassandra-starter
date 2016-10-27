package ch.daplab.nosql.cassandra.doodle.services.impl.dummy


import ch.daplab.nosql.cassandra.doodle.domains.Poll
import ch.daplab.nosql.cassandra.doodle.domains.Subscriber
import ch.daplab.nosql.cassandra.doodle.services.PollService
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class DummyPollServiceImpl : PollService {

    private val polls = ConcurrentHashMap<String, Poll>()

    override fun getPollById(pollId: String): Poll? {
        return polls[pollId]
    }

    override val allPolls: List<Poll>
        get() = polls.values.toList()

    override fun createPoll(poll: Poll): Poll {
        val uuid = UUID.randomUUID().toString()
        polls.put(uuid, poll)
        poll.id = uuid
        return poll
    }

    override fun addSubscriber(pollId: String, subscriber: Subscriber): Poll {
        val poll = polls[pollId]!!
        poll.subscribers.add(subscriber)
        return poll
    }

    override fun deletePoll(pollId: String) {
        polls.remove(pollId)
    }
}
