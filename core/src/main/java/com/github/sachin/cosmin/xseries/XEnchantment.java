
package com.github.sachin.cosmin.xseries;

import com.google.common.base.Enums;
import com.google.common.base.Strings;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang.WordUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

/**
 * Xmaterial class created by Crypto Morin
 * The spigot forum page can be found here https://www.spigotmc.org/threads/378136/
 */
public enum XEnchantment {
    ARROW_DAMAGE("POWER", "ARROW_DAMAGE", "ARROW_POWER", "AD"),
    ARROW_FIRE("FLAME", "FLAME_ARROW", "FIRE_ARROW", "AF"),
    ARROW_INFINITE("INFINITY", "INF_ARROWS", "INFINITE_ARROWS", "INFINITE", "UNLIMITED", "UNLIMITED_ARROWS", "AI"),
    ARROW_KNOCKBACK("PUNCH", "ARROW_KNOCKBACK", "ARROWKB", "ARROW_PUNCH", "AK"),
    BINDING_CURSE(true, "BINDING_CURSE", "BIND_CURSE", "BINDING", "BIND"),
    CHANNELING(true, "CHANNELLING", "CHANELLING", "CHANELING", "CHANNEL"),
    DAMAGE_ALL("SHARPNESS", "ALL_DAMAGE", "ALL_DMG", "SHARP", "DAL"),
    DAMAGE_ARTHROPODS("BANE_OF_ARTHROPODS", "ARDMG", "BANE_OF_ARTHROPOD", "ARTHROPOD", "DAR"),
    DAMAGE_UNDEAD("SMITE", "UNDEAD_DAMAGE", "DU"),
    DEPTH_STRIDER(true, "DEPTH", "STRIDER"),
    DIG_SPEED("EFFICIENCY", "MINE_SPEED", "CUT_SPEED", "DS", "EFF"),
    DURABILITY("UNBREAKING", "DURA"),
    FIRE_ASPECT(true, "FIRE", "MELEE_FIRE", "MELEE_FLAME", "FA"),
    FROST_WALKER(true, "FROST", "WALKER"),
    IMPALING(true, "IMPALE", "OCEAN_DAMAGE", "OCEAN_DMG"),
    SOUL_SPEED(true, "SPEED_SOUL", "SOUL_RUNNER"),
    KNOCKBACK(true, "K_BACK", "KB"),
    LOOT_BONUS_BLOCKS("FORTUNE", "BLOCKS_LOOT_BONUS", "FORT", "LBB"),
    LOOT_BONUS_MOBS("LOOTING", "MOB_LOOT", "MOBS_LOOT_BONUS", "LBM"),
    LOYALTY(true, "LOYAL", "RETURN"),
    LUCK("LUCK_OF_THE_SEA", "LUCK_OF_SEA", "LUCK_OF_SEAS", "ROD_LUCK"),
    LURE(true, "ROD_LURE"),
    MENDING(true),
    MULTISHOT(true, "TRIPLE_SHOT"),
    OXYGEN("RESPIRATION", "BREATH", "BREATHING", "O2", "O"),
    PIERCING(true),
    PROTECTION_ENVIRONMENTAL("PROTECTION", "PROTECT", "PROT"),
    PROTECTION_EXPLOSIONS("BLAST_PROTECTION", "BLAST_PROTECT", "EXPLOSIONS_PROTECTION", "EXPLOSION_PROTECTION", "BLAST_PROTECTION", "PE"),
    PROTECTION_FALL("FEATHER_FALLING", "FALL_PROT", "FEATHER_FALL", "FALL_PROTECTION", "FEATHER_FALLING", "PFA"),
    PROTECTION_FIRE("FIRE_PROTECTION", "FIRE_PROT", "FIRE_PROTECT", "FIRE_PROTECTION", "FLAME_PROTECTION", "FLAME_PROTECT", "FLAME_PROT", "PF"),
    PROTECTION_PROJECTILE("PROJECTILE_PROTECTION", "PROJECTILE_PROTECTION", "PROJ_PROT", "PP"),
    QUICK_CHARGE(true, "QUICKCHARGE", "QUICK_DRAW", "FAST_CHARGE", "FAST_DRAW"),
    RIPTIDE(true, "RIP", "TIDE", "LAUNCH"),
    SILK_TOUCH(true, "SOFT_TOUCH", "ST"),
    SWEEPING_EDGE("SWEEPING", "SWEEPING_EDGE", "SWEEP_EDGE"),
    THORNS(true, "HIGHCRIT", "THORN", "HIGHERCRIT", "T"),
    VANISHING_CURSE(true, "VANISHING_CURSE", "VANISH_CURSE", "VANISHING", "VANISH"),
    WATER_WORKER("AQUA_AFFINITY", "WATER_WORKER", "AQUA_AFFINITY", "WATER_MINE", "WW");

   
    public static final List<XEnchantment> VALUES = Collections.unmodifiableList(Arrays.asList(values()));
   
