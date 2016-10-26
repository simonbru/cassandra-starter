package ch.daplab.nosql.cassandra.doodle.services.impl.cassandra

import ch.daplab.nosql.cassandra.doodle.domains.Poll
import ch.daplab.nosql.cassandra.doodle.domains.Subscriber
import ch.daplab.nosql.cassandra.doodle.services.PollService
import ch.daplab.nosql.cassandra.doodle.services.impl.cassandraOM.PollModel
import ch.daplab.nosql.cassandra.doodle.services.impl.cassandraOM.SubscriberModel
import com.datastax.driver.core.Row
import com.datastax.driver.core.Session
import com.datastax.driver.core.UDTValue
import com.datastax.driver.core.UserType
import com.datastax.driver.mapping.Mapper
import com.datastax.driver.mapping.MappingManager
import java.util.*

class CassandraOMPollServiceImpl(val session: Session, autoCreateSchema: Boolean = true) : PollService {
    protected val manager: MappingManager
    val pollMapper: Mapper<PollModel>
    val subscriberMapper: Mapper<SubscriberModel>

    init {
        if (autoCreateSchema) createSchema()
        manager = MappingManager(session)
        pollMapper = manager.mapper(PollModel::class.java)
        subscriberMapper = manager.mapper(SubscriberModel::class.java)
    }

    override fun getPollById(pollId: String): Poll? {
        val row = session.execute("SELECT * FROM doodle.polls WHERE id = ?", pollId.toUUID()).one()
        return when (row) {
            null -> null
            else -> rowToPoll(row)
        }
    }

    override fun createPoll(poll: Poll): Poll {
        val uuid = UUID.randomUUID()
        session.execute(
                "INSERT INTO doodle.polls (id, label, choices, email, maxChoices) VALUES (?,?,?,?,?)",
                uuid, poll.label, poll.choices, poll.email, poll.maxChoices
        )
        return getPollById(uuid.toString())!!
    }

    override fun addSubscriber(pollId: String, subscriber: Subscriber): Poll {
        val udtValue = subscriberUDT.newValue().apply {
            setString("label", subscriber.label)
            setList("choices", subscriber.choices)
        }
        session.execute(
                "UPDATE doodle.polls SET subscribers = subscribers + ? WHERE id = ?",
                listOf(udtValue), pollId.toUUID()
        )
        return getPollById(pollId)!!
    }

    override fun deletePoll(pollId: String) {
        session.execute("DELETE FROM doodle.polls WHERE id = ?", pollId.toUUID())
    }

    override val allPolls: List<Poll>
        get() = session.execute("SELECT * FROM doodle.polls").map(::rowToPoll)

}
