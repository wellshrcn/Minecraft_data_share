# Data Share Mod 中文说明

Data Share Mod 是一个 **仅客户端安装** 的 Forge Mod。它采集 Minecraft 客户端本地可见的玩家、背包、容器与状态数据，生成带缩进换行的 JSON，并通过 Windows 命名管道发送给外部软件。

服务器端不需要安装本 Mod。Mod 只读取客户端已经拥有的数据，不能读取服务器未同步给客户端的隐藏数据。

## 支持版本

- Minecraft `1.20.1` + Forge `47.4.22`
  - 工程：`mod\mc\dev-1.20.1`
  - 安装位置：`mod\mc\.minecraft\versions\1.20.1-Forge_47.4.22\mods\data_share-1.0.0.jar`
- Minecraft `1.21.1` + Forge `52.1.16`
  - 工程：`mod\mc\dev-1.21.1`
  - 安装位置：`mod\mc\.minecraft\versions\1.21.1-Forge_52.1.16\mods\data_share-1.0.0.jar`
- Minecraft `1.21.4` + Forge `54.1.17`
  - 工程：`mod\mc\dev-1.21.4`
  - 安装位置：`mod\mc\.minecraft\versions\1.21.4-Forge_54.1.17\mods\data_share-1.0.0.jar`
- Minecraft `1.21.11` + Forge `61.1.11`
  - 工程：`mod\mc\dev-1.21.11`
  - 安装位置：`mod\mc\.minecraft\versions\1.21.11-Forge_61.1.11\mods\data_share-1.0.0.jar`

四个版本的 jar 不能互换使用。

## 管道协议

- 管道名：`\\.\pipe\data_share`
- 角色：Mod 创建管道并写入，外部软件只连接读取
- 编码：UTF-8
- 格式：Pretty JSON，一个快照是一个多行 JSON 对象
- 分隔：两个快照之间有一个空行
- 频率：约 10Hz，也就是每 2 个游戏 tick 输出一次
- 布尔约定：所有“是/否”字段都用数字表示，`1` 表示是/true，`0` 表示否/false

外部软件读取时，建议一直读取管道字节流，按空行判断一个 JSON 快照结束。

## 快速查看

先启动游戏并进入世界，再运行：

```powershell
powershell -ExecutionPolicy Bypass -File "f:\_a\WELLS_HRToytoolbox\mod\mc\tools\read_data_share_pipe.ps1"
```

## 顶层字段

- `schema`：JSON 数据结构版本。当前为 `1`，以后字段结构变化时会递增。
- `ts`：生成快照的本机时间戳，单位毫秒。
- `pipe`：管道名称标识，当前为 `data_share`。
- `mcVersion`：1.21.x 版本包含，表示该快照来自哪个 Minecraft 构建。
- `game`：客户端窗口、账号、服务器与界面状态。
- `player`：玩家数据。未进入世界时为 `null`。

## `game` 字段

- `fps`：当前客户端 FPS。
- `paused`：游戏是否处于暂停状态。
- `inGame`：是否已经进入世界或服务器。
- `windowWidth` / `windowHeight`：游戏窗口像素宽高。
- `guiScale`：当前 GUI 缩放倍率。
- `accountName`：当前登录用户名。
- `accountUuid`：账号 UUID，仅 1.20.1 版本包含。
- `serverAddress`：当前服务器地址；单人世界显示 `singleplayer`。
- `serverName`：当前服务器名称；单人世界显示 `singleplayer`。
- `screen`：当前打开的 Minecraft 界面类名；没有打开界面时为 `null`。

## `player` 基础字段

- `name`：玩家显示名称。
- `uuid`：玩家 UUID。
- `id`：客户端内实体 ID。
- `pos.x/y/z`：玩家精确坐标，含小数。
- `blockPos.x/y/z`：玩家所在方块坐标，整数。
- `dimension`：所在维度，例如主世界、下界、末地。
- `biome`：玩家当前位置的生物群系。
- `dayTime`：当前世界日内时间。
- `gameTime`：当前世界累计 tick 时间。
- `difficulty`：当前世界难度。
- `raining`：是否下雨。
- `thundering`：是否雷暴。

## 视角与运动字段

- `yaw`：水平视角角度。
- `pitch`：垂直视角角度。
- `bodyYaw`：身体朝向。
- `headYaw`：头部朝向。
- `velocity.x/y/z`：玩家当前速度向量。

## 生命、生存与经验字段

