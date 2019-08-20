package net.ddns.pzshare;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class TgBot extends TelegramLongPollingBot implements MsgSender{
    private static final Logger log = LogManager.getLogger();
    private String token;
    private String botName;

    TgBot(String token, String name, DefaultBotOptions botOptions) {
        super(botOptions);
        this.token = token;
        this.botName = name;
    }

    @Override
    public void onUpdateReceived(Update update) {
        /*NO-OP*/
    }

    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public String getBotToken() {
        return token;
    }

    @Override
    public void send(Long chatId, String text) throws SendException {
        try {
            log.debug("Trying to send msg: " + text + " for chatId: " + chatId);

            execute(new SendMessage().setChatId(chatId).setText(text));

        } catch (TelegramApiException ex) {
            log.error(ex);

            throw new SendException(ex.getMessage());
        }
    }
}
