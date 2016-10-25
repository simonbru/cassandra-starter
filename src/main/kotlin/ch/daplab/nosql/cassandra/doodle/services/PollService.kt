package ch.daplab.nosql.cassandra.doodle.services

import ch.daplab.nosql.cassandra.doodle.domains.Poll
import ch.daplab.nosql.cassandra.doodle.domains.Subscriber


interface PollService {

    fun getPollById(pollId: String): Poll?

    val allPolls: List<Poll>

    fun createPoll(poll: Poll): Poll

    fun addSubscriber(pollId: String, subscriber: Subscriber): Poll

    fun deletePoll(pollId: String)

}
