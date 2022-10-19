package top.iseason.sakurapurchase.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.iseason.sakurapurchase.entity.Record;
import top.iseason.sakurapurchase.entity.Stat;
import top.iseason.sakurapurchase.entity.TotalStat;

import java.util.List;

public interface RecordService extends IService<Record> {
    /**
     * 删除记录并更新统计缓存
     *
     * @param record
     * @return
     */
    boolean removeRecord(Record record);


    List<Record> getPaidRecords();

    /**
     * 获取总支付金额
     *
     * @return
     */
    Double getTotalPaidAmount();

    /**
     * 获取总支付订单数
     *
     * @return
     */
    Integer getTotalPaidCount();

    /**
     * 按天统计结果
     *
     * @return
     */
    List<Stat> getHours(int limit);

    /**
     * 按天统计结果
     *
     * @return
     */
    List<Stat> getDays(int limit);

    /**
     * 按周统计结果
     *
     * @return
     */
    List<Stat> getAWeek(int offset);

    /**
     * 按月统计结果
     *
     * @return
     */
    List<Stat> getMonth(int limit);

    void modifyTotalPaidAmount(double amount);

    void modifyTotalPaidCount(int amount);


    TotalStat getDailyTotal();

    TotalStat getWeeklyTotal();

    TotalStat getYearlyTotal();
}
