package ch.daplab.nosql.cassandra.doodle.services.impl.cassandra

import ch.daplab.nosql.cassandra.doodle.domains.Poll
import ch.daplab.nosql.cassandra.doodle.domains.Subscriber
import ch.daplab.nosql.cassandra.doodle.services.PollService
import com.datastax.driver.core.Session
import com.datastax.driver.core.UDTValue
import java.util.*

class CassandraPollServiceImpl(val session: Session, autoCreateSchema: Boolean = true) : PollService {
    init {
        if (autoCreateSchema) createSchema()
    }
    override fun getPollById(pollId: String): Poll? {
        val row = session.execute("SELECT * FROM doodle.polls WHERE id = ?", UUID.fromString(pollId)).one()
        return when (row) {
            null -> null
            else -> {
                val poll = Poll()
                poll.id = row.getUUID("id").toString()
                poll.label = row.getString("label")
                poll.choices = row.getList("choices", String::class.java)
                poll.email = row.getString("email")
                poll.maxChoices = row.getInt("maxChoices")
                poll.subscribers = row.getList("subscribers", UDTValue::class.java).map {
                    Subscriber(
                            label = it.getString("label"),
                            choices = it.getList("choices", String::class.java)
                    )
                }.toMutableList()  // TODO: no mutable ?
                poll
            }
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
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun deletePoll(pollId: String) {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override val allPolls: List<Poll>
        get() = throw UnsupportedOperationException()

    fun createSchema() {
        with (session) {
            // The keyspace should probably be created elsewhere...
            execute("""
                CREATE KEYSPACE IF NOT EXISTS doodle
                    WITH replication = {'class': 'org.apache.cassandra.locator.SimpleStrategy',
                        'replication_factor': 3 }
            """)
            execute("""
                CREATE TYPE IF NOT EXISTS doodle.subscriber (
                    label text,
                    choices list<text>
                )
            """)
            execute("""
                CREATE TABLE IF NOT EXISTS doodle.polls (
                    id UUID PRIMARY KEY,
                    label text,
                    choices list<text>,
                    email text,
                    maxChoices int,
                    subscribers list<FROZEN <subscriber>>
                )
            """)
        }
    }
}