    public static final Set<EntityType> EFFECTIVE_SMITE_ENTITIES;
   
    public static final Set<EntityType> EFFECTIVE_BANE_OF_ARTHROPODS_ENTITIES;

    static {
        EntityType bee = Enums.getIfPresent(EntityType.class, "BEE").orNull();
        EntityType phantom = Enums.getIfPresent(EntityType.class, "PHANTOM").orNull();
        EntityType drowned = Enums.getIfPresent(EntityType.class, "DROWNED").orNull();
        EntityType witherSkeleton = Enums.getIfPresent(EntityType.class, "WITHER_SKELETON").orNull();
        EntityType skeletonHorse = Enums.getIfPresent(EntityType.class, "SKELETON_HORSE").orNull();
        EntityType stray = Enums.getIfPresent(EntityType.class, "STRAY").orNull();
        EntityType husk = Enums.getIfPresent(EntityType.class, "HUSK").orNull();

        Set<EntityType> arthorposEffective = EnumSet.of(EntityType.SPIDER, EntityType.CAVE_SPIDER, EntityType.SILVERFISH, EntityType.ENDERMITE);
        if (bee != null) arthorposEffective.add(bee);
        EFFECTIVE_BANE_OF_ARTHROPODS_ENTITIES = Collections.unmodifiableSet(arthorposEffective);

        Set<EntityType> smiteEffective = EnumSet.of(EntityType.ZOMBIE, EntityType.SKELETON, EntityType.WITHER);
        if (phantom != null) smiteEffective.add(phantom);
        if (drowned != null) smiteEffective.add(drowned);
        if (witherSkeleton != null) smiteEffective.add(witherSkeleton);
        if (skeletonHorse != null) smiteEffective.add(skeletonHorse);
        if (stray != null) smiteEffective.add(stray);
        if (husk != null) smiteEffective.add(husk);
        EFFECTIVE_SMITE_ENTITIES = Collections.unmodifiableSet(smiteEffective);
    }

    @Nullable
    private final Enchantment enchantment;

