package com.yoyo.admin.common.constant;

/**
 * 返回结果状态的提示信息
 */
public class Result {

    /**
     * SUCCESS
     */
    public static String SUCCESS = "成功";

    /**
     * PARAM_ERROR
     */
    public static String PARAM_ERROR = "参数不正确";

    /**
     * PARENT_IS_CHILD
     */
    public static String PARENT_IS_CHILD = "父节点不能设置成该节点的子节点或本身";

    /**
     * CHILD_NOT_DELETE
     */
    public static String CHILD_NOT_DELETE = "存在未删除的子节点，请先删除子节点";

    /**
     * MENU__NOT_FOUND
     */
    public static String MENU_NOT_FOUND = "菜单已删除或不存在";

    /**
     * MENU_NAME_EXIST
     */
    public static String MENU_NAME_EXIST = "父级下已存在同名菜单";

    /**
     * AUTH_MENU_NONE
     */
    public static String AUTH_MENU_NONE = "没有菜单权限";

    /**
     * MENU_ROLE_USE
     */
    public static String MENU_ROLE_USE = "菜单已在角色中使用，请先取消";

    /**
     * MENU_CHILD_VISIBLE
     */
    public static String MENU_CHILD_VISIBLE = "存在显示的子菜单，请先隐藏子菜单";

    /**
     * MENU_PARENT_HIDE
     */
    public static String MENU_PARENT_HIDE = "父菜单隐藏，请先取消隐藏父菜单";

    /**
     * MENU_MAIN_ROOT_ERROR
     */
    public static String MENU_MAIN_ROOT_ERROR = "目录只能为根节点";

    /**
     * MENU_CHILD_ROOT_ERROR
     */
    public static String MENU_CHILD_ROOT_ERROR = "菜单不能为根节点";

    /**
     * MENU_CHILD_BUTTON_BIND_ERROR
     */
    public static String MENU_CHILD_BIND_ERROR = "菜单不能挂载在按钮下";

    /**
     * MENU_BUTTON_BIND_ERROR
     */
    public static String MENU_BUTTON_BIND_ERROR = "按钮只能挂载在菜单下";

    /**
     * MENU_URL_EXIST
     */
    public static String MENU_URL_EXIST = "已存在相同URL菜单";

    /**
     * ROLE_NOT_FOUND
     */
    public static String ROLE_NOT_FOUND = "角色已删除或不存在";

    /**
     * ROLE_EXIST
     */
    public static String ROLE_EXIST = "角色已存在";

    /**
     * ROLE_USER_USE
     */
    public static String ROLE_USER_USE = "存在用户角色设置为该角色，无法修改或删除";

    /**
     * ROLE_IS_ADMIN
     */
    public static String ROLE_IS_ADMIN = "管理员角色无法禁用或删除";

    /**
     * ROLE_HAS_NO_COMPANY
     */
    public static String ROLE_HAS_NO_COMPANY = "用户角色未设置数据权限";

    /**
     * USER_NOT_FOUND
     */
    public static String USER_NOT_FOUND = "用户不存在或已删除";

    /**
     * USER_EXIST
     */
    public static String USER_EXIST = "用户名已存在";

    /**
     * USER_NOT_LOGIN
     */
    public static String USER_NOT_LOGIN = "登录已超时，请重新登录";

    /**
     * USER_IS_ADMIN
     */
    public static String USER_IS_ADMIN = "管理员账号无法修改";

    /**
     * ADMIN_USER_CAN_NOT_DELETE
     */
    public static String ADMIN_USER_CAN_NOT_DELETE = "超级管理员账号无法删除";

    /**
     * USER_ADMIN_ROLE
     */
    public static String USER_ADMIN_ROLE = "管理员账号无法更换非管理员角色";

    /**
     * USER_DELETE_SELF
     */
    public static String USER_DELETE_SELF = "无法删除当前登录账号";

    /**
     * LOGIN_USER_NOT_EXIST
     */
    public static String LOGIN_USER_NOT_EXIST = "用户不存在";

    /**
     * LOGIN_USER_ERROR
     */
    public static String LOGIN_INFO_ERROR = "用户名或密码不正确";

    /**
     * LOGIN_USER_DISABLED
     */
    public static String LOGIN_USER_DISABLED = "账号已被禁用";

    /**
     * LOGIN_USER_FAIL
     */
    public static String LOGIN_USER_FAIL = "用户登录信息不完整";

    /**
     * LOGIN_USER_FAIL
     */
    public static String LOGIN_CONFIRMCODE_ERROR = "验证码不正确";

    /**
     * ERROR
     */
    public static String SERVER_ERROR = "服务出现异常";

    /**
     * FILE_EMPTY
     */
    public static String FILE_EMPTY = "文件内容为空";

    /**
     * FILE_FORMAT_DANGER
     */
    public static String FILE_FORMAT_DANGER = "文件格式不允许上传";

    /**
     * FILE_NOT_EXISTS
     */
    public static String FILE_NOT_EXISTS = "文件不存在";

    /**
     * TXT_TYPE_MUST
     */
    public static String TXT_TYPE_MUST = "文件格式只能为txt";

    /**
     * SERVER_SUCCESS
     */
    public static String SERVER_SUCCESS = "成功";

    /**
     * LOGIN_FAILED
     */
    public static String LOGIN_FAILED = "登录失败，用户名或密码错误";

    /**
     * LOGIN_SUCCESS
     */
    public static String LOGIN_SUCCESS = "登录成功";

    /**
     * LOGOUT_FAILED
     */
    public static String LOGOUT_FAILED = "登出失败";

    /**
     * LOGOUT_SUCCESS
     */
    public static String LOGOUT_SUCCESS = "登出成功";

    /**
     * AUTH_FAILD
     */
    public static String AUTH_FAILD = "权限不足";

    /**
     * AUTH_DATA_ERROR
     */
    public static String AUTH_DATA_ERROR = "数据出现错误";

    /**
     * ROLE_NO_MENU
     */
    public static String ROLE_NO_MENU = "该角色未分配菜单权限";

    /**
     * ROLE_NO_DEPT
     */
    public static String ROLE_NO_DEPT = "该角色未分配部门权限";

    /**
     * CAN_NOT_DEL_SELF
     */
    public static String CAN_NOT_DEL_SELF = "不能删除自己的账号";

    /**
     * DEPT_EXIST
     */
    public static String DEPT_EXIST = "父级下已存在同名部门";

    /**
     * DEPT_IS_DEFAULT
     */
    public static String DEPT_IS_DEFAULT = "无法禁用或删除根部门";

    /**
     * INCOMPLETE_INFORMATION
     */
    public static String INCOMPLETE_INFORMATION = "信息填写不完整";

}
