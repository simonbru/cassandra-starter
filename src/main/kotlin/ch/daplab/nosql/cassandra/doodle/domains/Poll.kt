package ch.daplab.nosql.cassandra.doodle.domains

import ch.daplab.nosql.cassandra.doodle.domains.impl.DataSubscriber
import ch.daplab.nosql.cassandra.doodle.domains.Subscriber

interface Poll <T : Subscriber>  {
    var id: String?
    var label: String?
    var choices: List<String>
    var email: String?
    var maxChoices: Int?
    var subscribers: MutableList<T>?
}
