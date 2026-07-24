# Data Share Mod（客户端专用）

Forge **1.20.1 / 1.21.1 / 1.21.4 / 1.21.11** 客户端 Mod：采集本地玩家与容器数据，标准化为 JSON，经 **Windows 命名管道** 推送给外部程序。

完整说明：

- 中文：`README.zh-CN.md`
- English: `README.en-US.md`

## 约束（按 Forge 手册）

- `clientSideOnly=true` + `displayTest=IGNORE_ALL_VERSION`
- 依赖 `side=CLIENT`
- 专用服加载会直接拒绝（不应安装到服务端）
- 可进无此 Mod 的多人服（仅本地读客户端可见数据）

## 管道协议

| 项 | 值 |
|---|---|
| 管道名 | `\\.\pipe\data_share` |
| 角色 | **Mod 创建管道并写入**；外部软件只连接读取 |
| 编码 | UTF-8 |
| 格式 | Pretty JSON：一个快照是一个多行 JSON 对象，快照之间用空行分隔 |
| 频率 | 约 10 Hz（有变化才写） |
| 布尔值 | 全部用数字表示：`1` 表示 true，`0` 表示 false |

示例字段：`player.pos` / `health` / `food` / `saturation` / `inventory` / `openContainer` / `effects` / `gamemode` 等。

## 外部读取示例

先启动带此 Mod 的游戏，再运行。这个脚本会直接显示 Mod 生成的多行 JSON：

```powershell
powershell -ExecutionPolicy Bypass -File mod\mc\tools\read_data_share_pipe.ps1
```

C# 连接示例：

```csharp
using var fs = new FileStream(@"\\.\pipe\data_share", FileMode.Open, FileAccess.Read, FileShare.ReadWrite);
using var reader = new StreamReader(fs, Encoding.UTF8);
string? line;
while ((line = reader.ReadLine()) != null) {
    // line 为完整 JSON
}
```

## 构建 / 安装

每个 Minecraft 版本一个独立工程，进入对应目录执行：

```bat
build.bat
install-to-pcl.bat
```

| Minecraft | Forge | 工程 | 安装目录 |
|---|---:|---|---|
| `1.20.1` | `47.4.22` | `mod\mc\dev-1.20.1` | `mod\mc\.minecraft\versions\1.20.1-Forge_47.4.22\mods\` |
| `1.21.1` | `52.1.16` | `mod\mc\dev-1.21.1` | `mod\mc\.minecraft\versions\1.21.1-Forge_52.1.16\mods\` |
| `1.21.4` | `54.1.17` | `mod\mc\dev-1.21.4` | `mod\mc\.minecraft\versions\1.21.4-Forge_54.1.17\mods\` |
| `1.21.11` | `61.1.11` | `mod\mc\dev-1.21.11` | `mod\mc\.minecraft\versions\1.21.11-Forge_61.1.11\mods\` |

产物都叫：`build\libs\data_share-1.0.0.jar`。

PCL 开启了**版本独立**（`VersionArgumentIndieV2`），Mod 安装在各版本目录的 `mods` 下，不是全局 `.minecraft\mods`。

四个 jar 都是版本专用，不能互换使用；管道名都相同：`\\.\pipe\data_share`。
