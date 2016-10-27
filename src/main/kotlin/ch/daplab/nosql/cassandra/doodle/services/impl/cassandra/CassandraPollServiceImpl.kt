package ch.daplab.nosql.cassandra.doodle.services.impl.cassandra

import ch.daplab.nosql.cassandra.doodle.services.impl.CassandraHelpers
import ch.daplab.nosql.cassandra.doodle.services.impl.CassandraHelpers.toUUID
import ch.daplab.nosql.cassandra.doodle.domains.Poll
import ch.daplab.nosql.cassandra.doodle.domains.Subscriber
import ch.daplab.nosql.cassandra.doodle.services.PollService
import com.datastax.driver.core.Row
import com.datastax.driver.core.Session
import com.datastax.driver.core.UDTValue
import java.util.*


class CassandraPollServiceImpl(val session: Session, autoCreateSchema: Boolean = true) : PollService {

    val subscriberUDT = session.cluster.metadata.getKeyspace("doodle").getUserType("subscriber")

    init {
        if (autoCreateSchema) CassandraHelpers.createSchema(session)
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

internal fun rowToPoll(row: Row): Poll {
    return Poll().apply {
        id = row.getUUID("id").toString()
        label = row.getString("label")
        choices = row.getList("choices", String::class.java)
        email = row.getString("email")
        maxChoices = row.getInt("maxChoices")
        subscribers = row.getList("subscribers", UDTValue::class.java).map {
            Subscriber(
                    label = it.getString("label"),
                    choices = it.getList("choices", String::class.java)
            )
        }.toMutableList()  // TODO: no mutable ?
    }
}