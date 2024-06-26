package com.ec.crm.controller;

import com.ec.common.core.controller.BaseController;
import com.ec.common.core.domain.AjaxResult;
import com.ec.common.core.page.TableDataInfo;
import com.ec.crm.domain.CrmCustomer;
import com.ec.crm.domain.enums.CustomerFolder;
import com.ec.crm.service.ICrmCustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 公海管理Controller
 */
@RestController
@RequestMapping("/crm/pool")
public class CrmPoolControler extends BaseController {
    @Autowired
    private ICrmCustomerService crmCustomerService;

    /**
     * 查询客户列表
     * @param crmCustomer
     * @return
     */
    @PreAuthorize("@ss.hasPermi('crm:pool:list')")
    @GetMapping("/list")
    public TableDataInfo list(CrmCustomer crmCustomer) {
        startPage();
        crmCustomer.setStatus(CustomerFolder.POOL.getCode());
        List<CrmCustomer> list = crmCustomerService.selectCrmCustomerList(crmCustomer);
        return getDataTable(list);
    }


    /**
     * 获取客户详细信息
     * @param id
     * @return
     */
    @PreAuthorize("@ss.hasPermi('crm:pool:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return AjaxResult.success(crmCustomerService.selectCrmCustomerById(id));
    }

    /**
     * 领取客户
     * @param id 客户ID
     * @return
     */
    @PreAuthorize("@ss.hasPermi('crm:pool:receive')")
    @GetMapping(value = "/receive/{id}")
    public AjaxResult receive(@PathVariable("id") Long id) {
        return AjaxResult.success(crmCustomerService.receiveCustomerById(id, getUsername()));
    }
}