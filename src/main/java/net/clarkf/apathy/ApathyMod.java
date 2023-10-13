package net.clarkf.apathy;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

@Mod(modid = Tags.MODID, version = Tags.VERSION, name = Tags.MODNAME, acceptedMinecraftVersions = "[1.7.10]")
public class ApathyMod {

    public static final Logger LOG = LogManager.getLogger(Tags.MODID);

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
    }

    /**
     * Cancels targeting of players by mobs.
     */
    @SubscribeEvent
    public void apathy(LivingSetAttackTargetEvent event) {
        if (event.entityLiving.worldObj.isRemote) return;

        EntityLivingBase base = event.entityLiving;
        // When a player attacks an enemy, they set a special "revenge" target to that player. For whatever
        // reason, the getter is called "AITarget".
        if (base.getAITarget() instanceof EntityPlayer) {
            base.setRevengeTarget(null);
        }

        // When an enemy sees a player, they become the 'attack target'. If the aggressor has an attack target,
        // null it out.
        if (base instanceof EntityLiving living) {
            if (living.getAttackTarget() instanceof EntityPlayer) {
                living.setAttackTarget(null);
            }
        }
    }

    /**
     * A handler which cancels damage to players. While {@link #apathy(LivingSetAttackTargetEvent)} prevents mobs from
     * targeting players, mobs like slimes move randomly and cause damage if they intersect with anything.
     */
    @SubscribeEvent
    public void ignoreDamage(LivingHurtEvent event) {
        if (event.entityLiving.worldObj.isRemote) return;
        if (!(event.entityLiving instanceof EntityPlayer)) return;

        if (event.source.getDamageType()
            .equals("mob")) {
            event.setCanceled(true);
        }
    }
}
