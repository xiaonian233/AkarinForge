package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenBigMushroom;
import net.minecraft.world.gen.feature.WorldGenerator;

public class BlockMushroom extends BlockBush implements IGrowable
{
    protected static final AxisAlignedBB MUSHROOM_AABB = new AxisAlignedBB(0.30000001192092896D, 0.0D, 0.30000001192092896D, 0.699999988079071D, 0.4000000059604645D, 0.699999988079071D);

    protected BlockMushroom()
    {
        this.setTickRandomly(true);
    }

    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        return MUSHROOM_AABB;
    }

    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
    {
        final int sourceX = pos.getX(), sourceY = pos.getY(), sourceZ = pos.getZ(); // CraftBukkit
        if (rand.nextInt(25) == 0)
        {
            int i = 5;
            int j = 4;

            for (BlockPos blockpos : BlockPos.getAllInBoxMutable(pos.add(-4, -1, -4), pos.add(4, 1, 4)))
            {
                if (worldIn.getBlockState(blockpos).getBlock() == this)
                {
                    --i;

                    if (i <= 0)
                    {
                        return;
                    }
                }
            }

            BlockPos blockpos1 = pos.add(rand.nextInt(3) - 1, rand.nextInt(2) - rand.nextInt(2), rand.nextInt(3) - 1);

            for (int k = 0; k < 4; ++k)
            {
                if (worldIn.isAirBlock(blockpos1) && this.canBlockStay(worldIn, blockpos1, this.getDefaultState()))
                {
                    pos = blockpos1;
                }

                blockpos1 = pos.add(rand.nextInt(3) - 1, rand.nextInt(2) - rand.nextInt(2), rand.nextInt(3) - 1);
            }

            if (worldIn.isAirBlock(blockpos1) && this.canBlockStay(worldIn, blockpos1, this.getDefaultState()))
            {
                // CraftBukkit start
                org.bukkit.World bworld = worldIn.getWorld();
                org.bukkit.block.BlockState blockState = bworld.getBlockAt(blockpos1.getX(), blockpos1.getY(), blockpos1.getZ()).getState();
                blockState.setType(org.bukkit.craftbukkit.util.CraftMagicNumbers.getMaterial(this)); // nms: this.id, 0, 2

                org.bukkit.event.block.BlockSpreadEvent event = new org.bukkit.event.block.BlockSpreadEvent(blockState.getBlock(), bworld.getBlockAt(sourceX, sourceY, sourceZ), blockState);
                worldIn.getServer().getPluginManager().callEvent(event);
                if (!event.isCancelled()) blockState.update(true);
                // CraftBukkit end
            }
        }
    }

    public boolean canPlaceBlockAt(World worldIn, BlockPos pos)
    {
        return super.canPlaceBlockAt(worldIn, pos) && this.canBlockStay(worldIn, pos, this.getDefaultState());
    }

    protected boolean canSustainBush(IBlockState state)
    {
        return state.isFullBlock();
    }

    public boolean canBlockStay(World worldIn, BlockPos pos, IBlockState state)
    {
        if (pos.getY() >= 0 && pos.getY() < 256)
        {
            IBlockState iblockstate = worldIn.getBlockState(pos.down());

            if (iblockstate.getBlock() == Blocks.MYCELIUM)
            {
                return true;
            }
            else if (iblockstate.getBlock() == Blocks.DIRT && iblockstate.getValue(BlockDirt.VARIANT) == BlockDirt.DirtType.PODZOL)
            {
                return true;
            }
            else
            {
                return worldIn.getLight(pos) < 13 && iblockstate.getBlock().canSustainPlant(iblockstate, worldIn, pos.down(), net.minecraft.util.EnumFacing.UP, this);
            }
        }
        else
        {
            return false;
        }
    }

    public boolean generateBigMushroom(World worldIn, BlockPos pos, IBlockState state, Random rand)
    {
        worldIn.setBlockToAir(pos);
        WorldGenerator worldgenerator = null;

        if (this == Blocks.BROWN_MUSHROOM)
        {
            BlockSapling.treeType = TreeType.BROWN_MUSHROOM; // CraftBukkit
            worldgenerator = new WorldGenBigMushroom(Blocks.BROWN_MUSHROOM_BLOCK);
        }
        else if (this == Blocks.RED_MUSHROOM)
        {
            BlockSapling.treeType = TreeType.RED_MUSHROOM; // CraftBukkit
            worldgenerator = new WorldGenBigMushroom(Blocks.RED_MUSHROOM_BLOCK);
        }

        if (worldgenerator != null && worldgenerator.generate(worldIn, rand, pos))
        {
            return true;
        }
        else
        {
            worldIn.setBlockState(pos, state, 3);
            return false;
        }
    }

    public boolean canGrow(World worldIn, BlockPos pos, IBlockState state, boolean isClient)
    {
        return true;
    }

    public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, IBlockState state)
    {
        return (double)rand.nextFloat() < 0.4D;
    }

    public void grow(World worldIn, Random rand, BlockPos pos, IBlockState state)
    {
        this.generateBigMushroom(worldIn, pos, state, rand);
    }
}