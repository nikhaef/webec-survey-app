package surveyapp.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import surveyapp.model.User;
import surveyapp.model.UserRole;
import surveyapp.repository.UserRepository;
import surveyapp.repository.SurveyRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AuthenticationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SurveyRepository surveyRepository;

    @BeforeEach
    void setUp() {
        surveyRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void testRegisterAndLoginFlow() throws Exception {
        // Register a new user
        mockMvc.perform(post("/register")
                .param("username", "newuser")
                .param("password", "password123")
                .param("coordinator", "false"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/"));

        // Verify user was created
        assertTrue(userRepository.findByUsername("newuser").isPresent());

        // Logout
        mockMvc.perform(get("/logout"))
            .andExpect(status().is3xxRedirection());

        // Login
        mockMvc.perform(post("/login")
                .param("username", "newuser")
                .param("password", "password123"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/"));
    }

    @Test
    void testRegisterCoordinator() throws Exception {
        mockMvc.perform(post("/register")
                .param("username", "coordinator_user")
                .param("password", "password")
                .param("coordinator", "true"))
            .andExpect(status().is3xxRedirection());

        User coord = userRepository.findByUsername("coordinator_user").orElse(null);
        assertNotNull(coord);
        assertEquals(UserRole.COORDINATOR, coord.getRole());
    }

    @Test
    void testLoginFailsWithWrongPassword() throws Exception {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("correctpassword");
        user.setRole(UserRole.RESPONDENT);
        userRepository.save(user);

        mockMvc.perform(post("/login")
                .param("username", "testuser")
                .param("password", "wrongpassword"))
            .andExpect(status().isOk())
            .andExpect(view().name("login"));
    }

    @Test
    void testRegisterDuplicateUsernameFails() throws Exception {
        User existingUser = new User();
        existingUser.setUsername("existinguser");
        existingUser.setPassword("password1");
        existingUser.setRole(UserRole.RESPONDENT);
        userRepository.save(existingUser);

        mockMvc.perform(post("/register")
                .param("username", "existinguser")
                .param("password", "password2"))
            .andExpect(status().isOk())
            .andExpect(view().name("register"));
    }
}

