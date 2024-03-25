package com.zoi4erom.factimagegen;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
@AllArgsConstructor
public class TelegramBot extends TelegramLongPollingBot {

	private BotConfig botConfig;

	@Override
	public String getBotUsername() {
		return botConfig.getBotName();
	}

	@Override
	public String getBotToken() {
		return botConfig.getToken();
	}

	@Override
	public void onUpdateReceived(Update update) {
		if (update.hasMessage() && update.getMessage().hasText()) {
			String chatId = update.getMessage().getChatId().toString();
			String text = update.getMessage().getText();

			if (!text.isBlank()) {

				BufferedImage image = ImageWork.darkenAndAddText(text);

				sendImage(chatId, image);
			}
		}
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
