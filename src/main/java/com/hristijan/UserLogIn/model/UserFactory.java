package com.hristijan.UserLogIn.model;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class UserFactory {
    public static User create(String email, String password, String firstName, String lastName)
    {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        User u = new User();
        u.email=email;
        u.password= passwordEncoder.encode(password);
        u.firstName=firstName;
        u.lastName=lastName;
        u.role=Role.USER;
        u.isActive=false;
        u.department = null;

        return u;
    }
}
