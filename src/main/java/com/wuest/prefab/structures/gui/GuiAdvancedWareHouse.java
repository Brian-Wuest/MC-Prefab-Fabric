package com.wuest.prefab.structures.gui;

import com.wuest.prefab.structures.messages.StructureTagMessage;

/**
 * @author WuestMan
 */
public class GuiAdvancedWareHouse extends GuiWareHouse {

	public GuiAdvancedWareHouse() {
		super();
		this.clientGUIIdentifier = "Advanced Warehouse";
	}

	@Override
	public void Initialize() {
		super.Initialize();
		this.configuration.advanced = true;
		this.structureConfiguration = StructureTagMessage.EnumStructureConfiguration.AdvancedWareHouse;
	}

}
