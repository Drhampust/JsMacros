package xyz.wagyourtail.jsmacros.client.api.helpers;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.CloudRenderMode;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.options.GraphicsMode;
import net.minecraft.client.util.Window;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Arm;
import org.lwjgl.glfw.GLFW;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Wagyourtail
 * @since 1.1.7
 */
@SuppressWarnings("unused")
public class OptionsHelper extends BaseHelper<GameOptions> {
    private static final Map<String, SoundCategory> SOUND_CATEGORY_MAP = Arrays.stream(SoundCategory.values()).collect(Collectors.toMap(SoundCategory::getName, Function.identity()));
    private MinecraftClient mc = MinecraftClient.getInstance();
    private ResourcePackManager rpm = mc.getResourcePackManager();
    
    public OptionsHelper(GameOptions options) {
        super(options);
    }
    /**
     * @since 1.1.7
     * @return 0: off, 2: fancy
     */
    public int getCloudMode() {
        switch (base.getCloudRenderMode()) {
            case FANCY:
                return 2;
            case FAST:
                return 1;
            default:
                return 0;
        }
    }
    /**
     * @since 1.1.7
     * @param mode 0: off, 2: fancy 
     * @return
     */
    public OptionsHelper setCloudMode(int mode) {
        switch(mode) {
            case 2:
                base.cloudRenderMode = CloudRenderMode.FANCY;
                return this;
            case 1:
                base.cloudRenderMode = CloudRenderMode.FAST;
                return this;
            default:
                base.cloudRenderMode = CloudRenderMode.OFF;
                return this;
        }
    }
    /**
     * @since 1.1.7
     * @return
     */
    public int getGraphicsMode() {
        switch (base.graphicsMode) {
            case FABULOUS:
                return 2;
            case FANCY:
                return 1;
            default:
                return 0;
        }
    }
    /**
     * @since 1.1.7
     * @param mode 0: fast, 2: fabulous
     * @return
     */
    public OptionsHelper setGraphicsMode(int mode) {
        switch(mode) {
            case 2:
                base.graphicsMode = GraphicsMode.FABULOUS;
                return this;
            case 1:
                base.graphicsMode = GraphicsMode.FANCY;
                return this;
            default:
                base.graphicsMode = GraphicsMode.FAST;
                return this;
        }
    }
    /**
     * @since 1.1.7
     * @return list of names of resource packs.
     */
    public List<String> getResourcePacks() {
        return new ArrayList<String>(rpm.getNames());
    }
    
    /**
     * @since 1.2.0
     * @return list of names of enabled resource packs.
     */
    public List<String> getEnabledResourcePacks() {
        return new ArrayList<String>(rpm.getEnabledNames());
    }
    
    /**
     * Set the enabled resource packs to the provided list.
     * 
     * @since 1.2.0
     * @param enabled
     * @return
     */
    public OptionsHelper setEnabledResourcePacks(String[] enabled) {
        Collection<String> en = new ArrayList<String>(Arrays.asList(enabled).stream().distinct().collect(Collectors.toList()));
        List<String> currentRP = ImmutableList.copyOf(base.resourcePacks);
        rpm.setEnabledProfiles(en);
        base.resourcePacks.clear();
        base.incompatibleResourcePacks.clear();
        for (ResourcePackProfile p : rpm.getEnabledProfiles()) {
            if (!p.isPinned()) {
                base.resourcePacks.add(p.getName());
                if (!p.getCompatibility().isCompatible()) {
                    base.incompatibleResourcePacks.add(p.getName());
                }
            }
        }
        base.write();
        List<String> newRP = ImmutableList.copyOf(base.resourcePacks);
        if (!currentRP.equals(newRP)) {
            mc.reloadResources();
        }
        return this;
    }
    
    /**
     * @since 1.1.7
     * @return
     */
    public boolean isRightHanded() {
        return base.mainArm == Arm.RIGHT;
    }
    
    /**
     * @since 1.1.7
     * @param val
     */
    public void setRightHanded(boolean val) {
        if (val) {
            base.mainArm = Arm.RIGHT;
        } else {
            base.mainArm = Arm.LEFT;
        }
    }
    
    /**
     * @since 1.1.7
     * @return
     */
    public double getFov() {
        return base.fov;
    }
    
    /**
     * @since 1.1.7
     * @param fov
     * @return
     */
    public OptionsHelper setFov(double fov) {
        base.fov = fov;
        return this;
    }
    
    /**
     * @since 1.1.7
     * @return
     */
    public int getRenderDistance() {
        return base.viewDistance;
    }
    
    /**
     * @since 1.1.7
     * @param d
     */
    public void setRenderDistance(int d) {
        base.viewDistance = d;
    }
    
    /**
     * @since 1.2.6
     * @return
     */
    public int getWidth() {
        return mc.getWindow().getWidth();
    }
    
    /**
     * @since 1.2.6
     * @return
     */
    public int getHeight() {
        return mc.getWindow().getHeight();
    }
    
    /**
     * @since 1.2.6
     * @param w
     */
    public void setWidth(int w) {
        Window win = mc.getWindow();
        GLFW.glfwSetWindowSize(win.getHandle(), w, win.getHeight());
    }
    
    /**
     * @since 1.2.6
     * @param h
     */
    public void setHeight(int h) {
        Window win = mc.getWindow();
        GLFW.glfwSetWindowSize(win.getHandle(), win.getWidth(), h);
    }
    
    /**
     * @since 1.2.6
     * @param w
     * @param h
     */
    public void setSize(int w, int h) {
        Window win = mc.getWindow();
        GLFW.glfwSetWindowSize(win.getHandle(), w, h);
    }
    
    /**
     * @since 1.3.0
     * normal values for gamam are between {@code 0} and {@code 1}
     */
    public double getGamma() {
        return base.gamma;
    }
    
    /**
     * @since 1.3.0
     * normal values for gamma are between {@code 0} and {@code 1}
     */
    public void setGamma(double gamma) {
        base.gamma = gamma;
    }
    
    /**
     * @since 1.3.1
     * @param vol
     */
    public void setVolume(double vol) {
        base.setSoundVolume(SoundCategory.MASTER, (float) vol);
    }
    
    /**
     * set volume by category.
     *
     * @since 1.3.1
     * @param category
     * @param volume
     */
    public void setVolume(String category, double volume) {
        base.setSoundVolume(SOUND_CATEGORY_MAP.get(category), (float) volume);
    }
    
    /**
     * @Since 1.3.1
     * @return
     */
    public Map<String, Float> getVolumes() {
        Map<String, Float> volumes = new HashMap<>();
        for (SoundCategory category : SoundCategory.values()) {
            volumes.put(category.getName(), base.getSoundVolume(category));
        }
        return volumes;
    }
    
    /**
     * @param category
     * @since 1.3.1
     * @return
     */
    public float getVolume(String category) {
        return base.getSoundVolume(SOUND_CATEGORY_MAP.get(category));
    }
}
