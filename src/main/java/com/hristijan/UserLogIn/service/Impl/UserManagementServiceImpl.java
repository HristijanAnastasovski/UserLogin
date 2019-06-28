package com.hristijan.UserLogIn.service.Impl;

import com.hristijan.UserLogIn.model.Department;
import com.hristijan.UserLogIn.model.Exception.*;
import com.hristijan.UserLogIn.repository.jpa.DepartmentRepository;
import com.hristijan.UserLogIn.repository.jpa.TokenRepository;
import com.hristijan.UserLogIn.repository.jpa.UserRepository;
import com.hristijan.UserLogIn.repository.mail.MailSenderRepository;
import com.hristijan.UserLogIn.model.Role;
import com.hristijan.UserLogIn.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Transactional
@Service
public class UserManagementServiceImpl implements com.hristijan.UserLogIn.service.UserManagementService {
    private final UserRepository userRepository;
    private final MailSenderRepository mailSenderRepository;
    private final DepartmentRepository departmentRepository;
    private PasswordEncoder passwordEncoder;
    @Autowired
    private HttpServletRequest httpServletRequest;

    @Autowired
    public KafkaTemplate<String,User> kafkaTemplate;

    public UserManagementServiceImpl(UserRepository userRepository, TokenRepository tokenRepository, MailSenderRepository mailSenderRepository, DepartmentRepository departmentRepository) {
        this.userRepository = userRepository;
        this.departmentRepository = departmentRepository;
        this.mailSenderRepository = mailSenderRepository;
        passwordEncoder = new BCryptPasswordEncoder();

    }

    @Override
    public void userForgotPassword(String email) throws UserNotFoundException, UserNotActivatedException, MessagingException, ServletException {
        User user = this.userRepository.findByEmail(email);
        if(user==null)
            throw new UserNotFoundException();
        if(!user.isActive)
        {
            throw new UserNotActivatedException();
        }
        String generatedPassword = user.forgotPassword();
        mailSenderRepository.sendMail(user.email,"Forgotten password changed","New password: "+generatedPassword);
       // httpServletRequest.logout();
        kafkaTemplate.send("USER_TOPIC", user);
    }



    @Override
    public void changePassword(String oldPassword, String newPassword) throws UserNotFoundException, NoDifferenceBetweenPasswordsException, MessagingException, IncorrectOldPassword, ServletException {
        User user = null;
        String email = null;
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if(principal instanceof UserDetails)
        {
            email = ((UserDetails)principal).getUsername();

        }
        else
        {
            email = principal.toString();
        }

        if(email != null)
        {
            user = this.userRepository.findByEmail(email);
        }
        if(user==null)
            throw new UserNotFoundException();
        String encodedNewPassword = passwordEncoder.encode(newPassword);
        if(!passwordEncoder.matches(oldPassword,user.password))
            throw new IncorrectOldPassword();
        if(user.passwordsMatch(encodedNewPassword))
            throw new NoDifferenceBetweenPasswordsException();
        user.password= encodedNewPassword;
        mailSenderRepository.sendMail(user.email,"Your password has been changed","Please contact us if you didn't make this change");
        //httpServletRequest.logout();
        new SecurityContextLogoutHandler().logout(httpServletRequest, null, null);

        kafkaTemplate.send("USER_TOPIC", user);
    }

    @Override
    public void editUserWithId(Long userId, String firstName, String lastName) throws UserNotFoundException {
        User user = this.userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        user.firstName = firstName;
        user.lastName = lastName;
        kafkaTemplate.send("USER_TOPIC", user);
    }

    @Override
    public void addUserToDepartment(Long userId, Long departmentId) throws DepartmentNotFoundException, UserNotFoundException {
        User user = this.userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        Department department = this.departmentRepository.findById(departmentId).orElseThrow(DepartmentNotFoundException::new);
        user.startedWorkingAtDepartment(department);
        kafkaTemplate.send("USER_TOPIC", user);
    }

    @Override
    public User deleteUserWithId(Long userId) throws UserNotFoundException {
        User user = this.userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        this.userRepository.delete(user);
        return user;
    }

    @Override
    public void addNewRoleToUser(String email, String role) throws UserNotFoundException, RoleNotFoundException {
        User user = this.userRepository.findByEmail(email);
        if(user==null)
            throw new UserNotFoundException();


        for(Role r : Role.values())
        {
            if(r.toString().equals(role))
            {
                user.changeRole(r);
                kafkaTemplate.send("USER_TOPIC", user);
                return;
            }
        }

        throw new RoleNotFoundException();


    }


    public List<User> getAllEmployees(String email, String password) throws UserNotFoundException, AccessNotGrantedException, WrongUserCredentialsException{

        /*Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
       // String email = "admin@gmail.com";
        String email = "";
        if(principal instanceof UserDetails)
        {
            email = ((UserDetails)principal).getUsername();

        }
        else
        {
            email = principal.toString();
        }*/

        User user = this.userRepository.findByEmail(email);
        if(user == null)
            throw new UserNotFoundException();
        if(!passwordEncoder.matches(password,user.password)){
            throw new WrongUserCredentialsException();
        }

        if(user.role== Role.ADMIN)
            return this.userRepository.findAll()
                    .stream()
                    .filter(u -> (u.role != Role.USER) && (u.isActive))
                    .collect(Collectors.toList());

        if(user.role == Role.MANAGER)
            return this.userRepository.findAll()
                    .stream()
                    .filter(u -> (u.department == user.department) && (u.isActive))
                    .collect(Collectors.toList());

        throw new AccessNotGrantedException();
    }
}
