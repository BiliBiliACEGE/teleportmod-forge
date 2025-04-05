package net.ace.teleportmod;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod(TeleportMod.MOD_ID)
public class TeleportMod {
    public static final String MOD_ID = "teleportmod";

    public TeleportMod() {
        MinecraftForge.EVENT_BUS.register(this); // 关键：注册主类到 Forge 事件总线
        System.out.println("TeleportMod 初始化成功！");
    }

    @SubscribeEvent
    public void onCommandRegister(RegisterCommandsEvent event) {
        CommandHandler.onCommandRegister(event); // 调用静态方法注册命令
    }
}