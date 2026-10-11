package com.Gways.gateway.dto;


import java.io.Serializable;
import java.time.LocalDateTime;

public record ServerStatus(String status, Long latencyMs, String detail, LocalDateTime checkedAt) implements Serializable {

}
