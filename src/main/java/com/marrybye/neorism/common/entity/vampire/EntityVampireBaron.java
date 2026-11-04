package com.marrybye.neorism.common.entity.vampire;

import java.util.Random;

import com.marrybye.neorism.api.entity.IVampireEntity;
import com.marrybye.neorism.common.core.ModItems;
import com.marrybye.neorism.common.entity.ai.EntityAIHurtByTargetFaction;
import com.marrybye.neorism.common.entity.projectile.EntityDarkBloodProjectile;
import com.marrybye.neorism.common.util.Helper;
import com.marrybye.neorism.common.util.ModLogger;
import com.marrybye.neorism.common.vampire.VampirePlayer;

import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

/**
 * Vampire Baron miniboss found in ancient crypts and dark spruce groves.
 * Casts dark blood projectiles, scales across levels 0-4, and drops corresponding pure blood tiers.
 */
public class EntityVampireBaron extends EntityVampireBase {

    public static final int MAX_LEVEL = 4;
    private static final int WATCHER_LEVEL = 20;
    private static final int WATCHER_LADY = 21;
    private static final int WATCHER_ENRAGED = 22;

    public static final int ABILITY_NONE = 0;
    public static final int ABILITY_BAT_SWARM = 1;
    public static final int ABILITY_BLOOD_SIPHON = 2;
    public static final int ABILITY_SHADOW_DASH = 3;
    public static final int ABILITY_VAMPIRE_RAGE = 4;

    private int specialAbility = ABILITY_NONE;
    private int abilityCooldown = 80;
    private int castCooldown = 40;
    private String baronBaseName = null;

    private static final String[] MALE_FIRST_NAMES = {
        "Влад", "Виктор", "Кассиан", "Люсьен", "Алистер", "Винсент", "Дамиан",
        "Морбиус", "Бальтазар", "Габриэль", "Раду", "Родерик", "Валериус", "Малакар", "Корнелиус"
    };

    private static final String[] FEMALE_FIRST_NAMES = {
        "Кармилла", "Элизабета", "Ленора", "Серафина", "Морвенна", "Изольда", "Розалинда",
        "Лилит", "Ванесса", "Вивьен", "Аурелия", "Селена", "Доротея", "Элеонора", "Моргана"
    };

    private static final String[] PARTICLES = {
        "фон", "де", "ван", "цу", "де ла", "дю"
    };

    private static final String[] SURNAMES = {
        "Дракула", "Цепеш", "Батори", "Карнштейн", "Корвинус", "Равенлофт", "Карштайн",
        "Сангвис", "Найтшейд", "Блэквуд", "Шадоуэнд", "Мортис", "Вейн", "Мальморт", "Бладворт"
    };

    public EntityVampireBaron(World world) {
        super(world);
        this.setSize(0.7F, 2.1F);
        this.experienceValue = 35;
        this.setAlwaysRenderNameTag(true);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataWatcher.addObject(WATCHER_LEVEL, Integer.valueOf(0));
        this.dataWatcher.addObject(WATCHER_LADY, Byte.valueOf((byte) 0));
        this.dataWatcher.addObject(WATCHER_ENRAGED, Byte.valueOf((byte) 0));
    }

    public void generateBaronName() {
        boolean lady = isLady();
        String firstName = lady
            ? FEMALE_FIRST_NAMES[this.rand.nextInt(FEMALE_FIRST_NAMES.length)]
            : MALE_FIRST_NAMES[this.rand.nextInt(MALE_FIRST_NAMES.length)];
        String surname = SURNAMES[this.rand.nextInt(SURNAMES.length)];

        StringBuilder sb = new StringBuilder();
        sb.append(lady ? "Баронесса " : "Барон ");
        sb.append(firstName).append(" ");
        if (this.rand.nextFloat() < 0.45F) {
            String particle = PARTICLES[this.rand.nextInt(PARTICLES.length)];
            sb.append(particle).append(" ");
        }
        sb.append(surname);
        this.baronBaseName = sb.toString();
        updateCustomNameTag();
    }

