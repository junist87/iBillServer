package com.ciaosgarage.iBill.beans.util.mailSender;


import com.ciaosgarage.iBill.beans.settings.IBillSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


@Component
public class MailSenderImpl implements MailSender {

    @Autowired
    IBillSettings settings;


    public void sendMail(String title, String context, String senderEmail, String targetEmail) throws CannotSendEmailException {

        Properties p = System.getProperties();
        p.put("mail.smtp.starttls.enable", "true");     // gmail은 무조건 true 고정
        p.put("mail.smtp.host", "smtp.gmail.com");      // smtp 서버 주소
        p.put("mail.smtp.auth", "true");                 // gmail은 무조건 true 고정
        p.put("mail.smtp.port", "587");                 // gmail 포트

        Authenticator auth = new MyAuthentication(settings.googleEmail, settings.googleEmail);

        //session 생성 및  MimeMessage생성
        Session session = Session.getDefaultInstance(p, auth);
        MimeMessage msg = new MimeMessage(session);

        try {
            //편지보낸시간
            msg.setSentDate(new Date());

            //편지보내는 사람
            InternetAddress from = new InternetAddress(senderEmail);

            // 이메일 보내는 사람
            msg.setFrom(from);

            // 이메일 받는 사람
            InternetAddress to = new InternetAddress(targetEmail);
            msg.setRecipient(Message.RecipientType.TO, to);

            // 이메일 제목
            msg.setSubject(title, "UTF-8");

            // 이메일 내용
            msg.setText(context, "UTF-8");

            // 이메일 헤더
            msg.setHeader("content-Type", "text/html");

            //메일보내기
            javax.mail.Transport.send(msg);

        } catch (MessagingException e) {
            e.printStackTrace();
            throw new CannotSendEmailException();
        }
    }

    class MyAuthentication extends Authenticator {
        PasswordAuthentication pa;

        public MyAuthentication(String id, String passwd) {


            // ID와 비밀번호를 입력한다.
            pa = new PasswordAuthentication(id, passwd);
        }

        // 시스템에서 사용하는 인증정보
        public PasswordAuthentication getPasswordAuthentication() {
            return pa;
        }
    }
}



