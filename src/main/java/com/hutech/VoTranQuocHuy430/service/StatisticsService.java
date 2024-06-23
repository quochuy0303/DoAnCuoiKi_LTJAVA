package com.hutech.VoTranQuocHuy430.service;

import com.hutech.VoTranQuocHuy430.DTO.MonthlyRevenueDTO;
import com.hutech.VoTranQuocHuy430.repository.StatisticsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StatisticsService {

    @Autowired
    private StatisticsRepository statisticsRepository;

    public List<MonthlyRevenueDTO> getMonthlyRevenue(int year) {
        return statisticsRepository.findMonthlyRevenue(year);
    }
}
