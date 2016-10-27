package ch.daplab.nosql.cassandra.doodle.services.impl

import com.datastax.driver.core.Cluster
import com.datastax.driver.core.Session
import java.util.*

object CassandraHelpers {

    fun getLocalhostConnection(): Session {
        val clusterBuilder = Cluster.Builder()
                .addContactPoint("localhost")
                .withClusterName("pollservice")
        //.withPort(9042)
        val cluster = clusterBuilder.build()
        return cluster.connect()
    }

    fun createSchema(session: Session) {
        with (session) {
            // The keyspace should probably be created elsewhere...
            execute("""
                CREATE KEYSPACE IF NOT EXISTS doodle
                    WITH replication = {'class': 'org.apache.cassandra.locator.SimpleStrategy',
                        'replication_factor': 3 }
            """)
            execute("""
                CREATE TYPE IF NOT EXISTS doodle.subscriber (
                    label text,
                    choices list<text>
                )
            """)
            execute("""
                CREATE TABLE IF NOT EXISTS doodle.polls (
                    id UUID PRIMARY KEY,
                    label text,
                    choices list<text>,
                    email text,
                    maxChoices int,
                    subscribers list<FROZEN <subscriber>>
                )
            """)
        }
    }

    fun String.toUUID() = UUID.fromString(this)
}