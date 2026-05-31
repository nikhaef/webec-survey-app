package surveyapp.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import surveyapp.model.User;
import surveyapp.model.UserRole;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void testFindByUsername_Found() {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("password123");
        user.setRole(UserRole.RESPONDENT);
        userRepository.save(user);

        Optional<User> found = userRepository.findByUsername("testuser");
        assertTrue(found.isPresent());
        assertEquals("testuser", found.get().getUsername());
    }

    @Test
    void testFindByUsername_NotFound() {
        Optional<User> found = userRepository.findByUsername("nonexistent");
        assertTrue(found.isEmpty());
    }

    @Test
    void testFindByUsername_Unique() {
        User user1 = new User();
        user1.setUsername("uniqueuser");
        user1.setPassword("pwd1");
        user1.setRole(UserRole.RESPONDENT);
        userRepository.save(user1);

        assertThrows(Exception.class, () -> {
            User user2 = new User();
            user2.setUsername("uniqueuser");
            user2.setPassword("pwd2");
            user2.setRole(UserRole.COORDINATOR);
            userRepository.save(user2);
            userRepository.flush();
        });
    }

    @Test
    void testUserRolePersistence() {
        User coordinator = new User();
        coordinator.setUsername("coord");
        coordinator.setPassword("pwd");
        coordinator.setRole(UserRole.COORDINATOR);
        userRepository.save(coordinator);

        Optional<User> fetched = userRepository.findByUsername("coord");
        assertTrue(fetched.isPresent());
        assertEquals(UserRole.COORDINATOR, fetched.get().getRole());
    }
}

