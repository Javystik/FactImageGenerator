package com.zoi4erom.factimagegen;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class TelegramBot extends TelegramLongPollingBot {

	private BotConfig botConfig;
	@Autowired
	public TelegramBot(BotConfig botConfig) {
		this.botConfig = botConfig;
	}

	@Override
	public String getBotUsername() {
		return botConfig.getBotName();
	}

	@Override
	public String getBotToken() {
		return botConfig.getToken();
	}

	private boolean waitingForNextMessage = false;
	private String lastChatId = null;

	@Override
	public void onUpdateReceived(Update update) {
		if (update.hasMessage() && update.getMessage().hasText()) {
			String chatId = update.getMessage().getChatId().toString();
			String text = update.getMessage().getText();
			String userName = update.getMessage().getFrom().getUserName();

			if (!text.isBlank()) {
				if (!waitingForNextMessage) {
					sendMessage(chatId,
					    "Привіт " + userName + " для початку роботи надішліть "
						  + "факт та ключове слово(факт:ключове слово)");
					waitingForNextMessage = true;
					lastChatId = chatId;
				} else if (chatId.equals(lastChatId)) {
					processNextMessage(chatId, text);
					waitingForNextMessage = false;
					lastChatId = null;
				}
			}
		}
	}

	private void processNextMessage(String chatId, String nextMessage) {
		String[] parts = nextMessage.split(":");

		if (parts.length != 2) {
			sendMessage(chatId, "Неправильний формат повідомлення. Будь ласка, використовуйте формат: факт:ключове_слово");
			return;
		}

		// Отримуємо факт та ключове слово
		String fact = parts[0].trim();
		String keyword = parts[1].trim();

		sendMessage(chatId, "Отримано факт: " + fact);
		sendMessage(chatId, "Отримано ключове слово: " + keyword);

		Parser parser = new Parser();
		parser.start(keyword);

		var bufferedImage = ImageWork.darkenAndAddText(fact);

		sendImage(chatId, bufferedImage);
	}



	private void sendMessage(String chatId, String textToSend) {
		SendMessage sendMessage = new SendMessage();
		sendMessage.setChatId(chatId);
		sendMessage.setText(textToSend);
		try {
			execute(sendMessage);
		} catch (TelegramApiException e) {
			e.printStackTrace();
		}
	}

	private void sendImage(String chatId, BufferedImage image) {
		if (image == null) {
			sendMessage(chatId, "На жаль, сталася помилка при створенні зображення.");
			return;
		}

		try {
			File outputfile = new File("tempImage.png");
			ImageIO.write(image, "png", outputfile);

			SendPhoto sendPhoto = new SendPhoto();
			sendPhoto.setChatId(chatId);
			sendPhoto.setPhoto(new InputFile(outputfile));
			execute(sendPhoto);

			outputfile.delete();
		} catch (TelegramApiException | IOException e) {
			e.printStackTrace();
		}
	}
}
