package com.ec.crm.controller;

import com.ec.common.core.controller.BaseController;
import com.ec.common.core.domain.AjaxResult;
import com.ec.crm.domain.CrmCustomer;
import com.ec.crm.service.ICrmCustomerService;
import com.ec.crm.service.ICrmStatisticService;
import com.ec.crm.vo.TodayUpdatesVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 统计数据Controller
 */
@RestController
@RequestMapping("/crm/index-data")
public class IndexDataController extends BaseController {


    @Autowired
    private ICrmCustomerService customerService;

    @Autowired
    private ICrmStatisticService statisticService;

    /**
     * 获取一些统计信息和更新列表
     * @return
     */
    @GetMapping("/info")
    public AjaxResult indexData() {
        Map<String, Object> result = new HashMap<>();
        // 获取客户信息
        Map<String, Object> customerInfo = statisticService.customerInfo(getUsername());
        result.put("customer_info", customerInfo);

        // 获取跟进信息
        Map<String, Object> followupInfo = statisticService.followupInfo(getUsername());
        result.put("followup_info", followupInfo);

        // 获取当天的跟进信息
        List<CrmCustomer> todayFollowupList = statisticService.todayFollowupList(getUsername());
        result.put("today_followup_list", todayFollowupList);

        // 获取今天的更新列表
        List<TodayUpdatesVO> todayUpdatesList = customerService.todayUpdates(getUsername());
        result.put("today_updates", todayUpdatesList);

        return AjaxResult.success(result);
    }

}
