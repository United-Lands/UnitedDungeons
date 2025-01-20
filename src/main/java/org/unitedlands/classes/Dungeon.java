package org.unitedlands.classes;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.ShulkerBox;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.unitedlands.UnitedDungeons;
import org.unitedlands.utils.MessageFormatter;

import com.destroystokyo.paper.ParticleBuilder;

import dev.lone.itemsadder.api.CustomStack;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.title.Title.Times;

import net.md_5.bungee.api.ChatColor;

public class Dungeon {

    private final int CYCLES_BEFORE_SLEEP = 300;

    public UUID uuid;
    public String name;
    public String description;
    public Location location;
    public Location exitLocation;
    public boolean isPublicWarp;
    public boolean isActive = false;
    public boolean isSleeping = true;
    public boolean isLocked = false;
    public boolean isLockable = true;

    public int width = 16;
    public int length = 16;
    public int height = 16;

    public boolean doRewardDrop;
    public Location rewardDropLocation;
    public String staticRewards;
    public String randomRewards;
    public int randomRewardsCount;

    public boolean doPressurePlate;
    public Location pressurePlateLocation;

    public boolean isOnCooldown = false;
    public double cooldownTime = 120;
    public double lockTime = 120;

    private Map<UUID, Spawner> spawners = new HashMap<>();

    private double cooldownStart;
    private int cyclesWithoutPlayers = Integer.MAX_VALUE;

    private List<Player> playersInDungeon = new ArrayList<Player>();

    private List<Player> lockedPlayersInDungeon = new ArrayList<Player>();
    private double lockStartTime;

    private final UnitedDungeons plugin = getPlugin();

    public Dungeon() {

    }

    public Dungeon(Location location) {
        this.location = location;
    }

    public Dungeon(File file) {
        load(file);
    }

    public boolean save() {

        if (uuid == null || name == null)
            return false;

        var filePath = File.separator + "dungeons" + File.separator + this.uuid + ".yml";

        File dungeonFile = new File(plugin.getDataFolder(), filePath);
        if (!dungeonFile.exists()) {
            dungeonFile.getParentFile().mkdirs();
            try {
                dungeonFile.createNewFile();
            } catch (IOException ex) {
                plugin.getLogger().severe(ex.getMessage());
                return false;
            }
        }

        FileConfiguration fileConfig = new YamlConfiguration();
        try {
            fileConfig.load(dungeonFile);
            fileConfig.set("uuid", this.uuid.toString());
            fileConfig.set("name", this.name);
            fileConfig.set("description", this.description);
            fileConfig.set("public", this.isPublicWarp);
            fileConfig.set("active", this.isActive);
            fileConfig.set("width", this.width);
            fileConfig.set("length", this.length);
            fileConfig.set("height", this.height);
            fileConfig.set("cooldowntime", this.cooldownTime);
            fileConfig.set("dorewarddrop", this.doRewardDrop);
            fileConfig.set("dopressureplate", this.doPressurePlate);
            fileConfig.set("staticrewards", this.staticRewards);
            fileConfig.set("randomrewards", this.randomRewards);
            fileConfig.set("randomrewardscount", this.randomRewardsCount);
            fileConfig.set("lockable", this.isLockable);
            fileConfig.set("locktime", this.lockTime);

            var locationSection = fileConfig.createSection("location");
            locationSection.set("world", this.location.getWorld().getName());
            locationSection.set("x", this.location.getX());
            locationSection.set("y", this.location.getY());
            locationSection.set("z", this.location.getZ());

            if (exitLocation != null) {
                var exitLocationSection = fileConfig.createSection("exitlocation");
                exitLocationSection.set("world", this.exitLocation.getWorld().getName());
                exitLocationSection.set("x", this.exitLocation.getX());
                exitLocationSection.set("y", this.exitLocation.getY());
                exitLocationSection.set("z", this.exitLocation.getZ());
            }

            if (this.rewardDropLocation != null) {
                var dropSection = fileConfig.createSection("rewarddrop");
                dropSection.set("world", this.rewardDropLocation.getWorld().getName());
                dropSection.set("x", this.rewardDropLocation.getX());
                dropSection.set("y", this.rewardDropLocation.getY());
                dropSection.set("z", this.rewardDropLocation.getZ());
            }

            if (this.pressurePlateLocation != null) {
                var plateSection = fileConfig.createSection("pressureplate");
                plateSection.set("world", this.pressurePlateLocation.getWorld().getName());
                plateSection.set("x", this.pressurePlateLocation.getX());
                plateSection.set("y", this.pressurePlateLocation.getY());
                plateSection.set("z", this.pressurePlateLocation.getZ());
            }

            if (this.spawners != null && !this.spawners.isEmpty()) {
                var spawnerList = fileConfig.createSection("spawners");
                int count = 0;
                for (Spawner s : this.spawners.values()) {
                    var spawnerSection = spawnerList.createSection("spawner" + count);
                    spawnerSection.set("uuid", s.uuid.toString());
                    spawnerSection.set("world", s.getWorld());
                    spawnerSection.set("x", s.getLocation().getX());
                    spawnerSection.set("y", s.getLocation().getY());
                    spawnerSection.set("z", s.getLocation().getZ());
                    spawnerSection.set("yaw", s.getLocation().getYaw());
                    spawnerSection.set("pitch", s.getLocation().getPitch());
                    spawnerSection.set("mobtype", s.mobType);
                    spawnerSection.set("radius", s.radius);
                    spawnerSection.set("frequency", s.spawnFrequency);
                    spawnerSection.set("maxmobs", s.maxMobs);
                    spawnerSection.set("mythicmob", s.isMythicMob);
                    spawnerSection.set("groupspawn", s.isGroupSpawn);
                    spawnerSection.set("killstocomplete", s.killsToComplete);
                    count++;
                }
            }
            fileConfig.save(dungeonFile);
            return true;
        } catch (IOException | InvalidConfigurationException ex) {
            plugin.getLogger().severe(ex.getMessage());
            return false;
        }

    }

