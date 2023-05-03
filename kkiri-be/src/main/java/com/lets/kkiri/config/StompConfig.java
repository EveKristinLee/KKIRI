package com.lets.kkiri.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class StompConfig implements WebSocketMessageBrokerConfigurer {

	@Value("${spring.rabbitmq.host}")
	private String host;
	@Value("${spring.rabbitmq.port}")
	private int port;
	@Value("${spring.rabbitmq.username}")
	private String username;
	@Value("${spring.rabbitmq.password}")
	private String password;

	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		registry.addEndpoint("/stomp/chat")
			.setAllowedOrigins("*")
			.withSockJS();
	}

	@Override
	public void configureMessageBroker(MessageBrokerRegistry registry) {
		registry.setPathMatcher(new AntPathMatcher(".")); //chat/room/3 => chat.room.3
		registry.setApplicationDestinationPrefixes("/pub");

		registry.enableStompBrokerRelay("/queue", "/topic", "/exchange", "/amq/queue")
			.setRelayHost(host)
			.setRelayPort(3001)
			// .setClientLogin()
			// .setClientPasscode()
			.setSystemLogin(username)
			.setSystemPasscode(password);

	}

}
