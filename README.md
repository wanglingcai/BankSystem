# 🏦 银行管理系统 (Bank Management System)

基于 Java Swing 和 SQL Server 开发的桌面端银行交易管理系统。本项目采用了经典的**三层架构 (UI 展示层、Service 业务逻辑层、DAO 数据访问层)**，代码结构清晰，职责划分明确，具备完整的事务管理与防 SQL 注入机制。

## 🛠️ 技术栈

* **编程语言**: Java SE (JDK 8+)
* **图形界面**: Java Swing
* **数据库**: Microsoft SQL Server
* **数据库连接**: JDBC (PreparedStatement 防注入)
* **架构模式**: MVC / 三层架构 (UI -> Service -> DAO)

## ✨ 核心功能

系统分为 **管理员 (Admin)** 和 **客户 (Customer)** 两个端：

### 👨‍💼 管理员端
* **安全登录**: 验证管理员身份。
* **交易大厅**: 实时查看全站所有交易记录（存款、取款、转账）。
* **高级查询**: 支持多条件组合查询（根据 用户ID、交易类型、交易日期 筛选）。
* **记录管理**: 支持对异常交易记录进行**新增**、**修改**和**删除**操作。

### 🙋‍♂️ 客户端
* **账户管理**: 新用户注册、账号密码登录、修改密码（校验手机号）。
* **基础业务**: 存款、取款（实时更新余额）。
* **转账业务**: 支持跨账户转账，内置**严谨的数据库事务 (Transaction) 控制**，确保转出方扣款与转入方加款同时成功或同时回滚，保障资金安全。
* **余额查询**: 实时查询当前账户余额。

## 📂 项目结构

```text
BankSystem/
├── src/
│   ├── main/       # 程序主入口 (Main.java)
│   ├── ui/         # 视图层：包含所有界面的 JFrame (登录页、管理员主页等)
│   ├── service/    # 业务层：处理业务逻辑、参数校验、数据库事务控制
│   ├── dao/        # 数据层：封装 JDBC 连接与所有的 SQL 增删改查操作
│   └── init/       # 初始化层：包含建库建表的 SQL 执行逻辑
├── images/         # 界面背景图片资源
└── lib/            # 第三方依赖库 (SQL Server JDBC 驱动)
🚀 快速启动
1. 环境准备
安装 JDK 8 或更高版本。

安装 Microsoft SQL Server。

确保已开启 SQL Server 的 TCP/IP 协议 (默认端口 1433)，并启用混合身份验证模式。

2. 数据库初始化
打开项目，进入 src/init/ 目录，运行 InstallDatabase.java（或者手动在 SQL Server 中执行建库建表语句）。

这将自动创建 BankDB 数据库，以及 Users、Admins、Transactions 表和 TransactionView 视图，并插入初始测试数据。

3. 配置数据库连接
打开 src/dao/BaseDao.java 文件。

检查 URL、USER 和 PASSWORD 是否与你本地的 SQL Server 配置一致（默认账号 sa，密码 123456）。

4. 运行程序
找到 src/main/Main.java，直接运行其中的 main 方法即可启动程序。

💡 开发亮点
事务保证 (ACID)：在客户转账等核心业务中，使用 conn.setAutoCommit(false) 与 conn.rollback() 实现手动事务管理，防止出现“钱扣了但没到账”的严重 Bug。

安全性：全站数据库交互均采用 PreparedStatement，有效防御 SQL 注入攻击。

健壮性：完善了 UI 层的空值校验、格式转换异常捕获以及业务层的逻辑校验（如不能向自己转账、余额不足提示等）。
