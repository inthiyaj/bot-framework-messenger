package com.botscrew.messenger.cdk.service.impl;

import com.botscrew.messenger.cdk.config.property.MessengerProperties;
import com.botscrew.messenger.cdk.exception.SendAPIException;
import com.botscrew.messenger.cdk.model.MessengerUser;
import com.botscrew.messenger.cdk.model.incomming.UserInfo;
import com.botscrew.messenger.cdk.model.outgoing.*;
import com.botscrew.messenger.cdk.service.TokenizedSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

@Slf4j
@RequiredArgsConstructor
public class TokenizedSenderImpl implements TokenizedSender {
    private final RestTemplate restTemplate;
    private final MessengerProperties properties;
    private final ThreadPoolTaskScheduler scheduler;

    @Override
    public void send(String token, MessengerUser recipient, String text) {
        Request message = Request.builder()
                .message(new Message(text))
                .recipient(new UserInfo(recipient.getChatId()))
                .build();
        post(message);
    }

    @Override
    public ScheduledFuture send(String token, MessengerUser recipient, String text, Integer delayMillis) {
        Date when = addToDate(new Date(), Calendar.MILLISECOND, delayMillis);
        return scheduler.schedule(() -> send(token, recipient, text), when);
    }

    @Override
    public void send(String token, MessengerUser recipient, String text, List<QuickReply> quickReplies) {
        Request message = Request.builder()
                .message(new QuickRepliesMessage(text, quickReplies))
                .recipient(new UserInfo(recipient.getChatId()))
                .build();

        post(message);
    }

    @Override
    public ScheduledFuture send(String token, MessengerUser recipient, String text, List<QuickReply> quickReplies, Integer delayMillis) {
        Date when = addToDate(new Date(), Calendar.MILLISECOND, delayMillis);
        return scheduler.schedule(() -> send(token, recipient, text, quickReplies), when);
    }

    @Override
    public void send(String token, MessengerUser recipient, List<GenericElement> elements) {
        TemplateAttachment templateAttachment = new TemplateAttachment(new TemplatePayload(elements));
        Request message = Request.builder()
                .message(new GenericTemplateMessage(templateAttachment))
                .recipient(new UserInfo(recipient.getChatId()))
                .build();

        post(message);
    }

    @Override
    public ScheduledFuture send(String token, MessengerUser recipient, List<GenericElement> elements, Integer delayMillis) {
        Date when = addToDate(new Date(), Calendar.MILLISECOND, delayMillis);
        return scheduler.schedule(() -> send(token, recipient, elements), when);
    }

    @Override
    public void send(String token, MessengerUser recipient, List<GenericElement> elements, List<QuickReply> quickReplies) {
        TemplateAttachment templateAttachment = new TemplateAttachment(new TemplatePayload(elements));
        Request message = Request.builder()
                .message(new GenericTemplateMessage(quickReplies, templateAttachment))
                .recipient(new UserInfo(recipient.getChatId()))
                .build();

        post(message);
    }

    @Override
    public ScheduledFuture send(String token, MessengerUser recipient, List<GenericElement> elements, List<QuickReply> quickReplies, Integer delayMillis) {
        Date when = addToDate(new Date(), Calendar.MILLISECOND, delayMillis);
        return scheduler.schedule(() -> send(token, recipient, elements, quickReplies), when);
    }

    private void post(Object message) {
        log.debug("Posting message: \n{0}", message);
        try {
            restTemplate.postForObject(properties.messagingUrl(), message, String.class);
        }catch (HttpClientErrorException|HttpServerErrorException e) {
            throw new SendAPIException(e.getResponseBodyAsString());
        }
    }

    private Date addToDate(Date date, int calendarField, int amount) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(calendarField, amount);
        return c.getTime();
    }
}
