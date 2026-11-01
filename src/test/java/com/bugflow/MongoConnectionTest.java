package com.bugflow;

import org.bson.Document;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@EnabledIfEnvironmentVariable(named = "MONGODB_URI", matches = "^mongodb.*")
class MongoConnectionTest {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Test
    void testMongoConnection() {
        assertNotNull(mongoTemplate, "MongoTemplate should be initialized");
        Document pingResult = mongoTemplate.executeCommand(new Document("ping", 1));
        assertNotNull(pingResult, "Ping result must not be null");
        Number ok = (Number) pingResult.get("ok");
        assertTrue(ok != null && ok.doubleValue() == 1.0, "Database ping should return ok: 1");
        System.out.println("[MONGO_CHECK] Database ping successful");
        System.out.println("[MONGO_CHECK] Database name: " + mongoTemplate.getDb().getName());
        System.out.println("[MONGO_CHECK] Collections found: " + mongoTemplate.getCollectionNames().size());
    }
}
