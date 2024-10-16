package fireflasher.rplog.fabric.config.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
#if MC_1_20_4
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

    #if MC_1_18_2 || MC_1_19_2
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

    #elif MC_1_20_4
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        int buttonY = 30; // Starting Y position for buttons
        int oldY = buttonY;

        // Render buttons with scroll offset
        for (Button button : buttons) {
            if(button.getY() != oldY) buttonY += button.getHeight()+5; // Increment Y for next button
            oldY = button.getY();
            button.setY(buttonY - scrollOffset); // Adjust position based on scroll offset
            // Determine visibility based on the button's Y position
            button.visible = button.getY() > 50 && button.getY() < height - 60;

            // Render the button if it is visible
            if (button.visible) {
                button.render(guiGraphics, mouseX, mouseY, delta);
            }
        }
    }

    #endif

    #if MC_1_18_2 || MC_1_19_2
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        scrollOffset += delta * 10; // Adjust scroll speed
    #elif MC_1_20_4
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        scrollOffset += scrollY * 10; // Adjust scroll speed
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