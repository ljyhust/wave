package com.wave.trip.dto.req;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class TripNewReqDto implements Serializable{
    private static final long serialVersionUID = -4669253468159501189L;

    @NotBlank
    private String account;

    @NotNull
    private Double sourceLongit;

    @NotNull
    private Double sourceLatit;

    @NotNull
    private Double destLongit;

    @NotNull
    private Double destLatit;
}
