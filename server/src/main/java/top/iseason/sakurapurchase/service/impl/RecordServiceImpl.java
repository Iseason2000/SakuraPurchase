package top.iseason.sakurapurchase.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lly835.bestpay.enums.OrderStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import top.iseason.sakurapurchase.entity.Record;
import top.iseason.sakurapurchase.entity.Stat;
import top.iseason.sakurapurchase.entity.TotalStat;
import top.iseason.sakurapurchase.mapper.RecordMapper;
import top.iseason.sakurapurchase.service.RecordService;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.DoubleAdder;

@Slf4j
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
        return totalPaidAmount.doubleValue();
    }

    @Override
    public Integer getTotalPaidCount() {
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
     * 初始化统计数据
     */
    @PostConstruct
    private void initStatics() {
        log.info("开始初始化统计数据...");
        DoubleAdder doubleAdder = new DoubleAdder();
        doubleAdder.add(baseMapper.getTotalPaidAmount());
        totalPaidAmount = doubleAdder;
        totalPaidCount = new AtomicInteger(baseMapper.getTotalPaidCount());
        log.info("统计数据初始化完成");
    }
}
