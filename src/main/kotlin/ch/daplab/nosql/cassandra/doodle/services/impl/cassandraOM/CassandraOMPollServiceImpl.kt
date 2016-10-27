package ch.daplab.nosql.cassandra.doodle.services.impl.cassandraOM

import ch.daplab.nosql.cassandra.doodle.domains.Poll
import ch.daplab.nosql.cassandra.doodle.domains.Subscriber
import ch.daplab.nosql.cassandra.doodle.services.PollService
import ch.daplab.nosql.cassandra.doodle.services.impl.cassandra.CassandraPollServiceImpl
import com.datastax.driver.core.Session
import com.datastax.driver.mapping.Mapper
import com.datastax.driver.mapping.MappingManager
import com.datastax.driver.mapping.Result
import com.datastax.driver.mapping.annotations.Accessor
import com.datastax.driver.mapping.annotations.Param
import com.datastax.driver.mapping.annotations.Query
import java.util.*

class CassandraOMPollServiceImpl(val session: Session, autoCreateSchema: Boolean = true) : PollService {

    @Accessor
    interface PollAccessor {
        @Query("SELECT * FROM doodle.polls")
        fun getAll(): Result<PollModel>

        @Query("UPDATE doodle.polls SET subscribers = subscribers + :sub WHERE id = :id")
        fun addSubscriber(
                @Param("id") id: UUID,
                @Param("sub") subscriber: List<SubscriberModel>
        )
    }

    val manager: MappingManager
    val pollMapper: Mapper<PollModel>
    val pollAccessor: PollAccessor

    init {
        if (autoCreateSchema) createSchema()
        manager = MappingManager(session)
        pollMapper = manager.mapper(PollModel::class.java)
        pollAccessor = manager.createAccessor(PollAccessor::class.java)
    }

    override fun getPollById(pollId: String): Poll? {
        return pollMapper.get(pollId.toUUID())?.toPoll()
    }

    override fun createPoll(poll: Poll): Poll {
        val model = PollModel().apply {
            id = UUID.randomUUID()
            label = poll.label
            choices = poll.choices
            email = poll.email
            maxChoices = poll.maxChoices
        }
        pollMapper.save(model)
        return getPollById(model.id.toString())!!
    }

    override fun addSubscriber(pollId: String, subscriber: Subscriber): Poll {
        val sub = SubscriberModel().apply {
            label = subscriber.label
            choices = subscriber.choices ?: emptyList()
        }
        pollAccessor.addSubscriber(pollId.toUUID(), listOf(sub))
        return getPollById(pollId)!!
    }

    override fun deletePoll(pollId: String) {
        val model = PollModel().apply { id = pollId.toUUID() }
        pollMapper.delete(model)
    }

    override val allPolls: List<Poll>
        get() = pollAccessor.getAll().map { it.toPoll() }


    fun createSchema() {
        // TODO: Fix this
        val impl = CassandraPollServiceImpl(session, autoCreateSchema = false)
        impl.createSchema()
    }
}

private fun String.toUUID() = UUID.fromString(this)
