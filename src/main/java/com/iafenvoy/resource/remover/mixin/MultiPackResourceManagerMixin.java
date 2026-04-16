package com.iafenvoy.resource.remover.mixin;

import com.iafenvoy.resource.remover.util.MatcherManager;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.MultiPackResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.List;

@Mixin(MultiPackResourceManager.class)
public class MultiPackResourceManagerMixin {
    @ModifyVariable(method = "<init>", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private static List<PackResources> modifyPackResources(List<PackResources> packs, @Local(argsOnly = true) PackType type) {
        MatcherManager.load();
        return packs.stream().map(pack -> MatcherManager.createWrapped(type, pack)).toList();
    }
}
