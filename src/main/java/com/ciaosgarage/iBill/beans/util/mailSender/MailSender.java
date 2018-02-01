package com.ciaosgarage.iBill.beans.util.mailSender;

/**
 * The interface MailSender.
 * 매일전송을 담당하는 클래스
 */
public interface MailSender {
    /**
     * 매일을 발송한다.
     *
     * @param title       메일 제목
     * @param context     메일 내용
     * @param senderEmail 발신자 주소
     * @param targetEmail 수신자 주소
     * @throws CannotSendEmailException 메일 발송에 실패하였을때 발생하는 예외
     */
    void sendMail(String title, String context, String senderEmail, String targetEmail) throws CannotSendEmailException;
}
