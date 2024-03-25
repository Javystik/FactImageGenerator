package com.zoi4erom.factimagegen.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:application.properties")
@ComponentScan("com.zoi4erom.factimagegen")
@Getter
@Setter
public class BotConfig {

	@Value("${bot.name}")
	private String botName;
	@Value("${bot.token}")
	private String token;
}