    public boolean load(File file) {

        FileConfiguration fileConfig = new YamlConfiguration();
        try {
            fileConfig.load(file);
            this.uuid = UUID.fromString(fileConfig.getString("uuid"));
            this.name = fileConfig.getString("name");
            this.description = fileConfig.getString("description");
            this.isPublicWarp = fileConfig.getBoolean("public", false);
            this.isActive = fileConfig.getBoolean("active", false);
            this.width = fileConfig.getInt("width", 64);
            this.length = fileConfig.getInt("length", 64);
            this.height = fileConfig.getInt("height", 64);

            this.cooldownTime = fileConfig.getDouble("cooldowntime", 120.0);
            this.lockTime = fileConfig.getDouble("locktime", 120.0);
            this.isLockable = fileConfig.getBoolean("lockable", true);
            this.doRewardDrop = fileConfig.getBoolean("dorewarddrop", false);
            this.doPressurePlate = fileConfig.getBoolean("dopressureplate", false);
            this.staticRewards = fileConfig.getString("staticrewards");
            this.randomRewards = fileConfig.getString("randomrewards");
            this.randomRewardsCount = fileConfig.getInt("randomrewardscount", 0);

            ConfigurationSection locationSection = fileConfig.getConfigurationSection("location");
            if (locationSection != null) {
                this.location = new Location(Bukkit.getWorld(locationSection.getString("world")),
                        locationSection.getDouble("x"),
                        locationSection.getDouble("y"),
                        locationSection.getDouble("z"));
            }

            ConfigurationSection exitLocationSection = fileConfig.getConfigurationSection("exitlocation");
            if (exitLocationSection != null) {
                this.exitLocation = new Location(Bukkit.getWorld(exitLocationSection.getString("world")),
                        exitLocationSection.getDouble("x"),
                        exitLocationSection.getDouble("y"),
                        exitLocationSection.getDouble("z"));
            }

            ConfigurationSection rewardSection = fileConfig.getConfigurationSection("rewarddrop");
            if (rewardSection != null) {
                this.rewardDropLocation = new Location(Bukkit.getWorld(rewardSection.getString("world")),
                        rewardSection.getDouble("x"),
                        rewardSection.getDouble("y"),
                        rewardSection.getDouble("z"));
            }

            ConfigurationSection plateSection = fileConfig.getConfigurationSection("pressureplate");
            if (plateSection != null) {
                this.pressurePlateLocation = new Location(Bukkit.getWorld(plateSection.getString("world")),
                        plateSection.getDouble("x"),
                        plateSection.getDouble("y"),
                        plateSection.getDouble("z"));
            }

            this.spawners = new HashMap<UUID, Spawner>();
            ConfigurationSection spawerList = fileConfig.getConfigurationSection("spawners");
            if (spawerList != null) {
                for (String key : spawerList.getKeys(false)) {
                    var spawnerSection = spawerList.getConfigurationSection(key);
                    if (spawnerSection != null) {
                        var spawner = new Spawner();
                        spawner.uuid = UUID.fromString(spawnerSection.getString("uuid"));
                        spawner.setWorld(spawnerSection.getString("world"));
                        spawner.setLocation(new Location(Bukkit.getWorld(spawner.getWorld()), spawnerSection.getDouble("x"),
                                spawnerSection.getDouble("y"), spawnerSection.getDouble("z"), (float)spawnerSection.getDouble("yaw", 0), (float)spawnerSection.getDouble("pitch", 0)));
                        spawner.setDungeon(this);
                        spawner.mobType = spawnerSection.getString("mobtype");
                        spawner.radius = spawnerSection.getInt("radius", 16);
                        spawner.spawnFrequency = spawnerSection.getDouble("frequency", 5.0);
                        spawner.maxMobs = spawnerSection.getInt("maxmobs", 1);
                        spawner.isMythicMob = spawnerSection.getBoolean("mythicmob", false);
                        spawner.isGroupSpawn = spawnerSection.getBoolean("groupspawn", false);
                        spawner.killsToComplete = spawnerSection.getInt("killstocomplete", Integer.MAX_VALUE);

                        addSpawner(spawner);
                    }
                }
            }

            return true;
        } catch (IOException | InvalidConfigurationException ex) {
            plugin.getLogger().severe(ex.getMessage());
            return false;
        }
    }

