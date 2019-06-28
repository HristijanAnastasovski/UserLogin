package com.hristijan.UserLogIn.model;

public class DepartmentFactory {
    public static Department create(String departmentName, User departmentManager){
        Department department = new Department();
        department.departmentName= departmentName;
        department.departmentManager = departmentManager;
        return department;
    }
}
