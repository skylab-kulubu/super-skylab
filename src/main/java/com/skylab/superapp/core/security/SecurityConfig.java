package com.skylab.superapp.core.security;

import com.skylab.superapp.business.abstracts.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public SecurityConfig(JwtAuthFilter jwtAuthFilter, UserService userService, PasswordEncoder passwordEncoder) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .csrf(AbstractHttpConfigurer::disable)

                .authorizeHttpRequests(x ->
                        x
                                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                                .requestMatchers("/api/auth/login").permitAll()

                                .requestMatchers("/api/announcements/addAnnouncement").hasAnyRole("ADMIN", "BIZBIZE_ADMIN", "AGC_ADMIN", "GECEKODU_ADMIN")
                                .requestMatchers("/api/announcements/deleteAnnouncement").hasAnyRole("ADMIN", "BIZBIZE_ADMIN", "AGC_ADMIN", "GECEKODU_ADMIN")
                                .requestMatchers("/api/announcements/updateAnnouncement").hasAnyRole("ADMIN", "AGC_ADMIN", "GECEKODU_ADMIN", "BIZBIZE_ADMIN")
                                .requestMatchers("/api/announcements/getAllByTenantAndType").permitAll()
                                .requestMatchers("/api/announcements/getAllByTenant").permitAll()
                                .requestMatchers("/api/announcements/addPhotosToAnnouncement").hasAnyRole("ADMIN", "AGC_ADMIN", "GECEKODU_ADMIN", "BIZBIZE_ADMIN")

                                .requestMatchers("/api/events/addEvent").hasAnyRole("ADMIN", "AGC_ADMIN", "GECEKODU_ADMIN", "BIZBIZE_ADMIN")
                                .requestMatchers("/api/events/deleteEvent").hasAnyRole("ADMIN", "AGC_ADMIN", "GECEKODU_ADMIN", "BIZBIZE_ADMIN")
                                .requestMatchers("/api/events/updateEvent").hasAnyRole("ADMIN", "AGC_ADMIN", "GECEKODU_ADMIN", "BIZBIZE_ADMIN")
                                .requestMatchers("/api/events/updateBizbizeEvent").hasAnyRole("ADMIN", "BIZBIZE_ADMIN")
                                .requestMatchers("/api/events/getAllByTenantAndType").permitAll()
                                .requestMatchers("/api/events/getAllByTenant").permitAll()
                                .requestMatchers("/api/events/getAllBizbizeEvents").permitAll()
                                .requestMatchers("/api/events/getAllFutureEventsByTenant").permitAll()
                                .requestMatchers("/api/events/addPhotosToEvent").hasAnyRole("ADMIN", "AGC_ADMIN", "GECEKODU_ADMIN", "BIZBIZE_ADMIN")

                                .requestMatchers("/api/photos/addPhoto").hasAnyRole("ADMIN", "AGC_ADMIN", "GECEKODU_ADMIN", "BIZBIZE_ADMIN")
                                .requestMatchers("api/photos/updatePhoto").hasAnyRole("ADMIN", "AGC_ADMIN", "GECEKODU_ADMIN", "BIZBIZE_ADMIN")
                                .requestMatchers("/api/photos/deletePhoto").hasAnyRole("ADMIN", "AGC_ADMIN", "GECEKODU_ADMIN", "BIZBIZE_ADMIN")
                                .requestMatchers("/api/photos/getAll").hasAnyRole("ADMIN", "AGC_ADMIN", "GECEKODU_ADMIN", "BIZBIZE_ADMIN")

                                .requestMatchers("/api/staff/addStaff").hasAnyRole("ADMIN", "AGC_ADMIN", "GECEKODU_ADMIN", "BIZBIZE_ADMIN")
                                .requestMatchers("/api/staff/deleteStaff").hasAnyRole("ADMIN", "AGC_ADMIN", "GECEKODU_ADMIN", "BIZBIZE_ADMIN")
                                .requestMatchers("/api/staff/updateStaff").hasAnyRole("ADMIN", "AGC_ADMIN", "GECEKODU_ADMIN", "BIZBIZE_ADMIN")
                                .requestMatchers("/api/staff/getAllByTenant").permitAll()
                                .requestMatchers("/api/staff/getAll").permitAll()

                                .requestMatchers("/api/users/addUser").hasAnyRole("ADMIN", "AGC_ADMIN", "GECEKODU_ADMIN", "BIZBIZE_ADMIN")
                                .requestMatchers("/api/users/deleteUser").hasAnyRole("ADMIN")
                                .requestMatchers("/api/users/addRole").hasAnyRole("ADMIN")
                                .requestMatchers("/api/users/removeRole").hasAnyRole("ADMIN")
                                .requestMatchers("/api/users/getAll").hasAnyRole("ADMIN", "AGC_ADMIN", "GECEKODU_ADMIN", "BIZBIZE_ADMIN")
                                .requestMatchers("/api/users/getById").hasAnyRole("ADMIN", "AGC_ADMIN", "GECEKODU_ADMIN", "BIZBIZE_ADMIN")

                                .requestMatchers("/api/seasons/addSeason").hasAnyRole("ADMIN", "AGC_ADMIN", "GECEKODU_ADMIN", "BIZBIZE_ADMIN")
                                .requestMatchers("/api/seasons/deleteSeason").hasAnyRole("ADMIN", "AGC_ADMIN", "GECEKODU_ADMIN", "BIZBIZE_ADMIN")
                                .requestMatchers("/api/seasons/getAllSeasons").hasAnyRole("ADMIN", "AGC_ADMIN", "GECEKODU_ADMIN", "BIZBIZE_ADMIN")
                                .requestMatchers("/api/seasons/addCompetitorToSeason").hasAnyRole("ADMIN", "AGC_ADMIN", "GECEKODU_ADMIN", "BIZBIZE_ADMIN")
                                .requestMatchers("/api/seasons/getAllSeasonsByTenant").permitAll()
                                .requestMatchers("/api/seasons/getSeasonByName").hasAnyRole("ADMIN", "AGC_ADMIN", "GECEKODU_ADMIN", "BIZBIZE_ADMIN")

                                .requestMatchers("/api/competitors/addCompetitor").hasAnyRole("ADMIN", "AGC_ADMIN", "GECEKODU_ADMIN", "BIZBIZE_ADMIN")
                                .requestMatchers("/api/competitors/deleteCompetitor").hasAnyRole("ADMIN", "AGC_ADMIN", "GECEKODU_ADMIN", "BIZBIZE_ADMIN")
                                .requestMatchers("/api/competitors/updateCompetitor").hasAnyRole("ADMIN", "AGC_ADMIN", "GECEKODU_ADMIN", "BIZBIZE_ADMIN")
                                .requestMatchers("/api/competitors/getAllBySeasonId").hasAnyRole("ADMIN", "AGC_ADMIN", "GECEKODU_ADMIN", "BIZBIZE_ADMIN")
                                .requestMatchers("/api/competitors/getAllCompetitors").hasAnyRole("ADMIN", "AGC_ADMIN", "GECEKODU_ADMIN", "BIZBIZE_ADMIN")
                                .requestMatchers("/api/competitors/getAllCompetitorsByTenant").hasAnyRole("ADMIN", "AGC_ADMIN", "GECEKODU_ADMIN", "BIZBIZE_ADMIN")
                                .requestMatchers("/api/seasons/addCompetitorToSeason").hasAnyRole("ADMIN", "AGC_ADMIN", "GECEKODU_ADMIN", "BIZBIZE_ADMIN")
                                .requestMatchers("/api/competitors/addPointsToCompetitor").hasAnyRole("ADMIN", "AGC_ADMIN", "GECEKODU_ADMIN", "BIZBIZE_ADMIN")

                                .requestMatchers("/api/images/addImage").hasAnyRole("ADMIN", "AGC_ADMIN", "GECEKODU_ADMIN", "BIZBIZE_ADMIN")
                                .requestMatchers("/api/images/getImageByUrl/**").permitAll()
                                .requestMatchers("/api/images/getImageDetailsByUrl/**").permitAll()
                                .requestMatchers("/api/images/deleteImageById/**").hasAnyRole("ADMIN")


                                .anyRequest().hasAnyRole("ADMIN")

                )
                .sessionManagement(x -> x.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .build();


    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userService);
        authenticationProvider.setPasswordEncoder(passwordEncoder);
        return authenticationProvider;

    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

}