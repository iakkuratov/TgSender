package net.ddns.pzshare;

import com.sun.net.httpserver.HttpServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("FieldCanBeLocal")
public class Application {
    private static final Logger log = LogManager.getLogger();

    private static String ENV_TG_PORT = "TGPORT";
    private static String ENV_TG_TOKEN = "TGTOKEN";
    private static String ENV_TG_NAME = "TGNAME";
    private static Integer DEFAULT_PORT = 8080;

    public static void main(String[] args) throws IOException {
        Map<String, String> params = System.getenv();

        int port = params.containsKey(ENV_TG_PORT) ? Integer.getInteger(params.get(ENV_TG_PORT)) : DEFAULT_PORT;

        String name = params.get(ENV_TG_NAME);
        String token = params.get(ENV_TG_TOKEN);

        MsgSender sender = startBot(name, token);

        startHttpServer(port, sender);
    }

    private static MsgSender startBot(String name, String token) {
        ApiContextInitializer.init();

        TgBot sender = new TgBot(token, name, new DefaultBotOptions());

        TelegramBotsApi botsApi = new TelegramBotsApi();

        try {
            botsApi.registerBot(sender);

            log.info("Bot successfully registered.");
        } catch (TelegramApiRequestException ex) {
            log.error("Failed to start bot.", ex);

            System.exit(1);
        }

        return sender;
    }

    private static void startHttpServer(Integer port, MsgSender sender) throws IOException {
        HttpServer server = HttpServer.create();

        server.bind(new InetSocketAddress(port), 0);

        server.createContext("/sendMsg", (http) -> {
            String query = http.getRequestURI().getQuery();

            log.debug("Get request:" + query);

            int resultCode = 200;

            String response = "";

            try {
                Map<String, String> params = parseQuery(query);

                Long receiverId = Long.parseLong(params.get("chatId"));

                String text = params.get("text");

                sender.send(receiverId, text);

            } catch (ParseException | NullPointerException ex) {
                resultCode = 400;

                response = ex.getMessage();
            } catch (SendException ex) {
                resultCode = 500;

                response = "Sender exception:" + ex.getMessage();
            }

            log.debug("Response code:" + resultCode + " with response:" + response);

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
                throw new ParseException("Failed to read param" + param, 0);
        }

        return result;
    }
}
