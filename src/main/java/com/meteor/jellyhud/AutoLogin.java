package com.meteor.jellyhud;

//import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ConnectScreen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import org.apache.commons.lang3.tuple.Pair;

import java.nio.charset.StandardCharsets;
import java.util.function.Supplier;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("autologin")
public class AutoLogin {

    private SimpleChannel channel;


    // 输入密码
    public static String password = null;

    public static class Setting
    {
        public final ForgeConfigSpec.ConfigValue   ipAddres;

        Setting(ForgeConfigSpec.Builder builder)
        {
            builder.push("AutoLoginSetting");
            this.ipAddres = builder
                    .comment("你的服务器ip地址")
                    .define("showCompass", "127.0.0.1:10003");
            builder.pop();
        }
    }

    Pair<Setting, ForgeConfigSpec> configure;

    public AutoLogin() {


        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::processIMC);

        MinecraftForge.EVENT_BUS.register(this);

        configure = new ForgeConfigSpec.Builder().configure(Setting::new);

        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT,configure.getRight());


    }

    private void setup(final FMLCommonSetupEvent event) {
        channel = NetworkRegistry.ChannelBuilder.named(new ResourceLocation("autologin", "main"))
                .networkProtocolVersion(() -> "bzdo")
                .serverAcceptedVersions(NetworkRegistry.ACCEPTVANILLA::equals)
                .clientAcceptedVersions(NetworkRegistry.ACCEPTVANILLA::equals)
                .simpleChannel();
        channel.registerMessage(222,String.class,this::enc,this::dec,this::proc);
    }

    private void enc(String str, FriendlyByteBuf buffer) {
        buffer.writeBytes(str.getBytes(StandardCharsets.UTF_8));
    }

    private String dec(FriendlyByteBuf buffer) {
        return buffer.toString(StandardCharsets.UTF_8);
    }

    private void proc(String str, Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.setPacketHandled(true);
        channel.reply(password, context);
    }

    private void enqueueIMC(final InterModEnqueueEvent event) {
    }

    private void processIMC(final InterModProcessEvent event) {
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
    }





    @SubscribeEvent
    public void guiInit(ScreenEvent.InitScreenEvent.Pre preEvent){
        if(preEvent.getScreen() instanceof TitleScreen || preEvent.getScreen() instanceof JoinMultiplayerScreen){
            preEvent.setCanceled(true);
            Minecraft instance = Minecraft.getInstance();
            RegisterScreen registerScreen = new RegisterScreen(preEvent.getScreen(),this::show,null);
            instance.setScreen(registerScreen);
        }
    }

    public void show(boolean b) {
        RegisterScreen registerScreen = new RegisterScreen(Minecraft.getInstance().screen,this::show,null);
        ConnectScreen.startConnecting(registerScreen,Minecraft.getInstance(), ServerAddress.parseString(
                (String) configure.getLeft().ipAddres.get()
        ), new ServerData("服务器",(String) configure.getLeft().ipAddres.get(),false));
    }




    // You can use EventBusSubscriber to automatically subscribe events on the contained class (this is subscribing to the MOD
    // Event bus for receiving Registry Events)
    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {
        @SubscribeEvent
        public static void onBlocksRegistry(final RegistryEvent.Register<Block> blockRegistryEvent) {
            // Register a new block here
//            LOGGER.info("HELLO from Register Block");
        }
    }
}
