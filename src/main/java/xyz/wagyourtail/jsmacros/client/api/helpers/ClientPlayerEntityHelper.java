package xyz.wagyourtail.jsmacros.client.api.helpers;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import xyz.wagyourtail.jsmacros.client.access.IMinecraftClient;
import xyz.wagyourtail.jsmacros.client.api.sharedclasses.PositionCommon;
import xyz.wagyourtail.jsmacros.core.Core;

import java.util.concurrent.Semaphore;

/**
 * @author Wagyourtail
 * @see xyz.wagyourtail.jsmacros.client.api.helpers.PlayerEntityHelper
 * @since 1.0.3
 */
@SuppressWarnings("unused")
public class ClientPlayerEntityHelper<T extends EntityPlayerSP> extends PlayerEntityHelper<T> {
    protected final Minecraft mc = Minecraft.getMinecraft();

    public ClientPlayerEntityHelper(T e) {
        super(e);
    }

    /**
     * @param yaw   (was pitch prior to 1.2.6)
     * @param pitch (was yaw prior to 1.2.6)
     * @return
     * @since 1.0.3
     */
    public ClientPlayerEntityHelper<T> lookAt(double yaw, double pitch) {
        pitch = MathHelper.clamp_double(pitch, -90.0D, 90.0D);
        base.prevRotationPitch = base.rotationPitch;
        base.prevRotationYaw = base.rotationYaw;
        base.rotationPitch = (float) pitch;
        base.rotationYaw = (float) MathHelper.wrapAngleTo180_double(yaw);
        return this;
    }

    /**
     * look at the specified coordinates.
     *
     * @param x
     * @param y
     * @param z
     * @return
     * @since 1.2.8
     */
    public ClientPlayerEntityHelper<T> lookAt(double x, double y, double z) {
        PositionCommon.Vec3D vec = new PositionCommon.Vec3D(base.posX, base.posY + base.getEyeHeight(), base.posZ, x, y, z);
        lookAt(vec.getYaw(), vec.getPitch());
        return this;
    }

    /**
     * @param entity
     * @since 1.5.0
     */
    public ClientPlayerEntityHelper<T> attack(EntityHelper<?> entity) throws InterruptedException {
        return attack(entity, false);
    }

    /**
     * @since 1.6.0
     *
     * @param await
     * @param entity
     */
    public ClientPlayerEntityHelper<T> attack(EntityHelper<?> entity, boolean await) throws InterruptedException {
        boolean joinedMain = Minecraft.getMinecraft().isCallingFromMinecraftThread() || Core.instance.profile.joinedThreadStack.contains(Thread.currentThread());
        assert mc.playerController != null;
        if (entity.getRaw() == mc.thePlayer) throw new AssertionError("Can't interact with self!");
        if (joinedMain) {
            assert mc.thePlayer != null;
            mc.thePlayer.swingItem();
            mc.playerController.attackEntity(mc.thePlayer, entity.getRaw());
        } else {
            Semaphore wait = new Semaphore(await ? 0 : 1);
            mc.addScheduledTask(() -> {
                assert mc.thePlayer != null;
                mc.thePlayer.swingItem();
                mc.playerController.attackEntity(mc.thePlayer, entity.getRaw());
                wait.release();
            });
            wait.acquire();
        }
        return this;
    }
    /**
     * @param x
     * @param y
     * @param z
     * @param direction 0-5 in order: [DOWN, UP, NORTH, SOUTH, WEST, EAST];
     * @since 1.5.0
     */

    public ClientPlayerEntityHelper<T> attack(int x, int y, int z, int direction) throws InterruptedException {
        return attack(x, y, z, direction, false);
    }

    /**
     * @since 1.6.0
     *
     * @param x
     * @param y
     * @param z
     * @param direction 0-5 in order: [DOWN, UP, NORTH, SOUTH, WEST, EAST];
     * @param await
     *
     * @throws InterruptedException
     */
    public ClientPlayerEntityHelper<T> attack(int x, int y, int z, int direction, boolean await) throws InterruptedException {
        assert mc.playerController != null;
        boolean joinedMain = Minecraft.getMinecraft().isCallingFromMinecraftThread() || Core.instance.profile.joinedThreadStack.contains(Thread.currentThread());
        if (joinedMain) {
            assert mc.thePlayer != null;
            mc.thePlayer.swingItem();
            mc.playerController.clickBlock(new BlockPos(x, y, z), EnumFacing.values()[direction]);
        } else {
            Semaphore wait = new Semaphore(await ? 0 : 1);
            mc.addScheduledTask(() -> {
                assert mc.thePlayer != null;
                mc.thePlayer.swingItem();
                mc.playerController.clickBlock(new BlockPos(x, y, z), EnumFacing.values()[direction]);
                wait.release();
            });
            wait.acquire();
        }
        return this;
    }

