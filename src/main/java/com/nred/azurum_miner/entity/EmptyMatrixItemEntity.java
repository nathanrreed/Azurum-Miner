package com.nred.azurum_miner.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.PowerParticleOption;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.NetherPortalBlock;
import net.minecraft.world.level.block.Portal;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityTravelToDimensionEvent;

import java.util.List;
import java.util.UUID;

import static com.nred.azurum_miner.registration.EntityTypeRegistration.EMPTY_DIMENSIONAL_MATRIX_TYPE;
import static com.nred.azurum_miner.registration.ItemRegistration.DIMENSIONAL_MATRIX;
import static com.nred.azurum_miner.registration.ItemRegistration.EMPTY_DIMENSIONAL_MATRIX;
import static net.minecraft.sounds.SoundEvents.*;
import static net.minecraft.world.level.Level.NETHER;

@EventBusSubscriber
public class EmptyMatrixItemEntity extends ItemEntity {
    private int fillLevel = 0;
    private Vec3 dest = null;

    public EmptyMatrixItemEntity(EntityType<EmptyMatrixItemEntity> type, Level level) {
        super(type, level);
    }

    private EmptyMatrixItemEntity(ValueInput input, Level level) {
        this(input, level, 1);
    }

    private EmptyMatrixItemEntity(ValueInput input, Level level, int count) {
        this(EMPTY_DIMENSIONAL_MATRIX_TYPE.get(), level);
        this.load(input);
        this.setPickUpDelay(60);
        this.setUnlimitedLifetime();
        this.getItem().setCount(count);
    }

    @SubscribeEvent
    public static void onEntityTravelToDimension(EntityTravelToDimensionEvent event) {
        if (event.getEntity() instanceof ItemEntity entity && entity.getItem().is(EMPTY_DIMENSIONAL_MATRIX.get()) && event.getDimension() == NETHER) {
            event.setCanceled(true);

            ProblemReporter reporter = new ProblemReporter.Collector();
            TagValueOutput output = TagValueOutput.createWithContext(reporter, entity.registryAccess());
            entity.save(output);
            ValueInput input = TagValueInput.create(reporter, entity.registryAccess(), output.buildResult());
            entity.remove(Entity.RemovalReason.KILLED);

            EmptyMatrixItemEntity freshEntity = new EmptyMatrixItemEntity(input, entity.level());
            entity.level().addFreshEntity(freshEntity);
            if (entity.getItem().count() > 1) {
                freshEntity = new EmptyMatrixItemEntity(input, entity.level(), entity.getItem().count() - 1);
                freshEntity.uuid = UUID.randomUUID();
                entity.level().addFreshEntity(freshEntity);
            }
        }
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putInt("matrixFillLevel", fillLevel);
        if (this.dest != null) {
            output.store("matrixDest", Vec3.CODEC, dest);
        }
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        this.fillLevel = input.getIntOr("matrixFillLevel", 0);
        this.dest = input.read("matrixDest", Vec3.CODEC).orElse(null);
    }

    @Override
    public boolean isAlwaysTicking() {
        return true;
    }

