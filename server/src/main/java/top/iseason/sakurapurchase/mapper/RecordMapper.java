package top.iseason.sakurapurchase.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import top.iseason.sakurapurchase.entity.Record;
import top.iseason.sakurapurchase.entity.Stat;

import java.util.List;

@Mapper
@CacheNamespace
public interface RecordMapper extends BaseMapper<Record> {

    @Select("select sum(order_amount) from record where status = 'SUCCESS'")
    Double getTotalPaidAmount();

    @Select("select count(order_id) from record where status = 'SUCCESS'")
    Integer getTotalPaidCount();

    @Select("SELECT DATE_FORMAT(period.period,'%Y-%m-%d') AS period,IFNULL(result.total_amount,0.0) AS amount,IFNULL(result.total_count,0) AS count FROM (SELECT @DATE :=DATE_ADD(@DATE,INTERVAL-1 DAY) period FROM (SELECT @DATE :=CURRENT_DATE+INTERVAL #{offset} DAY FROM record LIMIT #{limit}) TIME) AS period LEFT JOIN (SELECT DATE_FORMAT(record.create_time,'%Y-%m-%d') AS period,count(record.order_id) AS total_count,sum(record.order_amount) AS total_amount FROM record WHERE record.`status`='SUCCESS' GROUP BY period) AS result ON result.period=DATE_FORMAT(period.period,'%Y-%m-%d') ORDER BY period.period")
    List<Stat> getDays(int limit, int offset);

    @Select("SELECT DATE_FORMAT(period.period,'%a') AS period,IFNULL(result.total_amount,0.0) AS amount,IFNULL(result.total_count,0) AS count FROM (SELECT @DATE :=DATE_ADD(@DATE,INTERVAL 1 DAY) period FROM (SELECT @DATE :=DATE_SUB(curdate()+INTERVAL #{offset} WEEK,INTERVAL WEEKDAY(curdate())+2 DAY) FROM record LIMIT 7) TIME) AS period LEFT JOIN (SELECT DATE_FORMAT(record.create_time,'%Y-%m-%d') AS period,count(record.order_id) AS total_count,sum(record.order_amount) AS total_amount FROM record WHERE record.`status`='SUCCESS' GROUP BY period) AS result ON result.period=DATE_FORMAT(period.period,'%Y-%m-%d') ORDER BY period.period")
    List<Stat> getAWeek(int offset);

    @Select("SELECT DATE_FORMAT(period.period,'%b') AS period,IFNULL(result.total_amount,0.0) AS amount,IFNULL(result.total_count,0) AS count FROM (SELECT @DATE :=DATE_ADD(@DATE,INTERVAL-1 MONTH) period FROM (SELECT @DATE :=concat(YEAR (curdate())+1+#{offset},'-01-31') FROM record LIMIT #{limit}) TIME) AS period LEFT JOIN (SELECT DATE_FORMAT(record.create_time,'%Y-%m') AS period,count(record.order_id) AS total_count,sum(record.order_amount) AS total_amount FROM record WHERE record.`status`='SUCCESS' GROUP BY period) AS result ON result.period=DATE_FORMAT(period.period,'%Y-%m') ORDER BY period.period")
    List<Stat> getMonth(int limit, int offset);

    @Select("SELECT DATE_FORMAT(period.period,'%H:00') AS period,IFNULL(result.total_amount,0.0) AS amount,IFNULL(result.total_count,0) AS count FROM (SELECT @TIME :=DATE_ADD(@TIME,INTERVAL+1 HOUR) period FROM (SELECT @TIME :=CURRENT_DATE FROM record LIMIT #{limit}) TIME) AS period LEFT JOIN (SELECT DATE_FORMAT(record.create_time,'%Y-%m-%d-%H') AS period,count(record.order_id) AS total_count,sum(record.order_amount) AS total_amount FROM record WHERE record.`status`='SUCCESS' GROUP BY period) AS result ON result.period=DATE_FORMAT(period.period,'%Y-%m-%d-%H') ORDER BY period.period")
    List<Stat> getHour(int limit);
}
