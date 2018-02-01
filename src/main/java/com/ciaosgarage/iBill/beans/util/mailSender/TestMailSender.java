package com.ciaosgarage.iBill.beans.util.mailSender;

import org.springframework.stereotype.Component;

//@Component
public class TestMailSender implements MailSender {
    public void sendMail(String title, String context, String senderEmail, String targetEmail) throws CannotSendEmailException {

    }
}
