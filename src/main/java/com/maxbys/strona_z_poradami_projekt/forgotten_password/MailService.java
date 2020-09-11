package com.maxbys.strona_z_poradami_projekt.forgotten_password;

import javax.mail.MessagingException;

public interface MailService {
    void sendMail(String to, String subject, String text, boolean isHtmlContent)
            throws MessagingException;
}
