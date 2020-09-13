package com.maxbys.strona_z_poradami_projekt.mail_service;

import javax.mail.MessagingException;

public interface MailService {
    void sendMail(String to, String subject, String text, boolean isHtmlContent)
            throws MessagingException;
}
