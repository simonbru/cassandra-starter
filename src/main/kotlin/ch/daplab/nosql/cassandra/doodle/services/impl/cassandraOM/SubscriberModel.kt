package ch.daplab.nosql.cassandra.doodle.services.impl.cassandraOM

import ch.daplab.nosql.cassandra.doodle.domains.Subscriber
import com.datastax.driver.mapping.annotations.UDT

/**
 * Created by simon on 26.10.16.
 */
@UDT(keyspace = "doodle", name = "subscriber")
class SubscriberModel() {
    var label: String? = null
    var choices: List<String>? = emptyList()

    fun toSubscriber() = Subscriber(label, choices)
}