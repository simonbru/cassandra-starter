package ch.daplab.nosql.cassandra.doodle.services.impl.cassandraOM

import ch.daplab.nosql.cassandra.doodle.domains.Poll
import ch.daplab.nosql.cassandra.doodle.domains.Subscriber
import com.datastax.driver.mapping.annotations.Column
import com.datastax.driver.mapping.annotations.FrozenValue
import com.datastax.driver.mapping.annotations.PartitionKey
import com.datastax.driver.mapping.annotations.Table
import java.util.*

/**
 * Created by simon on 26.10.16.
 */
@Table(keyspace = "doodle", name = "polls")
class PollModel {

    @PartitionKey
    var id: UUID? = null

    var label: String? = null
    var choices: List<String> = emptyList()
    var email: String? = null
    var maxChoices: Int? = null

    @FrozenValue
    var subscribers: List<SubscriberModel> = emptyList()

    fun toPoll(): Poll {
        val subs = subscribers.map { it.toSubscriber() }.toMutableList()
        return Poll(
                id.toString(), label, choices, email, maxChoices, subs
        )
    }
}