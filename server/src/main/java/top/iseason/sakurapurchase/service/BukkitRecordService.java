package top.iseason.sakurapurchase.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.iseason.sakurapurchase.entity.BukkitRecord;
import top.iseason.sakurapurchase.entity.Record;

import java.util.List;

public interface BukkitRecordService extends IService<BukkitRecord> {
    /**
     * 查询用户的所有订单
     * 数据量小时效率高点
     *
     * @param uuid 用户uuid
     * @return 用户的所有订单
     */
    List<Record> getUserRecords(String uuid);

    /**
     * 查询用户的所有订单
     * 数据量高时效率高点
     *
     * @param uuid 用户uuid
     * @return 用户的所有订单
     */
    List<Record> getUserRecordsBath(String uuid);

    /**
     * 获取用户上某个订单
     *
     * @param uuid
     * @return
     */
    Record getUserRecord(String uuid, int offset);

    /**
     * 获取用户上一个订单
     *
     * @param uuid
     * @return
     */
    Record getLastRecord(String uuid);

    /**
     * 查询用户是否有某个订单
     *
     * @param uuid
     * @param orderId
     * @return
     */
    Boolean hasOrder(String uuid, String orderId);

    /**
     * 获取所有bukkit支付记录
     *
     * @return
     */
    List<Record> getAll();

    /**
     * 获取bukkit端总充值金额
     *
     * @return
     */
    Double getAllTotal();

    /**
     * 获取bukkit端用户总充值金额
     *
     * @param uuid
     * @return
     */
    Double getUserTotal(String uuid);

    /**
     * 获取单个玩家若干个最近的订单
     *
     * @param uuid
     * @return
     */
    List<Record> getUserRecordIds(String uuid, int offset, int amount);
}
