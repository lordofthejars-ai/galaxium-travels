package com.galaxium.booking.service;

import com.galaxium.booking.boardingpass.BoardingPassData;
import com.galaxium.booking.dto.UserDto;
import com.galaxium.booking.entity.User;
import io.quarkus.mailer.Mail;
import io.quarkus.mailer.MailTemplate;
import io.quarkus.mailer.Mailer;
import io.quarkus.qute.CheckedTemplate;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

import java.io.File;

@ApplicationScoped
public class MailService {

    @Inject
    Mailer mailer;

    @Inject
    Logger logger;

    static File logo = new File("logogalaxy.png");

    @CheckedTemplate
    static class Templates {
        public static native MailTemplate.MailTemplateInstance boardingPass(UserDto userDto, BoardingPassData boardingPassData);
    }

    public void sendEmail(UserDto user, BoardingPassData boardingPassData, byte[] boardingPass) {
        Templates.boardingPass(user, boardingPassData)
            .to(user.email)
            .from("galaxy@example.com")
            .subject("Your Boarding Pass is Ready")
            .addInlineAttachment("logo.png", logo,"image/png", "<logo@quarkus.io>")
            .addAttachment("boarding-pass.pdf", boardingPass, "application/pdf")
            .send();
        //mailer.send(Mail.withText("to@acme.org", "A simple email from quarkus", "This is my body.").setFrom("from@acme.org"));
        System.out.println("Mail 3 sent");

    }

}
