package com.kitchome.auth.payload.projection;

import org.springframework.stereotype.Component;

import java.util.Set;
/*
* for propagating the user data
* across the application for authentication
* and authorisation
*
*/
@Component
public interface UserCredProjection {
    String getUsername();
    String getPassword();
    Set<RolesProjection> getRoles();
}
