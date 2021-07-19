package xyz.wagyourtail.jsmacros.client.access;


import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

public interface IBeaconScreen {

    int getLevel();

    boolean sendBeaconPacket();

    Potion jsmacros_getPrimaryEffect();

    void jsmacros_setPrimaryEffect(Potion effect);

    Potion jsmacros_getSecondaryEffect();

    void jsmacros_setSecondaryEffect(Potion effect);
}
