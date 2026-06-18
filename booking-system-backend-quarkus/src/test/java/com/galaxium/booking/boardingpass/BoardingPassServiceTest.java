package com.galaxium.booking.boardingpass;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

@QuarkusTest
public class BoardingPassServiceTest {

    @Inject
    BoardingPassService boardingPassService;

    @Inject
    BoardingPassStorage boardingPassStorage;

    @Test
    public void shouldGenerateAndStoreBoardingPass() {

        BoardingPassData pass =
            new BoardingPassData(
                "Captain Nova",
                "EARTH",
                "MARS COLONY",
                "GT-2085-042",
                "BK-9XZ73K",
                "FIRST CLASS",
                LocalDateTime.of(
                    2085,
                    7,
                    14,
                    8,
                    45)
            );

        String id = boardingPassService.checkin(pass);
        assertThat(id).isNotBlank();

        byte[] boardingPass = boardingPassStorage.getBoardingPass(id);
        assertThat(boardingPass).isNotNull().hasSizeGreaterThan(0);


    }

}
