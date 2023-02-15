package top.iseason.sakurapurchase.controller.api;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import top.iseason.sakurapurchase.entity.BukkitRecord;
import top.iseason.sakurapurchase.entity.Record;
import top.iseason.sakurapurchase.service.BukkitRecordService;
import top.iseason.sakurapurchase.service.RecordService;
import top.iseason.sakurapurchase.utils.Result;

import javax.annotation.Resource;
import java.util.List;

@Transactional
@Slf4j
@RequestMapping("/api/record")
@RestController
@Api(tags = "bukkitAPI")
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
    @ApiOperation(value = "查询用户的订单")
    @GetMapping("/user-all/{uuid}")
    @Transactional(readOnly = true)
    public Result<List<Record>> userAll(
            @ApiParam(value = "玩家uuid", required = true) @PathVariable("uuid") String uuid,
            @ApiParam(value = "偏移") @RequestParam(value = "offset", required = false) Integer offset,
            @ApiParam(value = "数量") @RequestParam(value = "amount", required = false) Integer amount
    ) {
        return Result.success((offset != null && amount != null) ?
                bukkitRecordService.getUserRecordIds(uuid, offset, amount) :
                bukkitRecordService.getUserRecords(uuid));
    }

    /**
     * 获取用户上一个订单
     *
     * @param uuid
     * @return
     */
    @ApiOperation(value = "获取用户上一个订单")
    @GetMapping("/user-last/{uuid}")
    @Transactional(readOnly = true)
    public Result<Record> userLast(
            @ApiParam(value = "玩家uuid", required = true) @PathVariable("uuid") String uuid) {
        return Result.success(bukkitRecordService.getLastRecord(uuid));
    }

    /**
     * 获取所有bukkit支付记录
     *
     * @return
     */
    @ApiOperation(value = "获取所有bukkit支付记录")
    @GetMapping("/all")
    @Transactional(readOnly = true)
    public Result<List<Record>> all() {
        return Result.success(bukkitRecordService.getAll());
    }

    /**
     * 获取bukkit端总充值金额
     *
     * @return
     */
    @Transactional(readOnly = true)
    @GetMapping("/all-total")
    @ApiOperation(value = "获取bukkit端总充值金额")
    public Result<Double> allTotal() {
        return Result.success(bukkitRecordService.getAllTotal());
    }

    /**
     * 获取bukkit端用户总充值金额
     *
     * @param uuid
     * @return
     */
    @Transactional(readOnly = true)
    @GetMapping("/user-total/{uuid}")
    @ApiOperation(value = "获取bukkit端用户总充值金额")
    public Result<Double> userTotal(@ApiParam(value = "玩家uuid", required = true) @PathVariable("uuid") String uuid) {
        return Result.success(bukkitRecordService.getUserTotal(uuid));
    }

    /**
     * 判断用户是否存在某个订单的支付记录
     *
     * @param uuid
     * @param orderId
     * @return
     */
    @ApiOperation(value = "判断用户是否存在某个订单的支付记录")
    @GetMapping("/user-is-purchased/{uuid}/{orderId}")
    public Result<Object> userHasPurchased(
            @ApiParam(value = "玩家uuid", required = true) @PathVariable("uuid") String uuid,
            @ApiParam(value = "订单ID", required = true) @PathVariable("orderId") String orderId) {
        if (bukkitRecordService.hasOrder(uuid, orderId)) {
            return Result.success();
        } else {
            return Result.of(999, "订单不存在!");
        }
    }

    @Transactional
    @ApiOperation(value = "保存用户订单并标记已支付")
    @PostMapping("/save")
    public Result<Object> save(
            @ApiParam(value = "玩家uuid", required = true) @RequestParam("uuid") String uuid,
            @ApiParam(value = "订单ID", required = true) @RequestParam("orderId") Long orderId) {
        log.info("bukkit 订单: " + orderId + "已完成");
        recordService.update(new LambdaUpdateWrapper<Record>()
                .set(Record::getStatus, "SUCCESS")
                .eq(Record::getOrderId, orderId)
        );
        if (bukkitRecordService.save(new BukkitRecord(null, uuid, orderId))) {
            return Result.success();
        } else {
            return Result.of(999, "保存失败!");
        }
    }
}
