package com.hughbone.scaffoldforever.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(Block.class)
public class BlockMixin {

    @Inject(method = "onBroken", at = @At("HEAD"))
    private void onBroken(WorldAccess world, BlockPos pos, BlockState state, CallbackInfo ci) {

        if (state.getBlock().equals(Blocks.SCAFFOLDING)) {

            BlockPos yplus = new BlockPos(pos.getX(), pos.getY() + 1, pos.getZ());
            BlockPos xplus = new BlockPos(pos.getX() + 1, pos.getY(), pos.getZ());
            BlockPos xneg = new BlockPos(pos.getX() - 1, pos.getY(), pos.getZ());
            BlockPos zplus = new BlockPos(pos.getX(), pos.getY(), pos.getZ() + 1);
            BlockPos zneg = new BlockPos(pos.getX(), pos.getY(), pos.getZ() - 1);

            BlockPos[] blockPosList = {xplus, xneg, yplus, zplus, zneg};

            for (BlockPos bp : blockPosList) {
                List<BlockPos> calledAlready = new ArrayList<>();
                if (world.getBlockState(bp).getBlock().equals(Blocks.SCAFFOLDING)) {
                    if (!isConnectedToGround(bp, world, calledAlready)) {
                        calledAlready.clear();
                        breakScaffoldChain(bp, world, calledAlready, bp);
                    }
                }
            }
        }

    }

    private void breakScaffoldChain(BlockPos pos, WorldAccess world, List<BlockPos> calledAlready, BlockPos defaultBlockPos) {
        if (calledAlready.contains(pos)) {
            return;
        }
        calledAlready.add(pos);
        world.breakBlock(pos, false);

        ItemEntity itemEntity = new ItemEntity(EntityType.ITEM, (World) world);
        itemEntity.setStack(Items.SCAFFOLDING.getDefaultStack());
        itemEntity.getStack().setCount(1);
        itemEntity.setPosition(defaultBlockPos.getX(), defaultBlockPos.getY(), defaultBlockPos.getZ());
        world.spawnEntity(itemEntity);

        BlockPos xplus = new BlockPos(pos.getX() + 1, pos.getY(), pos.getZ());
        BlockPos xneg = new BlockPos(pos.getX() - 1, pos.getY(), pos.getZ());
        BlockPos yplus = new BlockPos(pos.getX(), pos.getY() + 1, pos.getZ());
        BlockPos zplus = new BlockPos(pos.getX(), pos.getY(), pos.getZ() + 1);
        BlockPos zneg = new BlockPos(pos.getX(), pos.getY(), pos.getZ() - 1);

        BlockPos[] blockPosList = {xplus, xneg, yplus, zplus, zneg};
        List<BlockPos> adjascentList = new ArrayList<>();

        for (BlockPos bp : blockPosList) {
            if (world.getBlockState(bp).getBlock().equals(Blocks.SCAFFOLDING)) {
                adjascentList.add(bp);
            }
        }

        for (BlockPos bp : adjascentList) {
            breakScaffoldChain(bp, world, calledAlready, defaultBlockPos);
        }
    }

    private boolean isConnectedToGround(BlockPos pos, WorldAccess world, List<BlockPos> calledAlready) {

        if (calledAlready.contains(pos)) {
            return false;
        }
        calledAlready.add(pos);

        // Return true if connected to ground
        BlockPos yminus = new BlockPos(pos.getX(), pos.getY() - 1, pos.getZ());
        Block belowScaffold = world.getBlockState(yminus).getBlock();
        if (!belowScaffold.equals(Blocks.AIR) && !belowScaffold.equals(Blocks.SCAFFOLDING)) {
            return true;
        }

        BlockPos xplus = new BlockPos(pos.getX() + 1, pos.getY(), pos.getZ());
        BlockPos xneg = new BlockPos(pos.getX() - 1, pos.getY(), pos.getZ());
        BlockPos zplus = new BlockPos(pos.getX(), pos.getY(), pos.getZ() + 1);
        BlockPos zneg = new BlockPos(pos.getX(), pos.getY(), pos.getZ() - 1);

        BlockPos[] blockPosList = {xplus, xneg, yminus, zplus, zneg};
        List<BlockPos> adjascentList = new ArrayList<>();

        for (BlockPos bp : blockPosList) {
            if (world.getBlockState(bp).getBlock().equals(Blocks.SCAFFOLDING)) {
                adjascentList.add(bp);
            }
        }

        for (BlockPos bp : adjascentList) {
			if (isConnectedToGround(bp, world, calledAlready)) {
				return true;
			}
        }

        return false;
    }

}
