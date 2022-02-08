package com.wuest.prefab.structures.render;

import it.unimi.dsi.fastutil.ints.IntArrays;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;

public class TranslucentBufferBuilderWrapper implements VertexConsumer {

    private final BufferBuilder inner;
    private final int tintAlpha;

    private ArrayList<Float> vertexBuffer = new ArrayList<>();
    private ArrayList<Integer> indexBuffer = new ArrayList<>();
    private int currentVertex = 0;

    public TranslucentBufferBuilderWrapper(BufferBuilder inner, int alpha) {
        this.inner = inner;

        // Alpha value should be between 0 and 255
        this.tintAlpha = MathHelper.clamp(alpha, 0, 0xFF);
    }

    @Override
    public VertexConsumer vertex(double x, double y, double z) {
        indexBuffer.add(currentVertex);
        vertexBuffer.set(currentVertex++, (float) x);
        vertexBuffer.set(currentVertex++, (float) y);
        vertexBuffer.set(currentVertex++, (float) z);

        return inner.vertex(x, y, z);
    }

    public void sort(BlockPos camera) {
        int[] buffer = new int[indexBuffer.size()];

        int i = 0;
        for (int a : indexBuffer) {
            buffer[i++] = a;
        }

        IntArrays.quickSort(buffer, (a, b) -> {
            float da = getDistance(a, camera);
            float db = getDistance(b, camera);
            if (da < db) return a;
            return b;
        });

        indexBuffer.clear();
        for (int a : buffer) {
            indexBuffer.add(a);
        }
    }

    private float getDistance(int i, BlockPos pos) {
        return (float) Math.pow(
                Math.pow(vertexBuffer.get(i) - pos.getX(), 2) +
                Math.pow(vertexBuffer.get(i + 1) - pos.getY(), 2) +
                Math.pow(vertexBuffer.get(i + 2) - pos.getZ(), 2), 0.5
        );
    }

    @Override
    public VertexConsumer color(int red, int green, int blue, int alpha) {
        return inner.color(red, green, blue, alpha * tintAlpha / 0xFF);
    }

    @Override
    public VertexConsumer texture(float u, float v) {
        return inner.texture(u, v);
    }

    @Override
    public VertexConsumer overlay(int u, int v) {
        return inner.overlay(u, v);
    }

    @Override
    public VertexConsumer light(int u, int v) {
        return inner.light(u, v);
    }

    @Override
    public VertexConsumer normal(float x, float y, float z) {
        return inner.normal(x, y, z);
    }

    @Override
    public void next() {
        inner.next();
    }

    @Override
    public void fixedColor(int red, int green, int blue, int alpha) {
        inner.fixedColor(red, green, blue, alpha * tintAlpha / 0xFF);
    }

    @Override
    public void unfixColor() {
        inner.unfixColor();
    }

    public void begin(VertexFormat.DrawMode drawMode, VertexFormat vertexFormat) {
        inner.begin(drawMode, vertexFormat);
    }

    public void end() {
        inner.end();
    }

    public void clear() {
        inner.clear();
    }

    public BufferBuilder getInner() {
        return inner;
    }

}
