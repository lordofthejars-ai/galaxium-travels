package com.galaxium.booking.service;

import com.galaxium.booking.boardingpass.BoardingPassData;
import com.galaxium.booking.boardingpass.BoardingPassPdfGenerator;
import com.galaxium.booking.boardingpass.BoardingPassStorage;
import com.galaxium.booking.boardingpass.QRCodeService;
import com.galaxium.booking.dto.UserDto;
import com.galaxium.booking.signal.EmailMessage;
import com.galaxium.booking.signal.MailService;
import io.quarkus.signals.Signal;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

@ApplicationScoped
public class BoardingPassService {

    @Inject
    Logger logger;

    @Inject
    Signal<EmailMessage> sendEmail;

    private final BoardingPassPdfGenerator boardingPassPdfGenerator;
    private final QRCodeService qrCodeService;
    private final BoardingPassStorage boardingPassStorage;
    private final MailService mailService;

    public BoardingPassService(BoardingPassPdfGenerator boardingPassPdfGenerator,
                               QRCodeService qrCodeService,
                               BoardingPassStorage boardingPassStorage,
                               MailService mailService) {
        this.boardingPassPdfGenerator = boardingPassPdfGenerator;
        this.qrCodeService = qrCodeService;
        this.boardingPassStorage = boardingPassStorage;
        this.mailService = mailService;
    }

    public String checkin(UserDto user, BoardingPassData pass) {
        var qr =
            qrCodeService.generate(pass);

        var boardingPass = boardingPassPdfGenerator.generate(
            pass,
            qr);

        String boardingObjectName =
            pass.passengerName()
                .replaceAll("\\s+", "_") + "_" + pass.bookingId();

        this.boardingPassStorage
            .storeBoardingPass(boardingObjectName, boardingPass);

        logger.info("Sending Boarding Pass");
        sendEmail.send(new EmailMessage(user, pass, boardingPass));
        //this.mailService.sendEmail(user, pass, boardingPass);

        return boardingObjectName;
    }

}
