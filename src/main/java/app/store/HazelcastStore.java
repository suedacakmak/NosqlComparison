package app.store;

import app.model.Student;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import jakarta.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;


@Repository
public class HazelcastStore {
    
    private static final String MAP_NAME = "students";
    
    @Autowired
    private HazelcastInstance hazelcastInstance;
    
    private IMap<String, Student> studentMap;
    
    @PostConstruct
    public void init() {
        studentMap = hazelcastInstance.getMap(MAP_NAME);
    }
   
    public void saveStudent(Student student) {
        try {
            studentMap.put(student.getStudentNo(), student, 24, TimeUnit.HOURS);
        } catch (Exception e) {
            throw new RuntimeException("Error saving student to Hazelcast: " + e.getMessage(), e);
        }
    }
    
   
    public Student getStudent(String studentNo) {
        try {
            return studentMap.get(studentNo);
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving student from Hazelcast: " + e.getMessage(), e);
        }
    }
    

    public boolean deleteStudent(String studentNo) {
        try {
            Student removed = studentMap.remove(studentNo);
            return removed != null;
        } catch (Exception e) {
            throw new RuntimeException("Error deleting student from Hazelcast: " + e.getMessage(), e);
        }
    }
    
    
    public boolean existsStudent(String studentNo) {
        try {
            return studentMap.containsKey(studentNo);
        } catch (Exception e) {
            throw new RuntimeException("Error checking student existence in Hazelcast: " + e.getMessage(), e);
        }
    }
    
  
    public long getStudentCount() {
        try {
            return studentMap.size();
        } catch (Exception e) {
            throw new RuntimeException("Error getting student count from Hazelcast: " + e.getMessage(), e);
        }
    }
    
   
    public void clearAllStudents() {
        try {
            studentMap.clear();
        } catch (Exception e) {
            throw new RuntimeException("Error clearing students from Hazelcast: " + e.getMessage(), e);
        }
    }
    
  
    public String getClusterInfo() {
        return "Cluster Name: " + hazelcastInstance.getConfig().getClusterName() +
               ", Instance Name: " + hazelcastInstance.getName() +
               ", Cluster Size: " + hazelcastInstance.getCluster().getMembers().size();
    }
}
