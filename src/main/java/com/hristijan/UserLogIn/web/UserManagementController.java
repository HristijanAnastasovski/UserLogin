package com.hristijan.UserLogIn.web;

import com.hristijan.UserLogIn.model.Exception.*;
import com.hristijan.UserLogIn.model.User;
import com.hristijan.UserLogIn.service.UserManagementService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.servlet.ServletException;
import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class UserManagementController {
    public final UserManagementService userManagementService;

    public UserManagementController(UserManagementService userManagementService) {
        this.userManagementService = userManagementService;
    }

    @RequestMapping(value = "/forgotPassword", method = RequestMethod.POST)
    public String userForgotPassword(@RequestParam("email") String email) throws UserNotFoundException, MessagingException, UserNotActivatedException, ServletException {
        try {
            userManagementService.userForgotPassword(email);
        }catch (UserNotFoundException e) {
            return "Email not found!";
        }
        return "Forgotten password changed";
    }

    @RequestMapping(value="/changePassword", method = RequestMethod.POST)
    public String changePassword(@RequestParam("oldPassword") String oldPassword, @RequestParam("newPassword") String newPassword) throws UserNotFoundException, IncorrectOldPassword, MessagingException, NoDifferenceBetweenPasswordsException, ServletException {
        userManagementService.changePassword(oldPassword,newPassword);
        return "Password changed";
    }

    @RequestMapping(value="/addNewRoleToUser", method = RequestMethod.POST)
    public String addNewRoleToUser(@RequestParam("email") String email, @RequestParam("role") String role) throws RoleNotFoundException, UserNotFoundException {
        userManagementService.addNewRoleToUser(email,role);
        return "Role of user "+email+" changed to "+role;
    }

    @RequestMapping(value = "/addUserToDepartment", method = RequestMethod.POST)
    public String addUserToDepartment(@RequestParam("userId") Long userId, @RequestParam("departmentId") Long departmentId) throws UserNotFoundException, DepartmentNotFoundException {
        userManagementService.addUserToDepartment(userId,departmentId);
        return "User added to Department";
    }


    @RequestMapping(value = "/allEmployees", method = RequestMethod.POST, produces = "application/json")
    public List<User> getAllEmployees(@RequestParam("email") String email, @RequestParam("password") String password) throws UserNotFoundException, AccessNotGrantedException, WrongUserCredentialsException {
        return userManagementService.getAllEmployees(email,password);
    }

}
