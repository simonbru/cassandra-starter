package ch.daplab.nosql.cassandra.doodle.services.impl.dummy

import java.util.*
import java.util.concurrent.ConcurrentHashMap

import ch.daplab.nosql.cassandra.doodle.domains.impl.DataPoll
import ch.daplab.nosql.cassandra.doodle.domains.impl.DataSubscriber
import ch.daplab.nosql.cassandra.doodle.services.PollService
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class DummyPollServiceImpl : PollService {

    private val polls = ConcurrentHashMap<String, DataPoll>()


    override fun getPollById(pollId: String): DataPoll? {
        return polls[pollId]
    }

    override val allDataPolls: List<DataPoll>
        get() = ArrayList(polls.values)

    override fun createPoll(dataPoll: DataPoll): DataPoll {
        val uuid = UUID.randomUUID().toString()
        polls.put(uuid, dataPoll)
        dataPoll.id = uuid
        if (dataPoll.subscribers == null) {
            dataPoll.subscribers = LinkedList<DataSubscriber>()
        }
        return dataPoll
    }

    override fun addSubscriber(pollId: String, dataSubscriber: DataSubscriber): DataPoll {
        val poll = polls[pollId]
        poll!!.subscribers!!.add(dataSubscriber)
        return poll
    }

    override fun deletePoll(pollId: String) {
        polls.remove(pollId)
    }

    companion object {

        private val logger_c = LoggerFactory.getLogger(DummyPollServiceImpl::class.java!!)
    }

}
