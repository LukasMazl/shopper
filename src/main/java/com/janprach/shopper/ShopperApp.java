package com.janprach.shopper;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import lombok.val;

@ComponentScan(basePackageClasses = ShopperApp.class)
@EnableScheduling
@EnableJpaRepositories
@EnableTransactionManagement
@SpringBootApplication
public class ShopperApp {
	public static void main(final String[] args) throws Exception {
//		System.setProperty("log4jdbc.spylogdelegator.name", "net.sf.log4jdbc.log.slf4j.Slf4jSpyLogDelegator");
		val app = new SpringApplication(ShopperApp.class);
//		app.setBanner(false);
		app.run(args);
	}
}
