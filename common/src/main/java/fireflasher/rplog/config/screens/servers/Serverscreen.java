package fireflasher.rplog.config.screens.servers;


#if MC_1_18_2 ||  MC_1_19_4
import com.mojang.blaze3d.vertex.PoseStack;
#elif MC_1_20_1 || MC_1_20_4
import net.minecraft.client.gui.GuiGraphics;
#endif
import fireflasher.rplog.*;
import fireflasher.rplog.config.ScrollPane;
import fireflasher.rplog.config.json.ServerConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

import static fireflasher.rplog.ChatLogManager.*;
import static fireflasher.rplog.config.screens.options.Optionsscreen.*;

public class Serverscreen extends Screen {

    private final Screen previous;
    private final ServerConfig.ServerDetails serverDetails;
    private ScrollPane scrollPane;

    public Serverscreen(Screen previous, ServerConfig.ServerDetails serverDetails) {
        super(Component.nullToEmpty(getMainDomain(serverDetails.getServerNames().get(0))));
        this.previous = previous;
        this.serverDetails = serverDetails;
    }


    @Override
    public void resize(Minecraft minecraft, int width, int height) {
        super.resize(minecraft, width, height);
    }

    @Override
    protected void init() {

        scrollPane = new ScrollPane(this.width,this.height, B_HEIGHT,borderOffsetFill+5);
        addButtonsToScrollPane(serverDetails);
        //implement static buttons

        Button reset = buttonBuilder(RPLog.translateAbleStrings.get("rplog.config.serverscreen.reset_defaults"),
                this.width / 2 - this.width / 4 - B_WIDTH/2, 13, B_WIDTH, B_HEIGHT,
                button -> {
                    serverDetails.setServerKeywords(new ArrayList<>(RPLog.CONFIG.getDefaultKeywords()));
                    Minecraft.getInstance().setScreen(new Serverscreen(previous, serverDetails));
                });


        Button done = buttonBuilder(RPLog.translateAbleStrings.get("rplog.config.screen.done"),
                this.width / 2 + this.width / 4 - reset.getWidth() / 2 , 13, reset.getWidth(), B_HEIGHT,
                button -> {
                    RPLog.CONFIG.saveConfig();
                    onClose();
                });

        EditBox insert = new EditBox(this.font, this.width / 2 - this.width / 4 - reset.getWidth()/2, this.height-30, reset.getWidth(), B_HEIGHT,
                Component.nullToEmpty("Keyword"));

        Button add = buttonBuilder(RPLog.translateAbleStrings.get("rplog.config.serverscreen.add_Keywords"),
                this.width / 2 + this.width / 4 - insert.getWidth() / 2, insert.getY(), insert.getWidth(), B_HEIGHT,
                button -> {
                        String keyword = insert.getValue();
                        if(!keyword.isEmpty() && !serverDetails.getServerKeywords().contains(keyword)){
                            serverDetails.addServerKeyword(keyword);
                            insert.setValue("");
                            addButtonsToScrollPane(serverDetails);
                        }
                    });

        addRenderableWidget(add);
        addRenderableWidget(insert);
        addRenderableWidget(done);
        addRenderableWidget(reset);
    }

    private void addButtonsToScrollPane(ServerConfig.ServerDetails serverDetails){
        scrollPane.getButtons().clear();
        List<String> keywords = serverDetails.getServerKeywords();
        int i = borderOffsetFill;
        for (String keyword : keywords) {
            i = i + 20;
            Button delete = buttonBuilder(RPLog.translateAbleStrings.get("rplog.config.screen.delete"),
                    this.width / 2 + this.width / 4 - B_WIDTH / 2, i - 5, B_WIDTH, B_HEIGHT,
                    button -> {
                        if(!button.visible)return;
                        serverDetails.removeServerKeywords(keyword);
                        addButtonsToScrollPane(serverDetails);
                        Minecraft.getInstance().setScreen(new Serverscreen(previous, serverDetails));
                    });

            Button keywordBox = buttonBuilder(Component.nullToEmpty(keyword),
                    this.width / 2 - this.width / 4 - delete.getWidth()/2, i - 5, delete.getWidth(),B_HEIGHT,
                    button -> {});
            keywordBox.active=false;

            scrollPane.addButton(delete);
            scrollPane.addButton(keywordBox);
            addWidget(delete);
        }
    }

    #if MC_1_18_2 || MC_1_19_4
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
    #else
    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        #if MC_1_20_1
        this.renderBackground(guiGraphics);
        #elif MC_1_20_4
        this.renderBackground(guiGraphics,mouseX,mouseY,partialTick);
        #endif
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.fill(0, borderOffsetFill, this.width, this.height-borderOffsetFill, 0xFF222222);
        scrollPane.render(guiGraphics,mouseX,mouseY,partialTick);
        int lengthOfTitle = this.title.getContents().toString().length();
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2 - lengthOfTitle , 18, 0xffffff);
    }
    #endif

    @Override
    public void onClose(){
        this.minecraft.setScreen(previous);
    }

    #if MC_1_20_4
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        return scrollPane.mouseScrolled(mouseX,mouseY,mouseX,mouseY);
    }
    #else
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        return scrollPane.mouseScrolled(mouseX,mouseY,delta);
    }
    #endif


}