    @Override
    public void tick() {
        super.tick();
        if (level() instanceof ServerLevel level) {
            if (this.dest != null) {
                if (this.fillLevel < 4) {
                    this.lerpPositionAndRotationStep(4, this.dest.x, this.dest.y, this.dest.z, this.getRotationVector().y, this.getRotationVector().x);
                    this.needsSync = true;
                }

                if (this.fillLevel % 5 == 0) {
                    level.sendParticles(PowerParticleOption.create(ParticleTypes.DRAGON_BREATH, 1f), this.getX(), this.getY(), this.getZ(), 3, 0.0, 0.4, 0.0, 0.02);
                }
                if (this.fillLevel % 30 == 0 && level.getRandom().nextBoolean()) {
                    this.playSound(BOTTLE_FILL, level.getRandom().nextFloat() * 0.6f + 0.4f, level.getRandom().nextFloat() * 0.5f + 0.5f);
                }

                this.fillLevel += 1;
                if (this.fillLevel > 2400) {
                    level.sendParticles(ParticleTypes.EXPLOSION, this.getX(), this.getY(), this.getZ(), 2, 0.0, 0.4, 0.0, 0.05);
                    this.playSound(DRAGON_FIREBALL_EXPLODE);
                    this.playSound(PLAYER_LEVELUP);
                    level.destroyBlock(new BlockPos(Mth.floor(dest.x), Mth.floor(dest.y), Mth.floor(dest.z)), false);
                    this.spawnSprintParticle();
                    this.setItem(new ItemStack(DIMENSIONAL_MATRIX.asItem(), getItem().getCount()));
                    this.setNoGravity(false);
                    this.setDefaultPickUpDelay();
                    this.dest = null;
                }
            } else {
                if (getItem().getCount() < 64) { // Merge stacks
                    for (EmptyMatrixItemEntity itemEntity : level.getEntitiesOfClass(this.getClass(), this.getBoundingBox().inflate(0.5, 0.0, 0.5), e -> e != this && areMergable(this.getItem(), e.getItem()))) {
                        if (areMergable(this.getItem(), itemEntity.getItem()) && itemEntity.dest == null) {
                            this.setItem(merge(this.getItem(), itemEntity.getItem(), this.getItem().count() + itemEntity.getItem().count()));
                            itemEntity.discard();
                        }
                    }
                }
            }
        }
    }

    public AABB findPortalBounds(BlockPos pos, Direction.Axis axis) {
        MutableBlockPos top = pos.mutable();
        MutableBlockPos bottom = pos.mutable();

        Level level = level();
        while (level.getBlockState(top.above()).getBlock() instanceof NetherPortalBlock) {
            top = top.move(Direction.UP);
        }

        while (level.getBlockState(bottom.below()).getBlock() instanceof NetherPortalBlock) {
            bottom = bottom.move(Direction.DOWN);
        }

        while (level.getBlockState(top.relative(axis.getPositive())).getBlock() instanceof NetherPortalBlock) {
            top = top.move(axis.getPositive());
        }

        while (level.getBlockState(bottom.relative(axis.getNegative())).getBlock() instanceof NetherPortalBlock) {
            bottom = bottom.move(axis.getNegative());
        }

        return AABB.of(BoundingBox.fromCorners(top, bottom));
    }

    @Override
    public void setAsInsidePortal(Portal portal, BlockPos pos) {
        if (this.level().isClientSide() || fillLevel != 0 || dest != null || !(portal instanceof NetherPortalBlock)) return;

        Direction.Axis axis = level().getBlockState(pos).getValue(NetherPortalBlock.AXIS);
        AABB portalBounds = findPortalBounds(pos, axis);

        List<? extends EmptyMatrixItemEntity> found = level().getEntitiesOfClass(this.getClass(), portalBounds);
        if (this.getItem().count() > 1 || found.stream().filter(it -> it.getItem().count() == 1).toList().size() > 1) { // Make sure only one is in the portal
            this.addDeltaMovement(new Vec3((axis == Direction.Axis.Z) ? this.getDeltaMovement().reverse().scale(1.5).x : this.getDeltaMovement().x, 0.0, (axis == Direction.Axis.X) ? this.getDeltaMovement().reverse().scale(1.5).z : this.getDeltaMovement().z));
            level().playSeededSound(null, this.getX(), this.getY(), this.getZ(), PLAYER_TELEPORT, this.getSoundSource(), 0.5f, 0.1f, -198679135428719823L);
            this.fillLevel = 1;
            this.setDefaultPickUpDelay();
            return;
        }

        Vec3 center = Vec3.atCenterOf(pos);
        AABB bounds = this.makeBoundingBox().move(this.getX(), this.getY(), center.z);
        if (axis == Direction.Axis.Z) {

            this.dest = new Vec3(AABB.ofSize(center, bounds.getXsize(), bounds.getYsize(), bounds.getZsize()).getCenter().x(), this.getY(), this.getZ());
        } else {
            this.dest = new Vec3(this.getX(), this.getY(), AABB.ofSize(center, bounds.getXsize(), bounds.getYsize(), bounds.getZsize()).getCenter().z);
        }

        this.setNoGravity(true);
        this.setDeltaMovement(Vec3.ZERO);
    }
}