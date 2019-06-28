package com.hristijan.UserLogIn.service;

import com.hristijan.UserLogIn.model.Exception.*;
import com.hristijan.UserLogIn.model.User;

import javax.mail.MessagingException;
import javax.servlet.ServletException;
import java.util.List;

public interface UserManagementService {
    void addNewRoleToUser(String email, String role) throws UserNotFoundException, RoleNotFoundException;

    //void sendNewPasswordToMail(String mail) throws MailNotFoundException;

    void userForgotPassword(String email) throws UserNotFoundException, UserNotActivatedException, MessagingException, ServletException;

    void changePassword(String oldPassword, String newPassword) throws UserNotFoundException, NoDifferenceBetweenPasswordsException, MessagingException, IncorrectOldPassword, ServletException;

    void editUserWithId(Long userId, String firstName, String lastName) throws UserNotFoundException;

    User deleteUserWithId(Long userId) throws UserNotFoundException;

    void addUserToDepartment(Long userId, Long departmentId) throws DepartmentNotFoundException, UserNotFoundException;

    List<User> getAllEmployees(String email, String password) throws UserNotFoundException, AccessNotGrantedException, WrongUserCredentialsException;



}
