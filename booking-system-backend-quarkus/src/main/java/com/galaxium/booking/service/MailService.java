package com.galaxium.booking.service;

import com.galaxium.booking.boardingpass.BoardingPassData;
import com.galaxium.booking.dto.UserDto;
import com.galaxium.booking.entity.User;
import io.quarkus.mailer.Mail;
import io.quarkus.mailer.MailTemplate;
import io.quarkus.mailer.Mailer;
import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

@ApplicationScoped
public class MailService {

    @Inject
    Mailer mailer;

    @Inject
    Logger logger;

    byte[] logo;

    @PostConstruct
    public void readLogo() throws IOException {
        logo = MailService.class.getResourceAsStream("/logogalaxy.png").readAllBytes();
    }

    /**@CheckedTemplate
    static class Templates {
        public static native MailTemplate.MailTemplateInstance boardingPass(UserDto userDto, BoardingPassData boardingPassData);
    }**/

    @CheckedTemplate
    static class Templates {
        public static native TemplateInstance boardingPass(UserDto userDto, BoardingPassData boardingPassData);
    }
    public void sendEmail(UserDto user, BoardingPassData boardingPassData, byte[] boardingPass) {

        String body = Templates.boardingPass(user, boardingPassData).render();
        Mail yourBoardingPassIsReady = Mail.withHtml(user.email, "Your Boarding Pass is Ready", body)
            .setFrom("galaxy@example.com")
            .addInlineAttachment("logo.png", logo, "image/png", "<logo@quarkus.io>")
            .addAttachment("boarding-pass.pdf", boardingPass, "application/pdf");
        mailer.send(yourBoardingPassIsReady);

        /**Templates.boardingPass(user, boardingPassData)
            .to(user.email)
            .from("galaxy@example.com")
            .subject("Your Boarding Pass is Ready")
            .addInlineAttachment("logo.png", logo, "image/png", "<logo@quarkus.io>")
            .addAttachment("boarding-pass.pdf", boardingPass, "application/pdf")
            .send();**/

    }

}
