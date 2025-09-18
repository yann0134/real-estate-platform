package com.realestate.service;

import org.springframework.security.core.userdetails.UserDetailsService;

public interface AuthUserDetailsService extends UserDetailsService {
    // Cette interface hérite de UserDetailsService
    // et pourra être étendue si nécessaire
}
