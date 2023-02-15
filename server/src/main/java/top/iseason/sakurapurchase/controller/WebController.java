package top.iseason.sakurapurchase.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import top.iseason.sakurapurchase.entity.Record;
import top.iseason.sakurapurchase.service.RecordService;

import javax.annotation.Resource;
import java.text.DecimalFormat;
import java.util.List;


@Controller
public class WebController {
    final DecimalFormat fmt = new DecimalFormat("##,###,###,###,###0.00");
    @Resource
    RecordService recordService;

    @RequestMapping("/")
    public String index() {
        return "redirect:index";
    }

    @RequestMapping("/index")
    public String index(Model model, Authentication authentication) {
        model.addAttribute("dailyTotal", recordService.getDailyTotal());
        model.addAttribute("weeklyTotal", recordService.getWeeklyTotal());
        model.addAttribute("yearlyTotal", recordService.getYearlyTotal());
        model.addAttribute("username", authentication.getName());
        model.addAttribute("totalAmount", fmt.format(recordService.getTotalPaidAmount()));
        model.addAttribute("totalCount", recordService.getTotalPaidCount());
        return "index";
    }

    @RequestMapping("/orders")
    public String orders(Model model,
                         Authentication authentication,
                         @RequestParam(required = false) String keywords,
                         @RequestParam(name = "page", required = false) String pageStr) {
        int pageIndex = 1;
        if (pageStr != null) {
            try {
                pageIndex = Integer.parseInt(pageStr);
                if (pageIndex <= 0) pageIndex = 1;
            } catch (NumberFormatException ignored) {
            }
        }
        QueryWrapper<Record> qw = new QueryWrapper<>();
        qw.orderByDesc("create_time").last("limit " + (pageIndex - 1) * 8 + ",8");
        if (keywords != null && !keywords.equals("null")) {
            qw = qw.like("order_id", keywords).or()
                    .like("order_name", keywords).or()
                    .like("order_amount", keywords).or()
                    .like("attach", keywords).or()
                    .like("status", keywords);
        }
        List<Record> records = recordService.getBaseMapper().selectList(qw);
        model.addAttribute("records", records);
        model.addAttribute("page", pageIndex);
        model.addAttribute("keywords", keywords);
        model.addAttribute("username", authentication.getName());
        return "orders";
    }

    @RequestMapping("/login")
    public String login() {
        return "login";
    }
}
