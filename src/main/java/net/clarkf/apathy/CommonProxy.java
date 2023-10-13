package net.clarkf.apathy;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class CommonProxy {

    // preInit "Run before anything else. Read your config, create blocks, items, etc, and register them with the
    // GameRegistry." (Remove if not needed)
    public void preInit(FMLPreInitializationEvent event) {
        Config.synchronizeConfiguration(event.getSuggestedConfigurationFile());

        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void ignoreDamage(LivingHurtEvent e) {
        if (e.entityLiving.worldObj.isRemote) return;
        if (!(e.entityLiving instanceof EntityPlayer)) return;

        if (e.source.getDamageType()
            .equals("mob")) {
            e.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void apathy(LivingSetAttackTargetEvent event) {
        EntityLivingBase base = event.entityLiving;
        if (base.getAITarget() instanceof EntityPlayer) {
            base.setRevengeTarget(null);
        }

        if (base instanceof EntityLiving living) {
            if (living.getAttackTarget() instanceof EntityPlayer) {
                living.setAttackTarget(null);
            }
        }
    }
}
