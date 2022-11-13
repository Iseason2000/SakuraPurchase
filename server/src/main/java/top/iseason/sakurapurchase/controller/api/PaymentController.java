package top.iseason.sakurapurchase.controller.api;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Sequence;
import com.lly835.bestpay.enums.BestPayPlatformEnum;
import com.lly835.bestpay.enums.BestPayTypeEnum;
import com.lly835.bestpay.enums.OrderStatusEnum;
import com.lly835.bestpay.model.*;
import com.lly835.bestpay.service.BestPayService;
import com.lly835.bestpay.utils.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import top.iseason.sakurapurchase.entity.Record;
import top.iseason.sakurapurchase.service.RecordService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * 支付相关api
 */
@RestController
@Slf4j
@RequestMapping("/api/pay")
public class PaymentController {
    private final Sequence sequence = new Sequence(null);
    @Resource
    private BestPayService bestPayService;
    @Resource
    private RecordService recordService;

    @GetMapping("/test/{version}")
    public String test(@PathVariable("version") String version, HttpServletRequest request) {
        log.info("服务器: " + getIpAddr(request) + " 已连接,版本: " + version);
        return "Success";
    }

    /**
     * 获取真实ip地址,不返回内网地址
     *
     * @param request
     * @return
     */
    public String getIpAddr(HttpServletRequest request) {
        //目前则是网关ip
        String ip = request.getHeader("X-Real-IP");
        if (ip != null && !"".equals(ip) && !"unknown".equalsIgnoreCase(ip)) {
            return ip;
        }
        ip = request.getHeader("X-Forwarded-For");
        if (ip != null && !"".equals(ip) && !"unknown".equalsIgnoreCase(ip)) {
            int index = ip.indexOf(',');
            if (index != -1) {
                //只获取第一个值
                return ip.substring(0, index);
            } else {
                return ip;
            }
        } else {
            //取不到真实ip则返回内网地址。
            return request.getRemoteAddr();
        }
    }

    /**
     * 发起二维码支付 /api/pay/buy?payType=ALIPAY_QRCODE&orderName=测试商品&amount=0.01
     */
    @Transactional
    @PostMapping(value = "/buy")
    public PayResponse pay(
            @RequestParam("type") BestPayTypeEnum payType,
            @RequestParam("name") String orderName,
            @RequestParam("amount") Double amount,
            @RequestParam(value = "openid", required = false) String openid,
            @RequestParam(value = "attach", required = false) String attach) {
        if (payType != BestPayTypeEnum.ALIPAY_QRCODE && payType != BestPayTypeEnum.WXPAY_NATIVE)
            return new PayResponse();
        //支付请求参数
        long orderId = sequence.nextId();
        PayRequest request = new PayRequest();
        request.setPayTypeEnum(payType);
        request.setOrderId(String.valueOf(orderId));
        request.setOrderName(orderName);
        request.setOrderAmount(amount);
        request.setOpenid(openid);
        request.setAttach(attach);

        log.debug("[尝试发起支付] request={}", JsonUtil.toJson(request));

        PayResponse payResponse = bestPayService.pay(request);
        log.info("[发起支付成功] response={}", JsonUtil.toJson(payResponse));
        payResponse.setOrderId(String.valueOf(orderId));
        //记录
        recordService.save(Record.builder()
                .orderId(orderId)
                .payType(payType.ordinal())
                .status(OrderStatusEnum.NOTPAY.name())
                .orderName(orderName)
                .orderAmount(amount)
                .outTradeNo(openid)
                .createTime(new Date())
                .attach(attach).build());

        return payResponse;
    }

    /**
     * 查询API
     *
     * @param orderId
     * @return
     */
    @Transactional
    @GetMapping("/query/{orderId}")
    public OrderQueryResponse query(@PathVariable("orderId") String orderId) {
        //没有缓存则在线查询
        Record byId = recordService.getById(orderId);
        if (byId == null) return null;

        OrderQueryRequest orderQueryRequest = new OrderQueryRequest();
        orderQueryRequest.setOrderId(orderId);
        orderQueryRequest.setOutOrderId(byId.getOutTradeNo());
        orderQueryRequest.setPlatformEnum(byId.getPlatformEnum());

        OrderQueryResponse queryResponse;
        try {
            queryResponse = bestPayService.query(orderQueryRequest);
        } catch (Exception e) {
            //发生异常
            return OrderQueryResponse.builder()
                    .orderId(orderId)
                    .orderStatusEnum(OrderStatusEnum.NOTPAY)
                    .resultMsg(e.getMessage())
                    .build();
        }
        OrderStatusEnum orderStatusEnum = queryResponse.getOrderStatusEnum();
        //状态不一致则更新
        if (byId.getOrderStatus() != orderStatusEnum && orderStatusEnum != OrderStatusEnum.CLOSED) {
            byId.setStatus(queryResponse.getOrderStatusEnum().name());
            if (queryResponse.getOutTradeNo() != null) {
                byId.setOutTradeNo(queryResponse.getOutTradeNo());
            }
            recordService.updateById(byId);
            if (orderStatusEnum == OrderStatusEnum.SUCCESS) {
                recordService.modifyTotalPaidAmount(byId.getOrderAmount());
                recordService.modifyTotalPaidCount(1);
            }
        }
        return queryResponse;
    }

