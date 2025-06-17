package app.controller;

import app.model.Student;
import app.store.HazelcastStore;
import app.store.MongoStore;
import app.store.RedisStore;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class DataInitializer {
    
    private static final String[] FIRST_NAMES = {
        "Ahmet", "Mehmet", "Ali", "Mustafa", "Hasan", "İbrahim", "Osman", "Süleyman", "Yusuf", "Ömer",
        "Fatma", "Ayşe", "Emine", "Hatice", "Zeynep", "Elif", "Meryem", "Sultan", "Büşra", "Esra",
        "Münip", "Nağme", "Aysun", "Berk", "Can", "Deniz", "Ece", "Furkan", "Gizem", "Hakan"
    };
    
    private static final String[] LAST_NAMES = {
        "Yılmaz", "Kaya", "Demir", "Şahin", "Çelik", "Özkan", "Arslan", "Doğan", "Kılıç", "Aslan",
        "Utandı", "Yarkın", "Gültekin", "Özdemir", "Koç", "Erdoğan", "Güngör", "Korkmaz", "Özkan", "Polat",
        "Aydın", "Bulut", "Karaca", "Öztürk", "Aksoy", "Tekin", "Gürkan", "Başaran", "Çetin", "Kara"
    };
    
    private static final String[] DEPARTMENTS = {
        "Classical Turkish Music",
        "Turkish Folk Music", 
        "Computer Engineering",
        "Electrical Engineering",
        "Mechanical Engineering",
        "Civil Engineering",
        "Industrial Engineering",
        "Business Administration",
        "Economics",
        "Mathematics",
        "Physics",
        "Chemistry",
        "Biology",
        "Psychology",
        "Turkish Language and Literature",
        "History",
        "Philosophy",
        "Fine Arts",
        "Architecture",
        "Medicine"
    };
    
    private static final Random random = new Random();
    
   
    public static void initializeAllDatabases(RedisStore redisStore, 
                                            HazelcastStore hazelcastStore, 
                                            MongoStore mongoStore) {
        
        System.out.println("Starting data initialization...");
        
       
        redisStore.clearAllStudents();
        hazelcastStore.clearAllStudents();
        mongoStore.clearAllStudents();
        
        List<Student> students = generateStudents(10000);
        
        System.out.println("Generated 10,000 students. Inserting into databases...");
        
      
        long startTime = System.currentTimeMillis();
        for (Student student : students) {
            redisStore.saveStudent(student);
        }
        long redisTime = System.currentTimeMillis() - startTime;
        System.out.println("Redis insertion completed in " + redisTime + "ms");
        
        
        startTime = System.currentTimeMillis();
        for (Student student : students) {
            hazelcastStore.saveStudent(student);
        }
        long hazelcastTime = System.currentTimeMillis() - startTime;
        System.out.println("Hazelcast insertion completed in " + hazelcastTime + "ms");
        
       
        startTime = System.currentTimeMillis();
        for (Student student : students) {
            mongoStore.saveStudent(student);
        }
        long mongoTime = System.currentTimeMillis() - startTime;
        System.out.println("MongoDB insertion completed in " + mongoTime + "ms");
        
        System.out.println("Data initialization completed!");
        System.out.println("Insertion Performance Summary:");
        System.out.println("  Redis: " + redisTime + "ms");
        System.out.println("  Hazelcast: " + hazelcastTime + "ms");
        System.out.println("  MongoDB: " + mongoTime + "ms");
    }
    
    private static List<Student> generateStudents(int count) {
        List<Student> students = new ArrayList<>();
        
        for (int i = 1; i <= count; i++) {
            String studentNo = String.format("2025%06d", i);
            String firstName = FIRST_NAMES[random.nextInt(FIRST_NAMES.length)];
            String lastName = LAST_NAMES[random.nextInt(LAST_NAMES.length)];
            String fullName = firstName + " " + lastName;
            String department = DEPARTMENTS[random.nextInt(DEPARTMENTS.length)];
            
            Student student = new Student(studentNo, fullName, department);
            students.add(student);
        }
        
        return students;
    }
    
    
    public static List<Student> generateSampleStudents() {
        List<Student> students = new ArrayList<>();
        
        students.add(new Student("2025000001", "Münip Utandı", "Classical Turkish Music"));
        students.add(new Student("2025000002", "Nağme Yarkın", "Classical Turkish Music"));
        students.add(new Student("2025000003", "Aysun Gültekin", "Turkish Folk Music"));
        students.add(new Student("2025000004", "Ahmet Yılmaz", "Computer Engineering"));
        students.add(new Student("2025000005", "Ayşe Kaya", "Electrical Engineering"));
        
        return students;
    }
}
