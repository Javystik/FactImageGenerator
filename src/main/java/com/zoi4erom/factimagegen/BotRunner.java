package com.zoi4erom.factimagegen;

import com.zoi4erom.factimagegen.TelegramBot;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Component
public final class BotRunner {

	private final TelegramBot telegramBot;

	@Autowired
	public BotRunner(TelegramBot telegramBot) {
		this.telegramBot = telegramBot;
	}

	@PostConstruct
	public void start() {
		try {
			TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
			telegramBotsApi.registerBot(telegramBot);
		} catch (TelegramApiException e) {
			throw new RuntimeException(e);
		}
	}
}
