package com.kitchome.auth.payload.projection;

import com.kitchome.auth.util.Role;
/*
*mapping relations in the table
* role------1:n------>users
*/
public interface RolesProjection {
    Role getRole();
}
