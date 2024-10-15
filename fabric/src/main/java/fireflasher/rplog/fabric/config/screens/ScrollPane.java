package fireflasher.rplog.fabric.config.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

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
    public void render(PoseStack poseStack, int mouseX, int mouseY, float delta) {
        int buttonY = 30; // Starting Y position for buttons
        int oldY = buttonY;

        // Render buttons with scroll offset
        for (Button button : buttons) {
            if(button.y != oldY) buttonY += button.getHeight()+5; // Increment Y for next button
            oldY = button.y;
            button.y = buttonY - scrollOffset; // Adjust position based on scroll offset
            // Determine visibility based on the button's Y position
            button.visible = button.y > 50 && button.y < height - 60;

            // Render the button if it is visible
            if (button.visible) {
                button.render(poseStack, mouseX, mouseY, delta);
            }
        }
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        scrollOffset += delta * 10; // Adjust scroll speed
        // Prevent scrolling above the first button
        if (scrollOffset < maxTop) {
            scrollOffset = maxTop;
        }
        // Prevent scrolling below the last button
        int maxScroll = Math.max(0,  buttons.size() * (buttonHeight+5) - this.height - 30 );
        if (scrollOffset > maxScroll) {
            scrollOffset = maxScroll;
        }
        return true;
    }
}
