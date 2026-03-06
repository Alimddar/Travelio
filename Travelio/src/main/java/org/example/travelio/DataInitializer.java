package org.example.travelio;

import lombok.RequiredArgsConstructor;
import org.example.travelio.Entities.Role;
import org.example.travelio.Repositories.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    private final RoleRepository roleRepository;

    @Override
    public void run(String... args) throws Exception {
        Stream.of("ROLE_ADMIN", "ROLE_USER")
                .filter(name -> !roleRepository.existsByName(name))
                .forEach(name -> {
                    Role role = new Role();
                    role.setName(name);
                    role.setUsers(new HashSet<>());
                    roleRepository.save(role);
                });
    }
}
