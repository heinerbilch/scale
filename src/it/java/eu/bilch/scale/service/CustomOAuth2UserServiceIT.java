package eu.bilch.scale.service;

import eu.bilch.scale.config.CustomOAuth2User;
import eu.bilch.scale.config.CustomOAuth2UserService;
import eu.bilch.scale.model.Role;
import eu.bilch.scale.model.User;
import eu.bilch.scale.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("default")
class CustomOAuth2UserServiceIT {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomOAuth2UserService customOAuth2UserService;

    @AfterEach
    void tearDown() {
 //       userRepository.deleteAll();
    }

    private OAuth2UserRequest createOAuth2UserRequest(Map<String, Object> attributes) {
        ClientRegistration clientRegistration = ClientRegistration.withClientId("test-client-id")
                .clientSecret("test-secret")
                .clientAuthenticationMethod(org.springframework.security.oauth2.core.ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(org.springframework.security.oauth2.core.AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUri("http://localhost:8080/login/oauth2/code/google")
                .scope(Collections.singleton("openid"))
                .authorizationUri("https://accounts.google.com/o/oauth2/auth")
                .tokenUri("https://oauth2.googleapis.com/token")
                .userInfoUri("https://www.googleapis.com/oauth2/v3/userinfo")
                .userNameAttributeName("sub")
                .clientName("google")
                .registrationId("test-client")
                .build();

        return new OAuth2UserRequest(clientRegistration, null);
    }

    @Test
    void loadUser_shouldPersistNewUserOnFirstLogin() throws OAuth2AuthenticationException {
        // Given: OAuth2UserRequest mit Google-Attributen
        Map<String, Object> attributes = Map.of(
                "sub", "123456789",
                "email", "test.user@example.com",
                "given_name", "Test",
                "family_name", "User",
                "picture", "https://example.com/profile.jpg"
        );

        OAuth2UserRequest userRequest = createOAuth2UserRequest(attributes);

        // When: loadUser wird aufgerufen
        OAuth2User result = customOAuth2UserService.loadUser(userRequest);

        // Then: User sollte in der Datenbank persistiert sein
        assertNotNull(result);
        assertInstanceOf(CustomOAuth2User.class, result);

        CustomOAuth2User customUser = (CustomOAuth2User) result;
        User persistedUser = customUser.getUser();

        assertNotNull(persistedUser.getId());
        assertEquals("123456789", persistedUser.getGoogleId());
        assertEquals("test.user@example.com", persistedUser.getEmail());
        assertEquals("Test", persistedUser.getFirstName());
        assertEquals("User", persistedUser.getLastName());
        assertEquals("https://example.com/profile.jpg", persistedUser.getProfilePicture());
        assertEquals("test.user", persistedUser.getUsername());
        assertEquals(Role.USER, persistedUser.getRole());
        assertNotNull(persistedUser.getCreatedAt());

        // Verify in database
        User foundUser = userRepository.findByGoogleId("123456789").orElse(null);
        assertNotNull(foundUser);
        assertEquals(persistedUser.getId(), foundUser.getId());
    }

    @Test
    void loadUser_shouldReturnExistingUserOnSubsequentLogin() throws OAuth2AuthenticationException {
        // Given: Bereit existierender User in der Datenbank
        User existingUser = new User();
        existingUser.setGoogleId("987654321");
        existingUser.setEmail("existing.user@example.com");
        existingUser.setFirstName("Existing");
        existingUser.setLastName("User");
        existingUser.setProfilePicture("https://example.com/existing.jpg");
        existingUser.setUsername("existing.user");
        existingUser.setRole(Role.USER);
        existingUser.setCreatedAt(LocalDateTime.now().minusDays(1));
        userRepository.save(existingUser);

        // OAuth2UserRequest mit den gleichen Google-Attributen
        Map<String, Object> attributes = Map.of(
                "sub", "987654321",
                "email", "existing.user@example.com",
                "given_name", "Existing",
                "family_name", "User",
                "picture", "https://example.com/existing.jpg"
        );

        OAuth2UserRequest userRequest = createOAuth2UserRequest(attributes);

        // When: loadUser wird aufgerufen
        OAuth2User result = customOAuth2UserService.loadUser(userRequest);

        // Then: Der existierende User sollte zurueckgegeben werden
        assertNotNull(result);
        assertInstanceOf(CustomOAuth2User.class, result);

        CustomOAuth2User customUser = (CustomOAuth2User) result;
        User returnedUser = customUser.getUser();

        assertEquals(existingUser.getId(), returnedUser.getId());
        assertEquals("987654321", returnedUser.getGoogleId());
        assertEquals("existing.user@example.com", returnedUser.getEmail());

        // Verify: Es sollte nur ein User in der Datenbank sein
        long count = userRepository.count();
        assertEquals(1, count);
    }

    @Test
    void loadUser_shouldReturnExistingUserEvenWithUpdatedAttributes() throws OAuth2AuthenticationException {
        // Given: Bereit existierender User in der Datenbank
        User existingUser = new User();
        existingUser.setGoogleId("111222333");
        existingUser.setEmail("update.user@example.com");
        existingUser.setFirstName("OldFirst");
        existingUser.setLastName("OldLast");
        existingUser.setProfilePicture("https://example.com/old.jpg");
        existingUser.setUsername("old.user");
        existingUser.setRole(Role.USER);
        existingUser.setCreatedAt(LocalDateTime.now().minusDays(1));
        userRepository.save(existingUser);

        // OAuth2UserRequest mit aktualisierten Attributen
        Map<String, Object> attributes = Map.of(
                "sub", "111222333",
                "email", "update.user@example.com",
                "given_name", "NewFirst",
                "family_name", "NewLast",
                "picture", "https://example.com/new.jpg"
        );

        OAuth2UserRequest userRequest = createOAuth2UserRequest(attributes);

        // When: loadUser wird aufgerufen
        OAuth2User result = customOAuth2UserService.loadUser(userRequest);

        // Then: Der existierende User sollte zurueckgegeben werden
        assertNotNull(result);
        assertInstanceOf(CustomOAuth2User.class, result);

        CustomOAuth2User customUser = (CustomOAuth2User) result;
        User returnedUser = customUser.getUser();

        assertEquals(existingUser.getId(), returnedUser.getId());
        assertEquals("111222333", returnedUser.getGoogleId());

        // Verify: Es sollte nur ein User in der Datenbank sein
        long count = userRepository.count();
        assertEquals(1, count);
    }

    @Test
    void loadUser_shouldCreateUserWithMinimalAttributes() throws OAuth2AuthenticationException {
        // Given: OAuth2UserRequest mit minimalen Attributen
        Map<String, Object> attributes = Map.of(
                "sub", "minimal123",
                "email", "minimal@example.com"
        );

        OAuth2UserRequest userRequest = createOAuth2UserRequest(attributes);

        // When: loadUser wird aufgerufen
        OAuth2User result = customOAuth2UserService.loadUser(userRequest);

        // Then: User sollte mit minimalen Attributen erstellt werden
        assertNotNull(result);
        assertInstanceOf(CustomOAuth2User.class, result);

        CustomOAuth2User customUser = (CustomOAuth2User) result;
        User persistedUser = customUser.getUser();

        assertNotNull(persistedUser.getId());
        assertEquals("minimal123", persistedUser.getGoogleId());
        assertEquals("minimal@example.com", persistedUser.getEmail());
        assertEquals("minimal", persistedUser.getUsername());
        assertNull(persistedUser.getFirstName());
        assertNull(persistedUser.getLastName());
        assertNull(persistedUser.getProfilePicture());
        assertEquals(Role.USER, persistedUser.getRole());
        assertNotNull(persistedUser.getCreatedAt());
    }

    @Test
    void loadUser_shouldNotCreateDuplicateUsers() throws OAuth2AuthenticationException {
        // Given: Erzeuge zwei Users mit unterschiedlichen Google IDs
        Map<String, Object> attributes1 = Map.of(
                "sub", "unique1",
                "email", "user1@example.com",
                "given_name", "User1",
                "family_name", "Test1"
        );

        Map<String, Object> attributes2 = Map.of(
                "sub", "unique2",
                "email", "user2@example.com",
                "given_name", "User2",
                "family_name", "Test2"
        );

        OAuth2UserRequest userRequest1 = createOAuth2UserRequest(attributes1);
        OAuth2UserRequest userRequest2 = createOAuth2UserRequest(attributes2);

        // When: Beide Users werden nacheinander erstellt
        customOAuth2UserService.loadUser(userRequest1);
        customOAuth2UserService.loadUser(userRequest2);

        // Then: Es sollten genau zwei Users in der Datenbank sein
        long count = userRepository.count();
        assertEquals(2, count);

        User user1 = userRepository.findByGoogleId("unique1").orElse(null);
        User user2 = userRepository.findByGoogleId("unique2").orElse(null);

        assertNotNull(user1);
        assertNotNull(user2);
        assertNotEquals(user1.getId(), user2.getId());
    }

    @Test
    void loadUser_shouldFindUserByEmail() {
        // Given: User wird erstellt
        Map<String, Object> attributes = Map.of(
                "sub", "email123",
                "email", "email.test@example.com",
                "given_name", "Email",
                "family_name", "Test"
        );

        OAuth2UserRequest userRequest = createOAuth2UserRequest(attributes);

        try {
            customOAuth2UserService.loadUser(userRequest);
        } catch (OAuth2AuthenticationException e) {
            fail("Should not throw exception", e);
        }

        // When: Suche nach Email
        User foundUser = userRepository.findByEmail("email.test@example.com").orElse(null);

        // Then: User sollte gefunden werden
        assertNotNull(foundUser);
        assertEquals("email.test@example.com", foundUser.getEmail());
        assertEquals("email123", foundUser.getGoogleId());
    }

    @Test
    void loadUser_shouldFindUserByUsername() {
        // Given: User wird erstellt
        Map<String, Object> attributes = Map.of(
                "sub", "username123",
                "email", "username.test@example.com",
                "given_name", "Username",
                "family_name", "Test"
        );

        OAuth2UserRequest userRequest = createOAuth2UserRequest(attributes);

        try {
            customOAuth2UserService.loadUser(userRequest);
        } catch (OAuth2AuthenticationException e) {
            fail("Should not throw exception", e);
        }

        // When: Suche nach Username
        User foundUser = userRepository.findByUsername("username.test").orElse(null);

        // Then: User sollte gefunden werden
        assertNotNull(foundUser);
        assertEquals("username.test", foundUser.getUsername());
    }
}

