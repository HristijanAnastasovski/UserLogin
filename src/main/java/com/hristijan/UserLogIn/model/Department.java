package com.hristijan.UserLogIn.model;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "department")
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long departmentId;

    public String departmentName;

    @OneToOne
    public User departmentManager;


    public void changeManagerOfDepartment(User newManager){
        departmentManager= newManager;
    }


}