    /**
     * 退款API
     *
     * @param orderId 订单ID
     * @return
     */
    @Transactional
    @PostMapping("/refund")
    public RefundResponse refund(@RequestParam String orderId) {
        Record byId = recordService.getById(orderId);
        if (byId == null) return null;
        RefundRequest request = new RefundRequest();
        request.setOrderId(orderId);
        request.setPayPlatformEnum(byId.getPlatformEnum());
        request.setRefundAmount(byId.getOrderAmount());
        request.setOrderAmount(byId.getOrderAmount());
        log.debug("[尝试退款] request={}", JsonUtil.toJson(request));
        RefundResponse response;
        try {
            response = bestPayService.refund(request);
        } catch (Exception e) {
            log.info("[退款失败] message={}", e.getMessage());
            return null;
        }
        log.info("[退款成功] request={}", JsonUtil.toJson(response));
        byId.setStatus(OrderStatusEnum.REFUND.name());
        recordService.modifyTotalPaidAmount(-byId.getOrderAmount());
        recordService.modifyTotalPaidCount(-1);
        recordService.saveOrUpdate(byId);
        return response;
    }

    /**
     * 关闭订单
     *
     * @param orderId
     * @return
     */
    @Transactional
    @PostMapping("/close")
    public CloseResponse close(@RequestParam String orderId) {
        Record byId = recordService.getById(orderId);
        if (byId == null) return null;
        CloseRequest closeRequest = new CloseRequest();
        closeRequest.setOrderId(orderId);
        closeRequest.setPayTypeEnum(byId.getPayTypeEnum());
        closeRequest.setOutOrderId(byId.getOutTradeNo());
        closeRequest.setOperatorId("platform");
        byId.setStatus(OrderStatusEnum.CLOSED.name());
        recordService.updateById(byId);
        CloseResponse response;
        try {
            response = bestPayService.close(closeRequest);
        } catch (Exception e) {
            log.info("[关闭失败] message={}", e.getMessage());
            return null;
        }
        log.info("[关闭成功] request={}", JsonUtil.toJson(response));
        return response;
    }

    /**
     * 异步回调
     */
    @PostMapping(value = "/notify")
    public String notify(@RequestBody String notifyData) {
        log.debug("[异步通知] 支付平台的数据request={}", notifyData);
        PayResponse response = null;
        try {
            response = bestPayService.asyncNotify(notifyData);
        } catch (Exception e) {
            log.debug(e.getMessage());
        }
        log.info("[异步通知] 处理后的数据data={}", JsonUtil.toJson(response));
        //返回成功信息给支付平台，否则会不停的异步通知
        String result = null;
        if (response.getPayPlatformEnum() == BestPayPlatformEnum.WX) {
            result = "<xml>\n<return_code><![CDATA[SUCCESS]]></return_code>\n<return_msg><![CDATA[OK]]></return_msg>\n</xml>";
        } else if (response.getPayPlatformEnum() == BestPayPlatformEnum.ALIPAY) {
            result = "success";
        }
        if (result == null) throw new RuntimeException("错误的支付平台");
        // 插入记录
        recordService.update(new UpdateWrapper<Record>()
                .eq("order_id", response.getOrderId())
                .set("status", OrderStatusEnum.SUCCESS));
        return result;
    }

    //    /**
//     * 关闭支付，只对支付宝有效
//     *
//     * @param orderId
//     * @return
//     */
//    @GetMapping("/pay/close")
//    @ResponseBody
//    public CloseResponse close(@RequestParam String orderId) {
//        Record byId = recordService.getById(orderId);
//        if (byId == null) return null;
//        CloseRequest request = new CloseRequest();
//        request.setPayTypeEnum(BestPayTypeEnum.ALIPAY_PC);
//        request.setOrderId(orderId);
//        byId.setStatus(OrderStatusEnum.CLOSED.name());
//        recordService.saveOrUpdate(byId);
//        CloseResponse close = bestPayService.close(request);
//        return close;
//    }
    @PostMapping("remove")
    public String remove(@RequestParam("orderId") String orderId) {
        if (recordService.removeById(orderId)) {
            return "删除成功";
        }
        return "删除失败";
    }
}
