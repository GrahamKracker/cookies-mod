package dev.morazzer.cookies.mod.features.mining;

import com.mojang.datafixers.types.templates.Tag;
import dev.morazzer.cookies.mod.config.ConfigManager;
import dev.morazzer.cookies.mod.events.profile.ServerSwapEvent;
import dev.morazzer.cookies.mod.render.Renderable;
import dev.morazzer.cookies.mod.render.WorldRender;
import dev.morazzer.cookies.mod.render.types.BlockHighlight;
import dev.morazzer.cookies.mod.render.types.Composite;
import dev.morazzer.cookies.mod.render.types.Line;
import dev.morazzer.cookies.mod.utils.Constants;
import dev.morazzer.cookies.mod.utils.dev.DevUtils;
import dev.morazzer.cookies.mod.utils.minecraft.LocationUtils;
import dev.morazzer.mods.cookies.generated.Regions;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.TagEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class PuzzlerSolver {
    private static final Identifier DEBUG = DevUtils.createIdentifier("puzzler_debug");
    private static final BlockPos location = new BlockPos(181, 195, 135);
    private BlockHighlight highlight = null;
    private Renderable line = null;

    public PuzzlerSolver() {
        ClientReceiveMessageEvents.GAME.register(this::messageReceive);
        ServerSwapEvent.SERVER_SWAP.register(() -> this.setHighlight(null));
    }

    private void messageReceive(Text text, boolean overlay) {
        if (overlay) {
            return;
        }
        if (!LocationUtils.Island.DWARVEN_MINES.isActive()) {
            return;
        }
        if (LocationUtils.getRegion() != Regions.DWARVEN_MINES) {
            return;
        }
        if (!ConfigManager.getConfig().miningConfig.puzzlerSolver.getValue()) {
            return;
        }

        final String literalContent = text.getString().replaceAll("(?i)§[A-Z0-9]", "");
        if (!literalContent.startsWith("[NPC] Puzzler: ")) {
            return;
        }

        final String puzzlerString = literalContent.substring(15).trim();
        BlockPos block = location;
        boolean isTask = true;

        for (char c : puzzlerString.toCharArray()) {
            switch (c) {
                case '▲' -> block = move(block, 0, 1);
                case '◀' -> block = move(block, 1, 0);
                case '▶' -> block = move(block, -1, 0);
                case '▼' -> block = move(block, 0, -1);
                default -> isTask = false;
            }
        }

        if (!isTask) {
            this.solved();
        } else {
            setHighlight(block);
        }
    }

    private void setHighlight(BlockPos highlight) {
        WorldRender.removeRenderable(this.highlight);
        WorldRender.removeRenderable(this.line);
        if (highlight == null) {
            this.highlight = null;
            this.line = null;
            return;
        }
        if (this.line != null) {
            WorldRender.addRenderable(this.line);
        }

        this.highlight = new BlockHighlight(highlight, Constants.SUCCESS_COLOR);
        WorldRender.addRenderable(this.highlight);
        final ClientWorld world = MinecraftClient.getInstance().world;
        if (world == null) {
            return;
        }

        final BlockState blockState = world.getBlockState(highlight);
        if (blockState.getBlock() == Blocks.OAK_PLANKS || blockState.getBlock() == Blocks.SPRUCE_PLANKS ||
            blockState.getBlock() == Blocks.BIRCH_PLANKS) {
            world.setBlockState(highlight, Blocks.WARPED_PLANKS.getDefaultState());
        }
    }

    private BlockPos move(BlockPos pos, int x, int y) {
        final BlockPos add = pos.add(x, 0, y);
        if (DevUtils.isEnabled(DEBUG)) {
            Line newLine = new Line(centerPos(pos), centerPos(add), Constants.MAIN_COLOR);
            if (this.line == null) {
                this.line = newLine;
            } else {
                this.line = new Composite(this.line, newLine);
            }
        }
        return add;
    }

    private void solved() {
        setHighlight(null);
    }

    private Vec3d centerPos(BlockPos pos) {
        return new Vec3d(pos.getX() + 0.5, pos.getY() + 1.1, pos.getZ() + 0.5);
    }

}
