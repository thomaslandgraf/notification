package com.landgraf.notification.business;

import com.landgraf.notification.business.dto.TasksDTO;
import com.landgraf.notification.infrastructure.exception.EmailException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;


import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;

    @Value("${send.email.from}")
    private String from;

    @Value("${send.email.nameFrom}")
    private String nameFrom;

    public void sendEmail(TasksDTO tasksDTO){
        try{
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());

            mimeMessageHelper.setFrom(new InternetAddress(from, nameFrom));
            mimeMessageHelper.setTo(InternetAddress.parse(tasksDTO.getUserEmail()));
            mimeMessageHelper.setSubject("Task Notification");

            Context context = new Context();
            context.setVariable("taskName", tasksDTO.getTaskName());
            context.setVariable("eventTime", tasksDTO.getEventDate());
            context.setVariable("description", tasksDTO.getDescription());
            String template = templateEngine.process("Notification", context);
            mimeMessageHelper.setText(template, true);
            javaMailSender.send(message);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new EmailException("Error trying to send email", e.getCause());
        }
    }
}
