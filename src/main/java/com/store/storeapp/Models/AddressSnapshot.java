package com.store.storeapp.Models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Embeddable;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
@Embeddable
public class AddressSnapshot {

    @NotBlank
    private String fullName;

    @NotBlank
    private String line1;

    private String line2;

    @NotBlank
    private String district;

    @NotBlank
    private String city;

    @NotBlank
    private String country;

    @NotBlank
    private String postalCode;

    @NotBlank
    private String phone;
}
