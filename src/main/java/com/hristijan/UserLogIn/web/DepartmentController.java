package com.hristijan.UserLogIn.web;


import com.hristijan.UserLogIn.model.Exception.DepartmentNotFoundException;
import com.hristijan.UserLogIn.model.Exception.DepartmentWithSameNameExistsException;
import com.hristijan.UserLogIn.model.Exception.UserNotFoundException;
import com.hristijan.UserLogIn.model.User;
import com.hristijan.UserLogIn.service.DepartmentManagementService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class DepartmentController {
    public final DepartmentManagementService departmentManagementService;

    public DepartmentController(DepartmentManagementService departmentManagementService) {
        this.departmentManagementService = departmentManagementService;
    }

    @RequestMapping(value = "/createNewDepartment",method = RequestMethod.POST)
    public String createNewDepartment(@RequestParam("departmentName") String departmentName, @RequestParam("userId") Long userId) throws UserNotFoundException, DepartmentWithSameNameExistsException {
        departmentManagementService.createNewDepartment(departmentName,userId);
        return "Department created";
    }


    @RequestMapping(value = "/changeDepartmentManager",method = RequestMethod.POST)
    public String changeDepartmentManager(@RequestParam("departmentId") Long departmentId, @RequestParam("userId") Long userId) throws UserNotFoundException, DepartmentNotFoundException {
        departmentManagementService.changeManagerOfDepartment(departmentId,userId);
        return "Department manager changed";
    }


}
