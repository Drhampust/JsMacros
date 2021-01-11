package xyz.wagyourtail.jsmacros.client.gui.elements.overlays;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import xyz.wagyourtail.jsmacros.client.gui.elements.TextInput;
import xyz.wagyourtail.jsmacros.client.gui.elements.Button;

import java.util.function.Consumer;

public class TextPrompt extends OverlayContainer {
    private final Text message;
    private final Consumer<String> accept;
    private final Consumer<Element> setFocused;
    private TextInput ti;
    private final String defText;

    public TextPrompt(int x, int y, int width, int height, TextRenderer textRenderer, Text message, String defaultText, Consumer<AbstractButtonWidget> addButton, Consumer<AbstractButtonWidget> removeButton, Consumer<OverlayContainer> close, Consumer<Element> setFocused, Consumer<String> accept) {
        super(x, y, width, height, textRenderer, addButton, removeButton, close);
        this.message = message;
        this.accept = accept;
        this.setFocused = setFocused;
        this.defText = defaultText == null ? "" : defaultText;
    }

    public void init() {
        super.init();
        int w = width - 4;

        ti = (TextInput) this.addButton(new TextInput(x + 3, y + 25, w - 2, 14, textRenderer, 0xFF101010, 0, 0xFF4040FF, 0xFFFFFF, defText, null, null));
        
        this.addButton(new Button(x + 2, y + height - 14, w / 2, 12, textRenderer, 0, 0, 0x7FFFFFFF, 0xFFFFFF, new TranslatableText("gui.cancel"), (btn) -> close()));

        this.addButton(new Button(x + w / 2, y + height - 14, w / 2, 12, textRenderer, 0, 0, 0x7FFFFFFF, 0xFFFFFF, new TranslatableText("jsmacros.confirm"), (btn) -> {
            if (this.accept != null) this.accept.accept(ti.content);
            close();
        }));
        
        if (setFocused != null) setFocused.accept(ti);
        ti.setSelected(true);
    }

    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        DrawableHelper.drawCenteredText(matrices, textRenderer, message, x + width / 2, y + 5, 0xFFFFFF);
        super.render(matrices, mouseX, mouseY, delta);
    }

}
