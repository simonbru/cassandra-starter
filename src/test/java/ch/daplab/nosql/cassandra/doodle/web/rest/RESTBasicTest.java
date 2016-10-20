package ch.daplab.nosql.cassandra.doodle.web.rest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ch.daplab.nosql.cassandra.doodle.domains.Poll;
import ch.daplab.nosql.cassandra.doodle.domains.Subscriber;
import com.fasterxml.jackson.databind.ObjectMapper;

import okhttp3.*;
import org.junit.Assert;
import org.junit.Test;


public class RESTBasicTest {

    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    public static final String URL = "http://localhost:8080/rest/polls";

    @Test
    public void createPollTest() throws IOException {
        OkHttpClient okHttpClient = new OkHttpClient();

        Poll poll = new Poll();
        // poll.setId( null ); id is returned by the REST API, not set manually.
        poll.setEmail("email@address.com");

        poll.setLabel("Afterwork");
        poll.setMaxChoices(1);

        @SuppressWarnings("serial")
        List<String> choices = new ArrayList<String>() {{
            add("Monday");
            add("Tuesday");
            add("Friday");
        }};
        poll.setChoices(choices);

        // JSON serialization.
        ObjectMapper mapper = new ObjectMapper();

        RequestBody requestBody = RequestBody.create(JSON, mapper.writeValueAsString(poll));

        Request request = new Request.Builder()
                .url(URL)
                .post(requestBody)
                .build();

        Response response = okHttpClient.newCall(request).execute();

        Assert.assertEquals(201, response.code());
        Assert.assertNotNull(response.header("Location"));
        System.out.println(response.header("Location"));

        Poll receivedPoll = mapper.readValue(response.body().bytes(), Poll.class);
        Assert.assertNotNull(receivedPoll.getId());

        Request request2 = new Request.Builder()
                .url(URL + "/" + receivedPoll.getId())
                .get()
                .build();
        Response response2 = okHttpClient.newCall(request2).execute();
        Assert.assertEquals(200, response2.code());
        Poll receivedPoll2 = mapper.readValue(response2.body().bytes(), Poll.class);

        Assert.assertEquals(receivedPoll.getId(), receivedPoll2.getId());


        Subscriber s = new Subscriber();
        s.setLabel("Benoit");
        s.setChoices(Arrays.asList("Monday", "Tuesday"));

        RequestBody subcriber = RequestBody.create(JSON, mapper.writeValueAsString(s));

        Request request3 = new Request.Builder()
                .url(URL + "/" + receivedPoll.getId())
                .put(subcriber)
                .build();
        Response response3 = okHttpClient.newCall(request3).execute();
        Assert.assertEquals(201, response.code());
        Poll receivedPoll3 = mapper.readValue(response3.body().bytes(), Poll.class);
        Assert.assertEquals(receivedPoll.getId(), receivedPoll3.getId());
        Assert.assertEquals(1, receivedPoll3.getSubscribers().size());

    }

}
