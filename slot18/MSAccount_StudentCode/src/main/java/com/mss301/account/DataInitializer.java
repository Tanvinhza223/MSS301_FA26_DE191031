package com.mss301.account;

import com.mss301.account.entity.StoreAccount;
import com.mss301.account.repository.AccountRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {
    private final AccountRepository accounts;
    private final PasswordEncoder encoder;
    public DataInitializer(AccountRepository accounts, PasswordEncoder encoder) { this.accounts = accounts; this.encoder = encoder; }
    @Override public void run(String... args) {
        if (accounts.count() != 0) return;
        String password = encoder.encode("@1");
        accounts.saveAll(List.of(
            new StoreAccount("adminsys", "admin@drawingpen.com.vn", password, 1, true),
            new StoreAccount("staff02", "staff02@drawingpen.com.vn", password, 2, true),
            new StoreAccount("member01", "member01@drawingpen.com.vn", password, 3, true),
            new StoreAccount("member02", "member02@drawingpen.com.vn", password, 3, false),
            new StoreAccount("staff01", "staff01@drawingpen.com.vn", password, 2, false)
        ));
    }
}

