package net.ddns.pzshare;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

import java.io.IOException;
import java.util.Map;

@SuppressWarnings("FieldCanBeLocal")
public class Application {
    private static final Logger log = LogManager.getLogger();

    private static String ENV_TG_PORT = "TGPORT";
    private static String ENV_TG_TOKEN = "TGTOKEN";
    private static String ENV_TG_NAME = "TGNAME";
    private static Integer DEFAULT_PORT = 8080;

    public static void main(String[] args) throws Exception {
        Map<String, String> params = System.getenv();

        int port = params.containsKey(ENV_TG_PORT) ? Integer.valueOf(params.get(ENV_TG_PORT)) : DEFAULT_PORT;

        String name = params.get(ENV_TG_NAME);
        String token = params.get(ENV_TG_TOKEN);

        if (name == null | token ==null)
            throw new Exception("TGNAME and TGTOKEN environment variables both should be specified");

        MsgSender sender = startBot(name, token);

        new HttpReceiver(port, sender);
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


}
