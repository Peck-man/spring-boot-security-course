package com.example.demo.auth;

import org.springframework.context.annotation.Bean;

import java.util.Optional;
// tohle interface mi pomůže pokud bych chtěl změnit například typ databáze
public interface ApplicationUserDAO {


     Optional<ApplicationUserModel> selectApplicationUserByUsername(String username);
}
