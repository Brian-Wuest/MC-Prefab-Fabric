package com.wuest.prefab.base;

import com.wuest.prefab.Prefab;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.math.BlockPos;
import org.apache.logging.log4j.Level;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * This is the base tile entity used by the mod.
 *
 * @param <T> The base configuration used by this tile entity.
 * @author WuestMan
 */
public abstract class TileEntityBase<T extends BaseConfig> extends BlockEntity {
    protected T config;

    protected TileEntityBase(BlockEntityType<?> tileEntityTypeIn, BlockPos pos, BlockState state) {
        super(tileEntityTypeIn, pos, state);
    }

    /**
     * @return Gets the configuration class used by this tile entity.
     */
    public T getConfig() {
        return this.config;
    }

    /**
     * Sets the configuration class used by this tile entity.
     *
     * @param value The updated tile entity.
     */
    public void setConfig(T value) {
        this.config = value;
        this.markDirty();
    }

    public Class<T> getTypeParameterClass() {
        Type type = getClass().getGenericSuperclass();
        ParameterizedType paramType = (ParameterizedType) type;
        return (Class<T>) paramType.getActualTypeArguments()[0];
    }

    /**
     * Allows for a specialized description packet to be created. This is often used
     * to sync tile entity data from the server to the client easily. For example
     * this is used by signs to synchronize the text to be displayed.
     */
    @Override
    public BlockEntityUpdateS2CPacket toUpdatePacket() {
        // Don't send the packet until the position has been set.
        if (this.pos.getX() == 0 && this.pos.getY() == 0 && this.pos.getZ() == 0) {
            return null;
        }

        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public boolean onSyncedBlockEvent(int id, int type) {
        return true;
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
    	NbtCompound tag = new NbtCompound();
        this.writeNbt(tag);

        return tag;
    }

    @Override
    public void writeNbt(NbtCompound compound) {
        super.writeNbt(compound);

        if (this.config != null) {
            this.config.WriteToNBTCompound(compound);
        }
    }

    @Override
    public void readNbt(NbtCompound compound) {
        super.readNbt(compound);

        this.config = this.createConfigInstance().ReadFromCompoundNBT(compound);
    }

    public T createConfigInstance() {
        try {
            return this.getTypeParameterClass().newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            Prefab.logger.log(Level.ERROR, e.getMessage());
            e.printStackTrace();
        }

        return null;
    }
}
