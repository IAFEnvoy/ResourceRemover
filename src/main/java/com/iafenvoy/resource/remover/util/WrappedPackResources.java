package com.iafenvoy.resource.remover.util;

import com.iafenvoy.resource.remover.ResourceRemover;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
//? >=1.21.4 {
import net.minecraft.server.packs.metadata.MetadataSectionType;
//?} else {
/*import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
*///?}
//? >=1.20.5 {
import net.minecraft.server.packs.PackLocationInfo;
//?}
import net.minecraft.server.packs.resources.IoSupplier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

public record WrappedPackResources(PackResources parent, StringMatcher matcher) implements PackResources {
    @Override
    public @Nullable IoSupplier<InputStream> getRootResource(String... elements) {
        //Seems like only used for pack.mcmeta, so do not wrap it
        return this.parent.getRootResource(elements);
    }

    @Override
    public @Nullable IoSupplier<InputStream> getResource(PackType packType, ResourceLocation location) {
        if (this.matcher.match(location.toString())) {
            ResourceRemover.LOGGER.info("Prevented loading of resource: {}", location);
            return null;
        }
        return this.parent.getResource(packType, location);
    }

    @Override
    public void listResources(PackType packType, String namespace, String path, ResourceOutput resourceOutput) {
        this.parent.listResources(packType, namespace, path, (rl, supplier) -> {
            if (this.matcher.match(rl.toString())) ResourceRemover.LOGGER.info("Prevented listing of resource: {}", rl);
            else resourceOutput.accept(rl, supplier);
        });
    }

    @Override
    public @NotNull Set<String> getNamespaces(PackType type) {
        return this.parent.getNamespaces(type);
    }

    //? >=1.21.4 {
    @Override
    public @Nullable <T> T getMetadataSection(MetadataSectionType<T> type) throws IOException {
        return this.parent.getMetadataSection(type);
    }
    //?} else {
    /*@Override
    public @Nullable <T> T getMetadataSection(MetadataSectionSerializer<T> type) throws IOException {
        return this.parent.getMetadataSection(type);
    }
    *///?}

    //? >=1.20.5 {
    @Override
    public @NotNull PackLocationInfo location() {
        return this.parent.location();
    }
    //?}

    @Override
    public @NotNull String packId() {
        return this.parent.packId();
    }

    @Override
    public void close() {
        this.parent.close();
    }
}
