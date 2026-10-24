package net.scottishfrogg.cutenames;

import com.mojang.logging.LogUtils;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.scottishfrogg.cutenames.client.renderer.CuteCatRenderer;
import net.scottishfrogg.cutenames.client.renderer.CutePigRenderer;
import net.scottishfrogg.cutenames.client.renderer.CuteWardenRenderer;
import net.scottishfrogg.cutenames.client.renderer.CuteWolfRenderer;
import org.slf4j.Logger;

@Mod(CuteMod.MOD_ID)
public class CuteMod {
    public static final String MOD_ID = "cutemod";
    public static final Logger LOGGER = LogUtils.getLogger();
    public CuteMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::commonSetup);
        MinecraftForge.EVENT_BUS.register(this);
        modEventBus.addListener(this::addCreative);
    }
    private void commonSetup(final FMLCommonSetupEvent event) {
    }
    private void addCreative(BuildCreativeModeTabContentsEvent event) {
    }
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
    }
    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
        }
        @SubscribeEvent
        public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
            event.registerEntityRenderer(EntityType.CAT, CuteCatRenderer::new);
            event.registerEntityRenderer(EntityType.WOLF, CuteWolfRenderer::new);
            event.registerEntityRenderer(EntityType.PIG, CutePigRenderer::new);
            event.registerEntityRenderer(EntityType.WARDEN, CuteWardenRenderer::new);
        }
    }
}
