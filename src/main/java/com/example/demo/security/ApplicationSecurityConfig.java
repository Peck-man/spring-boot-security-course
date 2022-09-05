package com.example.demo.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import static com.example.demo.security.ApplicationUserPermission.COURSE_WRITE;
import static com.example.demo.security.ApplicationUserRole.*;

@Configuration
@EnableWebSecurity
// tato anotace odkáže na @PreAuthorize()
@EnableGlobalMethodSecurity(prePostEnabled = true)
// Class ApplicationSecurityConfig - zde se konfiguruje úplně vse, vstupní bod security
public class ApplicationSecurityConfig extends WebSecurityConfigurerAdapter {
    // injection of passwordencoder
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public ApplicationSecurityConfig(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {


        http
                .csrf().disable() //
                .authorizeRequests()
                .antMatchers("/login/**") // - se kterými endpointy chci něco udělat
                .permitAll() // - co s nimi chci udělat
                .antMatchers("/api/**")// tento endpoint
                .hasRole(STUDENT.name()) // povoluji dosáhnout jen userovi s rolí student
                // tyto jsou vyměněny za @PreAuthorized u jednotlivých endpointu
                    //.antMatchers(HttpMethod.DELETE, "/management/api/**").hasAuthority(COURSE_WRITE.getPermission())
                    //.antMatchers(HttpMethod.POST, "/management/api/**").hasAuthority(COURSE_WRITE.getPermission())
                    //.antMatchers(HttpMethod.PUT, "/management/api/**").hasAuthority(COURSE_WRITE.getPermission())
                    //.antMatchers(HttpMethod.GET, "/management/api/**").hasAnyRole(ADMIN.name(), ADMINTRAINEE.name())
                .anyRequest() // - každý požadavek...
                .authenticated() // - ... musí byt autentikovan
                .and()
                .httpBasic();   //  - typ kterým chceme autentikovat
    }

    // Zde budu vytahovat uživatele z databaze
    // a buildovat z toho datový typ User který vychází z package security
    @Override
    @Bean
    protected UserDetailsService userDetailsService() {
        UserDetails annaSmithUser = User.builder()
                .username("annasmith")
                .password(passwordEncoder.encode("password")) // - musí byt zakódované přes PasswordConfig - níže
               // .roles(STUDENT.name()) // - toto si na pozadí prevede na ROLE_STUDENT a přiřadí jí tuto roli
                .authorities(STUDENT.getGrantedAuthorities()) //připisuje jednotlivé permissions uživateli
                .build();

        UserDetails lindaUser = User.builder()
                .username("linda")
                .password(passwordEncoder.encode("password123"))
               // .roles(ADMIN.name()) // ROLE_ADMIN
                .authorities(ADMIN.getGrantedAuthorities())

                .build();

        UserDetails tomUser = User.builder()
                .username("tom")
                .password(passwordEncoder.encode("password123"))
           //     .roles(ADMINTRAINEE.name()) // ROLE_ADMINTRAINEE
                .authorities(ADMINTRAINEE.getGrantedAuthorities())

                .build();


        return new InMemoryUserDetailsManager( // ukládá do lokální memory pro používání
                annaSmithUser,
                lindaUser,
                tomUser
        );
    }
}

