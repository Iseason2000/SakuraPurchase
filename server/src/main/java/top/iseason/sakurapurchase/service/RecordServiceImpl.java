package top.iseason.sakurapurchase.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import top.iseason.sakurapurchase.entity.Record;
import top.iseason.sakurapurchase.mapper.RecordMapper;

@Service
public class RecordServiceImpl extends ServiceImpl<RecordMapper, Record> implements RecordService {
}
