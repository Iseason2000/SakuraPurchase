package top.iseason.sakurapurchase.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@EnableTransactionManagement
@Configuration
@Import({SecurityConfig.class, PayConfig.class})
public class MainConfig {

    @Bean
    public PlatformTransactionManager txManager(DataSource datasource) {
        return new DataSourceTransactionManager(datasource);
    }
}
