package com.Gways.gateway.dto;

import java.time.ZonedDateTime;

public record BetweenDto(
        ZonedDateTime dateTime1,
        ZonedDateTime dateTime2
) {
}