    /**
     * @param entity
     * @param offHand
     * @since 1.5.0, renamed from {@code interact} in 1.6.0
     */
    public ClientPlayerEntityHelper<T> interactEntity(EntityHelper<?> entity, boolean offHand) throws InterruptedException {
        return interactEntity(entity, offHand, false);
    }

    /**
     * @param entity
     * @param offHand
     * @param await
     * @since 1.6.0
     * @throws InterruptedException
     */
    public ClientPlayerEntityHelper<T> interactEntity(EntityHelper<?> entity, boolean offHand, boolean await) throws InterruptedException {
        assert mc.playerController != null;
        if (entity.getRaw() == mc.thePlayer) throw new AssertionError("Can't interact with self!");
        boolean joinedMain = Minecraft.getMinecraft().isCallingFromMinecraftThread() || Core.instance.profile.joinedThreadStack.contains(Thread.currentThread());
        if (joinedMain) {
            boolean result = mc.playerController.func_178894_a(mc.thePlayer, entity.getRaw(), mc.objectMouseOver) ||
                mc.playerController.interactWithEntitySendPacket(mc.thePlayer, entity.getRaw());
            assert mc.thePlayer != null;
            if (!result) {
                ItemStack itemstack1 = mc.thePlayer.inventory.getCurrentItem();

                boolean result2 = !net.minecraftforge.event.ForgeEventFactory.onPlayerInteract(mc.thePlayer, net.minecraftforge.event.entity.player.PlayerInteractEvent.Action.RIGHT_CLICK_AIR, mc.theWorld, null, null).isCanceled();
                if (result2 && itemstack1 != null && mc.playerController.sendUseItem(mc.thePlayer, mc.theWorld, itemstack1))
                {
                    mc.entityRenderer.itemRenderer.resetEquippedProgress2();
                }
            }
        } else {
            Semaphore wait = new Semaphore(await ? 0 : 1);
            mc.addScheduledTask(() -> {
                boolean result = mc.playerController.func_178894_a(mc.thePlayer, entity.getRaw(), mc.objectMouseOver) ||
                    mc.playerController.interactWithEntitySendPacket(mc.thePlayer, entity.getRaw());
                assert mc.thePlayer != null;
                if (!result) {
                    ItemStack itemstack1 = mc.thePlayer.inventory.getCurrentItem();

                    boolean result2 = !net.minecraftforge.event.ForgeEventFactory.onPlayerInteract(mc.thePlayer, net.minecraftforge.event.entity.player.PlayerInteractEvent.Action.RIGHT_CLICK_AIR, mc.theWorld, null, null).isCanceled();
                    if (result2 && itemstack1 != null && mc.playerController.sendUseItem(mc.thePlayer, mc.theWorld, itemstack1))
                    {
                        mc.entityRenderer.itemRenderer.resetEquippedProgress2();
                    }
                }
                wait.release();
            });
            wait.acquire();
        }
        return this;
    }

    /**
     * @param offHand
     * @since 1.5.0, renamed from {@code interact} in 1.6.0
     */
    public ClientPlayerEntityHelper<T> interactItem(boolean offHand) throws InterruptedException {
        return interactItem(offHand, false);
    }

    /**
     * @since 1.6.0
     * @param offHand
     * @param await
     */
    public ClientPlayerEntityHelper<T> interactItem(boolean offHand, boolean await) throws InterruptedException {
        assert mc.playerController != null;
        boolean joinedMain = Minecraft.getMinecraft().isCallingFromMinecraftThread() || Core.instance.profile.joinedThreadStack.contains(Thread.currentThread());
        if (joinedMain) {
            ItemStack itemstack1 = mc.thePlayer.inventory.getCurrentItem();

            boolean result = !net.minecraftforge.event.ForgeEventFactory.onPlayerInteract(mc.thePlayer, net.minecraftforge.event.entity.player.PlayerInteractEvent.Action.RIGHT_CLICK_AIR, mc.theWorld, null, null).isCanceled();
            if (result && itemstack1 != null && mc.playerController.sendUseItem(mc.thePlayer, mc.theWorld, itemstack1))
            {
                mc.entityRenderer.itemRenderer.resetEquippedProgress2();
            }
        } else {
            Semaphore wait = new Semaphore(await ? 0 : 1);
            mc.addScheduledTask(() -> {
                ItemStack itemstack1 = mc.thePlayer.inventory.getCurrentItem();

                boolean result = !net.minecraftforge.event.ForgeEventFactory.onPlayerInteract(mc.thePlayer, net.minecraftforge.event.entity.player.PlayerInteractEvent.Action.RIGHT_CLICK_AIR, mc.theWorld, null, null).isCanceled();
                if (result && itemstack1 != null && mc.playerController.sendUseItem(mc.thePlayer, mc.theWorld, itemstack1))
                {
                    mc.entityRenderer.itemRenderer.resetEquippedProgress2();
                }
                wait.release();
            });
            wait.acquire();
        }
        return this;
    }

