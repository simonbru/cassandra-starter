package ch.daplab.nosql.cassandra.doodle.services.impl.cassandra

import ch.daplab.nosql.cassandra.doodle.domains.Poll
import ch.daplab.nosql.cassandra.doodle.domains.Subscriber
import ch.daplab.nosql.cassandra.doodle.services.PollService

class CassandraPollServiceImpl : PollService {
    override fun getPollById(pollId: String): Poll? {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun createPoll(poll: Poll): Poll {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun addSubscriber(pollId: String, subscriber: Subscriber): Poll {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun deletePoll(pollId: String) {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override val allPolls: List<Poll>
        get() = throw UnsupportedOperationException()
}