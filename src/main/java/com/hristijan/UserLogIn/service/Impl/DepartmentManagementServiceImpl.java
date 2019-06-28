package com.hristijan.UserLogIn.service.Impl;

import com.hristijan.UserLogIn.model.Department;
import com.hristijan.UserLogIn.model.DepartmentFactory;
import com.hristijan.UserLogIn.model.Exception.DepartmentNotFoundException;
import com.hristijan.UserLogIn.model.Exception.DepartmentWithSameNameExistsException;
import com.hristijan.UserLogIn.model.Exception.UserNotFoundException;
import com.hristijan.UserLogIn.model.User;
import com.hristijan.UserLogIn.repository.jpa.DepartmentRepository;
import com.hristijan.UserLogIn.repository.jpa.UserRepository;
import com.hristijan.UserLogIn.service.DepartmentManagementService;
import org.springframework.stereotype.Service;

@Service
public class DepartmentManagementServiceImpl implements DepartmentManagementService {
    private final DepartmentRepository departmentRepository;
    private final UserRepository userRepository;

    public DepartmentManagementServiceImpl(DepartmentRepository departmentRepository, UserRepository userRepository) {
        this.departmentRepository = departmentRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void createNewDepartment(String departmentName, Long departmentManagerId) throws UserNotFoundException, DepartmentWithSameNameExistsException {
        if(this.departmentRepository.findByDepartmentName(departmentName)==null)
        {
            User user = this.userRepository.findById(departmentManagerId).orElseThrow(UserNotFoundException::new);
            Department department = DepartmentFactory.create(departmentName, user);
            this.departmentRepository.save(department);
            user.startedManagingDepartment(department);
            this.userRepository.save(user);

        }
        else
        throw new DepartmentWithSameNameExistsException();
    }

    @Override
    public void deleteDepartment(Long departmentId) throws DepartmentNotFoundException {
        Department department = this.departmentRepository.findById(departmentId).orElseThrow(DepartmentNotFoundException::new);
        this.departmentRepository.delete(department);
    }

    @Override
    public void changeManagerOfDepartment(Long departmentId, Long userId) throws UserNotFoundException, DepartmentNotFoundException {
        Department department = this.departmentRepository.findById(departmentId).orElseThrow(DepartmentNotFoundException::new);
        User user = this.userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        User oldManager = department.departmentManager;

        oldManager.startedWorkingAtDepartment(department);

        user.startedManagingDepartment(department);
        department.changeManagerOfDepartment(user);

        this.departmentRepository.save(department);
        this.userRepository.save(user);
        this.userRepository.save(oldManager);
    }
}
