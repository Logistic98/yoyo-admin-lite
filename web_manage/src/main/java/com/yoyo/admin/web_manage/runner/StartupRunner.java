package com.yoyo.admin.web_manage.runner;

import com.yoyo.admin.common.domain.*;
import com.yoyo.admin.common.domain.repository.*;
import com.yoyo.admin.common.enums.MenuTypeEnum;
import com.yoyo.admin.common.service.AreaService;
import com.yoyo.admin.common.service.DeptService;
import com.yoyo.admin.common.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;

/**
 * 初始化数据库数据
 */
@Configuration
@Order(1)
public class StartupRunner implements CommandLineRunner {

    private UserRepository userRepository;

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    private RoleRepository roleRepository;

    @Autowired
    public void setRoleRepository(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    private MenuRepository menuRepository;

    @Autowired
    public void setMenuRepository(MenuRepository menuRepository) {
        this.menuRepository = menuRepository;
    }

    private MenuService menuService;

    @Autowired
    public void setMenuService(MenuService menuService) {
        this.menuService = menuService;
    }

    private DeptRepository deptRepository;

    @Autowired
    public void setDeptRepository(DeptRepository deptRepository) {
        this.deptRepository = deptRepository;
    }

    private DeptService deptService;

    @Autowired
    public void setDeptService(DeptService deptService) {
        this.deptService = deptService;
    }

    private AreaService areaService;

    @Autowired
    public void setAreaService(AreaService areaService) {
        this.areaService = areaService;
    }

    @Override
    public void run(String... args) {
        addDefaultDept();
        addDefaultMenus();
        addDefaultRole();
        addDefaultUser();
        checkAndAddArea();
    }

    private void addDefaultDept() {
        if (deptRepository.count() == 0) {
            deptService.insertDept("主管部门", null, 1);
            deptService.insertDept("业务部门", 1L, 0);
        }
    }

    private void addDefaultRole() {
        if (roleRepository.count() == 0) {
            Role superAdmin = new Role();
            superAdmin.setName("超级管理员");
            superAdmin.setCode("superAdmin");
            List<Menu> menus = menuRepository.findAll();
            superAdmin.setMenus(menus);
            roleRepository.save(superAdmin);
        }
    }


    private void addDefaultUser() {
        if (userRepository.count() == 0) {
            User user = new User();
            user.setUsername("admin");
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            user.setPassword(encoder.encode("328mkl#Gt*dhk&u#"));
            user.setPhoneNumber("10000000000");
            user.setGender(1);
            user.setName("超级管理员");
            user.setRole(roleRepository.findFirstById(1L));
            user.setDept(deptRepository.findFirstById(1L));
            userRepository.save(user);
        }
    }

    private void addDefaultMenus() {
        if (menuRepository.count() == 0) {
            Menu systemManageMenu = menuService.insertMenu("系统管理", MenuTypeEnum.MAIN.toString(), "/web/system-management", "", 1, null, "系统管理主菜单");
            Menu userMenu = menuService.insertMenu("用户管理", MenuTypeEnum.CHILD.toString(), "/web/system-management/user", "user_list", 1, systemManageMenu, "用户管理菜单");
            menuService.insertMenu("详情", MenuTypeEnum.BUTTON.toString(), "", "user_detail", 1, userMenu, "用户详情按钮");
            menuService.insertMenu("新增", MenuTypeEnum.BUTTON.toString(), "", "user_add", 2, userMenu, "用户新增按钮");
            menuService.insertMenu("修改", MenuTypeEnum.BUTTON.toString(), "", "user_update", 3, userMenu, "用户修改按钮");
            menuService.insertMenu("删除", MenuTypeEnum.BUTTON.toString(), "", "user_delete", 4, userMenu, "用户删除按钮");
            menuService.insertMenu("重置", MenuTypeEnum.BUTTON.toString(), "", "user_reset", 5, userMenu, "用户密码重置按钮");
            Menu roleMenu = menuService.insertMenu("角色管理", MenuTypeEnum.CHILD.toString(), "/web/system-management/role", "role_list", 2, systemManageMenu, "角色管理菜单");
            menuService.insertMenu("详情", MenuTypeEnum.BUTTON.toString(), "", "role_detail", 1, roleMenu, "角色详情按钮");
            menuService.insertMenu("新增", MenuTypeEnum.BUTTON.toString(), "", "role_add", 2, roleMenu, "角色新增按钮");
            menuService.insertMenu("修改", MenuTypeEnum.BUTTON.toString(), "", "role_update", 3, roleMenu, "角色修改按钮");
            menuService.insertMenu("删除", MenuTypeEnum.BUTTON.toString(), "", "role_delete", 4, roleMenu, "角色删除按钮");
            Menu deptMenu = menuService.insertMenu("部门管理", MenuTypeEnum.CHILD.toString(), "/web/system-management/dept", "dept_list", 3, systemManageMenu, "部门管理菜单");
            menuService.insertMenu("详情", MenuTypeEnum.BUTTON.toString(), "", "dept_detail", 1, deptMenu, "部门详情按钮");
            menuService.insertMenu("新增", MenuTypeEnum.BUTTON.toString(), "", "dept_add", 2, deptMenu, "部门新增按钮");
            menuService.insertMenu("修改", MenuTypeEnum.BUTTON.toString(), "", "dept_update", 3, deptMenu, "部门修改按钮");
            menuService.insertMenu("删除", MenuTypeEnum.BUTTON.toString(), "", "dept_delete", 4, deptMenu, "部门删除按钮");
            menuService.insertMenu("启用", MenuTypeEnum.BUTTON.toString(), "", "dept_enable", 5, deptMenu, "部门启用按钮");
            menuService.insertMenu("禁用", MenuTypeEnum.BUTTON.toString(), "", "dept_disable", 6, deptMenu, "部门禁用按钮");
            Menu menuMenu =menuService.insertMenu("菜单管理", MenuTypeEnum.CHILD.toString(), "/web/system-management/menu", "menu_list", 4, systemManageMenu, "菜单管理菜单");
            menuService.insertMenu("详情", MenuTypeEnum.BUTTON.toString(), "", "menu_detail", 1, menuMenu, "菜单详情按钮");
            menuService.insertMenu("新增", MenuTypeEnum.BUTTON.toString(), "", "menu_add", 2, menuMenu, "菜单新增按钮");
            menuService.insertMenu("修改", MenuTypeEnum.BUTTON.toString(), "", "menu_update", 3, menuMenu, "菜单修改按钮");
            menuService.insertMenu("删除", MenuTypeEnum.BUTTON.toString(), "", "menu_delete", 4, menuMenu, "菜单删除按钮");
            Menu logMenu = menuService.insertMenu("日志管理", MenuTypeEnum.CHILD.toString(), "/web/system-management/operlog", "log_list", 5, systemManageMenu, "日志管理菜单");
            menuService.insertMenu("详情", MenuTypeEnum.BUTTON.toString(), "", "log_detail", 1, logMenu, "日志详情按钮");
        }
    }

    private void checkAndAddArea() {
        try {
            areaService.importAreasFromExcelFile();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


}
