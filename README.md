# 雨伞 Mod（Umbrella）— NeoForge 1.21.1

三种雨伞的轻量模组：**伸缩伞、直杆伞、油纸伞**。物品栏里始终是收拢状态，
右键撑开 / 收起，按 `V` 在主手与副手之间切换；持伞手臂姿势自然，走动时伞面保持稳定。

## 功能

- **三种雨伞**
  - 伸缩伞：轻便，收拢时较短，塑料握把，撑开时伞杆伸长。
  - 直杆伞：经典长杆，木质 J 形伞柄。
  - 油纸伞：竹制伞杆、暖色伞面与梅花纹样，带红色流苏。
- **收拢 / 撑开**
  - 物品栏、快捷栏里始终渲染为收拢的伞。
  - 右键切换，带 8 tick 的平滑动画（伞面、伞骨、滑套、伞杆一起变化）。
- **主手 / 副手切换**
  - 默认 `V`，可在“控制 → 按键绑定 → 雨伞”中修改。
  - 直接交换两只手的物品，开合状态跟着伞走。
- **姿势与稳定**
  - 撑伞手臂自然下垂、略微外展；`HumanoidModelMixin` 在 `setupAnim` 末尾覆盖手臂旋转，
    因此走路时持伞手臂不会摆动，伞面稳定。
  - 第一人称的伞绘制在固定视角空间，走路上下晃动时不跟着抖。
- **合成**
  - 伸缩伞：木棍 + 铁锭 + 2 个蓝色羊毛。
  - 直杆伞：木棍 + 铁锭 + 2 个黑色羊毛。
  - 油纸伞：木棍 + 3 张纸 + 1 根线。

## 构建（完全离线）

项目根目录：`F:\mcmod\MDK-1.21.1-ModDevGradle-main`

```bat
gradlew.bat build
```

产物：`build\libs\umbrella-1.0.0.jar`

也可以直接用 IDEA 打开该目录，等待 Gradle 同步完成后执行 `build`。

### 说明

原本的 ModDevGradle 构建需要 `net.neoforged:neoform-runtime`（NFRT），该工件在
BMCLAPI / 阿里云等国内镜像上**完全缺失**，而 `maven.neoforged.net` 在本机网络下无法访问，
所以这里改成了**离线 classpath**方案，构建不再依赖任何 Minecraft 插件：

- `libs/` 内是完整的编译期 classpath：反混淆后的 Minecraft 1.21.1 客户端
  （用 Mojang 官方映射 + AutoRenamingTool 从官方 client.jar 生成）、
  NeoForge 21.1.264 universal、FancyModLoader 及其依赖、原版库。
- `build.gradle` 只有 `java` 插件，`compileOnly fileTree('libs')`，因此断网也能 `build`。
- `build.gradle` 通过 `generateModMetadata` 把 `src/main/templates/META-INF/neoforge.mods.toml`
  展开后打进 jar。
- `gradle.properties` 固定了 JDK 21（本机 Microsoft OpenJDK 21）与 Gradle JVM。

### 版本

- Minecraft 1.21.1
- NeoForge 21.1.264（`neoforge.mods.toml` 的依赖范围写为 `[21.1,)`，21.1.x 均可）
- Java 21
- Gradle Wrapper：8.12.1（腾讯云镜像）

### 国内镜像

当前构建不需要联网，以下仅用于以后更新 `libs/`：

- Gradle 发行版：`https://mirrors.cloud.tencent.com/gradle/`
- Maven Central：`https://maven.aliyun.com/repository/public`
- NeoForge / Mojang 工件：`https://bmclapi2.bangbang93.com/maven/`
- Mojang 库：`https://bmclapi2.bangbang93.com/libraries/`

如果以后网络可以直连 `maven.neoforged.net`，也可以换回标准的 ModDevGradle 配置。

## 代码结构

| 路径 | 作用 |
| --- | --- |
| `UmbrellaMod` | `@Mod` 入口，注册物品 / 数据组件 / 创造栏 / 网络 |
| `item/UmbrellaType` | 三种伞的尺寸、颜色、伞柄样式、贴图 |
| `item/UmbrellaItem` | 右键开合，状态写入 `umbrella:umbrella_state` 数据组件 |
| `item/UmbrellaState` | `open` + `changed_at`（供客户端动画插值） |
| `client/UmbrellaAnimation` | 客户端按游戏时间计算 0..1 的展开进度 |
| `client/UmbrellaMesh` | 纯代码生成伞面 / 伞骨 / 伞杆 / 伞柄几何体 |
| `client/UmbrellaRenderer` | `BlockEntityWithoutLevelRenderer`：物品栏、手持、第一人称 |
| `mixin/HumanoidModelMixin` | 持伞手臂姿势 + 取消走路摆臂 |
| `network/SwitchHandPayload` | 客户端按 `V` → 服务端交换主副手 |

## 说明 / 可调参数

- 伞面半径、下垂量、伞杆高度在 `UmbrellaType` 的枚举参数里。
- 展开速度在 `UmbrellaAnimation.OPEN_TICKS`（默认 8 tick）。
- 手臂姿势角度在 `HumanoidModelMixin.poseArm`。
- 合成表在 `src/main/resources/data/umbrella/recipe/`。

## 许可

All Rights Reserved（可按需修改）。
