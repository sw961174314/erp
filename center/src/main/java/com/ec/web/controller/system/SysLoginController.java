package com.ec.web.controller.system;

import com.ec.auth.web.service.SysLoginService;
import com.ec.auth.web.service.SysPermissionService;
import com.ec.common.constant.Constants;
import com.ec.common.core.domain.AjaxResult;
import com.ec.common.core.domain.entity.SysMenu;
import com.ec.common.core.domain.entity.SysUser;
import com.ec.common.core.domain.model.LoginBody;
import com.ec.common.utils.SecurityUtils;
import com.ec.common.utils.StringUtils;
import com.ec.sys.service.ISysMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Set;

/**
 * 租户登录
 */
@RestController
public class SysLoginController {
    @Autowired
    private SysLoginService loginService;

    @Autowired
    private ISysMenuService menuService;

    @Autowired
    private SysPermissionService permissionService;

    /**
     * 租户登录
     * @param request
     * @param loginBody
     * @return
     */
    @PostMapping("/login")
    public AjaxResult login(HttpServletRequest request, @RequestBody LoginBody loginBody) {
        // 租户信息
        String tenant = request.getHeader("tenant");
        if (StringUtils.isEmpty(tenant)) {
            return AjaxResult.error("租户ID不能为空");
        }
        AjaxResult ajax = AjaxResult.success();
        // 生成令牌
        String token = loginService.login(tenant, loginBody.getUsername(), loginBody.getPassword(), loginBody.getCode(), loginBody.getUuid());
        ajax.put(Constants.TOKEN, token);
        return ajax;
    }

    /**
     * 获取用户信息
     * @return
     */
    @GetMapping("getInfo")
    public AjaxResult getInfo() {
        // 用户信息
        SysUser user = SecurityUtils.getLoginUser().getUser();
        // 获取角色列表
        Set<String> roles = permissionService.getRolePermission(user);
        // 获取权限列表
        Set<String> permissions = permissionService.getMenuPermission(user);

        AjaxResult ajax = AjaxResult.success();
        ajax.put("user", user);
        ajax.put("roles", roles);
        ajax.put("permissions", permissions);
        return ajax;
    }

    /**
     * 获取路由信息
     * @return
     */
    @GetMapping("getRouters")
    public AjaxResult getRouters() {
        Long userId = SecurityUtils.getUserId();
        List<SysMenu> menus = menuService.selectMenuTreeByUserId(userId);
        return AjaxResult.success(menuService.buildMenus(menus));
    }
}
