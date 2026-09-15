package eu.bilch.scale.config;

import eu.bilch.scale.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final PathPatternRequestMatcher h2ConsoleMatcher = PathPatternRequestMatcher.pathPattern("/h2-console/**");
    private final UserRepository userRepository;

    SecurityConfig(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.ignoringRequestMatchers(h2ConsoleMatcher))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/index", "/blog/posts", "/blog/posts/{id}", "/login**", "/oauth2/**",
                                "/css/**", "/js/**", "/error**")
                        .permitAll()
                        .requestMatchers("/blog/posts/new").authenticated()
                        .anyRequest().authenticated())
                .formLogin(form -> form
                        .loginPage("/login")
                        .permitAll())
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/login")
                        .defaultSuccessUrl("/blog/posts", true)
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService())))
                .logout(logout -> logout
                        .logoutSuccessUrl("/").permitAll());
        return http.build();
    }


@Bean
WebSecurityCustomizer webSecurityCustomizer() {
    return web -> web.ignoring()
            .requestMatchers(h2ConsoleMatcher);
}

@Bean
    CustomOAuth2UserService customOAuth2UserService() {
        return new CustomOAuth2UserService(userRepository);
    }

    @Bean
    GrantedAuthoritiesMapper userAuthoritiesMapper() {
        return (authorities) -> authorities;
    }
}
