package com.example.backend.util.initChecker;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Component
@RequiredArgsConstructor
@Slf4j
public class DatabaseConnectionChecker implements CommandLineRunner {

    private final DataSource dataSource;

    @Override
    public void run(String... args) {
        try (Connection connection = dataSource.getConnection()) {
            if (connection != null) {
                log.info("--Database 연결 성공--");
            } else {
                log.error("--Database 연결에 실패했습니다.--");
                System.out.println();
            }
        } catch (SQLException e) {
            log.error("SQL Exception: " + e.getMessage());
        }
    }
}
