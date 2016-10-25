package ch.daplab.nosql.cassandra.doodle.services.impl.dummy

import java.util.*
import java.util.concurrent.ConcurrentHashMap

import ch.daplab.nosql.cassandra.doodle.domains.Poll
import ch.daplab.nosql.cassandra.doodle.domains.Subscriber
import ch.daplab.nosql.cassandra.doodle.services.PollService
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class DummyPollServiceImpl : PollService {

    private val polls = ConcurrentHashMap<String, Poll>()


    override fun getPollById(pollId: String): Poll? {
        return polls[pollId]
    }

    override val allPolls: List<Poll>
        get() = ArrayList(polls.values)

    override fun createPoll(poll: Poll): Poll {
        val uuid = UUID.randomUUID().toString()
        polls.put(uuid, poll)
        poll.id = uuid
        if (poll.subscribers == null) {
            poll.subscribers = LinkedList<Subscriber>()
        }
        return poll
    }

    override fun addSubscriber(pollId: String, subscriber: Subscriber): Poll {
        val poll = polls[pollId]
        poll!!.subscribers!!.add(subscriber)
        return poll
    }

    override fun deletePoll(pollId: String) {
        polls.remove(pollId)
    }

    companion object {

        private val logger_c = LoggerFactory.getLogger(DummyPollServiceImpl::class.java!!)
    }

}
