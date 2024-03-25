package com.zoi4erom.factimagegen;

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

//Клас для роботи з картинкою: завантаження, затемнення, вставка тексту
@Component
public class ImageWork {

	private ImageWork() {
	}

	//Основний метод що керує іншими
	public static BufferedImage darkenAndAddText(String fact) {
		String imagePath = "C:\\Users\\user\\IdeaProjects\\FactImageGenerator\\src\\main\\resources\\userImage.jpg";
		float darkenFactor = 0.4f;
		try {
			BufferedImage userImage = loadImage(imagePath);
			BufferedImage darkenedImage = darkenImage(userImage, darkenFactor);
			addTextToImage(darkenedImage, fact);
			return darkenedImage;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}


	//метод для загрузки картинки
	private static BufferedImage loadImage(String imagePath) throws IOException {
		return ImageIO.read(new File(imagePath));
	}

	//метод для затемнення картинки
	private static BufferedImage darkenImage(BufferedImage image, float darkenFactor) {
		RescaleOp op = new RescaleOp(darkenFactor, 0, null);
		return op.filter(image, null);
	}

	//метод для додавання тексту на картинку
	private static void addTextToImage(BufferedImage image, String fact) {
		Graphics2D g2d = image.createGraphics();
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
		    RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setFont(new Font("Times New Roman", Font.ITALIC, 21)); // шрифт тексту
		g2d.setColor(Color.WHITE); // колір тексту

		FontMetrics fm = g2d.getFontMetrics();
		int maxWidth = image.getWidth() - 20; // відступ від боків

		//початкові координати
		int x = image.getWidth() / 2;
		int y = image.getHeight() / 2;

		String[] words = fact.split("\\s+");
		StringBuilder currentLine = new StringBuilder();
		int currentWidth = 0;
		int currentHeight = 0;

		//перебір рядків та переніс на наступний рядок
		for (String word : words) {
			int wordWidth = fm.stringWidth(word);
			int wordHeight = fm.getHeight();

			if (currentWidth + wordWidth
			    > maxWidth) { // Якщо слово не влізає, воно переноситься на наступний рядок
				g2d.drawString(currentLine.toString(), x, y + currentHeight);
				currentHeight += wordHeight;
				currentLine.setLength(0);
				currentWidth = 0;
			}

			currentLine.append(word).append(" ");
			currentWidth += wordWidth + fm.stringWidth(" ");
		}

		g2d.drawString(currentLine.toString(), x, y + currentHeight);
		g2d.dispose();
	}
}
