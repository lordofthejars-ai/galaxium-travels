package com.galaxium.booking.boardingpass;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class BoardingPassService {

    private final BoardingPassPdfGenerator boardingPassPdfGenerator;
    private final QRCodeService qrCodeService;
    private final BoardingPassStorage boardingPassStorage;

    public BoardingPassService(BoardingPassPdfGenerator boardingPassPdfGenerator,
                               QRCodeService qrCodeService,
                               BoardingPassStorage boardingPassStorage) {
        this.boardingPassPdfGenerator = boardingPassPdfGenerator;
        this.qrCodeService = qrCodeService;
        this.boardingPassStorage = boardingPassStorage;
    }

    public String checkin(BoardingPassData pass) {
        var qr =
            qrCodeService.generate(pass);

        var boardingPass = boardingPassPdfGenerator.generate(
            pass,
            qr);

        String boardingObjectName =
            pass.passengerName()
                .replaceAll("\\s+", "_") + "_" + pass.bookingId();

        this.boardingPassStorage.storeBoardingPass(boardingObjectName, boardingPass);
        return boardingObjectName;
    }

}
