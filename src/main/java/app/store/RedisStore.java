package app.store;

import app.model.Student;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
public class RedisStore {
    
    private static final String KEY_PREFIX = "student:";
    
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
 
    public void saveStudent(Student student) {
        try {
            String key = KEY_PREFIX + student.getStudentNo();
            String studentJson = objectMapper.writeValueAsString(student);
            redisTemplate.opsForValue().set(key, studentJson, 24, TimeUnit.HOURS);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error saving student to Redis: " + e.getMessage(), e);
        }
    }
    
    public Student getStudent(String studentNo) {
        try {
            String key = KEY_PREFIX + studentNo;
            String studentJson = redisTemplate.opsForValue().get(key);
            
            if (studentJson == null) {
                return null;
            }
            
            return objectMapper.readValue(studentJson, Student.class);
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving student from Redis: " + e.getMessage(), e);
        }
    }
   
    public boolean deleteStudent(String studentNo) {
        String key = KEY_PREFIX + studentNo;
        return Boolean.TRUE.equals(redisTemplate.delete(key));
    }
   
    public boolean existsStudent(String studentNo) {
        String key = KEY_PREFIX + studentNo;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }
    
    public long getStudentCount() {
        return redisTemplate.keys(KEY_PREFIX + "*").size();
    }
   
    public void clearAllStudents() {
        redisTemplate.delete(redisTemplate.keys(KEY_PREFIX + "*"));
    }
}

