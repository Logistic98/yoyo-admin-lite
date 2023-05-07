## 1. 前后端项目打包

### 1.1 前端项目打包

```
$ npm install -g @angular/cli   
$ npm install   
$ npm run build
```

### 1.2 后端项目打包

```
$ mvn clean
$ mvn install
$ mvn package
```

## 2. 准备项目部署包

### 2.1 部署包结构

```
.
├── config
    ├── application-prod.properties
    └── application.properties
├── dist.zip
├── dist_doc.zip
├── Dockerfile
├── nginx.conf
├── proxy.conf
├── yoyo_web.conf
├── yoyo_web_doc.conf
├── web_manage-0.0.1.jar
├── unzip.sh
├── build.sh
├── rebuild.sh
└── start_web.sh
```

### 2.2 修改配置文件

一般而言，需要修改的配置文件有 Dockerfile、yoyo_web.conf、yoyo_web_doc.conf、build.sh、rebuild.sh

## 3. 服务器部署

### 3.1 初次部署

```
切换到工作目录
$ chmod u+x unzip.sh build.sh rebuild.sh
$ ./build.sh
```

启动成功后，项目就部署好了，Chrome访问 `IP:8082`地址即可访问前端页面，8081端口是留给后端的，8083端口是留给前端文档的。

### 3.2 后续更新

```
切换到工作目录
把 dist.zip、dist_doc.zip和 web_manage-0.0.1.jar 更换掉，然后执行 rebuild.sh 脚本即可
```


