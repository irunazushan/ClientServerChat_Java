package edu.school21.sockets.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.sql.DataSource;

@Configuration
@PropertySource("classpath:db.properties")
@ComponentScan(basePackages = "edu.school21.sockets")
public class SocketsApplicationConfig {
    @Value( "${db.driver.name}" )
    private String driverClassName;
    @Value( "${db.url}" )
    private String url;
    @Value( "${db.user}" )
    private String username;
    @Value( "${db.password}" )
    private String password;

    @Bean
    public DataSource hikariDataSource() {
        HikariDataSource hds = new HikariDataSource();
        hds.setDriverClassName(driverClassName);
        hds.setJdbcUrl(url);
        hds.setUsername(username);
        hds.setPassword(password);
        return hds;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
