package rmc.mixins.guard_mekanism;

import java.lang.reflect.Method;

import org.bukkit.Bukkit;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import mekanism.common.entity.EntityFlame;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.world.World;

@Mixin(value = EntityFlame.class)
public abstract class EntityFlameMixin
extends Entity {

    private static Method getShooterCached;

    private Entity __getShooter__() {
        Entity shooter = null;
        try {
            if (getShooterCached == null) {
                getShooterCached = this.getClass().getMethod("func_234616_v_");
            }
            shooter = (Entity) getShooterCached.invoke(this);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return shooter;
    }

    @Inject(method = "Lmekanism/common/entity/EntityFlame;burn(Lnet/minecraft/entity/Entity;)V",
            remap = false,
            cancellable = true,
            at = @At(value = "HEAD"))
    private void burnMixin(Entity entity, CallbackInfo mixin) {
        boolean fail = true;
        Entity shooter = this.__getShooter__();
        if (shooter instanceof ServerPlayerEntity) {
            PlayerInteractEntityEvent event = new PlayerInteractEntityEvent(
                ((ServerPlayerEntity) shooter).getBukkitEntity(),
                entity.getBukkitEntity()
            );
            Bukkit.getPluginManager().callEvent(event);
            if (!event.isCancelled()) {
                fail = false;
            }
        }
        if (fail) {
            this.remove();
            mixin.cancel();
        }
    }

    private EntityFlameMixin(EntityType<?> entityTypeIn, World worldIn) {
        super (entityTypeIn, worldIn);
    }

}