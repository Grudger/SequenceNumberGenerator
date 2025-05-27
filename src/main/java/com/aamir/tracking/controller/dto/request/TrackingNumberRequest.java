package com.aamir.tracking.controller.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Getter
@Valid
@AllArgsConstructor
@Setter
public class TrackingNumberRequest {

    @NotNull
    @JsonProperty("sourceCountry")
    private String sourceCountry;
    @NotNull
    @JsonProperty("destinationCountry")
    private String destinationCountry;
    // weight in KGs
    @NotEmpty
    @NotNull
    @JsonProperty("weight")
    private String weight;
    @NotEmpty
    @NotNull
    @Pattern(regexp = "^[A-Z0-9]{16}$")
    @JsonProperty("customerId")
    private String customerId;
    @NotEmpty
    @NotNull
    @Pattern(regexp = "^[a-z A-Z0-9_-]{3,50}$")
    @JsonProperty("customerName")
    private String customerName;
}
