package top.iseason.sakurapurchase.mapper;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import top.iseason.sakurapurchase.entity.BukkitRecord;
import top.iseason.sakurapurchase.service.BukkitRecordService;

import javax.annotation.Resource;

@SpringBootTest
public class TestMapper {
    @Resource
    BukkitRecordService bukkitRecordService;
    @Resource
    BukkitRecordMapper bukkitRecordMapper;

    @Test
    public void query() {
        System.out.println(bukkitRecordService.hasOrder("8cad55de-17db-4668-bbc6-85b2bfd47a44", "1580816086234058754"));
    }

    @Test
    public void insert() {
        String test = "8cad55de-17db-4668-bbc6-85b2bfd47a44";
        bukkitRecordService.save(new BukkitRecord(null, test, 1580816086234058754L));
    }

    @Test
    public void test() {
        System.out.println(bukkitRecordService.getUserRecordsBath("8cad55de-17db-4668-bbc6-85b2bfd47a44"));
    }
}