    public void updateCustomNameTag() {
        if (this.baronBaseName == null) {
            generateBaronName();
            return;
        }
        int displayLevel = getEntityLevel() + 1; // 1 to 5
        this.setCustomNameTag(this.baronBaseName + " (Ур. " + displayLevel + ")");
        this.setAlwaysRenderNameTag(true);
    }

    @Override
    protected void setupAI() {
        this.getNavigator().setBreakDoors(true);
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(1, new com.marrybye.neorism.common.entity.ai.EntityAISunFlee(this, 1.25D));
        this.tasks.addTask(2, new EntityAIAttackOnCollide(this, EntityLivingBase.class, 1.15D, false));
        this.tasks.addTask(4, new EntityAIMoveTowardsRestriction(this, 1.0D));
        this.tasks.addTask(5, new EntityAIWander(this, 0.7D));
        this.tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 10.0F));
        this.tasks.addTask(7, new EntityAILookIdle(this));

        this.targetTasks.addTask(1, new EntityAIHurtByTargetFaction(this, com.marrybye.neorism.common.core.ModFactions.VAMPIRE));
        this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, 0, true, false, new IEntitySelector() {
            @Override
            public boolean isEntityApplicable(Entity entity) {
                if (entity instanceof EntityPlayer) {
                    EntityPlayer player = (EntityPlayer) entity;
                    int pLevel = Helper.getLevel(player);
                    return (pLevel - 8) / 2.0F - getEntityLevel() <= 0;
                }
                return false;
            }
        }));
        this.targetTasks.addTask(3, new EntityAINearestAttackableTarget(this, EntityVampireBaron.class, 0, true));
    }

    public int getEntityLevel() {
        return MathHelper.clamp_int(this.dataWatcher.getWatchableObjectInt(WATCHER_LEVEL), 0, MAX_LEVEL);
    }

    public void setEntityLevel(int level) {
        int clamped = MathHelper.clamp_int(level, 0, MAX_LEVEL);
        this.dataWatcher.updateObject(WATCHER_LEVEL, Integer.valueOf(clamped));
        this.experienceValue = 20 + 5 * clamped;
        this.updateAttributesForLevel();
        this.updateCustomNameTag();
    }

    public boolean isLady() {
        return this.dataWatcher.getWatchableObjectByte(WATCHER_LADY) != 0;
    }

    public void setLady(boolean lady) {
        this.dataWatcher.updateObject(WATCHER_LADY, Byte.valueOf((byte) (lady ? 1 : 0)));
        if (this.baronBaseName != null) {
            this.generateBaronName();
        }
    }

    public boolean isEnraged() {
        return this.dataWatcher.getWatchableObjectByte(WATCHER_ENRAGED) != 0;
    }

    public void setEnraged(boolean enraged) {
        this.dataWatcher.updateObject(WATCHER_ENRAGED, Byte.valueOf((byte) (enraged ? 1 : 0)));
    }

    @Override
    public void setAttackTarget(EntityLivingBase target) {
        super.setAttackTarget(target);
        this.setEnraged(target != null);
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(150.0D);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.30D);
        this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(8.0D);
        this.getEntityAttribute(SharedMonsterAttributes.knockbackResistance).setBaseValue(0.5D);
    }

    public void updateAttributesForLevel() {
        int lvl = getEntityLevel();
        double hp = 120.0D + lvl * 25.0D;
        double dmg = 7.0D + lvl * 2.0D;
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(hp);
        this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(dmg);
        this.setHealth((float) hp);
    }

    @Override
    public boolean attackEntityAsMob(Entity target) {
        boolean flag = super.attackEntityAsMob(target);
        if (flag && target instanceof EntityLivingBase) {
            EntityLivingBase living = (EntityLivingBase) target;
            float tm = 1.0F;
            int mr = 1;
            if (living instanceof EntityPlayer) {
                float pld = (this.getEntityLevel() + 1) - Helper.getLevel((EntityPlayer) living) / 3.0F;
                tm = Math.max(1.0F, pld + 1.0F);
                mr = pld < 1.5F ? 1 : (pld < 3.0F ? 2 : 3);
            }
            if (living instanceof EntityVampireBaron) {
                living.addPotionEffect(new PotionEffect(Potion.damageBoost.id, 40, 2));
            }
            living.addPotionEffect(new PotionEffect(Potion.weakness.id, (int) (200 * tm), this.rand.nextInt(mr)));
            living.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, (int) (100 * tm), this.rand.nextInt(mr)));
        }
        return flag;
    }

    public void initSpecialAbility() {
        if (this.specialAbility == ABILITY_NONE) {
            this.specialAbility = 1 + this.rand.nextInt(4);
        }
    }

    public int getSpecialAbility() {
        return this.specialAbility;
    }

    public void setSpecialAbility(int ability) {
        this.specialAbility = ability;
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();

        if (!this.worldObj.isRemote && this.isEntityAlive()) {
            if (this.baronBaseName == null) {
                generateBaronName();
            }
            if (this.specialAbility == ABILITY_NONE) {
                initSpecialAbility();
            }

            EntityLivingBase target = this.getAttackTarget();

            if (target != null && target.isEntityAlive()) {
                double distSq = this.getDistanceSqToEntity(target);

                // Special Combat Ability execution
                if (--abilityCooldown <= 0) {
                    useSpecialAbility(target, distSq);
                }

                // Ranged Dark Blood Projectile attack (canonical BaronAIAttackRanged)
                if (--castCooldown <= 0 && distSq > 16.0D && distSq < 400.0D && this.canEntityBeSeen(target)) {
                    castDarkBlood(target);
                    if (this.rand.nextInt(9) == 0) {
                        this.addPotionEffect(new PotionEffect(Potion.invisibility.id, 60, 0));
                    }
                    castCooldown = 50 + this.rand.nextInt(20);
                }
            }
        }
    }

    private void useSpecialAbility(EntityLivingBase target, double distSq) {
        switch (this.specialAbility) {
            case ABILITY_BAT_SWARM: {
                if (distSq < 256.0D && this.canEntityBeSeen(target)) {
                    this.worldObj.playSoundAtEntity(this, "mob.bat.takeoff", 1.2F, 0.8F);
                    this.worldObj.playSoundAtEntity(target, "mob.wither.shoot", 0.7F, 1.4F);
                    target.addPotionEffect(new PotionEffect(Potion.blindness.id, 80, 0));
                    target.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 80, 1));
                    for (int i = 0; i < 4; i++) {
                        net.minecraft.entity.passive.EntityBat bat = new net.minecraft.entity.passive.EntityBat(this.worldObj);
                        bat.setPosition(target.posX + (this.rand.nextDouble() - 0.5D) * 3.0D,
                            target.posY + 1.0D + this.rand.nextDouble() * 1.5D,
                            target.posZ + (this.rand.nextDouble() - 0.5D) * 3.0D);
                        this.worldObj.spawnEntityInWorld(bat);
                    }
                    this.swingItem();
                    abilityCooldown = 180 + this.rand.nextInt(60);
                    ModLogger.combat("Vampire Baron %s unleashed Bat Swarm on %s", this.getCommandSenderName(), target.getCommandSenderName());
                }
                break;
            }
            case ABILITY_BLOOD_SIPHON: {
                if (this.getHealth() < this.getMaxHealth() * 0.75F && distSq < 144.0D) {
                    this.worldObj.playSoundAtEntity(this, "random.drink", 1.0F, 0.8F);
                    this.worldObj.playSoundAtEntity(this, "mob.endermen.scream", 0.7F, 0.6F);
                    target.attackEntityFrom(net.minecraft.util.DamageSource.magic, 4.0F);
                    this.heal(18.0F);
                    this.addPotionEffect(new PotionEffect(Potion.regeneration.id, 60, 1));
                    if (this.worldObj instanceof net.minecraft.world.WorldServer) {
                        net.minecraft.world.WorldServer ws = (net.minecraft.world.WorldServer) this.worldObj;
                        ws.func_147487_a("heart", this.posX, this.posY + 1.8D, this.posZ, 8, 0.4D, 0.4D, 0.4D, 0.0D);
                        ws.func_147487_a("reddust", target.posX, target.posY + 1.0D, target.posZ, 12, 0.3D, 0.5D, 0.3D, 0.0D);
                    }
                    abilityCooldown = 220 + this.rand.nextInt(80);
                    ModLogger.combat("Vampire Baron %s channeled Blood Siphon on %s", this.getCommandSenderName(), target.getCommandSenderName());
                }
                break;
            }
            case ABILITY_SHADOW_DASH: {
                if (distSq > 36.0D && this.canEntityBeSeen(target)) {
                    double yawRad = Math.toRadians(target.rotationYaw);
                    double nx = target.posX - Math.sin(yawRad) * 2.2D;
                    double nz = target.posZ + Math.cos(yawRad) * 2.2D;
                    double ny = target.posY;

                    this.worldObj.playSoundAtEntity(this, "mob.endermen.portal", 1.0F, 0.8F);
                    if (this.worldObj instanceof net.minecraft.world.WorldServer) {
                        net.minecraft.world.WorldServer ws = (net.minecraft.world.WorldServer) this.worldObj;
                        ws.func_147487_a("largesmoke", this.posX, this.posY + 1.0D, this.posZ, 15, 0.3D, 0.5D, 0.3D, 0.05D);
                        ws.func_147487_a("portal", nx, ny + 1.0D, nz, 20, 0.4D, 0.6D, 0.4D, 0.1D);
                    }
                    this.setPositionAndUpdate(nx, ny, nz);
                    target.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 40, 1));
                    this.swingItem();
                    abilityCooldown = 160 + this.rand.nextInt(60);
                    ModLogger.combat("Vampire Baron %s executed Shadow Dash behind %s", this.getCommandSenderName(), target.getCommandSenderName());
                }
                break;
            }
            case ABILITY_VAMPIRE_RAGE: {
                if (this.getHealth() < this.getMaxHealth() * 0.5F) {
                    this.worldObj.playSoundAtEntity(this, "mob.wolf.growl", 1.5F, 0.6F);
                    this.addPotionEffect(new PotionEffect(Potion.damageBoost.id, 140, 1));
                    this.addPotionEffect(new PotionEffect(Potion.moveSpeed.id, 140, 1));
                    this.addPotionEffect(new PotionEffect(Potion.resistance.id, 140, 0));
                    if (this.worldObj instanceof net.minecraft.world.WorldServer) {
                        net.minecraft.world.WorldServer ws = (net.minecraft.world.WorldServer) this.worldObj;
                        ws.func_147487_a("flame", this.posX, this.posY + 1.5D, this.posZ, 12, 0.3D, 0.4D, 0.3D, 0.02D);
                        ws.func_147487_a("angryVillager", this.posX, this.posY + 2.0D, this.posZ, 5, 0.3D, 0.3D, 0.3D, 0.0D);
                    }
                    abilityCooldown = 260 + this.rand.nextInt(100);
                    ModLogger.combat("Vampire Baron %s entered Vampire Rage", this.getCommandSenderName());
                }
                break;
            }
            default:
                break;
        }
    }

    private void castDarkBlood(EntityLivingBase target) {
        float damage = 6.0F + getEntityLevel() * 1.5F;
        EntityDarkBloodProjectile projectile = new EntityDarkBloodProjectile(this.worldObj, this, damage);
        double dx = target.posX - this.posX;
        double dy = target.posY + (double) target.getEyeHeight() - 1.1D - projectile.posY;
        double dz = target.posZ - this.posZ;
        projectile.setThrowableHeading(dx, dy, dz, 1.4F, 2.0F);

        this.worldObj.playSoundAtEntity(this, "mob.ghast.fireball", 1.0F, 0.8F);
        this.worldObj.spawnEntityInWorld(projectile);
        this.swingItem();

        ModLogger.combat("Vampire Baron %s (level %d) cast Dark Blood Projectile at %s",
            this.getCommandSenderName(), getEntityLevel(), target.getCommandSenderName());
    }

    @Override
    protected void dropFewItems(boolean recentlyHit, int looting) {
        // Pure blood matching baron level (meta 0..4)
        int lvl = getEntityLevel();
        if (recentlyHit) {
            this.entityDropItem(new ItemStack(ModItems.pureBlood, 1, lvl), 0.0F);
        }

        // Fangs
        this.dropItem(ModItems.vampireFang, 2 + this.rand.nextInt(3 + looting));

        if (this.rand.nextInt(2) == 0) {
            this.dropItem(ModItems.bloodInfusedDiamond, 1);
        }

        if (this.rand.nextInt(3) == 0) {
            this.entityDropItem(new ItemStack(ModItems.vampireBook, 1, Math.min(5, 3 + lvl)), 0.0F);
        }
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound nbt) {
        super.writeEntityToNBT(nbt);
        nbt.setInteger("level", getEntityLevel());
        nbt.setBoolean("lady", isLady());
        nbt.setInteger("specialAbility", this.specialAbility);
        if (baronBaseName != null) {
            nbt.setString("baronBaseName", baronBaseName);
        }
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound nbt) {
        super.readEntityFromNBT(nbt);
        if (nbt.hasKey("level")) {
            setEntityLevel(nbt.getInteger("level"));
        }
        if (nbt.hasKey("lady")) {
            setLady(nbt.getBoolean("lady"));
        }
        if (nbt.hasKey("specialAbility")) {
            this.specialAbility = nbt.getInteger("specialAbility");
        }
        if (nbt.hasKey("baronBaseName")) {
            this.baronBaseName = nbt.getString("baronBaseName");
            updateCustomNameTag();
        }
    }

    public static int getRandomBaronLevel(World world, double x, double y, double z, Random rand) {
        EntityPlayer nearest = world.getClosestPlayer(x, y, z, 128.0D);
        int playerLevel = (nearest != null) ? Helper.getLevel(nearest) : 0;
        float r = rand.nextFloat();
        if (playerLevel >= 12) {
            if (r < 0.15F) return 0;
            if (r < 0.35F) return 1;
            if (r < 0.60F) return 2;
            if (r < 0.85F) return 3;
            return 4;
        } else if (playerLevel >= 8) {
            if (r < 0.25F) return 0;
            if (r < 0.55F) return 1;
            if (r < 0.80F) return 2;
            if (r < 0.94F) return 3;
            return 4;
        } else {
            // Default weighted progression: Tier 1 is common, Tier 5 is rare
            if (r < 0.38F) return 0; // Tier 1: 38%
            if (r < 0.65F) return 1; // Tier 2: 27%
            if (r < 0.83F) return 2; // Tier 3: 18%
            if (r < 0.94F) return 3; // Tier 4: 11%
            return 4;                // Tier 5: 6%
        }
    }

    @Override
    public boolean getCanSpawnHere() {
        int x = MathHelper.floor_double(this.posX);
        int y = MathHelper.floor_double(this.boundingBox.minY);
        int z = MathHelper.floor_double(this.posZ);
        if (y < 60) {
            return false;
        }
        if (!Helper.isVampireBiome(this.worldObj.getBiomeGenForCoords(x, z))) {
            return false;
        }
        net.minecraft.block.Block ground = this.worldObj.getBlock(x, y - 1, z);
        if (ground != com.marrybye.neorism.common.core.ModBlocks.cursedGrass && ground != com.marrybye.neorism.common.core.ModBlocks.cursedEarth) {
            return false;
        }
        return super.getCanSpawnHere();
    }

    @Override
    public net.minecraft.entity.IEntityLivingData onSpawnWithEgg(net.minecraft.entity.IEntityLivingData data) {
        data = super.onSpawnWithEgg(data);
        this.setLady(this.rand.nextBoolean());
        this.setEntityLevel(getRandomBaronLevel(this.worldObj, this.posX, this.posY, this.posZ, this.rand));
        this.generateBaronName();
        this.initSpecialAbility();
        return data;
    }

    @Override
    protected boolean canDespawn() {
        return !this.hasHome();
    }
}
