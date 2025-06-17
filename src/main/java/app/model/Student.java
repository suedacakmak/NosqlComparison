package app.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Objects;

@Document(collection = "students")
public class Student implements Serializable {
    
    @Id
    @JsonProperty("student_no")
    private String studentNo;
    
    @JsonProperty("name")
    private String name;
    
    @JsonProperty("department")
    private String department;
    
    public Student() {}
   
    public Student(String studentNo, String name, String department) {
        this.studentNo = studentNo;
        this.name = name;
        this.department = department;
    }
  
    public String getStudentNo() {
        return studentNo;
    }
    
    public void setStudentNo(String studentNo) {
        this.studentNo = studentNo;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDepartment() {
        return department;
    }
    
    public void setDepartment(String department) {
        this.department = department;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Student student = (Student) o;
        return Objects.equals(studentNo, student.studentNo);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(studentNo);
    }
    
    @Override
    public String toString() {
        return "Student{" +
                "studentNo='" + studentNo + '\'' +
                ", name='" + name + '\'' +
                ", department='" + department + '\'' +
                '}';
    }
}
