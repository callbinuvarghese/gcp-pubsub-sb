package com.binu.pubsub.send;

import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.google.cloud.spring.pubsub.integration.outbound.PubSubMessageHandler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.MessageHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@SpringBootApplication
@RestController
public class SenderApplication {
	private static final Log LOGGER = LogFactory.getLog(SenderApplication.class);

	@Autowired
	private PubsubOutboundGateway messagingGateway;

	public static void main(String[] args) {
		SpringApplication.run(SenderApplication.class, args);
	}


	@MessagingGateway(defaultRequestChannel = "pubsubOutputChannel")
	public interface PubsubOutboundGateway {
		void sendToPubsub(String text);
	}


	@Bean
	@ServiceActivator(inputChannel = "pubsubOutputChannel")
	public MessageHandler messageSender(PubSubTemplate pubsubTemplate) {
		return new PubSubMessageHandler(pubsubTemplate, "exampleTopic");
	}

	@PostMapping("/postMessage")
	public RedirectView postMessage(@RequestParam("message") String message) {
		LOGGER.info("Message publishing  ! Payload: " + message);
		this.messagingGateway.sendToPubsub(message);
		return new RedirectView("/");
	}


}
