package com.bsmooth.springtwiliomessagesender.controllers;

import com.bsmooth.springtwiliomessagesender.utils.LoggerUtil;

import io.swagger.annotations.ApiOperation;
import com.bsmooth.springtwiliomessagesender.properties.TwilioProps;
import com.bsmooth.springtwiliomessagesender.domains.twilio.VoiceDto;
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
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PreDestroy;

import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.texttospeech.v1.AudioConfig;
import com.google.cloud.texttospeech.v1.AudioEncoding;
import com.google.cloud.texttospeech.v1.ListVoicesRequest;
import com.google.cloud.texttospeech.v1.ListVoicesResponse;
import com.google.cloud.texttospeech.v1.SynthesisInput;
import com.google.cloud.texttospeech.v1.SynthesizeSpeechResponse;
import com.google.cloud.texttospeech.v1.TextToSpeechClient;
import com.google.cloud.texttospeech.v1.TextToSpeechSettings;
import com.google.cloud.texttospeech.v1.Voice;
import com.google.cloud.texttospeech.v1.VoiceSelectionParams;
import com.google.protobuf.ByteString;
import java.io.FileOutputStream;
import java.io.OutputStream;

@RestController
@CrossOrigin
@RequestMapping(PathConsts.BASE_API_PATH)
public class MessageSenderController {

    private static Logger logger = LoggerFactory.getLogger(MessageSenderController.class);
    private TextToSpeechClient textToSpeechClient;
    @Autowired
    private MessageSenderService messageSenderService;
    public MessageSenderController(TwilioProps appConfig) {
        try {
          ServiceAccountCredentials credentials = ServiceAccountCredentials
              .fromStream(Files.newInputStream(Paths.get(appConfig.getCredentialsPath())));
          TextToSpeechSettings settings = TextToSpeechSettings.newBuilder()
              .setCredentialsProvider(FixedCredentialsProvider.create(credentials)).build();
          this.textToSpeechClient = TextToSpeechClient.create(settings);
        }
        catch (IOException e) {
          LoggerFactory.getLogger(MessageSenderController.class)
              .error("init Text2SpeechController", e);
        }
      }
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
    @PreDestroy
    public void destroy() {
      if (this.textToSpeechClient != null) {
        this.textToSpeechClient.close();
      }
    }

    @GetMapping("voices")
    public List<VoiceDto> getSupportedVoices() {
      ListVoicesRequest request = ListVoicesRequest.getDefaultInstance();
      ListVoicesResponse listreponse = this.textToSpeechClient.listVoices(request);
      return listreponse.getVoicesList().stream()
          .map(voice -> new VoiceDto(getSupportedLanguage(voice), voice.getName(),
              voice.getSsmlGender().name()))
          .collect(Collectors.toList());
    }

    @PostMapping("speak")
    public byte[] speak(@RequestParam("language") String language,
        @RequestParam("voice") String voice, @RequestParam("text") String text,
        @RequestParam("pitch") double pitch,
        @RequestParam("speakingRate") double speakingRate) {

      SynthesisInput input = SynthesisInput.newBuilder().setText(text).build();

      VoiceSelectionParams voiceSelection = VoiceSelectionParams.newBuilder()
          .setLanguageCode(language).setName(voice).build();

      AudioConfig audioConfig = AudioConfig.newBuilder().setPitch(pitch)
          .setSpeakingRate(speakingRate).setAudioEncoding(AudioEncoding.MP3).build();

      SynthesizeSpeechResponse response = this.textToSpeechClient.synthesizeSpeech(input,
          voiceSelection, audioConfig);
      try (OutputStream out = new FileOutputStream("output.mp3")) {
          out.write(response.getAudioContent().toByteArray());
          System.out.println("Audio content written to file \"output.mp3\"");
        }catch(Exception e) {
        	  //  Block of code to handle errors
        }
      return response.getAudioContent().toByteArray();
    }

    private static String getSupportedLanguage(Voice voice) {
      List<ByteString> languageCodes = voice.getLanguageCodesList().asByteStringList();
      for (ByteString languageCode : languageCodes) {
        return languageCode.toStringUtf8();
      }
      return null;
    }
}