    XEnchantment(@Nonnull String... names) {
        this(false, names);
    }

    
    @SuppressWarnings("deprecation")
    XEnchantment(boolean self, @Nonnull String... aliases) {
        Data.NAMES.put(this.name(), this);
        for (String legacy : aliases) Data.NAMES.put(legacy, this);

        Enchantment enchantment;
        if (Data.ISFLAT) {
            String vanilla = self ? this.name() : aliases[0];
            enchantment = Enchantment.getByKey(NamespacedKey.minecraft(vanilla.toLowerCase(Locale.ENGLISH)));
        } else enchantment = Enchantment.getByName(this.name());
        this.enchantment = enchantment;
    }

   
    public static boolean isSmiteEffectiveAgainst(@Nullable EntityType type) {
        return type != null && EFFECTIVE_SMITE_ENTITIES.contains(type);
    }

   
    public static boolean isArthropodsEffectiveAgainst(@Nullable EntityType type) {
        return type != null && EFFECTIVE_BANE_OF_ARTHROPODS_ENTITIES.contains(type);
    }

    
    @Nonnull
    private static String format(@Nonnull String name) {
        int len = name.length();
        char[] chs = new char[len];
        int count = 0;
        boolean appendUnderline = false;

        for (int i = 0; i < len; i++) {
            char ch = name.charAt(i);

            if (!appendUnderline && count != 0 && (ch == '-' || ch == ' ' || ch == '_') && chs[count] != '_') appendUnderline = true;
            else {
                if ((ch >= 'A' && ch <= 'Z') || (ch >= 'a' && ch <= 'z')) {
                    if (appendUnderline) {
                        chs[count++] = '_';
                        appendUnderline = false;
                    }
                    chs[count++] = (char) (ch & 0x5f);
                }
            }
        }

        return new String(chs, 0, count);
    }

    
    @Nonnull
    public static Optional<XEnchantment> matchXEnchantment(@Nonnull String enchantment) {
        Validate.notEmpty(enchantment, "Enchantment name cannot be null or empty");
        return Optional.ofNullable(Data.NAMES.get(format(enchantment)));
    }

    @Nonnull
    @SuppressWarnings("deprecation")
    public static XEnchantment matchXEnchantment(@Nonnull Enchantment enchantment) {
        Objects.requireNonNull(enchantment, "Cannot parse XEnchantment of a null enchantment");
        return Objects.requireNonNull(Data.NAMES.get(enchantment.getName()), () -> "Unsupported enchantment: " + enchantment.getName());
    }


    @Nonnull
    public static ItemStack addEnchantFromString(@Nonnull ItemStack item, @Nullable String enchantment) {
        Objects.requireNonNull(item, "Cannot add enchantment to null ItemStack");
        if (Strings.isNullOrEmpty(enchantment) || enchantment.equalsIgnoreCase("none")) return item;

        String[] split = StringUtils.split(StringUtils.deleteWhitespace(enchantment), ',');
        if (split.length == 0) split = StringUtils.split(enchantment, ' ');

        Optional<XEnchantment> enchantOpt = matchXEnchantment(split[0]);
        if (!enchantOpt.isPresent()) return item;
        Enchantment enchant = enchantOpt.get().parseEnchantment();
        if (enchant == null) return item;

        int lvl = 1;
        if (split.length > 1) lvl = NumberUtils.toInt(split[1]);

        item.addUnsafeEnchantment(enchant, lvl);
        return item;
    }

    public ItemStack getBook(int level) {
        ItemStack book = new ItemStack(Material.ENCHANTED_BOOK);
        EnchantmentStorageMeta meta = (EnchantmentStorageMeta) book.getItemMeta();

        meta.addStoredEnchant(this.parseEnchantment(), level, true);
        book.setItemMeta(meta);
        return book;
    }

    @Nullable
    public Enchantment parseEnchantment() {
        return this.enchantment;
    }

    public boolean isSupported() {
        return parseEnchantment() != null;
    }

    @Override
    @Nonnull
    public String toString() {
        return WordUtils.capitalize(this.name().replace('_', ' ').toLowerCase(Locale.ENGLISH));
    }

    private static final class Data {
        private static final boolean ISFLAT;
        private static final Map<String, XEnchantment> NAMES = new HashMap<>();

        static {
            boolean flat;
            try {
                Class<?> namespacedKeyClass = Class.forName("org.bukkit.NamespacedKey");
                Class<?> enchantmentClass = Class.forName("org.bukkit.enchantments.Enchantment");
                enchantmentClass.getDeclaredMethod("getByKey", namespacedKeyClass);
                flat = true;
            } catch (ClassNotFoundException | NoSuchMethodException ex) {
                flat = false;
            }
            ISFLAT = flat;
        }
    }
}