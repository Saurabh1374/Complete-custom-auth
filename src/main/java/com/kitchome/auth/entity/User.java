package com.kitchome.auth.entity;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kitchome.auth.util.Role;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String username;
    private String email;
    private String password;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Set<Role> roles=new HashSet<>();;
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<RefreshToken> sessions;


    // Getters and Setters
    @JsonProperty("roles")
    public void setRoles(Set<Role> roles) { this.roles = roles; }
    
    public void setRoles (Role role){this.roles.add(role);}
}

