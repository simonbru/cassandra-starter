package ch.daplab.nosql.cassandra;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Session;
import org.junit.Assert;

public class TestCassandra {

    @org.junit.Test
    public void test1() {

        Cluster.Builder clusterBuilder = new Cluster.Builder().addContactPoint("localhost")
//                .withPort(9042)
                .withClusterName("test1");

        Cluster cluster = clusterBuilder.build();

        cluster.getMetadata().getKeyspaces().forEach(System.out::println);


        Session session = cluster.connect();                                           // (2)

        ResultSet rs = session.execute(
            "CREATE KEYSPACE doodle WITH REPLICATION = { 'class' : 'org.apache.cassandra.locator.SimpleStrategy', 'replication_factor': '1' }"
        );

        Assert.assertTrue(rs.wasApplied());


/*
        CREATE TABLE doodle.tables (
                keyspace_name text,
                table_name text,
                bloom_filter_fp_chance double,
        caching frozen<map<text, text>>,
        comment text,
        compaction frozen<map<text, text>>,
        compression frozen<map<text, text>>,
        crc_check_chance double,
        dclocal_read_repair_chance double,
        default_time_to_live int,
        extensions frozen<map<text, blob>>,
        flags frozen<set<text>>,
        gc_grace_seconds int,
        id uuid,
        max_index_interval int,
        memtable_flush_period_in_ms int,
        min_index_interval int,
        read_repair_chance double,
        speculative_retry text,
        PRIMARY KEY (keyspace_name, table_name)
) WITH CLUSTERING ORDER BY (table_name ASC)
        AND bloom_filter_fp_chance = 0.01
        AND caching = {'keys': 'ALL', 'rows_per_partition': 'NONE'}
        AND comment = 'table definitions'
        AND compaction = {'class': 'org.apache.cassandra.db.compaction.SizeTieredCompactionStrategy', 'max_threshold': '32', 'min_threshold': '4'}
        AND compression = {'chunk_length_in_kb': '64', 'class': 'org.apache.cassandra.io.compress.LZ4Compressor'}
        AND crc_check_chance = 1.0
        AND dclocal_read_repair_chance = 0.0
        AND default_time_to_live = 0
        AND gc_grace_seconds = 604800
        AND max_index_interval = 2048
        AND memtable_flush_period_in_ms = 3600000
        AND min_index_interval = 128
        AND read_repair_chance = 0.0
        AND speculative_retry = '99PERCENTILE';

        */
    }
}
