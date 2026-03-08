package org.example.travelio.Security;

import org.example.travelio.Entities.Role;
import org.example.travelio.Entities.UserAdmin;
import org.example.travelio.Repositories.UserAdminRepository;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.stream.Collectors;

@Configuration
public class CustomUserDetailService implements UserDetailsService {

    @Autowired
    UserAdminRepository userAdminRepository;

    @Override
    public UserDetails loadUserByUsername(@NonNull String username) throws UsernameNotFoundException {
        UserAdmin user = userAdminRepository.findByEmail(username)
                .orElseThrow(()-> new UsernameNotFoundException("User not found with email: " + username));

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                user.getRoles()
                        .stream()
                        .map(Role::getName)
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList())
        );
    }
}
