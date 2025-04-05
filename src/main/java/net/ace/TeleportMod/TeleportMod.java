package net.ace.TeleportMod;

import com.mojang.logging.LogUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(TeleportMod.MODID)
public class TeleportMod {
    public static final String MODID = "teleport-mod";

    public TeleportMod() {
        // 注册模组事件总线
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        // 注册 Forge 事件总线（用于指令注册）
        MinecraftForge.EVENT_BUS.register(this);
    }

    // ---- 指令注册逻辑 ----
    @SubscribeEvent
    public void onCommandRegister(RegisterCommandsEvent event) {
        // 在此处调用你的指令注册方法
        CommandHandler.onCommandRegister(event);
    }
}