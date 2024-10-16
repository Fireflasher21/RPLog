package fireflasher.rplog.config;

import net.minecraft.client.gui.components.Button;

import java.util.ArrayList;
import java.util.List;

#if MC_1_18_2 || MC_1_19_4
import com.mojang.blaze3d.vertex.PoseStack;
#elif  MC_1_20_1 || MC_1_20_4 || MC_1_20_6
import net.minecraft.client.gui.GuiGraphics;
#endif

public class ScrollPane {
    private final List<Button> buttons = new ArrayList<>();
    private int scrollOffset = 0;
    private final int width;
    private final int height;
    private final int buttonHeight ;
    private final int maxTop;

    public ScrollPane(int width, int height, int buttonHeight, int maxTop) {
        this.width = width;
        this.height = height;
        this.buttonHeight = buttonHeight;
        this.maxTop = maxTop;
    }

    public void addButton(Button button) {
        buttons.add(button);
    }
    public List<Button> getButtons(){return buttons;}

    #if MC_1_18_2
    public void render(PoseStack poseStack, int mouseX, int mouseY, float delta) {
        int buttonY = maxTop; // Starting Y position for buttons

        // Render buttons with scroll offset
        for (int i = 0; i < buttons.size(); i++) {
            Button button = buttons.get(i);

            button.y = buttonY - scrollOffset; // Adjust position based on scroll offset
            // Determine visibility based on the button's Y position
            button.visible = button.y >= maxTop && button.y <= height - maxTop - button.getHeight();

            // Render the button if it is visible
            if (button.visible) {button.render(poseStack, mouseX, mouseY, delta);}

            if((i+1)%2==0)buttonY += button.getHeight()+5; // Increment Y for next button
        }
    }

    #elif MC_1_19_4 || MC_1_20_1 || MC_1_20_4 || MC_1_20_6
    #if MC_1_19_4
    public void render(PoseStack poseStack, int mouseX, int mouseY, float delta) {
    #else
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {// Starting Y position for buttons
    #endif
        int buttonY = maxTop;
        // Render buttons with scroll offset
        for (int i = 0; i < buttons.size(); i++) {
            Button button = buttons.get(i);

            button.setY(buttonY - scrollOffset); // Adjust position based on scroll offset
            // Determine visibility based on the button's Y position
            button.visible = button.getY() >= maxTop && button.getY() <= height - maxTop - button.getHeight();

            // Render the button if it is visible
            #if MC_1_19_4
            if (button.visible) {button.render(poseStack, mouseX, mouseY, delta);}
            #else
            if (button.visible) {button.render(guiGraphics, mouseX, mouseY, delta);}
            #endif
            if((i+1)%2==0)buttonY += button.getHeight()+5; // Increment Y for next button
        }
    }

    #endif

    #if MC_1_18_2 || MC_1_19_4 || MC_1_20_1
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        scrollOffset += (int) (delta * 10); // Adjust scroll speed
    #elif MC_1_20_4 || MC_1_20_6
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        scrollOffset += (int) (scrollY * 10); // Adjust scroll speed
    #endif
        // Prevent scrolling above the first button
        scrollOffset = Math.max(scrollOffset, 0);

        // Prevent scrolling below the last button
        // every second button render: buttons/2 + the last button offset * height of window - border
        int maxScroll = Math.max(0,  buttons.size()/2 * (buttonHeight+5) - height + maxTop*2);
        if (scrollOffset > maxScroll) {
            scrollOffset = maxScroll;
        }
        return true;
    }
}
