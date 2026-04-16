package com.iafenvoy.resource.remover.util;

import com.iafenvoy.resource.remover.ResourceRemover;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

public class MatcherManager {
    public static final String PATH = "./config/resource_remover/";
    @Nullable
    private static StringMatcher ASSETS, DATA;

    public static void load() {
        readFile("assets.txt", lines -> ASSETS = new StringMatcher(lines));
        readFile("data.txt", lines -> DATA = new StringMatcher(lines));
    }

    public static PackResources createWrapped(PackType type, PackResources resources) {
        return new WrappedPackResources(resources, switch (type) {
            case CLIENT_RESOURCES -> ASSETS;
            case SERVER_DATA -> DATA;
        });
    }

    public static void readFile(String file, Consumer<List<String>> consumer) {
        try {
            consumer.accept(FileUtils.readLines(new File(PATH + file), "UTF-8"));
        } catch (IOException e) {
            ResourceRemover.LOGGER.error("Failed to read matcher file {}, creating new one", file, e);
            createEmpty(file);
        }
    }

    public static void createEmpty(String file) {
        try {
            FileUtils.write(new File(PATH + file), "", "UTF-8");
        } catch (IOException e) {
            ResourceRemover.LOGGER.error("Failed to create empty matcher file {}", file, e);
        }
    }
}
