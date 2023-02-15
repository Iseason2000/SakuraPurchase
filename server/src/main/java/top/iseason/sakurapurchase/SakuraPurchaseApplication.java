package top.iseason.sakurapurchase;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableCaching
@EnableTransactionManagement
@MapperScan("top.iseason.sakurapurchase.mapper")
@SpringBootApplication
public class SakuraPurchaseApplication {

    public static void main(String[] args) {
        SpringApplication.run(SakuraPurchaseApplication.class, args);
    }
}
