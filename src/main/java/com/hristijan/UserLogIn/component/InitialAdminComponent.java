package com.hristijan.UserLogIn.component;

import com.hristijan.UserLogIn.model.Role;
import com.hristijan.UserLogIn.model.User;
import com.hristijan.UserLogIn.model.UserFactory;
import com.hristijan.UserLogIn.repository.jpa.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Component
public class InitialAdminComponent {
    private final UserRepository userRepository;

    @Value("${app.admin.email}")
    public String adminEmail;

    @Value("${app.admin.password}")
    public String adminPassword;

    public InitialAdminComponent(UserRepository userRepository) {
        this.userRepository = userRepository;

    }

    @Bean
    public void initializeAdmin(){
        User admin = UserFactory.create(adminEmail, adminPassword,"Admin", "Admin");
        admin.role = Role.ADMIN;
        admin.isActive = true;
        this.userRepository.save(admin);
    }

}
