package com.sodiumcrash;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;

import java.util.Optional;

/**
 * 钠-崩溃 (Sodium Crash)
 * 
 * 检测 Sodium 模组是否安装。
 * 只要检测到 sodium，直接崩溃游戏。
 * 
 * 检测特征：
 * 1. 模组 ID: "sodium"
 * 2. 主类: net.caffeinemc.mods.sodium.fabric.SodiumFabricMod
 * 3. 提供 indium 兼容
 */
public class SodiumCrashMod implements ModInitializer {

    private static final String SODIUM_MOD_ID = "sodium";
    private static final String SODIUM_MAIN_CLASS = "net.caffeinemc.mods.sodium.fabric.SodiumFabricMod";

    @Override
    public void onInitialize() {
        FabricLoader loader = FabricLoader.getInstance();

        // 检测方法1: 通过模组 ID 检测
        Optional<ModContainer> sodiumMod = loader.getModContainer(SODIUM_MOD_ID);
        if (sodiumMod.isPresent()) {
            ModContainer mod = sodiumMod.get();
            String version = mod.getMetadata().getVersion().getFriendlyString();
            crashGame(
                "钠-崩溃 | Sodium Crash",
                "检测到 Sodium 模组已安装！",
                "模组 ID: " + SODIUM_MOD_ID,
                "版本: " + version,
                "名称: " + mod.getMetadata().getName(),
                "作者: JellySquid (jellysquid3)",
                "描述: " + mod.getMetadata().getDescription(),
                "",
                "此模组 (钠-崩溃) 检测到 Sodium 的存在，",
                "强制崩溃游戏。",
                "请删除 sodium 或 sodium-crash 模组后重试。"
            );
        }

        // 检测方法2: 通过主类检测
        boolean classExists = false;
        try {
            Class.forName(SODIUM_MAIN_CLASS);
            classExists = true;
        } catch (ClassNotFoundException ignored) {
        }

        if (classExists) {
            crashGame(
                "钠-崩溃 | Sodium Crash",
                "检测到 Sodium 核心类已加载！",
                "类路径: " + SODIUM_MAIN_CLASS,
                "",
                "此模组 (钠-崩溃) 通过类路径扫描检测到 Sodium，",
                "强制崩溃游戏。",
                "请删除 sodium 或 sodium-crash 模组后重试。"
            );
        }

        // 检测方法3: 扫描所有已加载模组，检查是否有模组提供 indium
        for (ModContainer container : loader.getAllMods()) {
            if (container.getMetadata().getProvides() != null) {
                for (String provided : container.getMetadata().getProvides()) {
                    if ("indium".equals(provided) && !SODIUM_MOD_ID.equals(container.getMetadata().getId())) {
                        crashGame(
                            "钠-崩溃 | Sodium Crash",
                            "检测到模拟 Sodium 接口的模组已安装！",
                            "模组 ID: " + container.getMetadata().getId(),
                            "提供: indium（Sodium 兼容层）",
                            "",
                            "此模组 (钠-崩溃) 检测到 Sodium 兼容接口，",
                            "强制崩溃游戏。",
                            "请删除相关模组后重试。"
                        );
                    }
                }
            }
        }
    }

    private void crashGame(String title, String... messages) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n");
        sb.append("╔══════════════════════════════════════════╗\n");
        sb.append("║  ").append(title).append("  ║\n");
        sb.append("╠══════════════════════════════════════════╣\n");
        for (String msg : messages) {
            sb.append("║  ").append(msg).append("\n");
        }
        sb.append("╚══════════════════════════════════════════╝\n");

        String crashMessage = sb.toString();
        System.err.println(crashMessage);

        throw new RuntimeException(crashMessage);
    }
}
