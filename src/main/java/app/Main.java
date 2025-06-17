package app;

import app.controller.DataInitializer;
import app.store.HazelcastStore;
import app.store.MongoStore;
import app.store.RedisStore;
import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@SpringBootApplication
public class Main implements CommandLineRunner {
    
    @Autowired
    private ApplicationContext applicationContext;
    
    public static void main(String[] args) {
        System.out.println("Starting NoSQL Performance Comparison Application...");
        SpringApplication.run(Main.class, args);
    }
    
    @Bean
    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new StringRedisSerializer());
        template.afterPropertiesSet();
        return template;
    }
    
   
    @Bean
    public HazelcastInstance hazelcastInstance() {
        Config config = new Config();
        config.setClusterName("nosql-lab-cluster");
        config.setInstanceName("nosql-lab-instance");
        
        config.getNetworkConfig().getJoin().getMulticastConfig().setEnabled(false);
        config.getNetworkConfig().getJoin().getTcpIpConfig().setEnabled(false);
       
        config.getMapConfig("students")
                .setTimeToLiveSeconds(86400) // 24 
                .setMaxIdleSeconds(3600);    // 1 
        
        return Hazelcast.newHazelcastInstance(config);
    }
    
    
    @Override
    public void run(String... args) throws Exception {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("NoSQL Performance Comparison Application Started!");
        System.out.println("=".repeat(60));
        
      
        RedisStore redisStore = applicationContext.getBean(RedisStore.class);
        HazelcastStore hazelcastStore = applicationContext.getBean(HazelcastStore.class);
        MongoStore mongoStore = applicationContext.getBean(MongoStore.class);
        
       
        printApplicationInfo();
        
        
        boolean autoInit = args.length > 0 && "init".equals(args[0]);
        
        if (autoInit) {
            System.out.println("\nAuto-initializing databases with test data...");
            try {
                DataInitializer.initializeAllDatabases(redisStore, hazelcastStore, mongoStore);
                printDatabaseCounts(redisStore, hazelcastStore, mongoStore);
            } catch (Exception e) {
                System.err.println("Failed to initialize data: " + e.getMessage());
            }
        } else {
            System.out.println("\nTo initialize databases with 10,000 test records:");
            System.out.println("POST http://localhost:8080/init-data");
            System.out.println("\nOr restart with 'java -jar app.jar init'");
        }
        
        printEndpoints();
        printTestCommands();
    }
    
    private void printApplicationInfo() {
        System.out.println("Server: http://localhost:8080");
        System.out.println("Databases configured:");
        System.out.println("  ✓ Redis (In-Memory Key-Value Store)");
        System.out.println("  ✓ Hazelcast (Distributed In-Memory Grid)");
        System.out.println("  ✓ MongoDB (Document Database)");
    }
    
    private void printDatabaseCounts(RedisStore redisStore, HazelcastStore hazelcastStore, MongoStore mongoStore) {
        try {
            System.out.println("\nDatabase Record Counts:");
            System.out.println("  Redis: " + redisStore.getStudentCount());
            System.out.println("  Hazelcast: " + hazelcastStore.getStudentCount());
            System.out.println("  MongoDB: " + mongoStore.getStudentCount());
        } catch (Exception e) {
            System.err.println("Error getting database counts: " + e.getMessage());
        }
    }
    
    private void printEndpoints() {
        System.out.println("\nAvailable Endpoints:");
        System.out.println("  GET  /nosql-lab-rd/student_no={id}   - Query Redis");
        System.out.println("  GET  /nosql-lab-hz/student_no={id}   - Query Hazelcast");
        System.out.println("  GET  /nosql-lab-mon/student_no={id}  - Query MongoDB");
        System.out.println("  GET  /health                         - Health Check");
        System.out.println("  POST /init-data                      - Initialize Data");
        System.out.println("  DELETE /clear-data                   - Clear All Data");
    }
    
    private void printTestCommands() {
        System.out.println("\nPerformance Test Commands:");
        System.out.println("\n1. Siege Load Testing (1000 requests, 10 concurrent):");
        System.out.println("siege -H \"Accept: application/json\" -c10 -r100 \"http://localhost:8080/nosql-lab-rd/student_no=2025000001\"");
        System.out.println("siege -H \"Accept: application/json\" -c10 -r100 \"http://localhost:8080/nosql-lab-hz/student_no=2025000001\"");
        System.out.println("siege -H \"Accept: application/json\" -c10 -r100 \"http://localhost:8080/nosql-lab-mon/student_no=2025000001\"");
        
        System.out.println("\n2. Time-based Testing (100 requests, 10 parallel):");
        System.out.println("time seq 1 100 | xargs -n1 -P10 -I{} curl -s \"http://localhost:8080/nosql-lab-rd/student_no=2025000001\"");
        System.out.println("time seq 1 100 | xargs -n1 -P10 -I{} curl -s \"http://localhost:8080/nosql-lab-hz/student_no=2025000001\"");
        System.out.println("time seq 1 100 | xargs -n1 -P10 -I{} curl -s \"http://localhost:8080/nosql-lab-mon/student_no=2025000001\"");
        
        System.out.println("\n" + "=".repeat(60));
    }
}
