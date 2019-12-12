package com.bsmooth.springtwiliomessagesender.services.impl;

import com.bsmooth.springtwiliomessagesender.constants.MessageConsts;
import com.bsmooth.springtwiliomessagesender.services.MessageSenderService;
import com.bsmooth.springtwiliomessagesender.services.responses.MessageSenderResponse;
import com.google.common.net.HttpHeaders;
import com.twilio.exception.ApiException;

import java.io.IOException;
import java.util.Arrays;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.sendgrid.Content;
import com.sendgrid.Email;
import com.sendgrid.Mail;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;

@Service
public class TwilioMessageSenderServiceImpl implements MessageSenderService {

    private TwilioMessageSenderClient twilioMessageSenderClient;
    final private String sendGridApi = "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";

    @Autowired
    public TwilioMessageSenderServiceImpl(TwilioMessageSenderClient twilioMessageSenderClient ) {
        this.twilioMessageSenderClient = twilioMessageSenderClient;
       
    }

    @Override
    public MessageSenderResponse sendSMS(String to, String body) {
        MessageSenderResponse messageSenderResponse = null;
        try {
            twilioMessageSenderClient.sendSMS(to, body);
            messageSenderResponse = buildSuccessResponse();
        }catch (Exception e){
            messageSenderResponse = buildResponseFromException(e);
        }
        return messageSenderResponse;
    }

    @Override
    public MessageSenderResponse sendWhatsApp(String to, String body,String url) {
        MessageSenderResponse messageSenderResponse = null;

        try {
            twilioMessageSenderClient.sendWhatsApp(to, body,url);
            messageSenderResponse = buildSuccessResponse();
        }catch (Exception e){
            messageSenderResponse = buildResponseFromException(e);
        }

        return messageSenderResponse;
    }
    @Override
	public MessageSenderResponse sendEmail(String from, String to, String title , String body) {
    	MessageSenderResponse messageSenderResponse = null;
		
		Response response = sendmail(from, to, title, new Content("text/plain", body));
		messageSenderResponse = buildSuccessResponse();
		
		return messageSenderResponse;
	}
    @Override
    public MessageSenderResponse makeVoiceCall(String to, String body) {
        MessageSenderResponse messageSenderResponse = null;

        try {
            twilioMessageSenderClient.makeVoiceCall(to, body);
            messageSenderResponse = buildSuccessResponse();
        }catch (Exception e){
            messageSenderResponse = buildResponseFromException(e);
        }

        return messageSenderResponse;
    }

    private MessageSenderResponse buildResponseFromException(ApiException apiException){
        MessageSenderResponse messageSenderResponse = null;

        if(apiException != null && apiException.getStatusCode() != null){
            messageSenderResponse = new MessageSenderResponse(
                    false,
                    apiException.getMessage(),
                    apiException.getStatusCode());
        }else{
            messageSenderResponse = buildGeneralErrorResponse();
        }
        return messageSenderResponse;
    }

    private MessageSenderResponse buildResponseFromException(Exception exception){
        MessageSenderResponse messageSenderResponse = null;

        if(exception != null){
            if(exception instanceof ApiException){
                ApiException apiException = (ApiException)exception;

                if(apiException != null && apiException.getStatusCode() != null){
                    messageSenderResponse = new MessageSenderResponse(
                            false,
                            apiException.getMessage(),
                            apiException.getStatusCode());
                }
            }
        }

        return messageSenderResponse == null ? buildGeneralErrorResponse() : messageSenderResponse;
    }
    
    private MessageSenderResponse buildGeneralErrorResponse(){
        return new MessageSenderResponse(false,
                MessageConsts.GENERAL_ERROR_MESSAGE,
                HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    private MessageSenderResponse buildSuccessResponse(){
        return new MessageSenderResponse(true,
               null,
                HttpStatus.CREATED.value());
    }
    
    private Response sendmail(String from, String to, String subject, Content content) {
    	SendGrid sg = new SendGrid(sendGridApi);
        Mail mail = new Mail(new Email(from), subject, new Email(to), content);
        mail.setReplyTo(new Email("hackatonckaton@gmail.com"));
        Request request = new Request();
        Response response = null;
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            response = sg.api(request);
            System.out.println(response.getStatusCode());
            System.out.println(response.getBody());
            System.out.println(response.getHeaders());
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        
        return response;
    }

	@Override
	public MessageSenderResponse sendEmailhtml(String to, String title, String body) {
		// TODO Auto-generated method stub
		return null;
	}

	
}
