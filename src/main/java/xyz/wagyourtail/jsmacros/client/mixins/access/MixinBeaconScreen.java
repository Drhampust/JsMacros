package xyz.wagyourtail.jsmacros.client.mixins.access;

import io.netty.buffer.Unpooled;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiBeacon;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.potion.Potion;
import net.minecraft.tileentity.TileEntityBeacon;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import xyz.wagyourtail.jsmacros.client.access.IBeaconScreen;

import java.util.Arrays;

@Mixin(GuiBeacon.class)
public abstract class MixinBeaconScreen extends GuiContainer implements IBeaconScreen {

    @Shadow private IInventory tileBeacon;

    @Override
    public Potion jsmacros_getPrimaryEffect() {
        int id = this.tileBeacon.getField(1);
        return Arrays.stream(TileEntityBeacon.effectsList).flatMap(Arrays::stream).filter(e -> e.id == id).findFirst().orElse(null);
    }

    @Override
    public void jsmacros_setPrimaryEffect(Potion effect) {
        this.tileBeacon.setField(1, effect.id);
    }

    @Override
    public Potion jsmacros_getSecondaryEffect() {
        int id = this.tileBeacon.getField(2);
        return Arrays.stream(TileEntityBeacon.effectsList).flatMap(Arrays::stream).filter(e -> e.id == id).findFirst().orElse(null);
    }

    @Override
    public void jsmacros_setSecondaryEffect(Potion effect) {
        this.tileBeacon.setField(2, effect.id);
    }

    @Override
    public int getLevel() {
        return this.tileBeacon.getField(0);
    }

    @Override
    public boolean sendBeaconPacket() {
        if (this.tileBeacon.getStackInSlot(0) != null && tileBeacon.getField(1) > 0) {
            String s = "MC|Beacon";
            PacketBuffer packetbuffer = new PacketBuffer(Unpooled.buffer());
            packetbuffer.writeInt(this.tileBeacon.getField(1));
            packetbuffer.writeInt(this.tileBeacon.getField(2));
            this.mc.getNetHandler().addToSendQueue(new C17PacketCustomPayload(s, packetbuffer));
            this.mc.displayGuiScreen((GuiScreen)null);
            return true;
        }
        return false;
    }

    //IGNORE
    public MixinBeaconScreen(Container inventorySlotsIn) {
        super(inventorySlotsIn);
    }
}
