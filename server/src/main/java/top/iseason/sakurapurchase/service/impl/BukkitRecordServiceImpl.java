package top.iseason.sakurapurchase.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
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
    RecordMapper recordMapper;

    @Override
    public List<Record> getUserRecords(String uuid) {
        return baseMapper.getUserRecords(uuid);
    }

    @Override
    public List<Record> getUserRecordsBath(String uuid) {
        List<String> userRecordIds = baseMapper.getUserRecordIds(uuid);
        return recordMapper.selectBatchIds(userRecordIds);
    }

    @Override
    public Record getLastRecord(String uuid) {
        return getUserRecord(uuid, 0);
    }

    @Override
    public Record getUserRecord(String uuid, int offset) {
        Long userRecord = baseMapper.getUserRecord(uuid, offset);
        return recordMapper.selectById(userRecord);
    }

    @Override
    public Boolean hasOrder(String uuid, String orderId) {
        return baseMapper.exists(new QueryWrapper<BukkitRecord>().eq("uuid", uuid).eq("order_id", orderId));
    }

    @Override
    public List<Record> getAll() {
        return baseMapper.getAllRecords();
    }

    @Override
    public Double getAllTotal() {
        return baseMapper.getTotalAmount();
    }

    @Override
    public Double getUserTotal(String uuid) {
        return baseMapper.getUserTotalAmount(uuid);
    }

    @Override
    public List<Record> getUserRecordIds(String uuid, int offset, int amount) {
        return baseMapper.getUserRecordIdsLimit(uuid, offset, amount);
    }
}
