package org.lilu.sns.service;

import freemarker.template.Template;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;
import java.util.Map;

/**
 * @Auther: lilu
 * @Date: 2019/2/25
 * @Description: 发送邮件服务
 */
@Service
public class MailService {
    private static final Logger logger = LoggerFactory.getLogger(MailService.class);

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private FreeMarkerConfigurer freeMarkerConfigurer;

    /**
     * 发送Freemarker模板渲染的邮件
     * @param to
     * @param subject
     * @param templatePath
     * @param data
     * @return
     */
    public boolean sendMailWithHtmlTemplate(String to, String subject, String templatePath, Map<String,Object> data) {
        try {
            String nick = MimeUtility.encodeText("格子伞社区");
            InternetAddress from = new InternetAddress(nick + "<tclilu@lilu.org.cn>");
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
            Template template = freeMarkerConfigurer.getConfiguration().getTemplate(templatePath);
            String text = FreeMarkerTemplateUtils.processTemplateIntoString(template,data);
            mimeMessageHelper.setTo(to);
            mimeMessageHelper.setFrom(from);
            mimeMessageHelper.setSubject(subject);
            mimeMessageHelper.setText(text,true);
            javaMailSender.send(mimeMessage);
            return true;
        } catch (Exception e) {
            logger.error("发送邮件失败：" + e.getMessage());
            return false;
        }
    }
}
