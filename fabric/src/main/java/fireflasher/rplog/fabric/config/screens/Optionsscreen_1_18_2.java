package fireflasher.rplog.fabric.config.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import fireflasher.rplog.RPLog;
import fireflasher.rplog.config.DefaultConfig;
import fireflasher.rplog.config.json.ServerConfig;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.List;

import java.util.regex.Pattern;

import static fireflasher.rplog.Chatlogger.*;
import static fireflasher.rplog.RPLog.LOGGER;

public class Optionsscreen_1_18_2 extends Screen {

    private final Screen previous;

    //ButtonWidth and ButtonHeight
    static final int B_HEIGHT = 20;
    static final int B_WIDTH =100;
    private final ServerConfig dummy = new ServerConfig("dummy", List.of("dummy"), List.of("dummy"));

    private ScrollPane scrollPane;
    public Optionsscreen_1_18_2(Screen previous) {
        super(new TranslatableComponent("rplog.config.optionscreen.title"));
        this.previous = previous;
    }



    protected void init() {
        scrollPane = new ScrollPane(this.width,this.height, B_HEIGHT,0);
        DefaultConfig defaultConfig = RPLog.CONFIG;
        List<ServerConfig> serverConfigList = defaultConfig.getList();
        if (serverConfigList.isEmpty()) {
            serverConfigList.add(dummy);
        }

        addButtonsToScrollPane(serverConfigList);
        serverConfigList.remove(dummy);

        Button addServer = new Button(this.width / 2 - this.width / 4 - 50, 13, B_WIDTH, B_HEIGHT,
                new TranslatableComponent("rplog.config.optionscreen.add_Server"),
                button -> {
                    if ( Minecraft.getInstance().getConnection() != null && !Minecraft.getInstance().hasSingleplayerServer()) {
                        String address = Minecraft.getInstance().getConnection().getConnection().getRemoteAddress().toString();
                        LOGGER.warn(address);
                        Pattern serverAddress = Pattern.compile("static.([0-9]{1,3}[.]){4}");
                        String serverName;
                        Boolean ipMatcher = serverAddress.matcher(address.split("/")[0]).find();
                        String ip = address.split("/")[1];
                        ip = ip.split(":")[0];
                        if(ipMatcher) serverName = ip;
                        else serverName = address.split("/")[0];
                        defaultConfig.addServerToList(ip, serverName);
                        defaultConfig.loadConfig();
                        addButtonsToScrollPane(serverConfigList);
                        //Minecraft.getInstance().setScreen(new Optionsscreen_1_18_2(previous));
                    }
                });


        Button defaultconfigbutton = new Button(this.width / 2 + this.width / 4 - B_WIDTH/2 , 13, B_WIDTH, B_HEIGHT,
                new TranslatableComponent("rplog.config.screen.defaults"),
                button -> {
                    ServerConfig defaults = new ServerConfig("Defaults",List.of("Defaults"),defaultConfig.getDefaultKeywords());
                    Minecraft.getInstance().setScreen(new Serverscreen_1_18_2(Minecraft.getInstance().screen, defaults));
                });


        Button done = new Button(this.width / 2 + this.width / 4 - B_WIDTH/2, this.height - 30, B_WIDTH , B_HEIGHT,
                new TranslatableComponent("rplog.config.screen.done"),
                button -> onClose());

        Button openFolder = new Button(this.width / 2 - this.width / 4 - B_WIDTH/2, this.height - 30, B_WIDTH , B_HEIGHT,
                new TranslatableComponent("rplog.config.optionscreen.open_LogFolder"),
                button ->{
                    openFolder(RPLog.getFolder());
                    });

        addRenderableWidget(defaultconfigbutton);
        addRenderableWidget(addServer);
        addRenderableWidget(done);
        addRenderableWidget(openFolder);
    }

