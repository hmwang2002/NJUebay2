package com.njuebay2.backend.service;

import jakarta.mail.MessagingException;

import java.util.List;

public interface MailService {
    // 发送简单文本内容
    boolean sendSimpleMail(String receiveMail, String subject, String content);
    // 发送html格式邮件
    void sendHtmlMail(String receiveMail, String subject, String content) throws MessagingException;
    // 发送包含附件的邮件
    void sendAttachmentMail(String receiveMail, String subject, String content, List<String> filePatahList) throws MessagingException;
}