- `health`：当前血量。
- `maxHealth`：最大血量。
- `absorption`：额外吸收护盾血量。
- `armor`：护甲值。
- `food`：饱食度，通常 0 到 20。
- `saturation`：饱和度。
- `exhaustion`：消耗度，仅 1.20.1 版本包含。
- `air`：当前氧气值。
- `maxAir`：最大氧气值。
- `xpLevel`：经验等级。
- `xpProgress`：当前等级进度，0 到 1。
- `xpTotal`：总经验值。
- `score`：玩家分数。
- `gamemode`：游戏模式名称，例如 survival、creative。
- `gamemodeId`：游戏模式数字 ID。

## 状态 0/1 字段

- `isAlive`：玩家是否存活。
- `isDeadOrDying`：玩家是否死亡或濒死。
- `isCrouching`：是否处于潜行动画。
- `isShiftKeyDown`：是否按下潜行键。
- `isSprinting`：是否疾跑。
- `isSwimming`：是否游泳。
- `isUnderWater`：是否在水下。
- `isInWater`：是否在水中。
- `isInLava`：是否在岩浆中。
- `isOnGround`：是否在地面上。
- `isOnFire`：是否着火。
- `isFallFlying`：是否使用鞘翅飞行。
- `isSleeping`：是否睡觉。
- `isPassenger`：是否骑乘实体。
- `isUsingItem`：是否正在使用物品。
- `isBlocking`：是否处于格挡状态。
- `fallDistance`：当前累计掉落距离。
- `hurtTime`：受伤后的无敌/闪烁计时。
- `deathTime`：死亡计时。

## 能力字段

- `flying`：是否正在飞行。
- `mayFly`：是否允许飞行。
- `instabuild`：是否拥有创造模式瞬间建造能力。
- `invulnerable`：是否无敌。
- `mayBuild`：是否允许建造。
- `flyingSpeed`：飞行速度。
- `walkingSpeed`：行走速度。

## 物品字段

这些字段会出现在 `mainHand`、`offHand`、`armorSlots`、`inventory`、`openContainer.slots` 等位置。

- `empty`：该槽位是否为空。
- `id`：物品 ID，例如 `minecraft:diamond_sword`。
- `count`：堆叠数量。
- `damage`：当前耐久损耗。
- `maxDamage`：最大耐久。
- `damageable`：物品是否有耐久。
- `enchanted`：物品是否带附魔光效。
- `displayName`：物品显示名称。
- `enchantments`：附魔列表，仅 1.20.1 版本包含。
- `nbt`：物品 NBT 字符串，仅 1.20.1 版本包含。
- `components`：1.21.x 的物品组件字符串，用于替代新版物品 NBT/组件数据。

## 背包与装备字段

- `selectedSlot`：当前快捷栏选中槽位。
- `inventoryUsedSlots`：背包中已经使用的槽位数量，也就是 `inventory` 里非空槽位的数量。
- `mainHand`：主手物品。
- `offHand`：副手物品。
- `armorSlots`：装备槽，顺序为脚、腿、胸、头。
- `inventory`：玩家背包槽位列表。
- `offhandInventory`：副手背包槽列表，仅 1.20.1 版本包含。
- `armorInventory`：护甲背包槽列表，仅 1.20.1 版本包含。

## 效果字段 `effects`

- `id`：药水/状态效果 ID。
- `amplifier`：效果等级，从 0 开始。
- `duration`：剩余 tick。
- `ambient`：是否为环境效果。
- `visible`：是否显示粒子。
- `showIcon`：是否显示图标。

## 容器字段

- `openContainer`：当前打开的非玩家背包容器；没有打开箱子、熔炉等容器时为 `null`。
- `playerContainer`：玩家自身背包容器快照。

容器对象字段：

- `menuType`：容器菜单类名。
- `containerId`：容器 ID。
- `slotCount`：槽位总数。
- `slots`：槽位列表。

槽位字段：

- `index`：槽位在菜单中的序号。
- `x` / `y`：槽位在界面中的坐标。
- `containerSlot`：底层容器槽位编号。
- `mayPickup`：当前玩家是否可从该槽取出物品。
- 其余物品字段同上。

## 外部软件读取建议

不要按“一行一个 JSON”解析。当前输出是多行 JSON，应该按空行分隔快照，拿到完整文本后再交给 JSON 解析器。

伪代码：

```text
buffer = ""
while pipe has line:
    if line is empty:
        parse buffer as JSON
        buffer = ""
    else:
        buffer += line + "\n"
```
