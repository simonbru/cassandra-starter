package ch.daplab.nosql.cassandra.doodle.services.impl.cassandraOM

import ch.daplab.nosql.cassandra.doodle.domains.Poll
import ch.daplab.nosql.cassandra.doodle.domains.impl.DataPoll
import ch.daplab.nosql.cassandra.doodle.domains.impl.DataSubscriber
import ch.daplab.nosql.cassandra.doodle.domains.Subscriber
import com.datastax.driver.mapping.annotations.Column
import com.datastax.driver.mapping.annotations.FrozenValue
import com.datastax.driver.mapping.annotations.PartitionKey
import com.datastax.driver.mapping.annotations.Table
import com.datastax.driver.mapping.annotations.Transient
import java.util.*


@Table(keyspace = "doodle", name = "polls")
class PollModel : Poll<SubscriberModel> {

    @PartitionKey
    @Column(name = "id")
    var realId: UUID? = null

    @get:Transient
    override var id: String?
        get() = realId.toString()
        set(strId) {
            realId = UUID.fromString(strId)
        }

    override var label: String? = null
    override var choices: List<String> = emptyList()
    override var email: String? = null
    override var maxChoices: Int? = null

    @FrozenValue
    override var subscribers: MutableList<SubscriberModel>? = mutableListOf()
}