# SakuraPurchase

## 简介

> 这不是一个支付平台, 而是由你自己来运营支付服务端, 你需要有**营业资格证书**并到微信\支付宝申请接口。
>
> 通过本插件可以让玩家在游戏内扫描二维码支付并直接入账你的微信/支付宝, 没有中间商赚差价!


本项目分为2个部分

* SakuraPurchaseServer --独立服务端
* SakuraPurchasePlugin --bukkit端插件

## 功能

* 支持跨服
* bukkit全版本支持
* 二维码支付
* 支付完运行命令(得以兼容所有经济类插件)
* 支持PlaceHolderAPI(命令)
* 兼容Authme
* 网页后台管理系统
* 退款功能

## 截图

![QQ截图20221015235508](https://user-images.githubusercontent.com/65019366/195996031-dde2470e-36b8-4203-b242-8ff4e704640c.png)
![QQ截图20221015235540](https://user-images.githubusercontent.com/65019366/195996039-d983ec74-dc17-4dab-b22f-52f8cda4b2b0.png)
![image](https://user-images.githubusercontent.com/65019366/196718781-aeb28b5a-d5b4-4a8e-9da4-3f4c171f5dea.png)
![image](https://user-images.githubusercontent.com/65019366/196718817-78d04d26-62f0-435f-8920-b5a6bef6bb72.png)

## 安装&配置

### SakuraPurchaseServer

这是支付服务端，负责向微信、支付宝发起支付请求，处理数据

前往 [Release](https://github.com/Iseason2000/SakuraPurchase/releases)

下载 SakuraPurchaseServer-x.x.x-xx.jar

`-full` 后缀的是全包;
`-thin` 的是瘦身包，启动时会下载依赖

选择一个即可

如果要使用前端界面则下载 `web.zip` 文件放到同目录下

并放到服务器的任意文件夹中,创建并配置好文件 `application.yml`

~~~ yaml
server:
  # 端口号
  port: 80
spring:
  thymeleaf:
    cache: true
    prefix: file:templates/ #如果使用前端管理界面则位前端资源的路径
  profiles:
    active: dev
  security:
    user:
      #配置 账户给bukkit端使用
      name: test   # 用户名
      password: 123456  # 密码
      roles: # 角色
        - API
        - ADMIN
  # 数据库设置
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/sakura_purchase?useSSL=false&allowPublicKeyRetrieval=true
    username: username
    password: password
  # 初始化数据库，如果你不清楚这是什么请不要动
  sql:
    init:
      mode: always
      schema-locations: classpath:db/schema.sql
  mvc:
    static-path-pattern: /static/**

# 支付宝支付，自行注册申请 https://certifyweb.alipay.com/certify/reg/guide#/
alipay:
  # 应用ID,您的APPID，收款账号既是您的APPID对应支付宝账号，在沙箱应用中获取
  appId:
  # 商户私钥，您的PKCS8格式RSA2私钥，通过开发助手生成的应用私钥
  privateKey:
  # 支付宝公钥,在沙箱应用获取，通过应用公钥生成支付宝公钥
  aliPayPublicKey:
  # 服务器异步通知页面路径需http://格式的完整路径
  notifyUrl: http://localhost:80/api/pay/notify
  # 签名方式
  sandbox: false

# 微信 支付设置 APIv2,自行去申请 https://pay.weixin.qq.com/index.php/apply/applyment_home/guide_normal
wechat:
  appId:
  mchId:
  mchKey:
  # 证书pkcs12格式 apiclient_cert.p12
  keyPath: 'C:\Users\Iseason2000\Desktop\apiclient_cert.p12'
  # 服务器异步通知页面路径需http://格式的完整路径
  notifyUrl: http://localhost:80/api/pay/notify

#日志管理
logging:
  file:
    path: ./logs
  level:
    root: info
    okhttp3: warn
    com.lly835.bestpay: warn
  logback:
    rollingpolicy:
      max-history: 7
      max-file-size: 10MB
      clean-history-on-start: true

~~~

服务端API 请运行服务端然后输入

服务端地址/doc.html

如

http://localhost:80/doc.html

创建启动脚本
Windows start.bat

~~~ text
java -Dthin.root=. -jar SakuraPurchaseServer-1.x.x.jar 
pause
~~~

Linux start.sh

~~~ text
java -Dthin.root=. -jar SakuraPurchaseServer-1.x.x.jar 
pause
~~~

其中 `-Dthin.root` 参数表示依赖存放位置，设置为`.`表示当前路径下
其他参数[见此](https://github.com/spring-projects-experimental/spring-boot-thin-launcher#command-line-options)

最后运行 start.sh / start.bat 即可，第一次启动需要下载依赖

---

## SakuraPurchasePlugin

前往 [Release](https://github.com/Iseason2000/SakuraPurchase/releases)

下载 SakuraPurchasePlugin-x.x.x.jar 版本与Server对应
丢入 bukkit 服务端的 plugins 文件夹中,启动服务端

等待 `plugins\SakuraPurchasePlugin` 配置文件夹出现

修改 `config.yml`

~~~ yaml
# 支付服务端地址
serverHost: http://localhost

# 支付服务端用户名
username: test

# 支付服务端密码
password: '123456'

# 最大支付超时时间,单位秒
maxTimeout: 60.0

# 订单支付状态查询频率,单位tick
queryPeriod: 100

# 发起订单的最小间隔(秒)，设置合适的值以避免刷单
coolDown: 30.0

# 取消支付的关键词
cancelWorld:
- cancel
- 取消

# sakurapurchase pay 完成之后运行的命令(分组),以控制台的身份
# 原生变量为%player%:玩家名, %amount%:充值的金额%, %10_amount%:表示充值的金额X10
command-group:
  default:
  - cmi money give %player% %10_amount%

# 二维码颜色 R,G,B
qrColorStr: 0,0,0

# 支付时的取消动作,默认 SHIFT_F
# SHIFT_F: 蹲下+F 取消
# HEAD_UP: 抬头取消
cancelAction: HEAD_UP

~~~

最后服务端输入 `sakurapurchase reConnect` 连接到服务器

**命令**

~~~ text
sakurapurchase 可用别名为 sp, sap, spurchase, purchase

sakurapurchase run <player> <group> [amount]  为玩家运行命令组
sakurapurchase pay <group> <platform> <player> <amount> <name> [attach]  发起支付
sakurapurchase log [index] [player]  查询支付过的订单
sakurapurchase check <player>  手动检查玩家上一个订单是否已支付但未领取
sakurapurchase reConnect  重新链接支付服务器
sakurapurchase debug  切换调试模式

~~~

**变量**

~~~ text
# 玩家变量
sakurapurchase_player_total 总充值金额
#以下的 [index] 如果等于 0 表示正在支付的订单，否则为最近支付的第 index 个订单
sakurapurchase_player_[index]_orderid 支付的订单ID
sakurapurchase_player_[index]_ordername 支付的订单名称
sakurapurchase_player_[index]_amount 支付的订单金额
sakurapurchase_player_[index]_paytype 支付的订单支付类型
sakurapurchase_player_[index]_attach 支付的订单附加信息
sakurapurchase_player_[index]_createtime 支付的订单创建时间
sakurapurchase_player_count 已经支付的订单数

# 服务端变量
sakurapurchase_total_amount 服务器总氪金金额
~~~
