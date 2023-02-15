package top.iseason.sakurapurchase.controller.api;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Sequence;
import com.lly835.bestpay.enums.BestPayPlatformEnum;
import com.lly835.bestpay.enums.BestPayTypeEnum;
import com.lly835.bestpay.enums.OrderStatusEnum;
import com.lly835.bestpay.model.*;
import com.lly835.bestpay.service.BestPayService;
import com.lly835.bestpay.utils.JsonUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import springfox.documentation.annotations.ApiIgnore;
import top.iseason.sakurapurchase.entity.Record;
import top.iseason.sakurapurchase.service.RecordService;
import top.iseason.sakurapurchase.utils.Result;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Map;

/**
 * 支付相关api
 */
@RestController
@Slf4j
@RequestMapping("/api/pay")
@Transactional
@Api(tags = "支付API")
public class PaymentController {

    private final Sequence sequence = new Sequence(null);
    @Resource
    private BestPayService bestPayService;
    @Resource
    private RecordService recordService;

    @ApiOperation(value = "测试连接有效性")
    @GetMapping("/test/{version}")
    public Result<Object> test(
            @ApiParam(value = "游戏版本", required = true) @PathVariable("version") String version,
            @ApiIgnore HttpServletRequest request) {
        log.info("服务器: " + getIpAddr(request) + " 已连接,版本: " + version);
        return Result.success();
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
    @ApiOperation(value = "发起支付")
    public Result<PayResponse> pay(
            @ApiParam(value = "支付类型", required = true) @RequestParam("type") BestPayTypeEnum payType,
            @ApiParam(value = "订单名称", required = true) @RequestParam("name") String orderName,
            @ApiParam(value = "订单金额", required = true) @RequestParam("amount") Double amount,
            @ApiParam(value = "第三方流水id") @RequestParam(required = false) String openid,
            @ApiParam(value = "附加信息") @RequestParam(required = false) String attach,
            @ApiParam(value = "购买者ID long, 仅ALIPAY_H5") @RequestParam(required = false) String buyerLogonId,
            @ApiParam(value = "购买者ID, 仅ALIPAY_H5") @RequestParam(required = false) String buyerId) {
//        if (payType != BestPayTypeEnum.ALIPAY_QRCODE && payType != BestPayTypeEnum.WXPAY_NATIVE)
//            return Result.failure("不支持的订单类型");
        //支付请求参数
        long orderId = sequence.nextId();
        PayRequest request = new PayRequest();
        request.setPayTypeEnum(payType);
        request.setOrderId(String.valueOf(orderId));
        request.setOrderName(orderName);
        request.setOrderAmount(amount);
        request.setOpenid(openid);
        request.setAttach(attach);
        if (payType == BestPayTypeEnum.ALIPAY_H5) {
            request.setBuyerLogonId(buyerLogonId);
            request.setBuyerId(buyerId);
        }
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
        return Result.success(payResponse);
    }

    /**
     * 查询API
     *
     * @param orderId
     * @return
     */
    @Transactional
    @GetMapping("/query/{orderId}")
    @ApiOperation(value = "查询订单状态")
    public Result<OrderQueryResponse> query(
            @ApiParam(value = "订单ID", required = true) @PathVariable("orderId") String orderId) {
        //没有缓存则在线查询
        Record byId = recordService.getById(orderId);
        if (byId == null) return Result.failure("订单不存在");

        OrderQueryRequest orderQueryRequest = new OrderQueryRequest();
        orderQueryRequest.setOrderId(orderId);
        orderQueryRequest.setOutOrderId(byId.getOutTradeNo());
        orderQueryRequest.setPlatformEnum(byId.getPlatformEnum());

        OrderQueryResponse queryResponse;
        try {
            queryResponse = bestPayService.query(orderQueryRequest);
        } catch (Exception e) {
            //发生异常
            return Result.failure(e.getMessage());
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
        return Result.success(queryResponse);
    }

    /**
     * 退款API
     *
     * @param orderId 订单ID
     * @return
     */
    @Transactional
    @PostMapping("/refund")
    @ApiOperation(value = "退款")
    public Result<RefundResponse> refund(@ApiParam(value = "订单ID", required = true) @RequestParam String orderId) {
        Record byId = recordService.getById(orderId);
        if (byId == null) return Result.failure("订单不存在");
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
            return Result.failure(e.getMessage());
        }
        log.info("[退款成功] request={}", JsonUtil.toJson(response));
        byId.setStatus(OrderStatusEnum.REFUND.name());
        recordService.modifyTotalPaidAmount(-byId.getOrderAmount());
        recordService.modifyTotalPaidCount(-1);
        recordService.saveOrUpdate(byId);
        return Result.success(response);
    }

    /**
     * 关闭订单
     *
     * @param orderId
     * @return
     */
    @Transactional
    @PostMapping("/close")
    @ApiOperation(value = "关闭订单")
    public Result<CloseResponse> close(
            @ApiParam(value = "订单ID", required = true) @RequestParam String orderId) {
        Record byId = recordService.getById(orderId);
        if (byId == null) return Result.failure("订单不存在");
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
            return Result.failure(e.getMessage());
        }
        log.info("[关闭成功] request={}", JsonUtil.toJson(response));
        return Result.success(response);
    }

    /**
     * 异步回调
     */
    @PostMapping(value = "/notify")
    @ApiOperation(value = "回调")
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

    @PostMapping("/remove")
    @ApiOperation(value = "删除订单")
    public Result<Object> remove(
            @ApiParam(value = "订单ID", required = true) @RequestParam("orderId") String orderId) {
        if (recordService.removeById(orderId)) {
            return Result.success();
        }
        return Result.failure();
    }

    /**
     * 微信h5支付，要求referer是白名单的地址，这里做个重定向
     *
     * @param prepayId
     * @param packAge
     * @return
     */
    @ApiIgnore
    @GetMapping("/wxpay_mweb_redirect")
    public ModelAndView wxpayMweb(@RequestParam("prepay_id") String prepayId,
                                  @RequestParam("package") String packAge,
                                  Map map) {
        String url = String.format("https://wx.tenpay.com/cgi-bin/mmpayweb-bin/checkmweb?prepay_id=%s&package=%s", prepayId, packAge);
        map.put("url", url);
        return new ModelAndView("pay/wxpayMwebRedirect");
    }


}
