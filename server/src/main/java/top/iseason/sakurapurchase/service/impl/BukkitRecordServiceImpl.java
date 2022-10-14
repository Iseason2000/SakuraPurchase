package top.iseason.sakurapurchase.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.iseason.sakurapurchase.entity.BukkitRecord;
import top.iseason.sakurapurchase.entity.Record;
import top.iseason.sakurapurchase.mapper.BukkitRecordMapper;
import top.iseason.sakurapurchase.mapper.RecordMapper;
import top.iseason.sakurapurchase.service.BukkitRecordService;

import javax.annotation.Resource;
import java.util.List;

@Service
public class BukkitRecordServiceImpl extends ServiceImpl<BukkitRecordMapper, BukkitRecord> implements BukkitRecordService {
    @Resource
    BukkitRecordMapper bukkitRecordMapper;
    @Resource
    RecordMapper recordMapper;

    @Override
    public List<Record> getUserRecords(String uuid) {
        return bukkitRecordMapper.getUserRecords(uuid);
    }

    @Transactional
    @Override
    public List<Record> getUserRecordsBath(String uuid) {
        List<String> userRecordIds = bukkitRecordMapper.getUserRecordIds(uuid);
        return recordMapper.selectBatchIds(userRecordIds);
    }

    @Override
    public Record getLastRecord(String uuid) {
        return getUserRecord(uuid, 0);
    }

    @Transactional
    @Override
    public Record getUserRecord(String uuid, int offset) {
        Long userRecord = bukkitRecordMapper.getUserRecord(uuid, offset);
        return recordMapper.selectById(userRecord);
    }

    @Override
    public Boolean hasOrder(String uuid, String orderId) {
        return bukkitRecordMapper.exists(new QueryWrapper<BukkitRecord>().eq("uuid", uuid).eq("order_id", orderId));
    }

    @Override
    public List<Record> getAll() {
        return bukkitRecordMapper.getAllRecords();
    }

    @Override
    public Double getAllTotal() {
        return bukkitRecordMapper.getTotalAmount();
    }

    @Override
    public Double getUserTotal(String uuid) {
        return bukkitRecordMapper.getUserTotalAmount(uuid);
    }

    @Override
    public List<Record> getUserRecordIds(String uuid, int offset, int amount) {
        return bukkitRecordMapper.getUserRecordIdsLimit(uuid, offset, amount);
    }
}
