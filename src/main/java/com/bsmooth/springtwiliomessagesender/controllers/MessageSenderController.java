package com.bsmooth.springtwiliomessagesender.controllers;

import com.bsmooth.springtwiliomessagesender.utils.LoggerUtil;

import io.swagger.annotations.ApiOperation;

import com.bsmooth.springtwiliomessagesender.constants.MessageConsts;
import com.bsmooth.springtwiliomessagesender.constants.PathConsts;
import com.bsmooth.springtwiliomessagesender.services.MessageSenderService;
import com.bsmooth.springtwiliomessagesender.services.responses.MessageSenderResponse;

import java.io.IOException;
import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(PathConsts.BASE_API_PATH)
public class MessageSenderController {

    private static Logger logger = LoggerFactory.getLogger(MessageSenderController.class);

    @Autowired
    private MessageSenderService messageSenderService;

    @PostMapping(PathConsts.SMS_API_ENDPOINT)
    @ApiOperation(value = "Send sms.", nickname = "send sms")
    public ResponseEntity<MessageSenderResponse> sendSMS(@RequestParam final String to, @RequestParam final String body){
        final String METHOD = "send sms ";
        logger.info(LoggerUtil.getEnteringMethodMessage(METHOD));
        MessageSenderResponse messageSenderResponse = messageSenderService.sendSMS(to, body);
        logger.info(METHOD + MessageConsts.RESPONSE_PREFIX_MESSAGE + messageSenderResponse.toString());
        logger.info(LoggerUtil.getExitingMethodMessage(METHOD));
        return new ResponseEntity<>(messageSenderResponse,
                HttpStatus.valueOf(messageSenderResponse.getStatus()));
    }
    @PostMapping(PathConsts.EMAIL_API_ENDPOINT)
    @ApiOperation(value = "Send email.", nickname = "send email")
    public ResponseEntity<MessageSenderResponse> sendEmail(@RequestParam final String to, @RequestParam final String title,@RequestParam final String body){
        final String METHOD = "send email ";
        logger.info(LoggerUtil.getEnteringMethodMessage(METHOD));
        MessageSenderResponse messageSenderResponse = messageSenderService.sendEmail("hackatonckaton@gmail.com",to, title, body);
        logger.info(METHOD + MessageConsts.RESPONSE_PREFIX_MESSAGE + messageSenderResponse.toString());
        logger.info(LoggerUtil.getExitingMethodMessage(METHOD));
        return new ResponseEntity<>(messageSenderResponse,
                HttpStatus.valueOf(messageSenderResponse.getStatus()));
    }
    @PostMapping(PathConsts.WHATSAPP_API_ENDPOINT)
    @ApiOperation(value = "Send whatsapp.", nickname = "send whatsapp")
    public ResponseEntity<MessageSenderResponse> sendWhatsApp(@RequestParam final String to, @RequestParam final String body,@RequestParam final String url){
        final String METHOD = "send whatsapp ";
        logger.info(LoggerUtil.getEnteringMethodMessage(METHOD));
        MessageSenderResponse messageSenderResponse = messageSenderService.sendWhatsApp(to, body,url);
        logger.info(METHOD + MessageConsts.RESPONSE_PREFIX_MESSAGE + messageSenderResponse.toString());
        logger.info(LoggerUtil.getExitingMethodMessage(METHOD));
        return new ResponseEntity<>(messageSenderResponse,
                HttpStatus.valueOf(messageSenderResponse.getStatus()));
    }

    @PostMapping(PathConsts.VOICE_API_ENDPOINT)
    @ApiOperation(value = "make call.", nickname = "send voie")
    public ResponseEntity<MessageSenderResponse> makeCall(@RequestParam final String to, @RequestParam final String body){
        final String METHOD = "make call ";
        logger.info(LoggerUtil.getEnteringMethodMessage(METHOD));
        MessageSenderResponse messageSenderResponse = messageSenderService.makeVoiceCall(to, body);
        logger.info(METHOD + MessageConsts.RESPONSE_PREFIX_MESSAGE + messageSenderResponse.toString());
        logger.info(LoggerUtil.getExitingMethodMessage(METHOD));
        return new ResponseEntity<>(messageSenderResponse,
                HttpStatus.valueOf(messageSenderResponse.getStatus()));
    } 
}
