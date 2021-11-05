package com.wuest.prefab.structures.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.wuest.prefab.ClientModRegistry;
import com.wuest.prefab.Prefab;
import com.wuest.prefab.blocks.BlockStructureScanner;
import com.wuest.prefab.config.StructureScannerConfig;
import com.wuest.prefab.gui.GuiLangKeys;
import com.wuest.prefab.structures.base.BuildBlock;
import com.wuest.prefab.structures.base.Structure;
import com.wuest.prefab.structures.config.StructureConfiguration;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Style;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.World;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * @author WuestMan, Dqu1J
 * Some parts of this class have been derived from Botania's MultiBlockRenderer.
 * http://botaniamod.net/license.php
 */
@SuppressWarnings({"WeakerAccess", "ConstantConditions"})
public class StructureRenderHandler {
    // player's overlapping on structures and other things.
    public static StructureConfiguration currentConfiguration;
    public static Structure currentStructure;
    public static Direction assumedNorth;
    public static boolean rendering = false;
    public static boolean showedMessage = false;

    private static int dimension;
    private static VertexBuffer vertexBuffer;

    /**
     * Resets the structure to show in the world.
     *
     * @param structure     The structure to show in the world, pass null to clear out the client.
     * @param assumedNorth  The assumed norther facing for this structure.
     * @param configuration The configuration for this structure.
     */
    public static void setStructure(Structure structure, Direction assumedNorth, StructureConfiguration configuration) {
        StructureRenderHandler.currentStructure = structure;
        StructureRenderHandler.assumedNorth = assumedNorth;
        StructureRenderHandler.currentConfiguration = configuration;
        StructureRenderHandler.showedMessage = false;

        MinecraftClient mc = MinecraftClient.getInstance();

        if (mc.world != null) {
            StructureRenderHandler.dimension = mc.world.getDimension().getLogicalHeight();
        }

        if (structure != null && mc.world != null) {
            renderSetup(mc.world, mc.player);
        } else {
            rendering = false;
            vertexBuffer.close();
        }
    }

    public static void renderSetup(World world, PlayerEntity player) {
        if (StructureRenderHandler.currentStructure != null
                && StructureRenderHandler.dimension == player.world.getDimension().getLogicalHeight()
                && StructureRenderHandler.currentConfiguration != null
                && Prefab.serverConfiguration.enableStructurePreview) {
            rendering = true;

            // Can only render on the render thread
            if (!RenderSystem.isOnRenderThread()) {
                throw new IllegalStateException("Prefab Structure Preview rendering attempted not on the render thread");
            }

            Set<BuildBlock> toRender = new HashSet<>();

            for (BuildBlock buildBlock : currentStructure.getBlocks()) {
                Block block = Registry.BLOCK.get(buildBlock.getResourceLocation());
                if (block == null) {
                    continue;
                }

                BlockPos pos = buildBlock.getStartingPosition().getRelativePosition(
                        currentConfiguration.pos,
                        currentStructure.getClearSpace().getShape().getDirection(),
                        currentConfiguration.houseFacing);

                // Don't render block if it's outside the world height limit
                if (pos.getY() > dimension) {
                    continue;
                }

                // Don't render block if it's not in air/liquid
                BlockState targetBlock = world.getBlockState(pos);
                if (!targetBlock.isAir() && !targetBlock.getMaterial().isLiquid()) {
                    continue;
                }

                BlockState blockState = block.getDefaultState();
                buildBlock = BuildBlock.SetBlockState(
                        currentConfiguration,
                        player.world,
                        currentConfiguration.pos,
                        assumedNorth,
                        buildBlock,
                        block,
                        blockState,
                        currentStructure);

                toRender.add(buildBlock);
            }

            buildVertexBuffer(toRender);

            if (!StructureRenderHandler.showedMessage) {
                TranslatableText message = new TranslatableText(GuiLangKeys.GUI_PREVIEW_NOTICE);
                message.setStyle(Style.EMPTY.withColor(Formatting.GREEN));
                player.sendMessage(message, false);

                message = new TranslatableText(GuiLangKeys.GUI_BLOCK_CLICKED);
                message.setStyle(Style.EMPTY.withColor(Formatting.YELLOW));
                player.sendMessage(message, false);

                StructureRenderHandler.showedMessage = true;
            }
        }
    }

