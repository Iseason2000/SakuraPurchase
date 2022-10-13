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
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import top.iseason.sakurapurchase.entity.Record;
import top.iseason.sakurapurchase.service.RecordService;

import javax.annotation.Resource;
import java.util.Date;

/**
 * 支付相关api
 */
@Controller
@Slf4j
@RequestMapping("/api/pay")
public class PaymentController {
    private final Sequence sequence = new Sequence(null);
    @Resource
    private BestPayService bestPayService;
    @Resource
    private RecordService recordService;

    @GetMapping("/test")
    @ResponseBody
    public String test() {
        return "Success";
    }

    /**
     * 发起二维码支付 /api/pay/buy?payType=ALIPAY_QRCODE&orderName=测试商品&amount=0.01
     */
    @PostMapping(value = "/buy")
    @ResponseBody
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
                .platform(payType == BestPayTypeEnum.ALIPAY_QRCODE ? 0 : 1)
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
    @GetMapping("/query/{orderId}")
    @ResponseBody
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

        //状态不一致则更新
        if (byId.getOrderStatus() != queryResponse.getOrderStatusEnum() && queryResponse.getOrderStatusEnum() != OrderStatusEnum.CLOSED) {
            byId.setStatus(queryResponse.getOrderStatusEnum().name());
            if (queryResponse.getOutTradeNo() != null) {
                byId.setOutTradeNo(queryResponse.getOutTradeNo());
            }
            recordService.updateById(byId);
        }
        return queryResponse;
    }

    /**
     * 退款API
     *
     * @param orderId 订单ID
     * @return
     */
    @GetMapping("/refund")
    @ResponseBody
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
        recordService.saveOrUpdate(byId);
        return response;
    }

    /**
     * 异步回调
     */
    @PostMapping(value = "/notify")
    @ResponseBody
    public String notify(@RequestBody String notifyData) {
        log.debug("[异步通知] 支付平台的数据request={}", notifyData);
        PayResponse response = bestPayService.asyncNotify(notifyData);
        log.info("[异步通知] 处理后的数据data={}", JsonUtil.toJson(response));
        //返回成功信息给支付平台，否则会不停的异步通知
        String result = null;
        if (response.getPayPlatformEnum() == BestPayPlatformEnum.WX) {
            result = "<xml>\n    <return_code><![CDATA[SUCCESS]]></return_code>\n    <return_msg><![CDATA[OK]]></return_msg>\n</xml>";
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
}
