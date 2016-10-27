package ch.daplab.nosql.cassandra.doodle.domains.impl

import ch.daplab.nosql.cassandra.doodle.domains.Subscriber
import com.google.common.eventbus.Subscribe

data class DataSubscriber (
        override var label: String? = null,
        override var choices: List<String>? = null
) : Subscriber

