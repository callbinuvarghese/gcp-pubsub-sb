package com.binu.pubsub.receive;

import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.google.cloud.spring.pubsub.integration.inbound.PubSubInboundChannelAdapter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.messaging.MessageChannel;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class ReceiverApplication {

	private static final Log LOGGER = LogFactory.getLog(ReceiverApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(ReceiverApplication.class, args);
	}

	@Bean
	public MessageChannel pubsubInputChannel() {
		return new DirectChannel();
	}

	@Bean
	public PubSubInboundChannelAdapter messageChannelAdapter(
			@Qualifier("pubsubInputChannel") MessageChannel inputChannel,
			PubSubTemplate pubSubTemplate) {
		PubSubInboundChannelAdapter adapter =
				new PubSubInboundChannelAdapter(pubSubTemplate, "exampleSubscription");
		LOGGER.info("Message Subscriptio  from Subscription: " + "exampleSubscription");
		adapter.setOutputChannel(inputChannel);

		return adapter;
	}
	@ServiceActivator(inputChannel = "pubsubInputChannel")
	public void messageReceiver(String payload) {
		LOGGER.info("Message arrived! Payload: " + payload);
	}
}
