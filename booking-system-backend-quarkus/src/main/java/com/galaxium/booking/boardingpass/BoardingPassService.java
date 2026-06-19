package com.galaxium.booking.boardingpass;

import com.galaxium.booking.dto.UserDto;
import com.galaxium.booking.service.MailService;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class BoardingPassService {

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
        this.mailService.sendEmail(user, pass, boardingPass);

        return boardingObjectName;
    }

}
