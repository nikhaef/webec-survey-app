package surveyapp;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import surveyapp.model.User;
import surveyapp.model.UserRole;
import surveyapp.repository.UserRepository;

@SpringBootApplication
public class SurveyApp {

    public static void main(String[] args) {
        SpringApplication.run(SurveyApp.class, args);
    }

    // create a default coordinator for convenience
    @Bean
    CommandLineRunner init(UserRepository userRepository) {
        return args -> {
            if (userRepository.findByUsername("coordinator").isEmpty()) {
                User u = new User();
                u.setUsername("coordinator");
                u.setPassword("password"); // plain-text for demo (not for prod)
                u.setRole(UserRole.COORDINATOR);
                userRepository.save(u);
            }
        };
    }
}

