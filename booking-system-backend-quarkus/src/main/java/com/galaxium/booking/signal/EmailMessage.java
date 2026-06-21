package com.galaxium.booking.signal;

import com.galaxium.booking.boardingpass.BoardingPassData;
import com.galaxium.booking.dto.UserDto;

public record EmailMessage(UserDto userDto, BoardingPassData boardingPassData, byte[] boardingPass) {
}
