package com.janprach.shopper;

import lombok.val;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@ComponentScan(basePackageClasses = ShopperApp.class)
@EnableAutoConfiguration
@EnableScheduling
@EnableJpaRepositories
@EnableTransactionManagement
public class ShopperApp {
	public static void main(String[] args) throws Exception {
		System.setProperty("log4jdbc.spylogdelegator.name", "net.sf.log4jdbc.log.slf4j.Slf4jSpyLogDelegator");
		val app = new SpringApplication(ShopperApp.class);
		// app.setShowBanner(false);
		app.run(args);
	}
}
