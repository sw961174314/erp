package com.ec.crm.controller;

import com.ec.common.annotation.Log;
import com.ec.common.core.controller.BaseController;
import com.ec.common.core.domain.AjaxResult;
import com.ec.common.core.page.TableDataInfo;
import com.ec.common.enums.BusinessType;
import com.ec.common.utils.DateUtils;
import com.ec.common.utils.poi.ExcelUtil;
import com.ec.crm.domain.CrmCustomer;
import com.ec.crm.domain.enums.CustomerFolder;
import com.ec.crm.form.ToCustomerDTO;
import com.ec.crm.service.ICrmCustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 线索管理Controller
 */
@RestController
@RequestMapping("/crm/clues")
public class CrmCluesController extends BaseController {
    @Autowired
    private ICrmCustomerService cluesService;

    /**
     * 查询我的线索列表
     * @param crmCustomer
     * @return
     */
    @PreAuthorize("@ss.hasPermi('crm:clues:person:list')")
    @GetMapping("/person-list")
    public TableDataInfo personList(CrmCustomer crmCustomer) {
        startPage();
        crmCustomer.setStatus(CustomerFolder.CLUES.getCode());
        crmCustomer.setOwner(getUsername());
        List<CrmCustomer> list = cluesService.selectCrmCustomerList(crmCustomer);
        return getDataTable(list);
    }

    /**
     * 查询线索列表
     * @param crmCustomer
     * @return
     */
    @PreAuthorize("@ss.hasPermi('crm:clues:list')")
    @GetMapping("/list")
    public TableDataInfo list(CrmCustomer crmCustomer) {
        startPage();
        crmCustomer.setStatus(CustomerFolder.CLUES.getCode());
        List<CrmCustomer> list = cluesService.selectCrmCustomerList(crmCustomer);
        return getDataTable(list);
    }

    /**
     * 导出线索列表
     * @param response
     * @param crmCustomer
     */
    @PreAuthorize("@ss.hasPermi('crm:clues:export')")
    @Log(title = "线索", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, CrmCustomer crmCustomer) {
        List<CrmCustomer> list = cluesService.selectCrmCustomerList(crmCustomer);
        ExcelUtil<CrmCustomer> util = new ExcelUtil<CrmCustomer>(CrmCustomer.class);
        util.exportExcel(response, list, "线索数据");
    }

    /**
     * 获取线索详细信息
     * @param id
     * @return
     */
    @PreAuthorize("@ss.hasPermi('crm:clues:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return AjaxResult.success(cluesService.selectCrmCustomerById(id));
    }

    /**
     * 新增线索
     * @param crmCustomer
     * @return
     */
    @PreAuthorize("@ss.hasPermi('crm:clues:add')")
    @Log(title = "线索", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody CrmCustomer crmCustomer) {
        crmCustomer.setStatus(CustomerFolder.CLUES.getCode());
        crmCustomer.setCreateBy(getUsername());
        crmCustomer.setOwner(getUsername());
        return toAjax(cluesService.insertCrmCustomer(crmCustomer));
    }

    /**
     * 修改线索
     * @param crmCustomer
     * @return
     */
    @PreAuthorize("@ss.hasPermi('crm:clues:edit')")
    @Log(title = "线索", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody CrmCustomer crmCustomer) {
        crmCustomer.setUpdateBy(getUsername());
        return toAjax(cluesService.updateCrmCustomer(crmCustomer));
    }

    /**
     * 删除我的线索
     * @param ids
     * @return
     */
    @PreAuthorize("@ss.hasPermi('crm:clues:remove')")
    @Log(title = "线索", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(cluesService.deleteCrmCustomerByIds(ids));
    }

    /**
     * 删除我的线索
     * @param ids
     * @return
     */
    @PreAuthorize("@ss.hasPermi('crm:clues:person:remove')")
    @Log(title = "线索", businessType = BusinessType.DELETE)
    @DeleteMapping("/person/{ids}")
    public AjaxResult personRemove(@PathVariable Long[] ids) {
        return toAjax(cluesService.deleteCrmCustomerByIds(ids));
    }

    /**
     * 转移线索
     * @param ids
     * @param newOwner
     * @return
     */
    @PreAuthorize("@ss.hasPermi('crm:clues:transfer')")
    @Log(title = "线索", businessType = BusinessType.UPDATE)
    @PostMapping("/transfer")
    public AjaxResult transfer(@RequestParam("ids") Long[] ids, @RequestParam("owner") String newOwner) {
        return toAjax(cluesService.transferCrmCustomerByIds(ids, newOwner, getUsername()));
    }

    /**
     * 转化成客
     * @param customerDTO
     * @return
     */
    @PreAuthorize("@ss.hasPermi('crm:clues:tocustomer')")
    @Log(title = "线索", businessType = BusinessType.UPDATE)
    @PutMapping("/to-customer")
    public AjaxResult toCustomer(@RequestBody ToCustomerDTO customerDTO) {
        CrmCustomer crmCustomer = cluesService.selectCrmCustomerById(customerDTO.getId());
        crmCustomer.setName(customerDTO.getName());
        crmCustomer.setCustomerRank(customerDTO.getCustomerRank());
        crmCustomer.setCustomerStatus(customerDTO.getCustomerStatus());
        crmCustomer.setStatus(CustomerFolder.CUSTOMER.getCode());
        crmCustomer.setToCustomerTime(DateUtils.getNowDate());
        return toAjax(cluesService.updateCrmCustomer(crmCustomer));
    }
}