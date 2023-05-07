# 介绍
<a href="https://angular.cn/start" target="_blank">![alt](https://img.shields.io/badge/angular-13.0.4-dd0031)</a>
<a href="https://ng.ant.design/docs/introduce/zh" target="_blank">![alt](https://img.shields.io/badge/NG--ZORRO-13.0.1-blue)</a>
<a href="https://www.typescriptlang.org/zh/" target="_blank">![alt](https://img.shields.io/badge/typeScript-4.4.4-blue)</a>
<a href="https://cn.rx.js.org/" target="_blank">![alt](https://img.shields.io/badge/RxJs-7.4.0-EC0C8E)</a>

ng-zorro-admin 是一个后台前端解决方案，它基于 [angular](https://angular.cn/start) 和 [Ant Design of Angular](https://ng.ant.design/docs/introduce/zh) 实现。它使用了最新的前端技术栈，内置了 动态路由，权限验证，提炼了典型的业务模型，提供了丰富的功能组件，它可以帮助你快速搭建企业级中后台产品原型。

::: tip 建议
项目的定位是后台集成方案，因为项目集成了很多你可能用不到的功能，会造成不少的代码冗余。如果你的项目不需要某部分功能，可以按需导入模块或组件。

:::

## 功能

``` text
- 登录 / 注销

- 权限验证
  - 菜单权限
  - 按钮权限
  - 权限配置

- 全局功能
  - 快捷导航 (历史页签)
  - Screenfull
  - 自适应收缩侧边栏

- 编辑器
  - 富文本

- 错误页面
  - 404 TODO

- 組件
  - 头像上传
  - 返回顶部
  - 拖拽Dialog TODO
  - CountTo TODO
  - 滚动加载
  - 相册
  - 可视化JSON

- 综合实例
- 错误日志
- Dashboard TODO
- 引导页 TODO
- ECharts 图表
- Clipboard(剪贴复制) TODO


```

## 前序准备

你需要在本地安装 <a href="https://nodejs.org/en/" target="_blank">node</a> 和 <a href="https://git-scm.com/" target="_blank">git</a> 。本项目技术栈基于 <a href="https://es6.ruanyifeng.com/" target="_blank">ES2015+</a>、<a href="https://www.tslang.cn/docs/handbook/basic-types.html" target="_blank">typeScript</a>、<a href="https://angular.cn/start" target="_blank">angular-cli</a>、<a href="https://cn.rx.js.org/" target="_blank">rxjs</a> 和 <a href="https://ng.ant.design/docs/introduce/zh" target="_blank">ngzorro</a>，提前了解和学习这些知识会对使用本项目有很大的帮助。

同时计划配套系列文章，如何从零构建一个完整的管理后台项目，及相关功能的代码解释，后续会陆续完善教程系列。


## 目录结构
```bash
    |-- .browserslistrc                   # 跨浏览器兼容
    |-- .editorconfig                     # 跨编辑器规范
    |-- angular.json                      # Angular 项目配置文件
    |-- package.json                      # package.json
    |-- proxy.conf.json                   # proxy配置文件
    |-- tsconfig.json                     # typescript 配置文件
    |-- src
        |-- index.html                    # 单页应用的宿主HTML
        |-- main.ts                       # 入口ts文件
        |-- polyfills.ts                  # 腻子脚本
        |-- styles.less                   # 样式引导入口
        |-- @components                   # 自定义全局组件
        |   |-- components.module.ts      # 自定义组件模块
        |-- @directive                    # 公用指令
        |-- @layout                       # 通用布局
        |   |-- widgets                   # 布局专属的小组件
        |-- @service                      # 服务文件夹
        |   |-- auth.guard.ts             # 路由守卫
        |   |-- auth.service.ts           # 登录服务
        |   |-- crypto.service.ts         # AES加密算法
        |   |-- default.interceptor.ts    # 默认HTTP拦截器
        |-- @shared                       # NG-ZORRO组件库
        |-- @utils                        # js工具库    
        |-- app
        |   |-- main
        |   |   |-- main-routing.module.ts # 业务路由注册口
        |   |   |-- main.module.ts         # 业务路由模块
        |   |   |-- pages                  # 业务目录
        |   |-- passport                  # 登录模块
        |-- assets                        # 本地静态资源
        |-- environments                  # 环境变量配置
        |   |-- environment.prod.ts
        |   |-- environment.ts
        |-- styles
```
## 安装

```bash
 # 克隆项目
git clone https://github.com/****.git

# 进入项目目录
cd ng-zorro-admin

# 安装依赖
npm install

# 可以通过如下操作解决 npm 下载速度慢的问题
npm install --registry=https://registry.npm.taobao.org

# 本地开发 启动项目
npm start
```

启动完成后会自动打开浏览器访问 <a href="http://localhost:7300" target="_blank">http://localhost:7300</a>， 你看到下面的页面就代表操作成功了。

接下来你可以修改代码进行业务开发了，本项目内建了典型业务模板、常用业务组件、HMR预览、状态管理TODO、国际化TODO、等等各种实用的功能来辅助开发。


## Browsers Support
Angular 支持大多数常用浏览器，包括下列[版本](https://v13.angular.io/guide/browser-support)：
| 浏览器        | 支持的版本           |
| ------------- |:-------------:|
| Chrome        | 最新 |
| Firefox       | 最新版以及扩展支持版本 (ESR) |
| Edge          | 最近的两个主版本 |
| Safari        | 最近的两个主版本 |

使用 ng new 创建项目时，会自动为你安装一些强制性腻子脚本（比如 zone.js），并且它对应的 import 语句已在 src/polyfills.ts 配置文件中启用。所以腻子脚本已经帮我们兼容绝大部分浏览器了。

## angular 生态圈
TODO




<style lang="stylus">
  a img{
    margin-right:8px;
  }
  .custom-block.tip{border-color: #1976d2}
</style>