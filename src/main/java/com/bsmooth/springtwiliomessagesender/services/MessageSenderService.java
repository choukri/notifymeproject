package com.bsmooth.springtwiliomessagesender.services;

import com.bsmooth.springtwiliomessagesender.services.responses.MessageSenderResponse;

public interface MessageSenderService {
    MessageSenderResponse sendSMS(final String to, final String body);
    MessageSenderResponse sendWhatsApp(final String to, final String body,final String url);
    MessageSenderResponse makeVoiceCall(final String to, final String body);
    MessageSenderResponse sendEmail(final String from,final String to , final String title,final String body);
    MessageSenderResponse sendEmailhtml(final String to, final String title,final String body);
}
