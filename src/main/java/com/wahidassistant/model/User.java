package com.wahidassistant.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * This class represents a user in the system with attributes like role, username, password,
 * schedule reference, custom events, hidden events, and settings data.
 * Author: Pehr
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document
public class User implements UserDetails {
    @Id
    private String id; // Identifier for the user
    private Role role; // Role of user
    private String username; // Username of the user
    private String password; // Password of the user


    private String scheduleIdRef; // Reference to users schedule
    private ArrayList<Event> customEvents; // List of custom events
    private ArrayList<Event> hiddenEvents; // List of hidden events

    private int timeToBikeToUniversity; // This sets in minutes when address is updated. No it doesnt
    private SettingsData settingsData; // Settings data for the user

    // Returns the authorities granted to the user
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() { 
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    // Returns the password used to authenticate the user
    @Override
    public String getPassword() {
        return password;
    }

  // Returns the username used to authenticate the user
    @Override
    public String getUsername() {
        return username;
    }

    // Indicates if the users account has expired
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    // Indicates if the user is locked or unlocked
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    //Indicates if the user credentials has expired
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    // Indicates wheter the user is enabled or disabled
    @Override
    public boolean isEnabled() {
        return true;
    }

}
