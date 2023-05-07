package com.yoyo.admin.common.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
public class MailService {

    private static final Logger logger = LoggerFactory.getLogger(MailService.class);

    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String sender;

    /**
     * 发送普通邮件
     *
     * @param to
     * @param subject
     * @param content
     */
    public void sendSimpleMailMessge(String to, String subject, String content) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(sender);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(content);
        try {
            javaMailSender.send(message);
            logger.info("send success.................");
        } catch (Exception e) {
            logger.error("发送简单邮件时发生异常!", e);
        }
    }

    /**
     * 发送 HTML 邮件
     *
     * @param to      收件人
     * @param subject 主题
     * @param content 内容（html）
     */
    public void sendHtmlMessge(String to, String subject, String content) {
        MimeMessage message = javaMailSender.createMimeMessage();
        try {
            //true表示需要创建一个multipart message
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(sender);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);
            javaMailSender.send(message);
            logger.info("send success.................");
        } catch (MessagingException e) {
            logger.error("发送MimeMessge时发生异常！", e);
        }
    }

    /**
     * 发送带附件的邮件
     *
     * @param to       收件人
     * @param subject  主题
     * @param content  内容
     * @param filePath 附件路径
     */
    public void sendAttachmentMessge(String to, String subject, String content, String filePath) {
        MimeMessage message = javaMailSender.createMimeMessage();
        try {
            // true表示需要创建一个multipart message
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(sender);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);
            FileSystemResource file = new FileSystemResource(filePath);
            String fileName = file.getFilename();
            helper.addAttachment(fileName, file);
            javaMailSender.send(message);
            logger.info("send success.................");
        } catch (MessagingException e) {
            logger.error("发送带附件的MimeMessge时发生异常！", e);
        }
    }

}
