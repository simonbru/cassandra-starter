package ch.daplab.nosql.cassandra.doodle.domains

class Subscriber {

    var label: String? = null
    var choices: List<String>? = null

    override fun toString(): String {
        return "Subscriber [label=$label, choices=$choices]"
    }
}