    public static void render(MatrixStack stack) {
        if (rendering) {
            MinecraftClient minecraft = MinecraftClient.getInstance();
            Camera camera = minecraft.getEntityRenderDispatcher().camera;
            BlockPos pos = currentConfiguration.pos;

            renderBlocks(vertexBuffer, pos, camera.getPos(), stack);
        }
    }

    private static void buildVertexBuffer(Set<BuildBlock> blocks) {
        vertexBuffer = new VertexBuffer();
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL);
        buildBlockMesh(blocks, new MatrixStack(), bufferBuilder);
        bufferBuilder.end();
        vertexBuffer.upload(bufferBuilder);
    }

    private static void buildBlockMesh(Set<BuildBlock> blocks, MatrixStack matrixStack, VertexConsumer vertexConsumer) {
        MinecraftClient mc = MinecraftClient.getInstance();
        BlockRenderManager blockRenderManager = mc.getBlockRenderManager();
        TranslucentVertexConsumer translucentConsumer = new TranslucentVertexConsumer(vertexConsumer, 100);

        for (BuildBlock block : blocks) {
            BlockPos pos = block.getStartingPosition().getRelativePosition(
                    new BlockPos(0, 0, 0),
                    currentStructure.getClearSpace().getShape().getDirection(),
                    currentConfiguration.houseFacing);
            BlockState state = block.getBlockState();

            int color = mc.getBlockColors().getColor(state, mc.world, pos, 50);
            float r = (float) (color >> 16 & 255) / 255.0F;
            float g = (float) (color >> 8 & 255) / 255.0F;
            float b = (float) (color & 255) / 255.0F;

            BakedModel bakedModel = blockRenderManager.getModel(state);

            matrixStack.push();
            matrixStack.translate(pos.getX(), pos.getY(), pos.getZ());
            renderBlockModel(mc.world, bakedModel, state, pos, matrixStack, translucentConsumer,
                    mc.world.random);
            matrixStack.pop();
        }
    }

    private static void renderBlockModel(BlockRenderView world, BakedModel bakedModel, BlockState state, BlockPos pos, MatrixStack matrix, VertexConsumer vertexConsumer, Random random) {
        if (!((FabricBakedModel) bakedModel).isVanillaAdapter()) {
            PrefabBakedModel prefabModel = new PrefabBakedModel(bakedModel);
            MinecraftClient.getInstance().getBlockRenderManager().getModelRenderer().render(world, prefabModel, state, pos, matrix, vertexConsumer, true, random, 32, OverlayTexture.DEFAULT_UV);
        } else {
            MinecraftClient.getInstance().getBlockRenderManager().getModelRenderer().render(world, bakedModel, state, pos, matrix, vertexConsumer, true, random, 32, OverlayTexture.DEFAULT_UV);
        }
    }

    private static void renderBlocks(VertexBuffer vertexBuffer, BlockPos pos, Vec3d cameraPos, MatrixStack matrixStack) {
        matrixStack.push();
        matrixStack.translate(pos.getX()-cameraPos.getX(), pos.getY()-cameraPos.getY(), pos.getZ()-cameraPos.getZ());
        
        RenderLayer layer = RenderLayer.getTranslucent();
        layer.startDrawing();
        vertexBuffer.setShader(matrixStack.peek().getModel(), RenderSystem.getProjectionMatrix(), GameRenderer.getPositionColorTexLightmapShader());
        layer.endDrawing();

        matrixStack.pop();
    }



    public static void RenderTest(World worldIn, MatrixStack matrixStack, double cameraX, double cameraY, double cameraZ) {
        if (StructureRenderHandler.currentStructure != null
                && StructureRenderHandler.dimension == MinecraftClient.getInstance().player.world.getDimension().getLogicalHeight()
                && StructureRenderHandler.currentConfiguration != null
                && Prefab.serverConfiguration.enableStructurePreview) {
            BlockPos originalPos = StructureRenderHandler.currentConfiguration.pos.up();

            double blockXOffset = originalPos.getX();
            double blockZOffset = originalPos.getZ();
            double blockStartYOffset = originalPos.getY();

            StructureRenderHandler.drawBox(
                    matrixStack,
                    blockXOffset,
                    blockZOffset,
                    blockStartYOffset,
                    cameraX,
                    cameraY,
                    cameraZ,
                    1,
                    1,
                    1);
        }
    }

    public static void drawBox(
            MatrixStack matrixStack,
            double blockXOffset,
            double blockZOffset,
            double blockStartYOffset,
            double cameraX,
            double cameraY,
            double cameraZ,
            int xLength,
            int zLength,
            int height) {
        RenderSystem.enableDepthTest();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();

        RenderSystem.disableTexture();
        RenderSystem.disableBlend();

        double translatedX = blockXOffset - cameraX;
        double translatedY = blockStartYOffset - cameraY + .02;
        double translatedYEnd = translatedY + height - .02D;
        double translatedZ = blockZOffset - cameraZ;
        RenderSystem.lineWidth(2.0f);

        // Draw the verticals of the box.
        bufferBuilder.begin(VertexFormat.DrawMode.DEBUG_LINE_STRIP, VertexFormats.POSITION_COLOR);
        bufferBuilder.vertex(translatedX, translatedY, translatedZ).color(1.0F, 1.0F, 0.0F, 1.0F).next();
        bufferBuilder.vertex(translatedX, translatedYEnd, translatedZ).color(1.0F, 1.0F, 0.0F, 1.0F).next();
        tessellator.draw();

        bufferBuilder.begin(VertexFormat.DrawMode.DEBUG_LINE_STRIP, VertexFormats.POSITION_COLOR);
        bufferBuilder.vertex(translatedX + xLength, translatedY, translatedZ).color(1.0F, 1.0F, 0.0F, 1.0F).next();
        bufferBuilder.vertex(translatedX + xLength, translatedYEnd, translatedZ).color(1.0F, 1.0F, 0.0F, 1.0F).next();
        tessellator.draw();

        bufferBuilder.begin(VertexFormat.DrawMode.DEBUG_LINE_STRIP, VertexFormats.POSITION_COLOR);
        bufferBuilder.vertex(translatedX, translatedY, translatedZ + zLength).color(1.0F, 1.0F, 0.0F, 1.0F).next();
        bufferBuilder.vertex(translatedX, translatedYEnd, translatedZ + zLength).color(1.0F, 1.0F, 0.0F, 1.0F).next();
        tessellator.draw();

        bufferBuilder.begin(VertexFormat.DrawMode.DEBUG_LINE_STRIP, VertexFormats.POSITION_COLOR);
        bufferBuilder.vertex(translatedX + xLength, translatedY, translatedZ + zLength).color(1.0F, 1.0F, 0.0F, 1.0F).next();
        bufferBuilder.vertex(translatedX + xLength, translatedYEnd, translatedZ + zLength).color(1.0F, 1.0F, 0.0F, 1.0F).next();
        tessellator.draw();

        // Draw bottom horizontals.
        bufferBuilder.begin(VertexFormat.DrawMode.DEBUG_LINE_STRIP, VertexFormats.POSITION_COLOR);

        bufferBuilder.vertex(translatedX, translatedY, translatedZ).color(1.0F, 1.0F, 0.0F, 1.0F).next();
        bufferBuilder.vertex(translatedX, translatedY, translatedZ + zLength).color(1.0F, 1.0F, 0.0F, 1.0F).next();

        bufferBuilder.vertex(translatedX + xLength, translatedY, translatedZ + zLength).color(1.0F, 1.0F, 0.0F, 1.0F).next();
        bufferBuilder.vertex(translatedX + xLength, translatedY, translatedZ).color(1.0F, 1.0F, 0.0F, 1.0F).next();

        bufferBuilder.vertex(translatedX, translatedY, translatedZ).color(1.0F, 1.0F, 0.0F, 1.0F).next();
        bufferBuilder.vertex(translatedX + xLength, translatedY, translatedZ).color(1.0F, 1.0F, 0.0F, 1.0F).next();

        bufferBuilder.vertex(translatedX + xLength, translatedY, translatedZ + zLength).color(1.0F, 1.0F, 0.0F, 1.0F).next();
        bufferBuilder.vertex(translatedX, translatedY, translatedZ + zLength).color(1.0F, 1.0F, 0.0F, 1.0F).next();
        tessellator.draw();

        // Draw top horizontals
        bufferBuilder.begin(VertexFormat.DrawMode.DEBUG_LINE_STRIP, VertexFormats.POSITION_COLOR);

        bufferBuilder.vertex(translatedX, translatedYEnd, translatedZ).color(1.0F, 1.0F, 0.0F, 1.0F).next();
        bufferBuilder.vertex(translatedX, translatedYEnd, translatedZ + zLength).color(1.0F, 1.0F, 0.0F, 1.0F).next();

        bufferBuilder.vertex(translatedX + xLength, translatedYEnd, translatedZ + zLength).color(1.0F, 1.0F, 0.0F, 1.0F).next();
        bufferBuilder.vertex(translatedX + xLength, translatedYEnd, translatedZ).color(1.0F, 1.0F, 0.0F, 1.0F).next();

        bufferBuilder.vertex(translatedX, translatedYEnd, translatedZ).color(1.0F, 1.0F, 0.0F, 1.0F).next();
        bufferBuilder.vertex(translatedX + xLength, translatedYEnd, translatedZ).color(1.0F, 1.0F, 0.0F, 1.0F).next();

        bufferBuilder.vertex(translatedX + xLength, translatedYEnd, translatedZ + zLength).color(1.0F, 1.0F, 0.0F, 1.0F).next();
        bufferBuilder.vertex(translatedX, translatedYEnd, translatedZ + zLength).color(1.0F, 1.0F, 0.0F, 1.0F).next();
        tessellator.draw();

        RenderSystem.lineWidth(1.0F);
        RenderSystem.enableBlend();
        RenderSystem.enableTexture();
    }

    public static void renderScanningBoxes(MatrixStack matrixStack,
                                           double cameraX,
                                           double cameraY,
                                           double cameraZ) {
        for (int i = 0; i < ClientModRegistry.structureScanners.size(); i++) {
            StructureScannerConfig config = ClientModRegistry.structureScanners.get(i);

            BlockPos pos = config.blockPos;
            boolean removeConfig = false;
            removeConfig = pos == null;

            // Make sure the block exists in the world at the block pos.
            if (pos != null) {
                removeConfig = !(MinecraftClient.getInstance().world.getBlockState(pos).getBlock() instanceof BlockStructureScanner);
            }

            if (removeConfig) {
                ClientModRegistry.structureScanners.remove(i);
                i--;
                continue;
            }

            Direction leftDirection = config.direction.rotateYCounterclockwise();

            BlockPos startingPosition = config.blockPos
                    .offset(leftDirection, config.blocksToTheLeft)
                    .offset(Direction.DOWN, config.blocksDown)
                    .offset(config.direction, config.blocksParallel);

            int xLength = config.blocksWide;
            int zLength = config.blocksLong;

            // Based on direction, width and length may be need to be modified;

            switch (config.direction) {
                case NORTH: {
                    zLength = -zLength;
                    startingPosition = startingPosition.offset(config.direction.getOpposite());
                    break;
                }

                case EAST: {
                    int tempWidth = xLength;
                    xLength = zLength;
                    zLength = tempWidth;
                    break;
                }

                case SOUTH: {
                    xLength = -xLength;
                    startingPosition = startingPosition.offset(config.direction.rotateYCounterclockwise());
                    break;
                }

                case WEST: {
                    int tempLength = zLength;
                    zLength = -xLength;
                    xLength = -tempLength;

                    startingPosition = startingPosition.offset(config.direction.getOpposite());
                    startingPosition = startingPosition.offset(config.direction.rotateYCounterclockwise());
                    break;
                }
            }

            StructureRenderHandler.drawBox(
                    matrixStack,
                    startingPosition.getX(),
                    startingPosition.getZ(),
                    startingPosition.getY(),
                    cameraX,
                    cameraY,
                    cameraZ,
                    xLength,
                    zLength,
                    config.blocksTall);
        }
    }
}
