package com.trygod.prophiusassessment.config;

import com.trygod.prophiusassessment.data.UserData;
import com.trygod.prophiusassessment.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserData> user = userRepository.findByEmailIgnoreCase(username);
        return user.map(CustomUserDetails::new).orElseThrow(() -> new UsernameNotFoundException("Username not found " + username));
    }
}
