package com.lets.kkiri.config.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lets.kkiri.dto.moim.MoimSessionReq;
import com.lets.kkiri.service.GpsService;
import com.lets.kkiri.service.MessageRoomService;
import com.lets.kkiri.service.MessageService;

import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.LinkedHashMap;

@Slf4j
@Component
@Log4j2
@RequiredArgsConstructor
public class MoimSessionHandler extends TextWebSocketHandler {

	private final ObjectMapper objectMapper;
	private final MessageService messageService;
	private final MessageRoomService messageRoomService;
	private WebSocketSession mySession;
	private final GpsService gpsService;

	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		String payload = message.getPayload();
		log.info("payload : {}", payload);
		mySession = session;
		MoimSessionReq msg = objectMapper.readValue(payload, MoimSessionReq.class);
		Object content = msg.getContent();
		log.debug("content : {}", content.toString());
		log.debug("content type : {}", content.getClass());
		switch (msg.getType()) {
			case MESSAGE:
				messageRoomService.handleActions(session, msg.getType(), content, messageService);
				break;
			case GPS:
				gpsService.handleActions(session, content);
				break;
			case EMOJI:

		}
	}
	public WebSocketSession getSession() {
		return mySession;
	}
}
