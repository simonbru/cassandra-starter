package ch.daplab.nosql.cassandra.doodle.services.impl.cassandraOM

import com.datastax.driver.mapping.annotations.UDT

/**
 * Created by simon on 26.10.16.
 */
@UDT(keyspace = "doodle", name = "subscriber")
class SubscriberModel() {
    var label: String? = null
    var choices: List<String> = emptyList()
}