    public void addSpawner(Spawner spawner) {
        if (this.spawners == null)
            this.spawners = new HashMap<UUID, Spawner>();
        this.spawners.put(spawner.uuid, spawner);
    }

    public void setLocation(Location location) {
        this.location = location.getBlock().getLocation().add(0.5, 0.5, 0.5);
    }

    public void setExitLocation(Location location) {
        var center = location.getBlock().getLocation().add(0.5, 0.5, 0.5);
        this.exitLocation = new Location(center.getWorld(), center.getX(), center.getY(), center.getZ(), location.getYaw(), location.getPitch());
    }

    public Location getBoundingBoxOrigin() {
        return new Location(this.location.getWorld(),
                this.location.getX() - Math.floor(this.width / 2),
                this.location.getY() - Math.floor(this.height / 2),
                this.location.getZ() - Math.floor(this.length / 2));
    }

    public void setRewardDropLocation(Location location) {
        this.rewardDropLocation = location.getBlock().getLocation().add(0.5, 0.5, 0.5);
    }

    public void setPressurePlateLocation(Location location) {
        this.pressurePlateLocation = location.getBlock().getLocation().add(0.5, 0.5, 0.5);
    }

    public void checkPlayerActivity() {
        updatePlayersInDungeon();

        if (playersInDungeon.isEmpty()) {
            if (cyclesWithoutPlayers < Integer.MAX_VALUE)
                cyclesWithoutPlayers++;
            if (cyclesWithoutPlayers >= CYCLES_BEFORE_SLEEP && !this.isSleeping) {
                plugin.getLogger().info("Dungeon " + this.name + " going to sleep.");
                this.isSleeping = true;
            }
        } else {
            if (this.isSleeping) {
                plugin.getLogger().info("Dungeon " + this.name + " waking up.");
                cyclesWithoutPlayers = 0;
                this.isSleeping = false;
            }
            if (this.isLocked) {
                for (Player player : playersInDungeon) {
                    if (player.hasPermission("united.dungeons.admin"))
                        continue;
                    if (!lockedPlayersInDungeon.contains(player)) {
                        player.teleport(this.exitLocation);
                        player.sendMessage(MessageFormatter.getWithPrefix(ChatColor.RED
                                + "This dungeon is locked, and you are not part of the dungeon party. You have been teleported back to the entrance."));
                    }
                }
            }
        }
    }

