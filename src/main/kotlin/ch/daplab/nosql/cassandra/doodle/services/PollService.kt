package ch.daplab.nosql.cassandra.doodle.services

import ch.daplab.nosql.cassandra.doodle.domains.Poll
import ch.daplab.nosql.cassandra.doodle.domains.Subscriber
import ch.daplab.nosql.cassandra.doodle.domains.impl.DataPoll
import ch.daplab.nosql.cassandra.doodle.domains.impl.DataSubscriber


interface PollService {

    fun getPollById(pollId: String): Poll?

    val allDataPolls: List<Poll>

    fun createPoll(poll: Poll): Poll

    fun addSubscriber(pollId: String, subscriber: Subscriber): Poll

    fun deletePoll(pollId: String)

}
