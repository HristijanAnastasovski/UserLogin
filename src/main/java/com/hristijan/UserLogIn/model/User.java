package com.hristijan.UserLogIn.model;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;
import javax.validation.constraints.NotNull;


@Entity
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long userId;

    public String email;

    public String password;

    public String firstName;

    public String lastName;

    public Role role;

    public boolean isActive;

    @ManyToOne
    public Department department;


    public String forgotPassword()
    {
        String generatedPassword = generateNewRandomPassword();
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        this.password = passwordEncoder.encode(generatedPassword);
        return generatedPassword;
    }

    private String generateNewRandomPassword()
    {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*";
        return RandomStringUtils.random( 15, characters );
    }

    public User changeRole(Role newRole)
    {

        this.role = newRole;
        return this;
    }

    public User activateUser()
    {
        this.isActive=true;
        return this;
    }

    public boolean passwordsMatch(String newPassword)
    {
        return password.equals(newPassword);
    }


    public void startedWorkingAtDepartment(Department department)
    {
        this.department = department;
        this.role = Role.EMPLOYEE;
    }

    public void startedManagingDepartment(Department department)
    {
        this.department = department;
        this.role = Role.MANAGER;
    }





}
