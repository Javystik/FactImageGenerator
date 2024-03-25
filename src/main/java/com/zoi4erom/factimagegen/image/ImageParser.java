package com.zoi4erom.factimagegen.image;

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
public class ImageParser {

	@Value("${pixabay.api.key}")
	private String apiKey;

	// Method for searching and loading an image based on a keyword
	public void searchAndDownloadImage(String keyword) {
		String searchUrl =
		    "https://pixabay.com/api/?key=" + apiKey + "&q=" + keyword + "&image_type=photo";

		String folderPath = "src/main/resources/image";

		try {
			// Performing an HTTP request and receiving a response
			Document doc = Jsoup.connect(searchUrl).ignoreContentType(true).get();
			JSONObject jsonResponse = new JSONObject(doc.text());

			// Obtaining an array of hits
			JSONArray hits = jsonResponse.getJSONArray("hits");

			JSONObject randomHit = hits.getJSONObject(getRandomIndex(hits.length()));

			// Retrieving the URL of the image
			String imageURL = randomHit.getString("webformatURL");

			downloadImage(imageURL, folderPath + "/userImage.jpg");

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Method for downloading an image using a URL
	private static void downloadImage(String imageURL, String destinationFile)
	    throws IOException {
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

	// Method for generating a random index
	private static int getRandomIndex(int totalHits) {
		Random random = new Random();
		return random.nextInt(totalHits);
	}
}