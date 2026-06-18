package com.galaxium.booking.boardingpass;


import jakarta.enterprise.context.ApplicationScoped;
import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;

@ApplicationScoped
public class BoardingPassPdfGenerator {

    private static final float PAGE_WIDTH = 1700;
    private static final float PAGE_HEIGHT = 680;

    public byte[] generate(
        BoardingPassData pass,
        BufferedImage qrCode)
    {

        try (PDDocument document = new PDDocument();
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            PDPage page =
                new PDPage(
                    new PDRectangle(
                        PAGE_WIDTH,
                        PAGE_HEIGHT));

            document.addPage(page);

            try (PDPageContentStream content =
                     new PDPageContentStream(
                         document,
                         page)) {

                drawTemplate(document, content);

                drawFields(content, pass);

                drawQr(document, content, qrCode);
            }

            document.save(baos);
            baos.flush();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private void drawTemplate(
        PDDocument document,
        PDPageContentStream content)
        throws Exception {

        InputStream template =
            getClass().getResourceAsStream(
                "/boarding-pass-template.png");

        BufferedImage image =
            ImageIO.read(template);

        PDImageXObject bg =
            LosslessFactory.createFromImage(
                document,
                image);

        content.drawImage(
            bg,
            0,
            0,
            PAGE_WIDTH,
            PAGE_HEIGHT);
    }

    private void drawFields(
        PDPageContentStream content,
        BoardingPassData pass)
        throws Exception {

        PDType1Font font =
            new PDType1Font(
                Standard14Fonts.FontName.HELVETICA_BOLD);

        content.beginText();
        content.setFont(font, 24);

        write(content, pass.passengerName(), 165, 385);
        write(content, pass.flightId(), 686, 385);

        write(content, pass.origin(), 165, 285);
        write(content, pass.destination(), 686, 285);

        write(
            content,
            pass.departureTime()
                .format(
                    DateTimeFormatter.ofPattern(
                        "yyyy-MM-dd HH:mm")),
            165,
            190);

        write(content, pass.bookingId(), 686, 190);

        write(content, pass.tripClass(), 1400, 485);

        content.endText();
    }

    private void write(
        PDPageContentStream content,
        String text,
        float x,
        float y)
        throws Exception {

        content.newLineAtOffset(x, y);
        content.setNonStrokingColor(
            Color.WHITE);
        content.showText(text);
        content.newLineAtOffset(-x, -y);
    }

    private void drawQr(
        PDDocument document,
        PDPageContentStream content,
        BufferedImage qr)
        throws Exception {

        PDImageXObject qrImage =
            LosslessFactory.createFromImage(
                document,
                qr);

        content.drawImage(
            qrImage,
            1332,
            160,
            285,
            285);
    }
}