package fireflasher.rplog.config.screens.servers;


#if MC_1_16_5
import fireflasher.rplog.*;
import com.mojang.blaze3d.vertex.PoseStack;
import fireflasher.rplog.config.json.ServerConfig;
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

        Button reset = new Button(this.width / 2 - this.width / 4 - B_WIDTH/2, 13, B_WIDTH, B_HEIGHT,
                RPLog.translateAbleStrings.get("rplog.config.serverscreen.reset_defaults"),
                button -> {
                    serverDetails.getServerKeywords().clear();
                    serverDetails.getServerKeywords().addAll(RPLog.CONFIG.getDefaultKeywords());
                    Minecraft.getInstance().setScreen(new Serverscreen(previous, serverDetails));
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
                    String keyword = insert.getValue();
                    if (!keyword.isEmpty() && !serverDetails.getServerKeywords().contains(keyword)) {
                        serverDetails.addServerKeyword(keyword);
                        insert.setValue("");
                        addButtonsToScrollPane(serverDetails);
                    }
                });

        addButton(add);
        addButton(insert);
        addButton(done);
        addButton(reset);
    }

    private void addButtonsToScrollPane(ServerConfig.ServerDetails serverDetails){
        scrollPane.getButtons().clear();
        List<String> keywords = serverDetails.getServerKeywords();
        int i = borderOffsetFill;
        for (String keyword : keywords) {
            i = i + 20;
            Button delete = new Button(this.width / 2 + this.width / 4 - B_WIDTH / 2, i - 5, B_WIDTH, B_HEIGHT,
                    RPLog.translateAbleStrings.get("rplog.config.screen.delete"),
                    button -> {
                        if(!button.visible)return;
                        serverDetails.removeServerKeywords(keyword);
                        addButtonsToScrollPane(serverDetails);
                        Minecraft.getInstance().setScreen(new Serverscreen(previous, serverDetails));
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
    public void onClose(){
        //dirty fix for not synchronized access to keylist after editing
        // TODO: needs proper fix
        //true because it could be a server that they are playing on
        //true also handles if its singleplayer or no world, false is more performant tho
        ChatLogManager.onClientConnectionStatus(true);
        this.minecraft.setScreen(previous);
    }


}


#endif