package fireflasher.rplog.config.screens.servers;


#if MC_1_20_1 || MC_1_20_4 || MC_1_20_6
import fireflasher.rplog.*;
import fireflasher.rplog.config.ScrollPane;
import fireflasher.rplog.config.json.ServerConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.List;

import static fireflasher.rplog.Chatlogger.*;
import static fireflasher.rplog.config.screens.options.Optionsscreen_1_20_1.B_HEIGHT;
import static fireflasher.rplog.config.screens.options.Optionsscreen_1_20_1.B_WIDTH;

public class Serverscreen_1_20_1 extends Screen {

    private final Screen previous;
    private final ServerConfig serverConfig;
    private ScrollPane scrollPane;

    public Serverscreen_1_20_1(Screen previous, ServerConfig serverConfig) {
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

        scrollPane = new ScrollPane(this.width,this.height, B_HEIGHT,0);
        addButtonsToScrollPane(serverDetails);
        //implement static buttons

        Button reset = Button.builder(RPLog.translateAbleStrings.get("rplog.config.serverscreen.reset_defaults"),
                button -> {
                    serverConfig.getServerDetails().getServerKeywords().clear();
                    serverConfig.getServerDetails().getServerKeywords().addAll(RPLog.CONFIG.getDefaultKeywords());
                    Minecraft.getInstance().setScreen(new Serverscreen_1_20_1(previous, serverConfig));
                }).bounds(this.width / 2 - this.width / 4 - B_WIDTH/2, 13, B_WIDTH, B_HEIGHT)
                .build();


        Button done = Button.builder(RPLog.translateAbleStrings.get("rplog.config.screen.done"),
                button -> {
                    RPLog.CONFIG.saveConfig();
                    onClose();
                }).bounds(this.width / 2 + this.width / 4 - reset.getWidth() / 2 , 13, reset.getWidth(), B_HEIGHT)
                .build();

        EditBox insert = new EditBox(this.font, this.width / 2 - this.width / 4 - reset.getWidth()/2, this.height-30, reset.getWidth(), B_HEIGHT,
                Component.nullToEmpty("Keyword"));

        Button add = Button.builder(RPLog.translateAbleStrings.get("rplog.config.serverscreen.add_Keywords"),
                button -> {
                    if(!keywords.contains(insert.getValue()) && !insert.getValue().isEmpty()){
                        keywords.add(insert.getValue());
                        insert.setValue("");
                        addButtonsToScrollPane(serverDetails);
                    }}).bounds(this.width / 2 + this.width / 4 - insert.getWidth() / 2, insert.getY(), insert.getWidth(), B_HEIGHT)
                .build();

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
            keywordBox.active=false;

            scrollPane.addButton(delete);
            scrollPane.addButton(keywordBox);
            addWidget(delete);
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        #if MC_1_20_1
        this.renderBackground(guiGraphics);
        #elif MC_1_20_4 || MC_1_20_6
        this.renderBackground(guiGraphics,mouseX,mouseY,partialTick);
        #endif
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.fill(0, 50, this.width, this.height-50, 0xFF222222);
        scrollPane.render(guiGraphics,mouseX,mouseY,partialTick);
        int lengthOfTitle = this.title.getContents().toString().length();
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2 - lengthOfTitle , 18, 0xffffff);
    }

    @Override
    public void onClose(){
        this.minecraft.setScreen(previous);
    }

    #if MC_1_20_1

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        return scrollPane.mouseScrolled(mouseX,mouseY,delta);
    }
    #elif MC_1_20_4 || MC_1_20_6
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        return scrollPane.mouseScrolled(mouseX,mouseY,mouseX,mouseY);
    }
    #endif


}


#endif