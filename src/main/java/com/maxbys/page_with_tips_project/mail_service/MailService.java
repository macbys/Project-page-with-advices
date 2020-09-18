package com.maxbys.page_with_tips_project.mail_service;

import javax.mail.MessagingException;

public interface MailService {
    void sendMail(String to, String subject, String text, boolean isHtmlContent)
            throws MessagingException;
}