    private void addButtonsToScrollPane(List<ServerConfig> serverConfigList){
        scrollPane.getButtons().clear();
        int currentPos = 30;
        for (ServerConfig server : serverConfigList) {
            currentPos += 25;
            Button serverNameButton = new Button(this.width / 2 - this.width / 4 - B_WIDTH /2, currentPos, B_WIDTH, B_HEIGHT,
                    Component.nullToEmpty(getServerNameShortener(server.getServerDetails().getServerNames())),
                    button ->{
                        if(!button.visible)return;
                        Minecraft.getInstance().setScreen(new Serverscreen_1_18_2(Minecraft.getInstance().screen, server));
                    });


            Button delete = new Button(this.width / 2 + this.width / 4 - serverNameButton.getWidth() / 2, currentPos, serverNameButton.getWidth(), B_HEIGHT,
                    new TranslatableComponent("rplog.config.screen.delete"),
                    button -> {
                        if(!button.visible)return;
                        Minecraft.getInstance().setScreen(new Verification(Minecraft.getInstance().screen, RPLog.CONFIG, server));
                    });

            if (!serverConfigList.contains(dummy)) {
                scrollPane.addButton(serverNameButton);
                scrollPane.addButton(delete);
                addWidget(serverNameButton);
                addWidget(delete);
            }
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        //super.mouseScrolled(mouseX,mouseY,delta);
        return scrollPane.mouseScrolled(mouseX,mouseY,delta);
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        // Render Order:
        // First Background
        this.renderBackground(poseStack);
        fill(poseStack, 0, 50, this.width, this.height-50, 0xFF222222);
        scrollPane.render(poseStack,mouseX,mouseY,partialTick);

        super.render(poseStack, mouseX, mouseY, partialTick);

        TranslatableComponent serverlist = new TranslatableComponent("rplog.config.optionscreen.configuration_Servers");
        TranslatableComponent deleteServer = new TranslatableComponent("rplog.config.optionscreen.delete_Servers");
        drawCenteredString(poseStack, this.font, this.title, this.width / 2, 18, 0xffffff);
        drawCenteredString(poseStack, this.font, serverlist, this.width / 2 - this.width / 4, 40, 0xffffff);
        drawCenteredString(poseStack, this.font, deleteServer, this.width / 2 + this.width / 4, 40, 0xffffff);
    }

    public void openFolder(String folderPath) {
        File folder = new File(folderPath);

        // Check if the folder exists and is a directory
        if (folder.exists() && folder.isDirectory()) {
            try {
                String os = System.getProperty("os.name").toLowerCase();

                if (os.contains("win")) {
                    // Windows
                    Runtime.getRuntime().exec("explorer \"" + folder.getAbsolutePath() + "\"");
                } else if (os.contains("mac")) {
                    // macOS
                    Runtime.getRuntime().exec("open \"" + folder.getAbsolutePath() + "\"");
                } else if (os.contains("nix") || os.contains("nux")) {
                    // Linux
                    Runtime.getRuntime().exec(new String[] { "xdg-open", folder.getAbsolutePath() });
                } else {
                    System.out.println("Unsupported operating system: " + os);
                }
            } catch (IOException e) {
                e.printStackTrace();
                LOGGER.error(new TranslatableComponent("rplog.screens.optionscreen.openfolder_error"));
            }
        } else {
            LOGGER.error(new TranslatableComponent("rplog.screens.optionscreen.openfolder_error"));
        }
    }

    @Override
    public void onClose(){
        RPLog.CONFIG.loadConfig();
        this.minecraft.setScreen(previous);
    }

    public class Verification extends Screen{

        private final Screen previous;
        private final DefaultConfig defaultConfig;
        private final ServerConfig serverConfig;

        Verification(Screen previous, DefaultConfig defaultConfig, ServerConfig serverConfig){
            super(Component.nullToEmpty(""));
            this.previous = previous;
            this.defaultConfig = defaultConfig;
            this.serverConfig = serverConfig;
        }

        public void init(){
            Button delete = new Button(this.width / 2 - this.width / 4 - 50, this.height / 2, B_WIDTH, B_HEIGHT,
                    new TranslatableComponent("rplog.config.optionscreen.verification.delete"),
                    button -> {
                        defaultConfig.removeServerFromList(serverConfig);
                        onClose();
                        //Minecraft.getInstance().setScreen(new Optionsscreen_1_18_2(previous));
                    });


            Button abort = new Button(this.width / 2 + this.width / 4 - 50, this.height / 2,B_WIDTH, B_HEIGHT,
                    new TranslatableComponent("rplog.config.optionscreen.verification.cancel"), button -> onClose());

            this.addRenderableWidget(delete);
            this.addRenderableWidget(abort);

        }

        @Override
        public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
            this.renderBackground(poseStack);
            super.render(poseStack, mouseX, mouseY, partialTick);
            TranslatableComponent verificationmessage = new TranslatableComponent("rplog.config.optionscreen.verification.message");
            drawCenteredString(poseStack, this.font, verificationmessage, this.width / 2, this.height / 2 - this.height / 4, 0xffffff);
        }

        @Override
        public void onClose(){
            this.minecraft.setScreen(previous);
        }

    }
}