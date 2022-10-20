package top.iseason.sakurapurchase.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lly835.bestpay.enums.OrderStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import top.iseason.sakurapurchase.entity.Record;
import top.iseason.sakurapurchase.entity.Stat;
import top.iseason.sakurapurchase.entity.TotalStat;
import top.iseason.sakurapurchase.mapper.RecordMapper;
import top.iseason.sakurapurchase.service.RecordService;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.DoubleAdder;

@Slf4j
@EnableScheduling
@EnableAsync
@Service
public class RecordServiceImpl extends ServiceImpl<RecordMapper, Record> implements RecordService {
    private DoubleAdder totalPaidAmount = null;
    private AtomicInteger totalPaidCount = null;
    private TotalStat daily;
    private TotalStat weekly;
    private TotalStat yearly;

    @Override
    public List<Record> getPaidRecords() {
        return baseMapper.selectList(new QueryWrapper<Record>().eq("status", "SUCCESS"));
    }


    @Override
    public Double getTotalPaidAmount() {
        if (totalPaidAmount == null) {
            DoubleAdder doubleAdder = new DoubleAdder();
            Double amount = baseMapper.getTotalPaidAmount();
            if (amount == null) amount = 0.0;
            doubleAdder.add(amount);
            totalPaidAmount = doubleAdder;
        }
        return totalPaidAmount.doubleValue();
    }

    @Override
    public Integer getTotalPaidCount() {
        if (totalPaidCount == null) {
            totalPaidCount = new AtomicInteger(baseMapper.getTotalPaidCount());
        }
        return totalPaidCount.get();
    }

    @Override
    public List<Stat> getHours(int limit) {
        return baseMapper.getHour(limit);
    }

    @Override
    public List<Stat> getDays(int limit) {
        return baseMapper.getDays(limit, 1);
    }

    @Override
    public List<Stat> getAWeek(int offset) {
        return baseMapper.getAWeek(offset);
    }

    @Override
    public List<Stat> getMonth(int limit) {
        return baseMapper.getMonth(limit, 0);
    }

    @Override
    public void modifyTotalPaidAmount(double amount) {
        totalPaidAmount.add(amount);
    }

    @Override
    public void modifyTotalPaidCount(int amount) {
        totalPaidCount.getAndAdd(amount);
    }

    @Override
    public TotalStat getDailyTotal() {
        if (daily == null) daily = new TotalStat(getHours(24));
        return daily;
    }

    @Override
    public TotalStat getWeeklyTotal() {
        if (weekly == null) weekly = new TotalStat(getAWeek(0));
        return weekly;
    }

    @Override
    public TotalStat getYearlyTotal() {
        if (yearly == null) yearly = new TotalStat(getMonth(12));
        return yearly;
    }

    @Override
    public boolean removeRecord(Record record) {
        boolean b = removeById(record);
        if (b && Objects.equals(record.getStatus(), OrderStatusEnum.SUCCESS.name())) {
            modifyTotalPaidAmount(-record.getOrderAmount());
            modifyTotalPaidCount(-1);
        }
        return b;
    }

    /**
     * 每小时更新
     */
    @Async
    @Scheduled(cron = "0 * 0/1 * * ?")
    void updatePerHour() {
        daily = new TotalStat(getHours(24));

        totalPaidCount = new AtomicInteger(baseMapper.getTotalPaidCount());

        DoubleAdder doubleAdder = new DoubleAdder();
        Double amount = baseMapper.getTotalPaidAmount();
        if (amount == null) amount = 0.0;
        doubleAdder.add(amount);
        totalPaidAmount = doubleAdder;
        log.info("数据已更新: daily、totalPaidCount、totalPaidAmount");
    }

    /**
     * 每天凌晨4点更新
     */
    @Async
    @Scheduled(cron = "0 0 4 * * ?")
    void updatePerDay() {
        weekly = new TotalStat(getAWeek(0));
        yearly = new TotalStat(getMonth(12));
        log.info("数据已更新: weekly、yearly");
    }

}