    public void updatePlayersInDungeon() {
        Collection<Entity> nearbyEntities = this.location.getWorld().getNearbyEntities(this.location, this.width / 2,
                this.height / 2, this.length / 2);

        var currentPlayersInDungeon = new ArrayList<Player>();
        for (Entity entity : nearbyEntities) {
            if (entity instanceof Player) {
                currentPlayersInDungeon.add((Player) entity);
            }
        }

        var pastPlayers = new ArrayList<>(playersInDungeon);
        pastPlayers.removeAll(currentPlayersInDungeon);

        for (Player player : pastPlayers)
            player.sendMessage(MessageFormatter.getWithPrefix("You have left " + getCleanName()));

        var newPlayers = new ArrayList<>(currentPlayersInDungeon);
        newPlayers.removeAll(playersInDungeon);

        var title = Title.title(
                Component.text(getCleanName()).color(NamedTextColor.DARK_RED),
                Component.text(this.description != null ? this.description : ""),
                Times.times(Duration.ofMillis(1000), Duration.ofMillis(3000), Duration.ofMillis(2000)));

        for (Player player : newPlayers) {
            player.showTitle(title);
            player.playSound(player.getLocation(), Sound.AMBIENT_CAVE, 1, 1);

            if (isOnCooldown) {
                player.sendMessage(MessageFormatter.getWithPrefix(
                        "This dungeon is on " + ChatColor.YELLOW + "cooldown" + ChatColor.GRAY
                                + ". It will be open again in " + ChatColor.BOLD + getCooldownTimeString()
                                + "."));
            }
        }

        playersInDungeon = currentPlayersInDungeon;
    }

    public boolean isPlayerInDungeon(Player player) {
        return playersInDungeon.contains(player);
    }

    public boolean isPlayerLockedInDungeon(Player player) {
        return lockedPlayersInDungeon.contains(player);
    }

    public List<Player> getPlayersInDungeon() {
        return playersInDungeon;
    }

    public void lockDungeon() {

        if (isLocked || isOnCooldown || !isActive)
            return;

        updatePlayersInDungeon();

        lockedPlayersInDungeon = playersInDungeon;
        lockStartTime = System.currentTimeMillis();
        isLocked = true;

        for (Player player : lockedPlayersInDungeon) {
            player.sendMessage(MessageFormatter
                    .getWithPrefix("Dungeon " + getCleanName()
                            + " is now locked for your party. No other players can enter in the next "
                            + Math.floor(this.lockTime / 60) + " minutes."));
        }
    }

    public void lockDungeonTest() {

        if (isLocked || isOnCooldown || !isActive)
            return;

        lockedPlayersInDungeon = new ArrayList<>();
        lockStartTime = System.currentTimeMillis();
        isLocked = true;
    }

    public void invitePlayer(Player player) {
        if (!isLocked)
            return;

        if (!lockedPlayersInDungeon.contains(player))
            lockedPlayersInDungeon.add(player);
        
        player.sendMessage(MessageFormatter.getWithPrefix("You have been added to the dungeon party of " + getCleanName()));
    }

    public void removeLockedPlayer(Player player) {
        if (!isLocked)
            return;

        lockedPlayersInDungeon.remove(player);

        if (lockedPlayersInDungeon.isEmpty())
            resetLock();
    }

    public void checkCooldown() {
        if (!isOnCooldown)
            return;

        if (System.currentTimeMillis() - cooldownStart >= cooldownTime * 1000) {
            resetCompletion();
        }
    }

    public void checkLock() {
        if (!isLocked)
            return;

        if (System.currentTimeMillis() - lockStartTime >= this.lockTime * 1000) {

            for (Player player : lockedPlayersInDungeon) {
                player.sendMessage(MessageFormatter
                        .getWithPrefix("Your lock on dungeon " + getCleanName()
                                + " has expired. It is now open for all players again."));
            }
            lockedPlayersInDungeon = new ArrayList<>();
            lockStartTime = 0;
            isLocked = false;
        }
    }

