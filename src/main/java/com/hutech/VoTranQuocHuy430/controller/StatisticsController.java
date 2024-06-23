package com.hutech.VoTranQuocHuy430.controller;

import com.hutech.VoTranQuocHuy430.DTO.MonthlyRevenueDTO;
import com.hutech.VoTranQuocHuy430.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    @GetMapping("/admin/statistics")
    public String viewStatistics(@RequestParam(value = "year", defaultValue = "2024") int year, Model model) {
        List<MonthlyRevenueDTO> revenueData = statisticsService.getMonthlyRevenue(year);
        model.addAttribute("revenueData", revenueData);
        model.addAttribute("year", year);
        return "admin/statistics/statistics";
    }

    @GetMapping("/admin/statistics/data")
    @ResponseBody
    public List<MonthlyRevenueDTO> getStatisticsData(@RequestParam(value = "year", defaultValue = "2024") int year) {
        return statisticsService.getMonthlyRevenue(year);
    }
}
