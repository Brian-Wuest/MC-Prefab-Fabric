package com.wuest.prefab.structures.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.text2speech.Narrator;
import com.wuest.prefab.ClientModRegistry;
import com.wuest.prefab.Prefab;
import com.wuest.prefab.blocks.BlockStructureScanner;
import com.wuest.prefab.config.StructureScannerConfig;
import com.wuest.prefab.gui.GuiLangKeys;
import com.wuest.prefab.structures.base.BuildBlock;
import com.wuest.prefab.structures.base.Structure;
import com.wuest.prefab.structures.config.StructureConfiguration;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author WuestMan
 * This class was derived from Botania's MultiBlockRenderer.
 * Most changes are for extra comments for myself as well as to use my blocks class structure.
 * http://botaniamod.net/license.php
 */
@SuppressWarnings({"WeakerAccess", "ConstantConditions"})
public class StructureRenderHandler {
    // player's overlapping on structures and other things.
    public static StructureConfiguration currentConfiguration;
    public static Structure currentStructure;
    public static boolean rendering = false;
    public static boolean showedMessage = false;
    private static int dimension;

    /**
     * Resets the structure to show in the world.
     *
     * @param structure     The structure to show in the world, pass null to clear out the client.
     * @param configuration The configuration for this structure.
     */
    public static void setStructure(Structure structure, StructureConfiguration configuration) {
        StructureRenderHandler.currentStructure = structure;
        StructureRenderHandler.currentConfiguration = configuration;
        StructureRenderHandler.showedMessage = false;

        Minecraft mc = Minecraft.getInstance();

        if (mc.level != null) {
            StructureRenderHandler.dimension = mc.level.dimensionType().logicalHeight();
        }
    }