    /**
     * @param x
     * @param y
     * @param z
     * @param direction
     * @param offHand
     * @since 1.5.0, renamed from {@code interact} in 1.6.0
     */
    public ClientPlayerEntityHelper<T> interactBlock(int x, int y, int z, int direction, boolean offHand) throws InterruptedException {
        return interactBlock(x, y, z, direction, offHand, false);
    }

    public ClientPlayerEntityHelper<T> interactBlock(int x, int y, int z, int direction, boolean offHand, boolean await) throws InterruptedException {
        assert mc.playerController != null;
        boolean joinedMain = Minecraft.getMinecraft().isCallingFromMinecraftThread() || Core.instance.profile.joinedThreadStack.contains(Thread.currentThread());
        if (joinedMain) {
            BlockPos blockpos = new BlockPos(x, y, z);
            if (!mc.theWorld.isAirBlock(blockpos)) {
                ItemStack itemstack = mc.thePlayer.getHeldItem();
                int i = itemstack != null ? itemstack.stackSize : 0;
                if (mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, itemstack, blockpos, EnumFacing.values()[direction], new Vec3(x, y, z))) {
                    mc.thePlayer.swingItem();
                }
                if (itemstack == null) {
                    return this;
                }
                if (itemstack.stackSize == 0) {
                    mc.thePlayer.inventory.mainInventory[mc.thePlayer.inventory.currentItem] = null;
                } else if (itemstack.stackSize != i || mc.playerController.isInCreativeMode()) {
                    mc.entityRenderer.itemRenderer.resetEquippedProgress();
                }
            }
        } else {
            Semaphore wait = new Semaphore(await ? 0 : 1);
            mc.addScheduledTask(() -> {
                BlockPos blockpos = new BlockPos(x, y, z);
                if (!mc.theWorld.isAirBlock(blockpos)) {
                    ItemStack itemstack = mc.thePlayer.getHeldItem();
                    int i = itemstack != null ? itemstack.stackSize : 0;
                    if (mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, itemstack, blockpos, EnumFacing.values()[direction], new Vec3(x, y, z))) {
                        mc.thePlayer.swingItem();
                    }
                    if (itemstack == null) {
                        wait.release();
                        return;
                    }
                    if (itemstack.stackSize == 0) {
                        mc.thePlayer.inventory.mainInventory[mc.thePlayer.inventory.currentItem] = null;
                    } else if (itemstack.stackSize != i || mc.playerController.isInCreativeMode()) {
                        mc.entityRenderer.itemRenderer.resetEquippedProgress();
                    }
                }
                wait.release();
            });
            wait.acquire();
        }
        return this;
    }

    /**
     * @since 1.5.0
     */
    public ClientPlayerEntityHelper<T> interact() throws InterruptedException {
        return interact(false);
    }

    /**
     * @since 1.6.0
     * @param await
     */
    public ClientPlayerEntityHelper<T> interact(boolean await) throws InterruptedException {
        boolean joinedMain = Minecraft.getMinecraft().isCallingFromMinecraftThread() || Core.instance.profile.joinedThreadStack.contains(Thread.currentThread());
        if (joinedMain) {
            ((IMinecraftClient) mc).jsmacros_doItemUse();
        } else {
            Semaphore wait = new Semaphore(await ? 0 : 1);
            mc.addScheduledTask(() -> {
                ((IMinecraftClient) mc).jsmacros_doItemUse();
                wait.release();
            });
            wait.acquire();
        }
        return this;
    }

    /**
     * @since 1.5.0
     */
    public ClientPlayerEntityHelper<T> attack() throws InterruptedException {
        return attack(false);
    }

    /**
     * @since 1.6.0
     * @param await
     */
    public ClientPlayerEntityHelper<T> attack(boolean await) throws InterruptedException {
        boolean joinedMain = Minecraft.getMinecraft().isCallingFromMinecraftThread() || Core.instance.profile.joinedThreadStack.contains(Thread.currentThread());
        if (joinedMain) {
            ((IMinecraftClient) mc).jsmacros_doAttack();
        } else {
            Semaphore wait = new Semaphore(await ? 0 : 1);
            mc.addScheduledTask(() -> {
                ((IMinecraftClient) mc).jsmacros_doAttack();
                wait.release();
            });
            wait.acquire();
        }
        return this;
    }

    /**
     * @return
     * @since 1.1.2
     */
    public int getFoodLevel() {
        return base.getFoodStats().getFoodLevel();
    }


    public String toString() {
        return "Client" + super.toString();
    }
}
