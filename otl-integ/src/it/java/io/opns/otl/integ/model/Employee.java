/*
 *     Copyright 2020 Opentracing-LiTE
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.opns.otl.integ.model;

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
