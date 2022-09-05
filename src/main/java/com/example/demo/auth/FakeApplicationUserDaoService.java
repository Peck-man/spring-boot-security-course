package com.example.demo.auth;

import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.example.demo.security.ApplicationUserRole.*;
// je to repository a je fetchuje uživatele z databáze - v tomto případě z listu
// kdybych chtěl tahat uživatele z jiné databáze tak mi stačí jen udělat novou class co bude implementovat ApplicationUserDAO
// přiřadit k ní @Repository("něco") a tuto anotaci poté dát do Qualifier v ApplicationUserService
@Repository("fake")
public class FakeApplicationUserDaoService implements ApplicationUserDAO{


    private final PasswordEncoder passwordEncoder;
    @Autowired
    public FakeApplicationUserDaoService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Optional<ApplicationUserModel> selectApplicationUserByUsername(String username) {
        return getApplicationUsers().stream().filter(applicationUserModel -> username.equals(applicationUserModel.getUsername()))
                .findFirst();
    }

    private List<ApplicationUserModel> getApplicationUsers() {
        return Lists.newArrayList(
                new ApplicationUserModel("annasmith",
                        passwordEncoder.encode("password"),
                        STUDENT.getGrantedAuthorities(),
                        true,
                        true,
                        true,
                        true
                        ),
                new ApplicationUserModel("linda",
                        passwordEncoder.encode("password"),
                        ADMIN.getGrantedAuthorities(),
                        true,
                        true,
                        true,
                        true
                ),
                new ApplicationUserModel("tom",
                        passwordEncoder.encode("password"),
                        ADMINTRAINEE.getGrantedAuthorities(),
                        true,
                        true,
                        true,
                        true
                )
        );
    }
}
