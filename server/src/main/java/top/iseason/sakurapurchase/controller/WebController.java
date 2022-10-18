package top.iseason.sakurapurchase.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import top.iseason.sakurapurchase.service.RecordService;

import javax.annotation.Resource;
import java.text.DecimalFormat;

@Controller
public class WebController {
    final DecimalFormat fmt = new DecimalFormat("##,###,###,###,###0.00");
    @Resource
    RecordService recordService;

    @RequestMapping("/")
    public String index() {
        return "redirect:index.html";
    }

    @RequestMapping("/index.html")
    public void index(Model model, Authentication authentication) {
        model.addAttribute("dailyTotal", recordService.getDailyTotal());
        model.addAttribute("weeklyTotal", recordService.getWeeklyTotal());
        model.addAttribute("yearlyTotal", recordService.getYearlyTotal());
        model.addAttribute("username", authentication.getName());
        model.addAttribute("totalAmount", fmt.format(recordService.getTotalPaidAmount()));
        model.addAttribute("totalCount", recordService.getTotalPaidCount());
    }

    @RequestMapping("/{page}.html")
    public String redirect(Model model, Authentication authentication, @PathVariable String page) {
        model.addAttribute("username", authentication.getName());
        return page;
    }


    @RequestMapping("/login")
    public String login() {
        return "login";
    }

}
