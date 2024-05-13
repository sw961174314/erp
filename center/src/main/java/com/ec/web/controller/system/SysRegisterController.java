package com.ec.web.controller.system;

import com.ec.auth.web.service.SysLoginService;
import com.ec.auth.web.service.SysPermissionService;
import com.ec.auth.web.service.TenantRegisterService;
import com.ec.common.constant.Constants;
import com.ec.common.constant.TenantConstants;
import com.ec.common.core.controller.BaseController;
import com.ec.common.core.domain.AjaxResult;
import com.ec.common.core.domain.entity.SysMenu;
import com.ec.common.core.domain.entity.SysUser;
import com.ec.common.core.domain.model.LoginBody;
import com.ec.common.utils.SecurityUtils;
import com.ec.common.utils.StringUtils;
import com.ec.saas.dto.TenantDatabaseDTO;
import com.ec.saas.form.TenantRegisterBody;
import com.ec.saas.service.IMasterTenantService;
import com.ec.sys.service.ISysConfigService;
import com.ec.sys.service.ISysMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

/**
 * 租户注册
 */
@RestController
public class SysRegisterController extends BaseController {
    @Autowired
    private SysRegisterController registerController;

    @Autowired
    private ISysConfigService configService;

    @Autowired
    private TenantRegisterService tenantRegisterService;

    @Autowired
    private IMasterTenantService masterTenantService;

    @Autowired
    private SysLoginService loginService;

    /**
     * 租户注册
     * @param tenantRegisterBody
     * @return
     */
    @PostMapping("/register")
    public AjaxResult registerTenant(@RequestBody TenantRegisterBody tenantRegisterBody) {
        loginService.validateCaptcha(tenantRegisterBody.getTenantName(), tenantRegisterBody.getCode(), tenantRegisterBody.getUuid());

        if (TenantConstants.NOT_UNIQUE.equals(masterTenantService.checkTenantNameUnique(tenantRegisterBody.tenantName))) {
            return AjaxResult.error("注册'" + tenantRegisterBody.getTenantName() + "'失败,账号已存在");
        }
        TenantDatabaseDTO tenantDatabase = null;
        try {
            // 初始化数据库
            tenantDatabase = tenantRegisterService.initDatabase(tenantRegisterBody);
        } catch (SQLException ex) {
            ex.printStackTrace();
            return AjaxResult.error("注册'" + tenantRegisterBody.getTenantName() + "'失败,创建租户时发生错误");
        } catch (Exception ex) {
            ex.printStackTrace();
            return AjaxResult.error("注册'" + tenantRegisterBody.getTenantName() + "'失败,请与我们联系");
        }
        // 创建租户
        int i = masterTenantService.insertMasterTenant(tenantDatabase);
        return toAjax(i);
    }
}
