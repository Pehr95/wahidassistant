package com.wahidassistant.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class SettingsData {
    private PreferredTransportation preferredTransportation;
    private String arrivalTimeOffset;
    private String url;
    private String address;
    private String postalCode;
}
