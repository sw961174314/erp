package com.ec.crm.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.ec.common.utils.DateUtils;
import com.ec.crm.form.CommentDTO;
import org.springframework.beans.BeanUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ec.common.annotation.Log;
import com.ec.common.core.controller.BaseController;
import com.ec.common.core.domain.AjaxResult;
import com.ec.common.enums.BusinessType;
import com.ec.crm.domain.CrmComment;
import com.ec.crm.service.ICrmCommentService;
import com.ec.common.utils.poi.ExcelUtil;
import com.ec.common.core.page.TableDataInfo;

/**
 * 跟进记录Controller
 */
@RestController
@RequestMapping("/crm/comment")
public class CrmCommentController extends BaseController {
    @Autowired
    private ICrmCommentService crmCommentService;

    /**
     * 查询跟进记录列表
     * @param crmComment
     * @return
     */
    @PreAuthorize("@ss.hasPermi('crm:comment:list')")
    @GetMapping("/list")
    public TableDataInfo list(CrmComment crmComment) {
        startPage();
        List<CrmComment> list = crmCommentService.selectCrmCommentList(crmComment);
        return getDataTable(list);
    }

    /**
     * 查询某个客户的跟进记录
     * @param customerId 客户id
     * @return
     */
    @PreAuthorize("@ss.hasPermi('crm:comment:list')")
    @GetMapping("/customer/{customerId}")
    public AjaxResult listByCustomer(@PathVariable("customerId") Long customerId) {
        CrmComment crmComment = new CrmComment();
        crmComment.setCustomerId(customerId);
        List<CrmComment> list = crmCommentService.selectCrmCommentList(crmComment);
        return AjaxResult.success(list);
    }

    /**
     * 导出跟进记录列表
     * @param response
     * @param crmComment
     */
    @PreAuthorize("@ss.hasPermi('crm:comment:export')")
    @Log(title = "跟进记录", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, CrmComment crmComment) {
        List<CrmComment> list = crmCommentService.selectCrmCommentList(crmComment);
        ExcelUtil<CrmComment> util = new ExcelUtil<CrmComment>(CrmComment.class);
        util.exportExcel(response, list, "跟进记录数据");
    }

    /**
     * 获取跟进记录详细信息
     * @param id
     * @return
     */
    @PreAuthorize("@ss.hasPermi('crm:comment:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return AjaxResult.success(crmCommentService.selectCrmCommentById(id));
    }

    /**
     * 新增跟进记录
     * @param commentDTO
     * @return
     */
    @PreAuthorize("@ss.hasPermi('crm:comment:add')")
    @Log(title = "跟进记录", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody CommentDTO commentDTO) {

        CrmComment crmComment = new CrmComment();
        BeanUtils.copyProperties(commentDTO, crmComment);
        if (commentDTO.getNextFollowupTime() == null) {
            commentDTO.setNextFollowupTime(DateUtils.addDays(DateUtils.getNowDate(), 1));
        }
        crmComment.setCreateBy(getUsername());
        return toAjax(crmCommentService.insertCrmComment(crmComment, commentDTO.nextFollowupTime));
    }

    /**
     * 修改跟进记录
     * @param crmComment
     * @return
     */
    @PreAuthorize("@ss.hasPermi('crm:comment:edit')")
    @Log(title = "跟进记录", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody CrmComment crmComment) {
        return toAjax(crmCommentService.updateCrmComment(crmComment));
    }

    /**
     * 删除跟进记录
     * @param ids
     * @return
     */
    @PreAuthorize("@ss.hasPermi('crm:comment:remove')")
    @Log(title = "跟进记录", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(crmCommentService.deleteCrmCommentByIds(ids));
    }
}
