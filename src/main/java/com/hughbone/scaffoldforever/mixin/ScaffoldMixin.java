package com.hughbone.scaffoldforever.mixin;

import net.minecraft.block.*;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;


@Mixin(ScaffoldingBlock.class)
public abstract class ScaffoldMixin extends Block {

    public ScaffoldMixin(Settings settings) {
        super(settings);
    }

    @Inject(method = "scheduledTick", at = @At("HEAD"), cancellable = true)
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo ci) {
        ci.cancel();
    }

}
