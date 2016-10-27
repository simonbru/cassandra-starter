package ch.daplab.nosql.cassandra.doodle.domains.impl

import ch.daplab.nosql.cassandra.doodle.domains.Poll
import ch.daplab.nosql.cassandra.doodle.domains.Subscriber

data class DataPoll (
        override var id: String? = null,
        override var label: String? = null,
        override var choices: List<String> = emptyList(),
        override var email: String? = null,
        override var maxChoices: Int? = null,
        override var subscribers: MutableList<DataSubscriber>? = null
) : Poll<DataSubscriber>
