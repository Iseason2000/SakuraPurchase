package top.iseason.sakurapurchase.controller.api;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import top.iseason.sakurapurchase.entity.BukkitRecord;
import top.iseason.sakurapurchase.entity.Record;
import top.iseason.sakurapurchase.service.BukkitRecordService;
import top.iseason.sakurapurchase.service.RecordService;

import javax.annotation.Resource;
import java.util.List;

@Transactional
@Controller
@Slf4j
@RequestMapping("/api/record")
public class BukkitRecordController {
    @Resource
    BukkitRecordService bukkitRecordService;
    @Resource
    private RecordService recordService;

    /**
     * 查询用户的订单
     *
     * @param uuid
     * @return
     */
    @GetMapping("/user-all/{uuid}")
    @ResponseBody
    public List<Record> userAll(@PathVariable("uuid") String uuid,
                                @RequestParam(value = "offset", required = false) Integer offset,
                                @RequestParam(value = "amount", required = false) Integer amount
    ) {
        return (offset != null && amount != null) ?
                bukkitRecordService.getUserRecordIds(uuid, offset, amount) :
                bukkitRecordService.getUserRecords(uuid);
    }

    /**
     * 获取用户上一个订单
     *
     * @param uuid
     * @return
     */
    @GetMapping("/user-last/{uuid}")
    @ResponseBody
    public Record userLast(@PathVariable("uuid") String uuid) {
        return bukkitRecordService.getLastRecord(uuid);
    }

    /**
     * 获取所有bukkit支付记录
     *
     * @return
     */
    @GetMapping("/all")
    @ResponseBody
    public List<Record> all() {
        return bukkitRecordService.getAll();
    }

    /**
     * 获取bukkit端总充值金额
     *
     * @return
     */
    @GetMapping("/all-total")
    @ResponseBody
    public Double allTotal() {
        return bukkitRecordService.getAllTotal();
    }

    /**
     * 获取bukkit端用户总充值金额
     *
     * @param uuid
     * @return
     */
    @GetMapping("/user-total/{uuid}")
    @ResponseBody
    public Double userTotal(@PathVariable("uuid") String uuid) {
        return bukkitRecordService.getUserTotal(uuid);
    }

    /**
     * 判断用户是否存在某个订单的支付记录
     *
     * @param uuid
     * @param orderId
     * @return
     */
    @GetMapping("/user-is-purchased/{uuid}/{orderId}")
    @ResponseBody
    public Boolean userHasPurchased(@PathVariable("uuid") String uuid, @PathVariable("orderId") String orderId) {
        return bukkitRecordService.hasOrder(uuid, orderId);
    }

    @PostMapping("/save")
    @ResponseBody
    public Boolean save(@RequestParam("uuid") String uuid, @RequestParam("orderId") Long orderId) {
        log.info("bukkit 订单: " + orderId + "已完成");
        recordService.update(new LambdaUpdateWrapper<Record>()
                .set(Record::getStatus, "SUCCESS")
                .eq(Record::getOrderId, orderId)
        );
        return bukkitRecordService.save(new BukkitRecord(null, uuid, orderId));
    }
}
