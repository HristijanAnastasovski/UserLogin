package com.hristijan.UserLogIn.service;

import com.hristijan.UserLogIn.model.Exception.DepartmentNotFoundException;
import com.hristijan.UserLogIn.model.Exception.DepartmentWithSameNameExistsException;
import com.hristijan.UserLogIn.model.Exception.UserNotFoundException;
import com.hristijan.UserLogIn.model.User;


public interface DepartmentManagementService {
    void createNewDepartment(String departmentName, Long departmentManagerId) throws UserNotFoundException, DepartmentWithSameNameExistsException;

    void deleteDepartment(Long departmentId) throws DepartmentNotFoundException;

    void changeManagerOfDepartment(Long departmentId, Long userId) throws UserNotFoundException, DepartmentNotFoundException;
}
