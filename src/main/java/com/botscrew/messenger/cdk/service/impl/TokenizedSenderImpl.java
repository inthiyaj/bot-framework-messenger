package com.botscrew.messenger.cdk.service.impl;

import com.botscrew.messenger.cdk.config.property.MessengerProperties;
import com.botscrew.messenger.cdk.exception.SendAPIException;
import com.botscrew.messenger.cdk.model.MessengerUser;
import com.botscrew.messenger.cdk.model.incomming.UserInfo;
import com.botscrew.messenger.cdk.model.outgoing.*;
import com.botscrew.messenger.cdk.model.outgoing.request.Request;
import com.botscrew.messenger.cdk.model.outgoing.template.generic.GenericTemplateMessage;
import com.botscrew.messenger.cdk.model.outgoing.template.generic.GenericTemplatePayload;
import com.botscrew.messenger.cdk.model.outgoing.template.TemplateAttachment;
import com.botscrew.messenger.cdk.model.outgoing.template.TemplateElement;
import com.botscrew.messenger.cdk.service.TokenizedSender;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ScheduledFuture;


@RequiredArgsConstructor
public class TokenizedSenderImpl implements TokenizedSender {
    private static final Logger LOGGER = LoggerFactory.getLogger(TokenizedSenderImpl.class);

    private final RestTemplate restTemplate;
    private final MessengerProperties properties;
    private final ThreadPoolTaskScheduler scheduler;

    @Override
    public void send(String token, Request request) {
        post(token, request);
    }

    @Override
    public void send(String token, MessengerUser recipient, String text) {
        Request message = Request.builder()
                .message(new Message(text))
                .recipient(new UserInfo(recipient.getChatId()))
                .build();

        post(token, message);
    }

    @Override
    public ScheduledFuture send(String token, MessengerUser recipient, String text, Integer delayMillis) {
        Date when = addToDate(new Date(), Calendar.MILLISECOND, delayMillis);
        return scheduler.schedule(() -> send(token, recipient, text), when);
    }

    @Override
    public void send(String token, MessengerUser recipient, String text, List<QuickReply> quickReplies) {
        Request message = Request.builder()
                .message(new QuickReplyMessage(text, quickReplies))
                .recipient(new UserInfo(recipient.getChatId()))
                .build();

        post(token, message);
    }

    @Override
    public ScheduledFuture send(String token, MessengerUser recipient, String text, List<QuickReply> quickReplies, Integer delayMillis) {
        Date when = addToDate(new Date(), Calendar.MILLISECOND, delayMillis);
        return scheduler.schedule(() -> send(token, recipient, text, quickReplies), when);
    }

    @Override
    public void send(String token, MessengerUser recipient, List<TemplateElement> elements) {
        TemplateAttachment templateAttachment = new TemplateAttachment(new GenericTemplatePayload(elements));
        Request message = Request.builder()
                .message(new GenericTemplateMessage(templateAttachment))
                .recipient(new UserInfo(recipient.getChatId()))
                .build();

        post(token, message);
    }

    @Override
    public ScheduledFuture send(String token, MessengerUser recipient, List<TemplateElement> elements, Integer delayMillis) {
        Date when = addToDate(new Date(), Calendar.MILLISECOND, delayMillis);
        return scheduler.schedule(() -> send(token, recipient, elements), when);
    }

    @Override
    public void send(String token, MessengerUser recipient, List<TemplateElement> elements, List<QuickReply> quickReplies) {
        TemplateAttachment templateAttachment = new TemplateAttachment(new GenericTemplatePayload(elements));
        Request message = Request.builder()
                .message(new GenericTemplateMessage(quickReplies, templateAttachment))
                .recipient(new UserInfo(recipient.getChatId()))
                .build();

        post(token, message);
    }

    @Override
    public ScheduledFuture send(String token, MessengerUser recipient, List<TemplateElement> elements, List<QuickReply> quickReplies, Integer delayMillis) {
        Date when = addToDate(new Date(), Calendar.MILLISECOND, delayMillis);
        return scheduler.schedule(() -> send(token, recipient, elements, quickReplies), when);
    }

    @Override
    public ScheduledFuture send(String token, Request request, Integer delayMillis) {
        Date when = addToDate(new Date(), Calendar.MILLISECOND, delayMillis);
        return scheduler.schedule(() -> send(token, request), when);
    }

    private void post(String token, Request message) {
        LOGGER.debug("Posting message: \n{}", message);
        try {
            restTemplate.postForObject(properties.getMessagingUrl(token), message, String.class);
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            throw new SendAPIException(e.getResponseBodyAsString(), e);
        }
    }

    private Date addToDate(Date date, int calendarField, int amount) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(calendarField, amount);
        return calendar.getTime();
    }
}