package fireflasher.rplog.config.screens.servers;


#if MC_1_18_2 || MC_1_19_4
import fireflasher.rplog.*;
import com.mojang.blaze3d.vertex.PoseStack;
import fireflasher.rplog.config.json.ServerConfig;
import fireflasher.rplog.config.ScrollPane;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.List;

import static fireflasher.rplog.Chatlogger.*;
import static fireflasher.rplog.config.screens.options.Optionsscreen_1_18_2.*;


public class Serverscreen_1_18_2 extends Screen {

    private final Screen previous;
    private final ServerConfig serverConfig;
    private ScrollPane scrollPane;

    public Serverscreen_1_18_2(Screen previous, ServerConfig serverConfig) {
        super(Component.nullToEmpty(getShortestNameOfList(serverConfig.getServerDetails().getServerNames())));
        this.previous = previous;
        this.serverConfig = serverConfig;
    }


    @Override
    public void resize(Minecraft minecraft, int width, int height) {
        super.resize(minecraft, width, height);
    }

    @Override
    protected void init() {
        ServerConfig.ServerDetails serverDetails = serverConfig.getServerDetails();
        List<String> keywords = serverDetails.getServerKeywords();

        scrollPane = new ScrollPane(this.width,this.height, B_HEIGHT,borderOffsetFill+5);
        addButtonsToScrollPane(serverDetails);
        //implement static buttons

        EditBox insert = new EditBox(this.font, this.width / 2 - this.width / 4 - B_WIDTH/2, this.height-30, B_WIDTH, B_HEIGHT,
                Component.nullToEmpty("Keyword"));
        #if MC_1_18_2
        Button reset = new Button(this.width / 2 - this.width / 4 - B_WIDTH/2, 13, B_WIDTH, B_HEIGHT,
                RPLog.translateAbleStrings.get("rplog.config.serverscreen.reset_defaults"),
                button -> {
                    serverConfig.getServerDetails().getServerKeywords().clear();
                    serverConfig.getServerDetails().getServerKeywords().addAll(RPLog.CONFIG.getDefaultKeywords());
                    Minecraft.getInstance().setScreen(new Serverscreen_1_18_2(previous, serverConfig));
                });

        Button done = new Button(this.width / 2 + this.width / 4 - reset.getWidth() / 2 , 13, reset.getWidth(), B_HEIGHT,
                RPLog.translateAbleStrings.get("rplog.config.screen.done"),
                button -> {
                    RPLog.CONFIG.saveConfig();
                    onClose();
                });

        Button add = new Button(this.width / 2 + this.width / 4 - insert.getWidth() / 2, insert.y, insert.getWidth(), B_HEIGHT,
                RPLog.translateAbleStrings.get("rplog.config.serverscreen.add_Keywords"),
                button -> {

                    if(!keywords.contains(insert.getValue()) && !insert.getValue().isEmpty()){
                        keywords.add(insert.getValue());
                        insert.setValue("");
                        //serverConfig.setServerDetails(serverDetails);
                        addButtonsToScrollPane(serverDetails);
                        //Minecraft.getInstance().setScreen(new Serverscreen_1_18_2(previous, serverConfig));
                    }});
        #elif MC_1_19_4

        Button reset = Button.builder(RPLog.translateAbleStrings.get("rplog.config.serverscreen.reset_defaults"),
                        button -> {
                            serverConfig.getServerDetails().getServerKeywords().clear();
                            serverConfig.getServerDetails().getServerKeywords().addAll(RPLog.CONFIG.getDefaultKeywords());
                            Minecraft.getInstance().setScreen(new Serverscreen_1_18_2(previous, serverConfig));
                        }).bounds(this.width / 2 - this.width / 4 - B_WIDTH/2, 13, B_WIDTH, B_HEIGHT)
                .build();


        Button done = Button.builder(RPLog.translateAbleStrings.get("rplog.config.screen.done"),
                        button -> {
                            RPLog.CONFIG.saveConfig();
                            onClose();
                        }).bounds(this.width / 2 + this.width / 4 - reset.getWidth() / 2 , 13, reset.getWidth(), B_HEIGHT)
                .build();


        Button add = Button.builder(RPLog.translateAbleStrings.get("rplog.config.serverscreen.add_Keywords"),
                        button -> {
                            if(!keywords.contains(insert.getValue()) && !insert.getValue().isEmpty()){
                                keywords.add(insert.getValue());
                                insert.setValue("");
                                addButtonsToScrollPane(serverDetails);
                            }}).bounds(this.width / 2 + this.width / 4 - insert.getWidth() / 2, insert.getY(), insert.getWidth(), B_HEIGHT)
                .build();
        #endif

        addRenderableWidget(add);
        addRenderableWidget(insert);
        addRenderableWidget(done);
        addRenderableWidget(reset);
    }

    private void addButtonsToScrollPane(ServerConfig.ServerDetails serverDetails){
        scrollPane.getButtons().clear();
        List<String> keywords = serverDetails.getServerKeywords();
        int i = 30;
        for (String keyword : keywords) {
            i = i + 20;
            #if MC_1_18_2
            Button delete = new Button(this.width / 2 + this.width / 4 - B_WIDTH / 2, i - 5, B_WIDTH, B_HEIGHT,
                    RPLog.translateAbleStrings.get("rplog.config.screen.delete"),
                    button -> {
                        if(!button.visible)return;
                        keywords.remove(keyword);
                        //serverConfig.setServerDetails(serverDetails);
                        Minecraft.getInstance().setScreen(new Serverscreen_1_18_2(previous, serverConfig));
                        //addButtonsToScrollPane(serverDetails);
                    });

            Button keywordBox = new Button((this.width / 2 - this.width / 4) - delete.getWidth()/2, i - 5, delete.getWidth(),B_HEIGHT,
                    Component.nullToEmpty(keyword), button -> {});
            #elif MC_1_19_4
            Button delete = Button.builder(RPLog.translateAbleStrings.get("rplog.config.screen.delete"),
                    button -> {
                        if(!button.visible)return;
                        keywords.remove(keyword);
                        addButtonsToScrollPane(serverDetails);
                    }).bounds(this.width / 2 + this.width / 4 - B_WIDTH / 2, i - 5, B_WIDTH, B_HEIGHT)
                    .build();

            Button keywordBox = Button.builder(Component.nullToEmpty(keyword), button -> {})
                    .bounds((this.width / 2 - this.width / 4) - delete.getWidth()/2, i - 5, delete.getWidth(),B_HEIGHT)
                    .build();
            #endif
            keywordBox.active=false;

            scrollPane.addButton(delete);
            scrollPane.addButton(keywordBox);
            addWidget(delete);
        }
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(poseStack);
        fill(poseStack, 0, borderOffsetFill, this.width, this.height-borderOffsetFill, 0xFF222222);
        scrollPane.render(poseStack,mouseX,mouseY,partialTick);
        super.render(poseStack, mouseX, mouseY, partialTick);
        int lengthOfTitle = 0;

        #if MC_1_18_2
        lengthOfTitle = this.title.getContents().length()/2;
        #elif MC_1_19_4
        lengthOfTitle = this.title.getContents().toString().length();
        #endif

        drawCenteredString(poseStack, this.font, this.title, this.width / 2 - lengthOfTitle , 18, 0xffffff);

    }


    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        //super.mouseScrolled(mouseX,mouseY,delta);
        return scrollPane.mouseScrolled(mouseX,mouseY,delta);
    }


    @Override
    public void onClose(){
        this.minecraft.setScreen(previous);
    }


}


#endif