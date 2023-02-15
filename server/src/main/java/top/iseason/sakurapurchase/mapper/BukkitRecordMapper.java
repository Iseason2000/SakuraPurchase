package top.iseason.sakurapurchase.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import top.iseason.sakurapurchase.entity.BukkitRecord;
import top.iseason.sakurapurchase.entity.Record;

import java.util.List;

@Mapper
@CacheNamespace
public interface BukkitRecordMapper extends BaseMapper<BukkitRecord> {
    /**
     * 获取单个玩家的所有订单
     *
     * @param uuid
     * @return
     */
    @Select("select * from record inner join bukkit_record on bukkit_record.order_id = record.order_id where bukkit_record.uuid = #{uuid}")
    List<Record> getUserRecords(String uuid);

    /**
     * 获取单个玩家若干个最近的订单
     *
     * @param uuid
     * @return
     */
    @Select("select * from record inner join bukkit_record on bukkit_record.order_id = record.order_id where bukkit_record.uuid = #{uuid} group by id limit #{offset},#{amount}")
    List<Record> getUserRecordIdsLimit(String uuid, int offset, int amount);

    /**
     * 获取单个玩家所有订单id
     *
     * @param uuid
     * @return
     */
    @Select("select order_id from bukkit_record where uuid = #{uuid}")
    List<String> getUserRecordIds(String uuid);


    /**
     * 获取用户总消费额度
     *
     * @param uuid
     * @return
     */
    @Select("select sum(record.order_amount) from record inner join bukkit_record on bukkit_record.order_id = record.order_id where bukkit_record.uuid = #{uuid}")
    Double getUserTotalAmount(String uuid);

    /**
     * 获取用户某次订单id
     *
     * @param uuid
     * @return
     */
    @Select("select bukkit_record.order_id from bukkit_record where uuid = #{uuid} order by id desc limit #{offset},1")
    Long getUserRecord(String uuid, int offset);

    @Select("select * from record inner join bukkit_record on bukkit_record.order_id = record.order_id")
    List<Record> getAllRecords();

    @Select("select sum(record.order_amount) from record inner join bukkit_record on bukkit_record.order_id = record.order_id")
    Double getTotalAmount();

}
