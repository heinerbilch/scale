package eu.bilch.scale.config;

import eu.bilch.scale.model.Post;
import eu.bilch.scale.model.User;
import eu.bilch.scale.repository.PostRepository;
import eu.bilch.scale.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.time.LocalDateTime;

@Configuration
@Profile("dev")
public class DataInitializer {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    @Bean
    public CommandLineRunner initData(PostRepository postRepository, UserRepository userRepository) {
        log.debug("Initializing users and posts");
        return args -> {
            // Prüfen, ob bereits Posts existieren
            if (postRepository.count() == 0) {
                // Dummy-User erstellen (falls nicht vorhanden)
                User dummyUser = userRepository.findById(1L).orElseGet(() -> {
                    User user = new User();
                    user.setId(1L);
                    user.setUsername("dummyUser");
                    user.setPassword("dummyTiger");
                    user.setEmail("dummy@example.com");
                    user.setCreatedAt(LocalDateTime.now());
                    return userRepository.save(user);
                });

                // Dummy-Post erstellen
                Post dummyPost = new Post();
                dummyPost.setTitle("Willkommen auf meinem Blog!");
                dummyPost.setContent("Dies ist ein Dummy-Post, der automatisch beim Start der Anwendung erstellt wird.");
                dummyPost.setCreatedAt(LocalDateTime.now());
                dummyPost.setUpdatedAt(LocalDateTime.now());
                dummyPost.setAuthor(dummyUser);

                postRepository.save(dummyPost);
                log.info("Dummy-Post wurde erfolgreich erstellt!");
            }
        };
    }
}