    public void complete() {

        for (var player : playersInDungeon) {
            player.sendMessage(MessageFormatter
                    .getWithPrefix(ChatColor.WHITE + "Area cleared, well done! No new monsters will arrive."));
        }

        if (this.spawners != null && !this.spawners.isEmpty()) {
            for (Spawner spawner : this.spawners.values())
                plugin.getMobManager().removeAllSpawnerMobs(spawner);
        }

        if (this.doRewardDrop && this.rewardDropLocation != null) {

            Block block = this.rewardDropLocation.getBlock();
            block.setType(Material.YELLOW_SHULKER_BOX);

            Bukkit.getScheduler().runTaskLater(plugin, () -> {

                BlockState state = block.getState();
                if (state instanceof ShulkerBox) {
                    ShulkerBox shulker = (ShulkerBox) state;

                    if (this.staticRewards != null) {
                        var staticRewards = ParseRewards(this.staticRewards);
                        if (staticRewards != null && !staticRewards.isEmpty()) {
                            for (var item : staticRewards) {
                                addRewardToShulker(shulker, item);
                            }
                        }
                    }

                    if (this.randomRewards != null && this.randomRewardsCount > 0) {
                        var randomrewards = ParseRewards(this.randomRewards);
                        if (randomrewards != null && !randomrewards.isEmpty()) {
                            {
                                Random random = new Random();
                                for (var i = 0; i < this.randomRewardsCount; i++) {
                                    int index = random.nextInt(randomrewards.size());
                                    var item = randomrewards.get(index);
                                    addRewardToShulker(shulker, item);
                                }
                            }
                        }
                    }
                }

                new ParticleBuilder(Particle.WAX_OFF)
                        .location(rewardDropLocation)
                        .offset(0.6, 0.6, 0.6)
                        .receivers(64)
                        .count(24)
                        .spawn();

                for (var player : playersInDungeon) {
                    ((Player) player).playSound(rewardDropLocation, Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
                    player.sendMessage(MessageFormatter
                            .getWithPrefix(ChatColor.WHITE + "A reward has appeared!"));
                }

            }, 1L);
        }

        if (this.doPressurePlate && this.pressurePlateLocation != null) {

            Block block = this.pressurePlateLocation.getBlock();
            block.setType(Material.BIRCH_PRESSURE_PLATE);

            new ParticleBuilder(Particle.WAX_OFF)
                    .location(pressurePlateLocation)
                    .offset(0.5, 0.5, 0.5)
                    .receivers(64)
                    .count(24)
                    .spawn();

            for (var player : playersInDungeon) {
                ((Player) player).playSound(pressurePlateLocation, Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
                player.sendMessage(MessageFormatter
                        .getWithPrefix(ChatColor.WHITE + "A new path has opened up!"));
            }
        }

        resetLock();

        cooldownStart = System.currentTimeMillis();
        isOnCooldown = true;

    }

    public void resetCompletion() {
        cooldownStart = 0;
        isOnCooldown = false;

        if (this.rewardDropLocation != null)
            this.rewardDropLocation.getBlock().setType(Material.AIR);
        if (this.pressurePlateLocation != null)
            pressurePlateLocation.getBlock().setType(Material.AIR);

        if (this.spawners != null && !this.spawners.isEmpty()) {
            for (Spawner spawner : this.spawners.values())
                spawner.resetCompletion();
        }

        playersInDungeon = new ArrayList<>();

        resetLock();

        plugin.getLogger().info("Dungeon " + this.name + " reset");
    }

    public void resetLock() {
        lockedPlayersInDungeon = new ArrayList<>();
        isLocked = false;
        lockStartTime = 0;
    }

    private void addRewardToShulker(ShulkerBox shulker, Tuple<String, Integer> item) {
        CustomStack customStack = CustomStack.getInstance(item.x);
        if (customStack != null) {
            var itemStack = customStack.getItemStack();
            itemStack.setAmount(item.y);

            shulker.getInventory().addItem(itemStack);
        } else {
            shulker.getInventory().addItem(new ItemStack(Material.getMaterial(item.x), item.y));
        }
    }

    private List<Tuple<String, Integer>> ParseRewards(String rewards) {
        var result = new ArrayList<Tuple<String, Integer>>();
        if (rewards != null) {
            var r1 = rewards.split(";");
            if (r1.length > 0) {
                for (var i = 0; i < r1.length; i++) {
                    var r2 = r1[i].split("#");
                    if (r2.length == 2) {
                        result.add(new Tuple<String, Integer>(r2[0], Integer.parseInt(r2[1])));
                    }
                }
            }
        }
        return result;
    }

    public Map<UUID, Spawner> getSpawners() {
        return this.spawners;
    }

    public void removeSpawner(UUID uuid) {
        if (this.spawners.containsKey(uuid))
            this.spawners.remove(uuid);
    }

    public String getCleanName() {
        return this.name.replace("_", " ");
    }

    public String getCooldownTimeString() {
        if (!isOnCooldown)
            return "-";

        var cooldownLeft = (cooldownTime * 1000) - (System.currentTimeMillis() - cooldownStart);
        long seconds = (long) cooldownLeft / 1000;
        long minutes = seconds / 60;
        seconds = seconds % 60;

        return minutes + "min " + seconds + "s";
    }

    public String getLockTimeString() {
        if (!isLocked)
            return "-";

        var lockLeft = (this.lockTime * 1000) - (System.currentTimeMillis() - lockStartTime);
        long seconds = (long) lockLeft / 1000;
        long minutes = seconds / 60;
        seconds = seconds % 60;

        return minutes + "min " + seconds + "s";
    }

    private UnitedDungeons getPlugin() {
        return (UnitedDungeons) Bukkit.getPluginManager().getPlugin("UnitedDungeons");
    }

}
