package com.zoi4erom.factimagegen;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Random;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Parser {

	@Value("${pixabay.api.key}")
	private String apiKey;
	public void start(String key) {
		String searchUrl = "https://pixabay.com/api/?key=" + apiKey + "&q=+ " + key + "&image_type=photo";

		String folderPath = "C:\\Users\\user\\IdeaProjects\\FactImageGenerator\\src\\main\\resources\\";

		try {
			// Кидаєм запит HTTP
			Document doc = Jsoup.connect(searchUrl).ignoreContentType(true).get();

			// Отримуємо JSON відповідь
			JSONObject jsonResponse = new JSONObject(doc.text());

			// Отримуємо масив хітів
			JSONArray hits = jsonResponse.getJSONArray("hits");

			// Отримуємо загальну кількість хітів
			int totalHits = hits.length();
			System.out.println("totalHits: " + totalHits);

			// Вибираємо випадковий індекс
			int randomIndex = getRandomIndex(totalHits);

			System.out.println("RandomIndex: " + randomIndex);

			// Вибираємо випадковий хіт з відповіді
			JSONObject randomHit = hits.getJSONObject(randomIndex);

			// Отримуємо URL картинки
			String imageURL = randomHit.getString("webformatURL");

			// Завантажуємо картинку за URL
			downloadImage(imageURL, folderPath + "userImage.jpg");

			System.out.println("Картинка завантажена успішно!");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Метод для завантаження картинки за URL
	private static void downloadImage(String imageURL, String destinationFile) throws IOException {
		URL url = new URL(imageURL);
		try (InputStream in = url.openStream();
		    FileOutputStream out = new FileOutputStream(destinationFile)) {
			byte[] buffer = new byte[4096];
			int bytesRead;
			while ((bytesRead = in.read(buffer)) != -1) {
				out.write(buffer, 0, bytesRead);
			}
		}
	}

	//Генерація рандомного індекса
	private static int getRandomIndex(int totalHits) {
		Random random = new Random();
		return random.nextInt(totalHits);
	}
}