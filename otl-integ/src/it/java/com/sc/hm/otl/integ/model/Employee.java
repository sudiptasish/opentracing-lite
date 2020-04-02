/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.hm.otl.integ.model;

import java.util.Date;

/**
 *
 * @author Sudiptasish Chanda
 */
public class Employee {
    
    private String id;
    private String name;
    private String location;
    private Date joinDate;
    
    private String deptId;
    private Department dept;
    
    public Employee() {}

    public Employee(String id, String name, String location, Date joinDate) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.joinDate = joinDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Date getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(Date joinDate) {
        this.joinDate = joinDate;
    }

    public Department getDept() {
        return dept;
    }

    public void setDept(Department dept) {
        this.dept = dept;
        this.deptId = dept.getId();
    }

    public String getDeptId() {
        return deptId;
    }

    @Override
    public String toString() {
        return "[EmpId: " + id + ", EmpName: " + name + ", Location: " + location + "]";
    }
}
