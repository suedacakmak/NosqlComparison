package app.store;

import app.model.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class MongoStore {
    
    private static final String COLLECTION_NAME = "students";
    
    @Autowired
    private MongoTemplate mongoTemplate;
    
    public void saveStudent(Student student) {
        try {
            mongoTemplate.save(student, COLLECTION_NAME);
        } catch (Exception e) {
            throw new RuntimeException("Error saving student to MongoDB: " + e.getMessage(), e);
        }
    }
    
    public Student getStudent(String studentNo) {
        try {
            Query query = new Query(Criteria.where("studentNo").is(studentNo));
            return mongoTemplate.findOne(query, Student.class, COLLECTION_NAME);
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving student from MongoDB: " + e.getMessage(), e);
        }
    }
    
  
    public boolean deleteStudent(String studentNo) {
        try {
            Query query = new Query(Criteria.where("studentNo").is(studentNo));
            return mongoTemplate.remove(query, Student.class, COLLECTION_NAME).getDeletedCount() > 0;
        } catch (Exception e) {
            throw new RuntimeException("Error deleting student from MongoDB: " + e.getMessage(), e);
        }
    }
    
  
    public boolean existsStudent(String studentNo) {
        try {
            Query query = new Query(Criteria.where("studentNo").is(studentNo));
            return mongoTemplate.exists(query, Student.class, COLLECTION_NAME);
        } catch (Exception e) {
            throw new RuntimeException("Error checking student existence in MongoDB: " + e.getMessage(), e);
        }
    }
    
 
    public long getStudentCount() {
        try {
            return mongoTemplate.count(new Query(), Student.class, COLLECTION_NAME);
        } catch (Exception e) {
            throw new RuntimeException("Error getting student count from MongoDB: " + e.getMessage(), e);
        }
    }
    
 
    public void clearAllStudents() {
        try {
            mongoTemplate.remove(new Query(), Student.class, COLLECTION_NAME);
        } catch (Exception e) {
            throw new RuntimeException("Error clearing students from MongoDB: " + e.getMessage(), e);
        }
    }
   
    public List<Student> getAllStudents() {
        try {
            return mongoTemplate.findAll(Student.class, COLLECTION_NAME);
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving all students from MongoDB: " + e.getMessage(), e);
        }
    }
   
    public List<Student> getStudentsByDepartment(String department) {
        try {
            Query query = new Query(Criteria.where("department").is(department));
            return mongoTemplate.find(query, Student.class, COLLECTION_NAME);
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving students by department from MongoDB: " + e.getMessage(), e);
        }
    }
}
