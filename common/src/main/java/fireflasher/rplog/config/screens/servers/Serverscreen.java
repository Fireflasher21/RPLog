package fireflasher.rplog.config.screens.servers;


#if MC_1_16_5
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
import static fireflasher.rplog.config.screens.options.Optionsscreen.*;


public class Serverscreen extends Screen {

    private final Screen previous;
    private final ServerConfig serverConfig;
    private ScrollPane scrollPane;

    public Serverscreen(Screen previous, ServerConfig serverConfig) {
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

        Button reset = new Button(this.width / 2 - this.width / 4 - B_WIDTH/2, 13, B_WIDTH, B_HEIGHT,
                RPLog.translateAbleStrings.get("rplog.config.serverscreen.reset_defaults"),
                button -> {
                    serverConfig.getServerDetails().getServerKeywords().clear();
                    serverConfig.getServerDetails().getServerKeywords().addAll(RPLog.CONFIG.getDefaultKeywords());
                    Minecraft.getInstance().setScreen(new Serverscreen(previous, serverConfig));
                });

        Button done = new Button(this.width / 2 + this.width / 4 - reset.getWidth() / 2 , 13, reset.getWidth(), B_HEIGHT,
                RPLog.translateAbleStrings.get("rplog.config.screen.done"),
                button -> {
                    RPLog.CONFIG.saveConfig();
                    onClose();
                });

        EditBox insert = new EditBox(this.font, this.width / 2 - this.width / 4 - reset.getWidth()/2, this.height-30, reset.getWidth(), B_HEIGHT,
                Component.nullToEmpty("Keyword"));

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

        addButton(add);
        addButton(insert);
        addButton(done);
        addButton(reset);
    }

    private void addButtonsToScrollPane(ServerConfig.ServerDetails serverDetails){
        scrollPane.getButtons().clear();
        List<String> keywords = serverDetails.getServerKeywords();
        int i = 30;
        for (String keyword : keywords) {
            i = i + 20;
            Button delete = new Button(this.width / 2 + this.width / 4 - B_WIDTH / 2, i - 5, B_WIDTH, B_HEIGHT,
                    RPLog.translateAbleStrings.get("rplog.config.screen.delete"),
                    button -> {
                        if(!button.visible)return;
                        keywords.remove(keyword);
                        //serverConfig.setServerDetails(serverDetails);
                        Minecraft.getInstance().setScreen(new Serverscreen(previous, serverConfig));
                        //addButtonsToScrollPane(serverDetails);
                    });

            Button keywordBox = new Button((this.width / 2 - this.width / 4) - delete.getWidth()/2, i - 5, delete.getWidth(),B_HEIGHT,
                    Component.nullToEmpty(keyword), button -> {});
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
        int lengthOfTitle = this.title.getContents().length()/2;
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