package com.galaxium.booking.signal;

import com.galaxium.booking.boardingpass.BoardingPassData;
import com.galaxium.booking.dto.UserDto;
import io.quarkus.mailer.Mail;
import io.quarkus.mailer.Mailer;
import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import io.quarkus.signals.Receives;
import io.smallrye.common.annotation.RunOnVirtualThread;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.jboss.logging.Logger;

import java.io.IOException;

@Singleton
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

    @RunOnVirtualThread
    public void sendEmail(@Receives EmailMessage emailMessage) {
        logger.info("Boarding Pass Email Preparation");

        UserDto user = emailMessage.userDto();
        BoardingPassData boardingPassData = emailMessage.boardingPassData();
        byte[] boardingPass = emailMessage.boardingPass();

        String body = Templates.boardingPass(user, boardingPassData).render();
        Mail yourBoardingPassIsReady = Mail.withHtml(user.email, "Your Boarding Pass is Ready", body)
            .setFrom("galaxy@example.com")
            .addInlineAttachment("logo.png", logo, "image/png", "<logo@quarkus.io>")
            .addAttachment("boarding-pass.pdf", boardingPass, "application/pdf");
        mailer.send(yourBoardingPassIsReady);

        logger.info("Boarding Pass Email sent");

        /**Templates.boardingPass(user, boardingPassData)
            .to(user.email)
            .from("galaxy@example.com")
            .subject("Your Boarding Pass is Ready")
            .addInlineAttachment("logo.png", logo, "image/png", "<logo@quarkus.io>")
            .addAttachment("boarding-pass.pdf", boardingPass, "application/pdf")
            .send();**/

    }

}
