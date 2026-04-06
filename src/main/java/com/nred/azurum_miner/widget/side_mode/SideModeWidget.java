package com.nred.azurum_miner.widget.side_mode;

import com.nred.azurum_miner.block_entity.ISidedBlockEntity;
import com.nred.azurum_miner.screen.SidebarScreen;
import com.nred.azurum_miner.widget.side_bar.CollapsableWidget;
import com.nred.azurum_miner.widget.side_bar.SideBarElementType;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;

import static com.nred.azurum_miner.util.Helpers.getRelative;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.FACING;
public class SideModeWidget<T extends BlockEntity & ISidedBlockEntity> extends CollapsableWidget {
    public final SideModeSaveButton<T> saveButton;
    public final SideBarElementType type;

    public SideModeWidget(T blockEntity, SidebarScreen<?, ?> screen, SideBarElementType type) {
        super(screen, type);
        this.type = type;

        SideModeType sideModeType = type.getSideModeType();
        saveButton = new SideModeSaveButton<>(blockEntity, sideModeType);
        this.addChild(new SideModeButton<>(blockEntity, saveButton, Direction.UP, sideModeType), 0, 1, newCellSettings().paddingTop(3));
        this.addChild(new SideModeButton<>(blockEntity, saveButton, Direction.DOWN, sideModeType), 2, 1, newCellSettings().paddingBottom(3));
        this.addChild(new SideModeButton<>(blockEntity, saveButton, getRelative(blockEntity.getBlockState().getValue(FACING), Direction.NORTH), sideModeType), 1, 1);
        this.addChild(new SideModeButton<>(blockEntity, saveButton, getRelative(blockEntity.getBlockState().getValue(FACING), Direction.EAST), sideModeType), 1, 2, newCellSettings().paddingRight(3));
        this.addChild(new SideModeButton<>(blockEntity, saveButton, getRelative(blockEntity.getBlockState().getValue(FACING), Direction.SOUTH), sideModeType), 2, 0, newCellSettings().paddingLeft(3).paddingBottom(3));
        this.addChild(new SideModeButton<>(blockEntity, saveButton, getRelative(blockEntity.getBlockState().getValue(FACING), Direction.WEST), sideModeType), 1, 0, newCellSettings().paddingLeft(3));

        this.addChild(saveButton, 2, 2, newCellSettings().align(0.65f, 0.55f));
    }
}