package ch.daplab.nosql.cassandra.doodle

import com.datastax.driver.core.Cluster
import com.datastax.driver.core.Session

object CassandraConnectionProvider {

    fun getLocalhostConnection(): Session {
        val clusterBuilder = Cluster.Builder()
                .addContactPoint("localhost")
                .withClusterName("pollservice")
        //.withPort(9042)
        val cluster = clusterBuilder.build()
        return cluster.connect()
    }
}