    /**
     * This is to render the currently bound structure.
     *
     * @param player The player to render the structure for.
     * @param src    The ray trace for where the player is currently looking.
     */
    public static void renderPlayerLook(Player player, HitResult src, PoseStack matrixStack, CallbackInfo callbackInfo) {
        if (StructureRenderHandler.currentStructure != null
                && StructureRenderHandler.dimension == player.level().dimensionType().logicalHeight()
                && StructureRenderHandler.currentConfiguration != null
                && Prefab.serverConfiguration.enableStructurePreview) {
            rendering = true;

            MultiBufferSource.BufferSource entityVertexConsumer = Minecraft.getInstance().renderBuffers().bufferSource();

            // Preparing cull frustum
            Vec3 playerEyePosition = player.getEyePosition(1.0F);
            Matrix4f matrix4f = matrixStack.last().pose();
            Frustum frustum = new Frustum(matrix4f, RenderSystem.getProjectionMatrix());
            frustum.prepare(playerEyePosition.x, playerEyePosition.y, playerEyePosition.z);

            /*Frustum frustum = new Frustum(matrixStack.last().pose(), RenderSystem.getProjectionMatrix());
            Vec3i vec = new Vec3i((int) player.getEyePosition(1.0F).x, (int) player.getEyePosition(1.0F).y, (int) player.getEyePosition(1.0F).z);
            BlockPos cameraPos = new BlockPos(vec);
            frustum.prepare(cameraPos.getX(), cameraPos.getY(), cameraPos.getZ());*/

            for (BuildBlock buildBlock : StructureRenderHandler.currentStructure.getBlocks()) {
                Block foundBlock = BuiltInRegistries.BLOCK.get(buildBlock.getResourceLocation());

                if (foundBlock != null) {
                    // In order to get the proper relative position I also need the structure's original facing.
                    BlockPos pos = buildBlock.getStartingPosition().getRelativePosition(
                            StructureRenderHandler.currentConfiguration.pos,
                            StructureRenderHandler.currentStructure.getClearSpace().getShape().getDirection(),
                            StructureRenderHandler.currentConfiguration.houseFacing);

                    // Don't render the block if it isn't visible (cull)
                    AABB box = new AABB(pos.getX() - 0.5, pos.getY() - 0.5, pos.getZ() - 0.5, pos.getX() + 1.5, pos.getY() + 1.5, pos.getZ() + 1.5);
                    if (!frustum.isVisible(box)) {
                        continue;
                    }

                    // Get the unique block state for this block.
                    BlockState blockState = foundBlock.defaultBlockState();
                    buildBlock = BuildBlock.SetBlockState(
                            StructureRenderHandler.currentConfiguration,
                            player.level(),
                            StructureRenderHandler.currentConfiguration.pos,
                            buildBlock,
                            foundBlock,
                            blockState,
                            StructureRenderHandler.currentStructure);

                    StructureRenderHandler.renderComponentInWorld(player.level(), buildBlock, entityVertexConsumer, matrixStack, pos);
                }
            }

            // Draw function.
            entityVertexConsumer.endBatch(Sheets.translucentItemSheet());

            if (!StructureRenderHandler.showedMessage) {
                Minecraft mc = Minecraft.getInstance();

                // Stop narrator from continuing narrating what was in the structure GUI
                Narrator.getNarrator().clear();

                MutableComponent message = Component.translatable(GuiLangKeys.GUI_PREVIEW_NOTICE);
                message.setStyle(Style.EMPTY.withColor(ChatFormatting.GREEN));

                mc.gui.getChat().addMessage(message);
                //mc.gui.handlePlayerChat(StructureRenderHandler.getMessageType(), message, ChatSender.system(message));

                message = Component.translatable(GuiLangKeys.GUI_BLOCK_CLICKED);
                message.setStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW));
                mc.gui.getChat().addMessage(message);
                //mc.gui.handlePlayerChat(StructureRenderHandler.getMessageType(), message, ChatSender.system(message));

                StructureRenderHandler.showedMessage = true;
            }
        }
    }

    private static ChatType getMessageType() {
        Registry<ChatType> registry = Minecraft.getInstance().level.registryAccess().registryOrThrow(Registries.CHAT_TYPE);
        return registry.get(ChatType.CHAT);
    }

    private static boolean renderComponentInWorld(Level world, BuildBlock buildBlock, MultiBufferSource.BufferSource entityVertexConsumer, PoseStack matrixStack, BlockPos pos) {
        // Don't render this block if it's going to overlay a non-air/water block.
        BlockState targetBlock = world.getBlockState(pos);

        if (targetBlock.getBlock() != Blocks.AIR && targetBlock.getBlock() != Blocks.WATER) {
            return false;
        }

        StructureRenderHandler.doRenderComponent(world, buildBlock, pos, entityVertexConsumer, matrixStack);

        if (buildBlock.getSubBlock() != null) {
            Block foundBlock = BuiltInRegistries.BLOCK.get(buildBlock.getSubBlock().getResourceLocation());
            BlockState blockState = foundBlock.defaultBlockState();

            BuildBlock subBlock = BuildBlock.SetBlockState(
                    StructureRenderHandler.currentConfiguration,
                    world, StructureRenderHandler.currentConfiguration.pos,
                    buildBlock.getSubBlock(),
                    foundBlock,
                    blockState,
                    StructureRenderHandler.currentStructure);

            BlockPos subBlockPos = subBlock.getStartingPosition().getRelativePosition(
                    StructureRenderHandler.currentConfiguration.pos,
                    StructureRenderHandler.currentStructure.getClearSpace().getShape().getDirection(),
                    StructureRenderHandler.currentConfiguration.houseFacing);

            return StructureRenderHandler.renderComponentInWorld(world, subBlock, entityVertexConsumer, matrixStack, subBlockPos);
        }

        return true;
    }

    private static void doRenderComponent(Level world, BuildBlock buildBlock, BlockPos pos, MultiBufferSource.BufferSource entityVertexConsumer, PoseStack matrixStack) {
        BlockState state = buildBlock.getBlockState();
        StructureRenderHandler.renderBlock(world, matrixStack, new Vec3(pos.getX(), pos.getY(), pos.getZ()), state, entityVertexConsumer, pos);
    }

    private static void renderBlock(Level world, PoseStack matrixStack, Vec3 pos, BlockState state, MultiBufferSource.BufferSource entityVertexConsumer, BlockPos blockPos) {
        Minecraft minecraft = Minecraft.getInstance();
        Camera camera = minecraft.getEntityRenderDispatcher().camera;
        Vec3 projectedView = camera.getPosition();

        if (state.getRenderShape() != RenderShape.INVISIBLE && state.getRenderShape() == RenderShape.MODEL) {
            matrixStack.pushPose();
            matrixStack.translate(-projectedView.x(), -projectedView.y(), -projectedView.z());
            matrixStack.translate(pos.x, pos.y, pos.z);

            BlockRenderDispatcher blockRenderManager = minecraft.getBlockRenderer();
            VertexConsumer consumer = entityVertexConsumer.getBuffer(Sheets.translucentItemSheet());
            TranslucentVertexConsumer translucentConsumer = new TranslucentVertexConsumer(consumer, 100);

            int color = minecraft.getBlockColors().getColor(state, world, blockPos, 50);
            float r = (float) (color >> 16 & 255) / 255.0F;
            float g = (float) (color >> 8 & 255) / 255.0F;
            float b = (float) (color & 255) / 255.0F;

            blockRenderManager.getModelRenderer().renderModel(
                    matrixStack.last(),
                    translucentConsumer,
                    state,
                    blockRenderManager.getBlockModel(state),
                    r, g, b,
                    0xF000F0, OverlayTexture.NO_OVERLAY);

            matrixStack.popPose();
        }
    }

    public static void RenderTest(Level worldIn, PoseStack matrixStack, MultiBufferSource multiBufferSource, float cameraX, float cameraY, float cameraZ) {
        if (StructureRenderHandler.currentStructure != null
                && StructureRenderHandler.dimension == Minecraft.getInstance().player.level().dimensionType().logicalHeight()
                && StructureRenderHandler.currentConfiguration != null
                && Prefab.serverConfiguration.enableStructurePreview) {
            BlockPos originalPos = StructureRenderHandler.currentConfiguration.pos.above();

            float blockXOffset = originalPos.getX();
            float blockZOffset = originalPos.getZ();
            float blockStartYOffset = originalPos.getY();

            StructureRenderHandler.drawBox(
                    matrixStack,
                    multiBufferSource,
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
            PoseStack matrixStack,
            MultiBufferSource multiBufferSource,
            float blockXOffset,
            float blockZOffset,
            float blockStartYOffset,
            float cameraX,
            float cameraY,
            float cameraZ,
            int xLength,
            int zLength,
            int height) {
        VertexConsumer bufferBuilder = multiBufferSource.getBuffer(RenderType.debugLineStrip(2.0));
        Matrix4f matrix4f = matrixStack.last().pose();

        float translatedX = blockXOffset - cameraX;
        float translatedY = (float) (blockStartYOffset - cameraY + .02);
        float translatedYEnd = (float) (translatedY + height - .02);
        float translatedZ = blockZOffset - cameraZ;

        // Draw the verticals of the box.
        bufferBuilder = multiBufferSource.getBuffer(RenderType.debugLineStrip(2.0));
        bufferBuilder.vertex(matrix4f, translatedX, translatedY, translatedZ).color(1.0F, 1.0F, 0.0F, 1.0F).endVertex();
        bufferBuilder.vertex(matrix4f, translatedX, translatedYEnd, translatedZ).color(1.0F, 1.0F, 0.0F, 1.0F).endVertex();

        bufferBuilder = multiBufferSource.getBuffer(RenderType.debugLineStrip(2.0));
        bufferBuilder.vertex(matrix4f, translatedX + xLength, translatedY, translatedZ).color(1.0F, 1.0F, 0.0F, 1.0F).endVertex();
        bufferBuilder.vertex(matrix4f, translatedX + xLength, translatedYEnd, translatedZ).color(1.0F, 1.0F, 0.0F, 1.0F).endVertex();

        bufferBuilder = multiBufferSource.getBuffer(RenderType.debugLineStrip(2.0));
        bufferBuilder.vertex(matrix4f, translatedX, translatedY, translatedZ + zLength).color(1.0F, 1.0F, 0.0F, 1.0F).endVertex();
        bufferBuilder.vertex(matrix4f, translatedX, translatedYEnd, translatedZ + zLength).color(1.0F, 1.0F, 0.0F, 1.0F).endVertex();

        bufferBuilder = multiBufferSource.getBuffer(RenderType.debugLineStrip(2.0));
        bufferBuilder.vertex(matrix4f, translatedX + xLength, translatedY, translatedZ + zLength).color(1.0F, 1.0F, 0.0F, 1.0F).endVertex();
        bufferBuilder.vertex(matrix4f, translatedX + xLength, translatedYEnd, translatedZ + zLength).color(1.0F, 1.0F, 0.0F, 1.0F).endVertex();

        // Draw bottom horizontals.
        bufferBuilder = multiBufferSource.getBuffer(RenderType.debugLineStrip(2.0));

        bufferBuilder.vertex(matrix4f, translatedX, translatedY, translatedZ).color(1.0F, 1.0F, 0.0F, 1.0F).endVertex();
        bufferBuilder.vertex(matrix4f, translatedX, translatedY, translatedZ + zLength).color(1.0F, 1.0F, 0.0F, 1.0F).endVertex();

        bufferBuilder.vertex(matrix4f, translatedX + xLength, translatedY, translatedZ + zLength).color(1.0F, 1.0F, 0.0F, 1.0F).endVertex();
        bufferBuilder.vertex(matrix4f, translatedX + xLength, translatedY, translatedZ).color(1.0F, 1.0F, 0.0F, 1.0F).endVertex();

        bufferBuilder.vertex(matrix4f, translatedX, translatedY, translatedZ).color(1.0F, 1.0F, 0.0F, 1.0F).endVertex();
        bufferBuilder.vertex(matrix4f, translatedX + xLength, translatedY, translatedZ).color(1.0F, 1.0F, 0.0F, 1.0F).endVertex();

        bufferBuilder.vertex(matrix4f, translatedX + xLength, translatedY, translatedZ + zLength).color(1.0F, 1.0F, 0.0F, 1.0F).endVertex();
        bufferBuilder.vertex(matrix4f, translatedX, translatedY, translatedZ + zLength).color(1.0F, 1.0F, 0.0F, 1.0F).endVertex();

        // Draw top horizontals
        bufferBuilder = multiBufferSource.getBuffer(RenderType.debugLineStrip(2.0));

        bufferBuilder.vertex(matrix4f, translatedX, translatedYEnd, translatedZ).color(1.0F, 1.0F, 0.0F, 1.0F).endVertex();
        bufferBuilder.vertex(matrix4f, translatedX, translatedYEnd, translatedZ + zLength).color(1.0F, 1.0F, 0.0F, 1.0F).endVertex();

        bufferBuilder.vertex(matrix4f, translatedX + xLength, translatedYEnd, translatedZ + zLength).color(1.0F, 1.0F, 0.0F, 1.0F).endVertex();
        bufferBuilder.vertex(matrix4f, translatedX + xLength, translatedYEnd, translatedZ).color(1.0F, 1.0F, 0.0F, 1.0F).endVertex();

        bufferBuilder.vertex(matrix4f, translatedX, translatedYEnd, translatedZ).color(1.0F, 1.0F, 0.0F, 1.0F).endVertex();
        bufferBuilder.vertex(matrix4f, translatedX + xLength, translatedYEnd, translatedZ).color(1.0F, 1.0F, 0.0F, 1.0F).endVertex();

        bufferBuilder.vertex(matrix4f, translatedX + xLength, translatedYEnd, translatedZ + zLength).color(1.0F, 1.0F, 0.0F, 1.0F).endVertex();
        bufferBuilder.vertex(matrix4f, translatedX, translatedYEnd, translatedZ + zLength).color(1.0F, 1.0F, 0.0F, 1.0F).endVertex();
    }

    public static void renderScanningBoxes(PoseStack matrixStack,
                                           MultiBufferSource multiBufferSource,
                                           float cameraX,
                                           float cameraY,
                                           float cameraZ) {
        for (int i = 0; i < ClientModRegistry.structureScanners.size(); i++) {
            StructureScannerConfig config = ClientModRegistry.structureScanners.get(i);

            BlockPos pos = config.blockPos;
            boolean removeConfig = false;
            removeConfig = pos == null;

            // Make sure the block exists in the world at the block pos.
            if (pos != null) {
                removeConfig = !(Minecraft.getInstance().level.getBlockState(pos.relative(Direction.UP)).getBlock() instanceof BlockStructureScanner);
            }

            if (removeConfig) {
                ClientModRegistry.structureScanners.remove(i);
                i--;
                continue;
            }

            Direction leftDirection = config.direction.getCounterClockWise();

            BlockPos startingPosition = config.blockPos
                    .relative(leftDirection, config.blocksToTheLeft)
                    .relative(Direction.DOWN, config.blocksDown)
                    .relative(config.direction, config.blocksParallel);

            int xLength = config.blocksWide;
            int zLength = config.blocksLong;

            // Based on direction, width and length may be need to be modified;

            switch (config.direction) {
                case NORTH: {
                    zLength = -zLength;
                    startingPosition = startingPosition.relative(config.direction.getOpposite());
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
                    startingPosition = startingPosition.relative(config.direction.getCounterClockWise());
                    break;
                }

                case WEST: {
                    int tempLength = zLength;
                    zLength = -xLength;
                    xLength = -tempLength;

                    startingPosition = startingPosition.relative(config.direction.getOpposite());
                    startingPosition = startingPosition.relative(config.direction.getCounterClockWise());
                    break;
                }
            }

            StructureRenderHandler.drawBox(
                    matrixStack,
                    multiBufferSource,
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
