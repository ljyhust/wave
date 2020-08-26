package com.wave.trip.dto.req;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Slf4j
@Data
public class TripDiscardReqDto implements Serializable{
    private static final long serialVersionUID = 1379904737081528789L;

    @NotBlank
    private String account;
    @NotNull
    private Long tripOrderId;
    @NotBlank
    private String discardReason;
}
