package com.zoi4erom.factimagegen;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Main {

	public static void main(String[] args) {
		AnnotationConfigApplicationContext context =
		    new AnnotationConfigApplicationContext(BotConfig.class);

		context.close();
	}
}