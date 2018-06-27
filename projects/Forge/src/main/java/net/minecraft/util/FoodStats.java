package net.minecraft.util;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.EnumDifficulty;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class FoodStats
{
    public int foodLevel = 20; // Akarin Forge - public
    private float foodSaturationLevel = 5.0F;
    private float foodExhaustionLevel;
    private int foodTimer;
    private int prevFoodLevel = 20;

    public void addStats(int foodLevelIn, float foodSaturationModifier)
    {
        this.foodLevel = Math.min(foodLevelIn + this.foodLevel, 20);
        this.foodSaturationLevel = Math.min(this.foodSaturationLevel + (float)foodLevelIn * foodSaturationModifier * 2.0F, (float)this.foodLevel);
    }

    public void addStats(ItemFood foodItem, ItemStack stack)
    {
        this.addStats(foodItem.getHealAmount(stack), foodItem.getSaturationModifier(stack));
    }

    public void onUpdate(EntityPlayer player)
    {
        EnumDifficulty enumdifficulty = player.world.getDifficulty();
        this.prevFoodLevel = this.foodLevel;

        if (this.foodExhaustionLevel > 4.0F)
        {
            this.foodExhaustionLevel -= 4.0F;

            if (this.foodSaturationLevel > 0.0F)
            {
                this.foodSaturationLevel = Math.max(this.foodSaturationLevel - 1.0F, 0.0F);
            }
            else if (enumdifficulty != EnumDifficulty.PEACEFUL)
            {
                this.foodLevel = Math.max(this.foodLevel - 1, 0);
            }
        }

        boolean flag = player.world.getGameRules().getBoolean("naturalRegeneration");

        if (flag && this.foodSaturationLevel > 0.0F && player.shouldHeal() && this.foodLevel >= 20)
        {
            ++this.foodTimer;

            if (this.foodTimer >= 10)
            {
                float f = Math.min(this.foodSaturationLevel, 6.0F);
                player.heal(f / 6.0F);
                this.addExhaustion(f);
                this.foodTimer = 0;
            }
        }
        else if (flag && this.foodLevel >= 18 && player.shouldHeal())
        {
            ++this.foodTimer;

            if (this.foodTimer >= 80)
            {
                player.heal(1.0F);
                this.addExhaustion(6.0F);
                this.foodTimer = 0;
            }
        }
        else if (this.foodLevel <= 0)
        {
            ++this.foodTimer;

            if (this.foodTimer >= 80)
            {
                if (player.getHealth() > 10.0F || enumdifficulty == EnumDifficulty.HARD || player.getHealth() > 1.0F && enumdifficulty == EnumDifficulty.NORMAL)
                {
                    player.attackEntityFrom(DamageSource.STARVE, 1.0F);
                }

                this.foodTimer = 0;
            }
        }
        else
        {
            this.foodTimer = 0;
        }
    }

    public void readNBT(NBTTagCompound compound)
    {
        if (compound.hasKey("foodLevel", 99))
        {
            this.foodLevel = compound.getInteger("foodLevel");
            this.foodTimer = compound.getInteger("foodTickTimer");
            this.foodSaturationLevel = compound.getFloat("foodSaturationLevel");
            this.foodExhaustionLevel = compound.getFloat("foodExhaustionLevel");
        }
    }

    public void writeNBT(NBTTagCompound compound)
    {
        compound.setInteger("foodLevel", this.foodLevel);
        compound.setInteger("foodTickTimer", this.foodTimer);
        compound.setFloat("foodSaturationLevel", this.foodSaturationLevel);
        compound.setFloat("foodExhaustionLevel", this.foodExhaustionLevel);
    }

    public int getFoodLevel()
    {
        return this.foodLevel;
    }

    public boolean needFood()
    {
        return this.foodLevel < 20;
    }

    public void addExhaustion(float exhaustion)
    {
        this.foodExhaustionLevel = Math.min(this.foodExhaustionLevel + exhaustion, 40.0F);
    }

    public float getSaturationLevel()
    {
        return this.foodSaturationLevel;
    }

    public void setFoodLevel(int foodLevelIn)
    {
        this.foodLevel = foodLevelIn;
    }

    @SideOnly(Side.CLIENT)
    public void setFoodSaturationLevel(float foodSaturationLevelIn)
    {
        this.foodSaturationLevel = foodSaturationLevelIn;
    }
}