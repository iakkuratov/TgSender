package net.ddns.pzshare;

import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SuppressWarnings("WeakerAccess")
public class HttpReceiver {
    private static final Logger log = LogManager.getLogger();
    private HttpServer server;

    public HttpReceiver(Integer port, MsgSender sender) throws IOException {
        server = HttpServer.create();

        server.bind(new InetSocketAddress(port), 0);

        server.createContext("/sendMsg", (http) -> {
            String query = http.getRequestURI().getQuery();

            log.debug("Get request: " + query);

            int resultCode = 200;

            String response = "";

            try {
                Map<String, String> params = parseQuery(query);

                String receiverId = params.get("chatId");

                String text = params.get("text");

                if (receiverId == null | text == null)
                    throw new NullPointerException("chatId or text argument is empty or both");

                sender.send(Long.parseLong(receiverId), text);

            } catch (ParseException | NullPointerException | NumberFormatException ex) {
                resultCode = 400;

                response = ex.getMessage();

                log.info("Failed to parse parameters: " + ex.getMessage());
            } catch (SendException ex) {
                resultCode = 500;

                response = "Sender exception: " + ex.getMessage();

                log.info("Internal error: " + ex.getMessage());
            }

            log.info("Processed request: " + http.getRequestURI()
                + " with result: " + resultCode
                + " and response: " + response
            );

            http.sendResponseHeaders(resultCode, response.length());

            OutputStream os = http.getResponseBody();

            os.write(response.getBytes());

            os.close();
        });

        log.info("Starting http server on port: " + port);

        server.start();
    }

    private static Map<String, String> parseQuery(String query) throws ParseException {
        Map<String, String> result = new HashMap<>();

        for (String param : query.split("&")) {
            String[] kv = param.split("=");

            if (kv.length == 2)
                result.put(kv[0], kv[1]);
            else
                throw new ParseException("Failed to read param: " + param, 0);
        }

        return result;
    }

    public void stop() {
        log.info("Stopping http server");

        server.stop(1);
    }
}
