# ShootEXP Fabric

这是 [ShootEXP](https://github.com/MoeArea/ShootEXP) 的 **Fabric 模组移植版本**，与原始 Bukkit 插件功能一致。

> **这是一个纯服务端模组**——玩家不需要在客户端安装任何东西就能享受完整的施法体验。服主只需要在服务器装好，玩家进来就能直接开冲，无需任何前戏准备。
> 当然，如果你想要在单机游戏里也能享受射出经验的快乐，你也可以在客户端安装此模组。

这是一个能让玩家之间通过进行深入交流而变得更加了解彼此的交友模组。当一个玩家对另一个玩家不停蹲起达到一定次数后，这个模组可以让该玩家射出"一滩粘稠的经验"，其中包含了一定量的经验值，当玩家吃掉"一滩粘稠的经验"时，就会获得其中蕴含的经验。

## 支持版本

| 平台 | MC 版本 |
|------|---------|
| Fabric | 1.21  |
| Fabric | 26.2  |

## 安装

1. 安装 [Fabric Loader](https://fabricmc.net/)
2. 将 mod jar 文件放入 `mods` 文件夹
3. 启动游戏

## 命令

| 命令 | 权限等级 | 描述 |
|------|----------|------|
| `/shootexp help` | 0 | 获取帮助 |
| `/shootexp status [玩家]` | 0/2 | 查看自己/他人状态 |
| `/shootexp item <所有者> <赠予者> <数量>` | 2 | 获取经验物品 |
| `/shootexp restore all <玩家>` | 2 | 恢复玩家全部状态 |
| `/shootexp restore times <玩家> <次数>` | 2 | 恢复射出次数 |
| `/shootexp restore stock <玩家> <数量>` | 2 | 恢复经验存量 |
| `/shootexp set <玩家> <射出次数> <经验存量>` | 2 | 设置玩家状态 |
| `/shootexp reload` | 2 | 重载配置 |

## 配置

配置文件位于 `config/shootexp_fabric/config.json`。方便起见，先解释一些术语：

- **"施法"** 指玩家不停蹲起，最后射出经验这一过程
- **"攻击"** 指玩家一次蹲起
- **"攻击者"** 指施法过程中蹲起的玩家
- **"防守者"** 指施法过程中被攻击的玩家
- **"所有者"** 指经验物品是由哪个攻击者产生的
- **"赠予者"** 指经验物品是在所有者对谁施法时产生的

### 语言

这个模组中文叫射出经验，真的没有任何谐音，所以该模组也适用于国际玩家。目前虽然仅支持 `zh_cn` 和 `en_us` 两种语言，但未来可能会开拓更多的国际市场，让我们先在这里画一个饼。

### 最大经验存量

众所周知，一个成年人每天的产量是有限的。`maxStock` 设定了玩家体内能储存的最大经验量，超过这个值就需要等待身体慢慢恢复了。

### 射出经验函数

玩家需要不停的**攻击**达到指定次数之后，就可以射出指定数量的经验。可是很多对射出经验很有研究的专家表示他的经验不该这样射。为了满足这些高端用户，我们提供了自定义射出经验函数的功能：

```json
{
  "requiredAttackTimes": "1.618^SHOOT + 10",
  "shootAmount": "STOCK / 2"
}
```

`SHOOT` 表示已施法次数，`STOCK` 表示经验存量，`MAXSTOCK` 表示最大经验存量，大部分的数学符号~~也许~~都能使用。

### 可施法实体类型

据说有些玩家的xp系统比较特殊，他们不满足于只和人类交流，还想和各种生物建立深厚的友谊。我们尊重每一位玩家的取向，所以提供了这个设置：

```json
{
  "entityTypes": ["Player", "LivingEntity"]
}
```

温馨提示：请遵守当地法律法规。

### 攻击设置

俗话说得好，"心有余而力不足"。就算你的心再热情，你的腿也有软下来的时候；就算你对着目标疯狂输出，别人跑远了你也只会感到索然无味：

```json
{
  "attackDistance": 2.0,
  "attackTimeout": 100
}
```

当目标超过 `attackDistance` 定义的距离后，你的深情就传达不到了。当超过 `attackTimeout` 定义的时间没有攻击，说明你已经体力不支，再起不能。

### 恢复设置

年轻人总觉得自己身体好恢复快，殊不知频繁施法对身体的消耗是巨大的。这里可以调整恢复速度，建议服主根据服务器玩家的平均年龄来设置——年轻人的服务器可以调快一些，老年服建议调慢，毕竟要爱惜身体：

```json
{
  "restoreShootPeriod": 6000,
  "restoreShootAmount": 1,
  "restoreStockPeriod": 6000,
  "restoreStockAmount": 200
}
```

### 自定义模型ID

说实话，原版骨粉的卖相确实不太行，白花花的看着就没食欲。如果你有美术功底，可以做一个看起来更加诱人的材质，让玩家吃得更开心：

```json
{
  "customModelDataEnable": false,
  "customModelDataValue": 0
}
```

建议参考某些饮料的包装设计。

### 自定义音效

默认的攻击音效是史莱姆——那种"啪啪啪"的声音，配合蹲起的动作，画面感极强。如果你觉得不够带感，也可以换成其他音效：

```json
{
  "soundAttack": "minecraft:entity.parrot.imitate.slime",
  "soundShoot": "minecraft:block.slime_block.step",
  "soundShootNoExp": "minecraft:entity.llama.eat",
  "soundEat": "minecraft:entity.generic.drink"
}
```

`soundShootNoExp` 是射不出东西时的音效，用的是羊驼进食的声音——那种干巴巴的咀嚼声，完美诠释了"心有余而力不足"的尴尬。

## 致谢

本项目基于 [MoeArea/ShootEXP](https://github.com/MoeArea/ShootEXP) 移植至 Fabric，感谢原作者的创意与实现。

## 许可证

与原项目保持一致。
