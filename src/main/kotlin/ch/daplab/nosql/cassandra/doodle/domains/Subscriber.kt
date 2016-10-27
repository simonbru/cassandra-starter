package ch.daplab.nosql.cassandra.doodle.domains

interface Subscriber {
    var label: String?
    var choices: List<String>?
}