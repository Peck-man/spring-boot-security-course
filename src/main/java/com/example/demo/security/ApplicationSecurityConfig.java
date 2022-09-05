package com.example.demo.security;

import com.example.demo.auth.ApplicationUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.concurrent.TimeUnit;

import static com.example.demo.security.ApplicationUserRole.*;

@Configuration
@EnableWebSecurity
// tato anotace odkáže na @PreAuthorize()
@EnableGlobalMethodSecurity(prePostEnabled = true)
// Class ApplicationSecurityConfig - zde se konfiguruje úplně vse, vstupní bod security
public class ApplicationSecurityConfig extends WebSecurityConfigurerAdapter {
    // injection of passwordencoder
    private final PasswordEncoder passwordEncoder;
    private final ApplicationUserService applicationUserService;

    @Autowired
    public ApplicationSecurityConfig(PasswordEncoder passwordEncoder, ApplicationUserService applicationUserService) {
        this.passwordEncoder = passwordEncoder;
        this.applicationUserService = applicationUserService;
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {


        http
                .csrf().disable() // cross site request forgery - po loginu se posílá CSRF token, pokud request neobsahuje tento token tak není request udělán - ochrana před třetí stranou
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
                .formLogin()  //  - typ kterým chceme autentikovat - httpBasic(), formLogin()
                    .loginPage("/login") //  - tady overriduju defaultní formulář login a vkládám endpoint s mým formulářem
                    .defaultSuccessUrl("/courses",true) //default page after successful login
                    .passwordParameter("password")
                    .usernameParameter("username")
                .and()
                .rememberMe()
                    .tokenValiditySeconds((int)TimeUnit.DAYS.toSeconds(21)) //defaults to 2 weeks
                    .key("somethingverysecured")
                    .rememberMeParameter("remember-me")
                .and()
                .logout()
                    .logoutUrl("/logout") //what is the default page for logout
                .clearAuthentication(true)
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID", "remember-me")
                .logoutSuccessUrl("/login"); // default page after successful logout
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder); // setting our passwords to be encoded
        provider.setUserDetailsService(applicationUserService);
        return provider;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(daoAuthenticationProvider());
    }

    /* ZDE VYTVÁŘÍM JEDNOTLIVÉ USERY BEZ JAKÉKOLIV IMPELEMENTACE
    Zde budu fetchovat uživatele z databaze
     a buildovat z toho datový typ User který vychází z package security
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
    }*/
}

