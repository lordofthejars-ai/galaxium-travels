package com.galaxium.booking.boardingpass;


import com.galaxium.booking.dto.UserDto;
import com.galaxium.booking.service.MailService;
import io.quarkiverse.mailpit.test.InjectMailbox;
import io.quarkiverse.mailpit.test.Mailbox;
import io.quarkiverse.mailpit.test.WithMailbox;
import io.quarkiverse.mailpit.test.invoker.ApiException;
import io.quarkiverse.mailpit.test.model.Message;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
@WithMailbox
public class MailServiceTest {

    @Inject
    MailService mailService;

    @InjectMailbox
    Mailbox mailbox;

    @AfterEach
    public void afterEach() throws ApiException {
        // clear the mailbox after each test run if you prefer
        mailbox.clear();
    }

    @Test
    public void shouldSendBoardingPass() {


        Message message = mailbox.findFirst("subject:Your Boarding Pass is Ready");
        assertThat(message).isNotNull();

    }

}
