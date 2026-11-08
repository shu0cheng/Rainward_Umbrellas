# 雨客雨具 · Rainward Umbrellas

Minecraft **1.21.1** 的雨伞 mod，同时支持 **Fabric** 和 **NeoForge**。

## 功能

- 伸缩伞、直杆伞、油纸伞，各自拥有模型、贴图、开合动画和合成配方。
- 右键开伞 / 收伞；物品数据组件保存状态并同步给其他玩家。
- 潜行右键摆放雨伞，支持撑开、收拢、倒置和倚靠等装饰姿态。
- 持伞手臂姿势与开伞辅助动作。
- 露天下雨时播放雨打伞面声音，支持附近玩家距离衰减和淡入淡出。
- O 打开手持模型设置，F9 打开摆放模型调试界面，可在按键设置中重新绑定。

## 开发环境

- JDK **21**：通过 `JAVA_HOME` 或 IDE 的 Gradle JVM 选择。
- Gradle **9.4.1**：使用仓库的 Wrapper，无需另行安装 Gradle。
- 依赖版本集中在根目录 `gradle.properties`。
- 首次构建需要联网下载 Minecraft、加载器和 GeckoLib 开发依赖。
- 不要在项目中提交个人 JDK 路径；必要时放在用户的 `~/.gradle/gradle.properties`。
- GeckoLib 使用官方 Maven 依赖，`libs/` 中保留的旧 JAR 不参与构建。

在 IntelliJ IDEA 中将根目录作为 Gradle 项目导入，Gradle JVM 选择 Java 21。
两个平台的运行目录分别是 `fabric/run` 和 `neoforge/run`。
Fabric 开发运行会自动加载 Mod Menu；它仅用于开发，不随发布包分发，也不要求玩家安装。

## 构建与运行

Windows PowerShell：

```powershell
# 一次构建两个加载器
.\gradlew.bat build

# 单独构建
.\gradlew.bat :fabric:build
.\gradlew.bat :neoforge:build

# 开发客户端
.\gradlew.bat :fabric:runClient
.\gradlew.bat :neoforge:runClient

# 开发专用服务器
.\gradlew.bat :fabric:runServer
.\gradlew.bat :neoforge:runServer
```

Linux / macOS 将 `.\gradlew.bat` 替换为 `bash ./gradlew`。
如果找不到 Java 21，请先设置 `JAVA_HOME` 为实际的 JDK 21 安装目录。

根目录 `build/libs/` 只收集两份发布包：

- `umbrella-fabric-1.0.0+mc1.21.1.jar`
- `umbrella-neoforge-1.0.0+mc1.21.1.jar`

平台目录的 `build/libs/` 还包含源码包和开发中间包；不要把 `-dev`、
`-dev-shadow` 或 `-sources` JAR 当作发布包安装。
GitHub Actions 会构建两个平台并上传根目录的发布包。
构建还会检查共享代码未导入加载器 API、两份发布包包含共享资源且只包含对应平台实现，
并检查依赖没有被意外打入发布包。

## 安装依赖

| 平台 | 必装依赖 |
| --- | --- |
| Fabric | Fabric Loader 0.19.3+、Fabric API 0.116.12+1.21.1 或更新的 1.21.1 版本、GeckoLib 4.9.3+（4.x） |
| NeoForge | NeoForge 21.1.264+（21.1.x）、GeckoLib 4.9.3+（4.x） |

选择与你的加载器匹配的雨伞和 GeckoLib JAR；两个雨伞发布包不要同时安装。
开发运行会自动带上依赖，发布包只包含本 mod 的共享代码和对应平台适配代码，
不内嵌 Fabric API、GeckoLib 或 Architectury API。项目不要求安装 Architectury API。

## 架构与协作

沿用 BlocksideSound 的三模块开发方式：

| 路径 | 职责 |
| --- | --- |
| `common/src/main/java` | 物品、摆放方块、状态组件、动画、渲染、雨声、设置界面和消息逻辑 |
| `common/src/main/resources` | 两个平台共享的模型、贴图、音效、配方、翻译和 Mixin |
| `fabric/src/main` | Fabric 入口、注册、网络、客户端事件和元数据 |
| `neoforge/src/main` | NeoForge 入口、延迟注册、网络、客户端事件和元数据 |
| `gradle/scripts` | Java 编译约定与平台打包约定 |
| `art-source` | Blockbench 源工程和美术预览 |

共享代码只依赖 Minecraft、GeckoLib 和本项目的 `platform/UmbrellaPlatform` 接口，
不直接调用加载器 API。每个平台通过 `META-INF/services` 提供一个实现；
NeoForge 使用延迟注册，Fabric 使用直接注册。

新增伞型时优先修改共享枚举、注册内容和资源。涉及生命周期、网络传输或加载器 API
时，在平台适配中实现，并在两个加载器上验证。
现有 `umbrella:*` 注册 ID、物品数据组件名和配置文件名保持不变。

GeckoLib 动画在 `assets/umbrella/animations/item/`，模型在 `geo/item/` 和
`geo/block/`。修改 Blockbench 源文件后，需要同步导出游戏使用的模型、动画和贴图。

## 手动验证

两个平台分别检查：

- 三种伞的合成、开合动画、主副手持握、第三人称姿势和第一人称显示。
- 潜行摆放、倒置 / 倚靠、破坏掉落、保存退出后重进。
- 雨声、O 设置、F9 调参、配置保存。
- 专用服务器能加载；两个同加载器客户端观察彼此开合和模型变换。

`build` 通过说明编译和打包成功，不代表上述游戏内场景已经验证。

## 许可

All Rights Reserved。作者：shu0cheng。美术与音效沿用原项目。
