package com.njuebay2.backend.service.impl;

import com.njuebay2.backend.service.MailService;
import com.njuebay2.backend.utils.RedisCache;
import jakarta.annotation.Resource;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class MailServiceImpl implements MailService {
    @Value("${spring.mail.username}")
    private String sendMail;
    @Resource
    private JavaMailSender javaMailSender;

    @Resource
    private RedisCache redisCache;

    /**
     * 发送只含文本的邮件
     * @param receiveMail
     * @param subject
     * @param content
     */
    public void sendSimpleMail(String receiveMail, String subject, String content) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom(sendMail);
        msg.setTo(receiveMail);
        msg.setSubject(subject);
        msg.setText(content);

        redisCache.setCacheObject(receiveMail, content, 60 * 5L, TimeUnit.SECONDS);

        javaMailSender.send(msg);
    }

    /**
     * 发送html邮件
     * @param receiveMail
     * @param subject
     * @param content
     * @throws MessagingException
     */
    public void sendHtmlMail(String receiveMail, String subject, String content) throws MessagingException {
        MimeMessage message =javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message, true);

        mimeMessageHelper.setFrom(sendMail);
        mimeMessageHelper.setTo(receiveMail);
        mimeMessageHelper.setSubject(subject);
        mimeMessageHelper.setText(content, true);

        javaMailSender.send(message);
    }

    /**
     * 发送含有附件的邮件
     * @param receiveMail
     * @param subject
     * @param content
     * @param filePatahList
     * @throws MessagingException
     */
    public void sendAttachmentMail(String receiveMail, String subject, String content, List<String> filePatahList) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message, true);

        mimeMessageHelper.setFrom(sendMail);
        mimeMessageHelper.setTo(receiveMail);
        mimeMessageHelper.setSubject(subject);
        mimeMessageHelper.setText(content, true);

        for (String item : filePatahList) {
            FileSystemResource file = new FileSystemResource(new File(item));
            String fileName = item.substring(item.lastIndexOf(File.separator));
            mimeMessageHelper.addAttachment(fileName, file);
        }

        javaMailSender.send(message);
    }
}
