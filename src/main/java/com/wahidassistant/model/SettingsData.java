package com.wahidassistant.model;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * This class represents the settings data for a user.
 * Includes preferredtransportation, arrival time, URL, address, postalcode
 * Author: Amer
 */
@AllArgsConstructor
@Data
public class SettingsData {
    private PreferredTransportation preferredTransportation;
    private String arrivalTimeOffset;
    private String url;
    private String address;
    private String postalCode;
}
