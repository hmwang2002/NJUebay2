package com.ebaynju.ebay_backend.service;

import java.util.List;

public interface MailService {
    // 发送简单文本内容
    void sendSimpleMail(String receiveMail, String subject, String content);
    // 发送html格式邮件
    void sendHtmlMail(String receiveMail, String subject, String content);
    // 发送包含附件的邮件
    void sendAttachmentMail(String receiveMail, String subject, String content, List<String> filePatahList);
}
