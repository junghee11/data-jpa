package com.develop.datajpa.service.sms;

import com.develop.core.exception.ClientException;
import lombok.RequiredArgsConstructor;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.exception.NurigoMessageNotReceivedException;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@PropertySource("classpath:application.properties")
public class SmsService {

    @Value("${spring.cool.sms.api.key}")
    String API_KEY;

    @Value("${spring.cool.sms.api.secret}")
    String API_SECRET_KEY;

    @Value("${spring.cool.sms.phone.from}")
    String PHONE_FROM;

    private static final String COOL_SMS_URL = "https://api.coolsms.co.kr";

    public void sendSms(String phone_to, String message) {
        DefaultMessageService messageService = NurigoApp.INSTANCE.initialize(API_KEY, API_SECRET_KEY, COOL_SMS_URL);

        Message coolSms = new Message();
        coolSms.setFrom(PHONE_FROM);
        coolSms.setTo(phone_to);
        coolSms.setText(message);

        try {
            LocalDateTime localDateTime = LocalDateTime.parse("2025-04-08 07:25:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            ZoneOffset zoneOffset = ZoneId.systemDefault().getRules().getOffset(localDateTime);
            Instant instant = localDateTime.toInstant(zoneOffset);

            messageService.send(coolSms, instant);
        } catch (NurigoMessageNotReceivedException exception) {
            System.out.println(exception.getFailedMessageList());
            System.out.println(exception.getMessage());

            throw new ClientException("메세지 발송 실패 : " + exception.getMessage());
        } catch (Exception exception) {
            System.out.println(exception.getMessage());
            throw new ClientException("메세지 발송 실패 : " + exception.getMessage());
        }
    }

}
