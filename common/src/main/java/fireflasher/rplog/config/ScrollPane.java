package fireflasher.rplog.config;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;

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


    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {// Starting Y position for buttons
        int buttonY = maxTop;
        // Render buttons with scroll offset
        for (int i = 0; i < buttons.size(); i++) {
            Button button = buttons.get(i);

            button.setY(buttonY - scrollOffset);// Adjust position based on scroll offset
            // Determine visibility based on the button's Y position
            int yOffButton = button.getY();
            button.visible = yOffButton >= maxTop && yOffButton <= height - maxTop - button.getHeight();

            // Render the button if it is visible
            if (button.visible) {button.render(guiGraphics, mouseX, mouseY, delta);}
            if((i+1)%2==0)buttonY += button.getHeight()+5; // Increment Y for next button
        }
    }
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        scrollOffset += (int) (scrollY * 10); // Adjust scroll speed
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

