package net.ddns.pzshare;

import java.io.IOException;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class BaseTest {
    private static final Logger log = LogManager.getLogger();
    private TestMsgSender sender;
    private HttpReceiver server;
    private String host = "localhost";
    private Integer port = 8080;
    private String baseUrl = "http://" + host + ":" + port;

    @Before
    public void beforeTest() throws IOException {
        sender = new TestMsgSender();

        server = new HttpReceiver(port, sender);
    }

    @After
    public void afterTest(){
        server.stop();
    }

    @Test
    public void testSendGoodRequest() throws IOException {
        assertEquals(200, sendGet(baseUrl + "/sendMsg?chatId=1&text=test"));
    }

    @Test
    public void testBadUrl() throws IOException {
        assertEquals(404, sendGet(baseUrl + "/sendBadMsg?chatId=1&text=test"));
    }

    @Test
    public void testBadParams() throws IOException {
        assertEquals(400, sendGet(baseUrl + "/sendMsg?chatId=1"));
    }

    @Test
    public void testSendFail() throws IOException {
        sender.riseException(true);

        assertEquals(500, sendGet(baseUrl + "/sendMsg?chatId=1&text=test"));

        sender.riseException(false);
    }

    private int sendGet(String url) throws IOException {
        log.debug("Trying to request: " + url);

        @SuppressWarnings("deprecation")
        HttpClient client = new DefaultHttpClient();

        HttpGet request = new HttpGet(url);

        HttpResponse response = client.execute(request);

        log.debug("Receiving response: " + response);

        return response.getStatusLine().getStatusCode();
    }
}
