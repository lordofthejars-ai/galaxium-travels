package com.galaxium.booking.boardingpass;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import jakarta.enterprise.context.ApplicationScoped;

import java.awt.image.BufferedImage;

@ApplicationScoped
public class QRCodeService {

    public BufferedImage generate(BoardingPassData pass) {

        String payload =
            """
            {
               "name":"%s",
               "origin":"%s",
               "destination":"%s",
               "flightId":"%s",
               "bookingId":"%s",
               "class":"%s"
            }
            """.formatted(
                pass.passengerName(),
                pass.origin(),
                pass.destination(),
                pass.flightId(),
                pass.bookingId(),
                pass.tripClass()
            );

        BitMatrix matrix =
            null;
        try {
            matrix = new MultiFormatWriter().encode(
                payload,
                BarcodeFormat.QR_CODE,
                300,
                300
            );
        } catch (WriterException e) {
            throw new IllegalStateException(e);
        }

        return MatrixToImageWriter.toBufferedImage(matrix);
    }
}
