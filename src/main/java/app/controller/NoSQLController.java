package app.controller;

import app.model.Student;
import app.store.HazelcastStore;
import app.store.MongoStore;
import app.store.RedisStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class NoSQLController {
    
    @Autowired
    private RedisStore redisStore;
    
    @Autowired
    private HazelcastStore hazelcastStore;
    
    @Autowired
    private MongoStore mongoStore;
    
    
    @GetMapping("/nosql-lab-rd/student_no={studentNo}")
    public ResponseEntity<Student> getStudentFromRedis(@PathVariable String studentNo) {
        try {
            Student student = redisStore.getStudent(studentNo);
            if (student != null) {
                return ResponseEntity.ok(student);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
   
    @GetMapping("/nosql-lab-hz/student_no={studentNo}")
    public ResponseEntity<Student> getStudentFromHazelcast(@PathVariable String studentNo) {
        try {
            Student student = hazelcastStore.getStudent(studentNo);
            if (student != null) {
                return ResponseEntity.ok(student);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
   
    @GetMapping("/nosql-lab-mon/student_no={studentNo}")
    public ResponseEntity<Student> getStudentFromMongoDB(@PathVariable String studentNo) {
        try {
            Student student = mongoStore.getStudent(studentNo);
            if (student != null) {
                return ResponseEntity.ok(student);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
   
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> health = new HashMap<>();
        
        try {
            health.put("redis_count", redisStore.getStudentCount());
            health.put("hazelcast_count", hazelcastStore.getStudentCount());
            health.put("mongodb_count", mongoStore.getStudentCount());
            health.put("status", "UP");
            
            return ResponseEntity.ok(health);
        } catch (Exception e) {
            health.put("status", "DOWN");
            health.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(health);
        }
    }
    
    
    @PostMapping("/init-data")
    public ResponseEntity<Map<String, Object>> initializeData() {
        try {
            DataInitializer.initializeAllDatabases(redisStore, hazelcastStore, mongoStore);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Data initialized successfully");
            response.put("redis_count", redisStore.getStudentCount());
            response.put("hazelcast_count", hazelcastStore.getStudentCount());
            response.put("mongodb_count", mongoStore.getStudentCount());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to initialize data: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
   
    @DeleteMapping("/clear-data")
    public ResponseEntity<Map<String, String>> clearAllData() {
        try {
            redisStore.clearAllStudents();
            hazelcastStore.clearAllStudents();
            mongoStore.clearAllStudents();
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "All data cleared successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to clear data: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}
