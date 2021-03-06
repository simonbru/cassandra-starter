package ch.daplab.nosql.cassandra.doodle.domains

class Poll(
    var id: String? = null,
    var label: String? = null,
    var choices: List<String> = emptyList(),
    var email: String? = null,
    var maxChoices: Int? = null,
    var subscribers: MutableList<Subscriber> = mutableListOf()
) {
    override fun toString(): String {
        return "Poll [id=$id, label=$label, " +
                "choices=$choices, email=$email, " +
                "subscribers=$subscribers]"
    }
}
