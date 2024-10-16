package fireflasher.rplog.config.screens.options;

#if MC_1_18_2 || MC_1_19_2
import com.mojang.blaze3d.vertex.PoseStack;
import fireflasher.rplog.*;
import fireflasher.rplog.config.DefaultConfig;
import fireflasher.rplog.config.json.ServerConfig;
import fireflasher.rplog.config.ScrollPane;
import fireflasher.rplog.config.screens.servers.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.io.File;
import java.io.IOException;
import java.util.List;


import static fireflasher.rplog.Chatlogger.*;
import static fireflasher.rplog.RPLog.*;

public class Optionsscreen_1_18_2 extends Screen {

    private final Screen previous;

    //ButtonWidth and ButtonHeight
    public static final int B_HEIGHT = 20;
    public static final int B_WIDTH =100;
    private ScrollPane scrollPane;

    public Optionsscreen_1_18_2(Screen previous) {
        super(RPLog.translateAbleStrings.get("rplog.config.optionscreen.title"));
        this.previous = previous;
    }



    protected void init() {
        DefaultConfig defaultConfig = RPLog.CONFIG;
        List<ServerConfig> serverConfigList = defaultConfig.getList();

        scrollPane = new ScrollPane(this.width,this.height, B_HEIGHT,55);
        addButtonsToScrollPane(serverConfigList);

        Button addServer = new Button(this.width / 2 - this.width / 4 - 50, 13, B_WIDTH, B_HEIGHT,
                RPLog.translateAbleStrings.get("rplog.config.optionscreen.add_Server"),
                button -> {
                        String[] address = Chatlogger.getCurrentServerIP();
                        if(address == null)return;

                        defaultConfig.addServerToList(address[1], address[0]);
                        defaultConfig.loadConfig();
                        addButtonsToScrollPane(serverConfigList);
                    }
                );


        Button defaultconfigbutton = new Button(this.width / 2 + this.width / 4 - B_WIDTH/2 , 13, B_WIDTH, B_HEIGHT,
                RPLog.translateAbleStrings.get("rplog.config.screen.defaults"),
                button -> {
                    ServerConfig defaults = new ServerConfig("Defaults",List.of("Defaults"),defaultConfig.getDefaultKeywords());
                    Minecraft.getInstance().setScreen(new Serverscreen_1_18_2(Minecraft.getInstance().screen, defaults));
                });


        Button done = new Button(this.width / 2 + this.width / 4 - B_WIDTH/2, this.height - 30, B_WIDTH , B_HEIGHT,
                RPLog.translateAbleStrings.get("rplog.config.screen.done"),
                button -> onClose());

        Button openFolder = new Button(this.width / 2 - this.width / 4 - B_WIDTH/2, this.height - 30, B_WIDTH , B_HEIGHT,
                RPLog.translateAbleStrings.get("rplog.config.optionscreen.open_LogFolder"),
                button ->{openFolder(RPLog.getFolder());
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
                    Component.nullToEmpty(getShortestNameOfList(server.getServerDetails().getServerNames())),
                    button ->{
                        if(!button.visible)return;
                        LOGGER.warn(server);
                        Minecraft.getInstance().setScreen(new Serverscreen_1_18_2(Minecraft.getInstance().screen, server));
                    });


            Button delete = new Button(this.width / 2 + this.width / 4 - serverNameButton.getWidth() / 2, currentPos, serverNameButton.getWidth(), B_HEIGHT,
                    RPLog.translateAbleStrings.get("rplog.config.screen.delete"),
                    button -> {
                        if(!button.visible)return;
                        Minecraft.getInstance().setScreen(new Verification(Minecraft.getInstance().screen, RPLog.CONFIG, server));
                    });


            scrollPane.addButton(serverNameButton);
            scrollPane.addButton(delete);
            addWidget(serverNameButton);
            addWidget(delete);

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

        Component serverlist = RPLog.translateAbleStrings.get("rplog.config.optionscreen.configuration_Servers");
        Component deleteServer = RPLog.translateAbleStrings.get("rplog.config.optionscreen.delete_Servers");
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
                LOGGER.error(RPLog.translateAbleStrings.get("rplog.logger.optionscreen.openfolder_error"));
            }
        } else {
            LOGGER.error(RPLog.translateAbleStrings.get("rplog.logger.optionscreen.openfolder_error"));
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
                    RPLog.translateAbleStrings.get("rplog.config.optionscreen.verification.delete"),
                    button -> {
                        defaultConfig.removeServerFromList(serverConfig);
                        onClose();
                        //Minecraft.getInstance().setScreen(new Optionsscreen_1_18_2(previous));
                    });


            Button abort = new Button(this.width / 2 + this.width / 4 - 50, this.height / 2,B_WIDTH, B_HEIGHT,
                    RPLog.translateAbleStrings.get("rplog.config.optionscreen.verification.cancel"), button -> onClose());

            this.addRenderableWidget(delete);
            this.addRenderableWidget(abort);

        }

        @Override
        public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
            this.renderBackground(poseStack);
            super.render(poseStack, mouseX, mouseY, partialTick);
            Component verificationmessage = RPLog.translateAbleStrings.get("rplog.config.optionscreen.verification.message");
            drawCenteredString(poseStack, this.font, verificationmessage, this.width / 2, this.height / 2 - this.height / 4, 0xffffff);
        }

        @Override
        public void onClose(){
            this.minecraft.setScreen(previous);
        }

    }
}

#endif