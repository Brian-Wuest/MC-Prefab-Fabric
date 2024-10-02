package com.wuest.prefab.structures.config.enums;

public class AquaBaseOptions extends BaseOption {
    public static AquaBaseOptions Default = new AquaBaseOptions(
            "item.prefab.aqua_base",
            "assets/prefab/structures/aqua_base.gz",
            "textures/gui/aqua_base.png",
            false,
            false);

    protected AquaBaseOptions(String translationString,
                              String assetLocation,
                              String pictureLocation,
                              boolean hasBedColor,
                              boolean hasGlassColor) {
        super(
                translationString,
                assetLocation,
                pictureLocation,
                hasBedColor,
                hasGlassColor);
    }
}
