package com.aamir.tracking.util;

import lombok.Getter;

@Getter
public enum Country {

    MY("MY"),
    SG("SG"),
    ID("ID"),
    TH("TH"),
    US("US"),
    BR("BR"),
    IN("IN"),
    CN("CN"),
    VN("VN");

    private final String code;
    Country(String code) {
        this.code = code;
    }

}
