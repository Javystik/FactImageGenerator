package com.zoi4erom.factimagegen.image;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.springframework.stereotype.Component;

@Component
public class ImageModifier {

	private ImageModifier() {
	}

	//Darkens the image, calls the addTextToImage method, and returns the image
	public static BufferedImage darkenAndAddText(String fact) {
		String imagePath = "src/main/resources/image/userImage.jpg";
		float darkenFactor = 0.4f;
		try {
			BufferedImage userImage = ImageIO.read(new File(imagePath));

			RescaleOp op = new RescaleOp(darkenFactor, 0, null);

			BufferedImage darkenedImage = op.filter(userImage, null);

			addTextToImage(darkenedImage, fact);

			return darkenedImage;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	//Adds text to the image, configuring the font, size, and color of the text.
	//In the first loop, we determine the initial position in y
	//In the following loop, we output words onto the image.
	private static void addTextToImage(BufferedImage image, String fact) {
		Graphics2D g2d = image.createGraphics();

		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setFont(new Font("Times New Roman", Font.ITALIC, 26));
		g2d.setColor(Color.WHITE);

		FontMetrics fm = g2d.getFontMetrics();
		int maxWidth = image.getWidth() - 20;
		int imageCenterX = image.getWidth() / 2;
		int imageCenterY = image.getHeight() / 2;

		String[] words = fact.split("\\s+");
		StringBuilder currentLine = new StringBuilder();
		int currentWidth = 0;
		int lineHeight = fm.getHeight();
		int lines = 0;

		for (String word : words) {
			int wordWidth = fm.stringWidth(word);

			if (currentWidth + wordWidth > maxWidth) {
				lines++;
				currentLine.setLength(0);
				currentWidth = 0;
			}

			currentLine.append(word).append(" ");
			currentWidth += wordWidth + fm.stringWidth(" ");
		}

		int startY = imageCenterY - (lines * lineHeight) / 2;

		currentLine.setLength(0);
		currentWidth = 0;
		int y = startY;

		for (String word : words) {
			int wordWidth = fm.stringWidth(word);

			if (currentWidth + wordWidth > maxWidth) {
				int x = imageCenterX - currentWidth / 2;
				g2d.drawString(currentLine.toString(), x, y);
				y += lineHeight;
				currentLine.setLength(0);
				currentWidth = 0;
			}

			currentLine.append(word).append(" ");
			currentWidth += wordWidth + fm.stringWidth(" ");
		}

		int x = imageCenterX - currentWidth / 2;
		g2d.drawString(currentLine.toString(), x, y);

		g2d.dispose();
	}
}