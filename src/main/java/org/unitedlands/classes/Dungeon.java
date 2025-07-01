package org.unitedlands.classes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.unitedlands.UnitedDungeons;
import org.unitedlands.events.DungeonCompleteEvent;
import org.unitedlands.events.DungeonOpenEvent;
import org.unitedlands.events.HighscoreEvent;
import org.unitedlands.events.PlayerEnterDungeonEvent;
import org.unitedlands.events.PlayerEnterRoomEvent;
import org.unitedlands.events.PlayerExitDungeonEvent;
import org.unitedlands.events.PlayerExitRoomEvent;
import org.unitedlands.utils.Formatter;
import org.unitedlands.utils.Logger;
import org.unitedlands.utils.Messenger;
import org.unitedlands.utils.annotations.Info;

import com.google.gson.annotations.Expose;

public class Dungeon {

    @Expose
    private UUID uuid;
    @Expose
    @Info
    private String name;
    @Expose
    @Info
    private String description;

    @Expose
    private Location location;
    @Expose
    private Location warpLocation;

    @Expose
    @Info
    private boolean isPublic;
    @Expose
    @Info
    private boolean isActive = false;
    private boolean isSleeping = true;
    private boolean isLocked = false;
    @Expose
    @Info
    private boolean isLockable = true;
    private boolean isOnCooldown = false;
    @Expose
    @Info
    private long pulloutTime = 30;
    @Expose
    @Info
    private long cooldownTime = 120;
    @Expose
    @Info
    private long lockTime = 120;
    private long cooldownStart;

    @Expose
    private Set<Room> rooms = new HashSet<>();

    @Expose
    @Info
    int ticksBeforeSleep = 60;
    private int ticksWithoutPlayers = Integer.MAX_VALUE;

    @Expose
    private ArrayList<HighScore> highscores = new ArrayList<>();

    private Collection<Player> playersInDungeon = new HashSet<Player>();
    private Collection<Player> playersInPullout = new HashSet<Player>();

    private Player partyLeader;
    private Collection<Player> lockedPlayersInDungeon = new HashSet<Player>();
    private long lockStartTime;

    public Dungeon() {
        this.uuid = UUID.randomUUID();
    }

    public Dungeon(String name) {
        this.name = name;
        this.uuid = UUID.randomUUID();
    }

    public Dungeon(Location location) {
        setLocation(location);
        this.uuid = UUID.randomUUID();
    }

    public void checkPlayerProximity(double detectionRange) {
        boolean playerNearby = false;

        World world = this.location.getWorld();
        double lx = this.location.getX();
        double ly = this.location.getY();
        double lz = this.location.getZ();

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!player.getWorld().equals(world))
                continue;

            double px = player.getX();
            double py = player.getY();
            double pz = player.getZ();

            if (Math.abs(px - lx) > detectionRange)
                continue;
            if (Math.abs(py - ly) > detectionRange)
                continue;
            if (Math.abs(pz - lz) > detectionRange)
                continue;

            playerNearby = true;
            break;
        }

        if (!playerNearby) {
            if (ticksWithoutPlayers < Integer.MAX_VALUE)
                ticksWithoutPlayers++;
            if (ticksWithoutPlayers >= ticksBeforeSleep && !this.isSleeping) {
                Logger.log("Dungeon " + this.name + " going to sleep.");
                this.isSleeping = true;
            }
        } else {
            if (this.isSleeping) {
                Logger.log("Dungeon " + this.name + " waking up.");
                ticksWithoutPlayers = 0;
                this.isSleeping = false;
            }
        }
    }

    public void checkPlayerActivity() {

        updatePlayersInDungeon();

        if (this.isLocked) {
            for (Player player : playersInDungeon) {
                if (player.hasPermission("united.dungeons.admin"))
                    continue;
                if (!lockedPlayersInDungeon.contains(player)) {
                    player.teleport(this.warpLocation);
                    Messenger.sendMessageTemplate(player, "dungeon-status-locked",
                            Map.of("lock-time", Formatter.formatDuration(this.getRemainingLockTime())),
                            true);
                }
            }
        }
        if (this.isOnCooldown) {
            for (Player player : playersInDungeon) {
                if (player.hasPermission("united.dungeons.admin"))
                    continue;
                if (playersInPullout == null || !playersInPullout.contains(player)) {
                    player.teleport(this.warpLocation);
                    Messenger.sendMessageTemplate(player, "dungeon-status-cooldown",
                            Map.of("cooldown-time", Formatter.formatDuration(this.getRemainingCooldown())),
                            true);
                }
            }
        }

    }

    public void updatePlayersInDungeon() {

        if (playersInDungeon == null)
            playersInDungeon = new ArrayList<>();

        updatePlayersInRooms();

        Set<Player> currentPlayersInDungeon = rooms.stream().flatMap(r -> r.getPlayersInRoom().stream())
                .collect(Collectors.toSet());

        if (!isOnCooldown) {
            var pastPlayers = new HashSet<>(playersInDungeon);
            pastPlayers.removeAll(currentPlayersInDungeon);
            for (Player player : pastPlayers) {
                (new PlayerExitDungeonEvent(this, player)).callEvent();
            }

            var newPlayers = new HashSet<>(currentPlayersInDungeon);
            newPlayers.removeAll(playersInDungeon);
            for (Player player : newPlayers) {
                (new PlayerEnterDungeonEvent(this, player)).callEvent();
            }
        }

        playersInDungeon = currentPlayersInDungeon;
    }

    public void updatePlayersInRooms() {

        var uncheckedOnlinePlayers = new HashSet<>(Bukkit.getOnlinePlayers());

        for (Room room : rooms) {

            Set<Player> currentPlayersInRoom = new HashSet<>();
            for (Player player : uncheckedOnlinePlayers) {
                var ploc = player.getLocation();
                if (room.getBoundingBox().contains(ploc.getX(), ploc.getY(), ploc.getZ())) {
                    currentPlayersInRoom.add(player);
                }
            }

            Set<Player> pastPlayersInRoom = new HashSet<>(room.getPlayersInRoom());
            pastPlayersInRoom.removeAll(currentPlayersInRoom);
            for (Player pastPlayer : pastPlayersInRoom) {
                (new PlayerExitRoomEvent(this, room, pastPlayer)).callEvent();
            }

            Set<Player> newPlayersInRoom = new HashSet<>(currentPlayersInRoom);
            newPlayersInRoom.removeAll(room.getPlayersInRoom());
            for (Player newPlayer : newPlayersInRoom) {
                (new PlayerEnterRoomEvent(this, room, newPlayer)).callEvent();
            }

            room.setPlayersInRoom(currentPlayersInRoom);

            uncheckedOnlinePlayers.removeAll(currentPlayersInRoom);
        }

    }

    public void checkRooms() {
        boolean allRoomsComplete = true;
        for (var room : rooms) {
            if (room.isComplete())
                continue;
            if (!room.getPlayersInRoom().isEmpty()) {
                var spawners = room.getSpawners();
                if (spawners != null && !spawners.isEmpty()) {
                    boolean allSpawnersComplete = true;
                    for (Spawner spawner : spawners) {
                        spawner.checkCompletion();
                        allSpawnersComplete = allSpawnersComplete && spawner.isComplete();
                        if (!spawner.isComplete()) {
                            if (spawner.isPlayerNearby()) {
                                spawner.prepareSpawn();
                            }
                        }
                    }
                    if (room.mustBeCompleted()) {
                        if (allSpawnersComplete)
                            room.complete();
                    }
                } else {
                    if (room.mustBeCompleted())
                        room.complete();
                }
            }
            allRoomsComplete = allRoomsComplete && (room.isComplete() || !room.mustBeCompleted());
        }
        if (allRoomsComplete)
            this.complete();
    }

    public boolean isPlayerInDungeon(Player player) {
        return playersInDungeon.contains(player);
    }

    public boolean isPlayerLockedInDungeon(Player player) {
        return lockedPlayersInDungeon.contains(player);
    }

    public Collection<Player> getPlayersInDungeon() {
        if (playersInDungeon == null)
            return new HashSet<>();
        return playersInDungeon;
    }

    public void lockDungeon(Player lockingPlayer) {
        partyLeader = lockingPlayer;
        lockDungeon();
    }

    public void lockDungeon() {

        if (isLocked || isOnCooldown || !isActive)
            return;

        updatePlayersInDungeon();

        lockedPlayersInDungeon = playersInDungeon;
        lockStartTime = System.currentTimeMillis();
        isLocked = true;

        for (Player player : lockedPlayersInDungeon) {
            Messenger.sendMessageTemplate(player, "dungeon-status-lock",
                    Map.of("lock-time", Formatter.formatDuration(this.getRemainingLockTime())), true);
        }
    }

    public void invitePlayer(Player player) {
        if (!isLocked)
            return;

        if (!lockedPlayersInDungeon.contains(player))
            lockedPlayersInDungeon.add(player);

        Messenger.sendMessageTemplate(player, "invitation-received",
                Map.of("dungeon-name", this.getCleanName()), true);
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
            reset();
        }
    }

    public void checkLock() {
        if (!isLocked)
            return;

        if (System.currentTimeMillis() - lockStartTime >= this.lockTime * 1000) {

            for (Player player : lockedPlayersInDungeon) {
                Messenger.sendMessageTemplate(player, "dungeon-status-lock-expired",
                        Map.of("dungeon-name", this.getCleanName()), true);
            }
            lockedPlayersInDungeon = new ArrayList<>();
            lockStartTime = 0;
            isLocked = false;
        }
    }

    public void complete() {
        updateHighscores();

        playersInPullout = new HashSet<>(playersInDungeon);
        Bukkit.getScheduler().runTaskLater(UnitedDungeons.getInstance(), () -> {
            playersInPullout = new HashSet<>();
        }, this.pulloutTime * 20L);

        resetLock();

        cooldownStart = System.currentTimeMillis();
        isOnCooldown = true;

        (new DungeonCompleteEvent(this)).callEvent();
    }

    private void updateHighscores() {
        if (!this.isLocked || this.lockStartTime == 0)
            return;

        if (this.lockedPlayersInDungeon == null || this.lockedPlayersInDungeon.isEmpty())
            return;

        Long timeToCompletion = System.currentTimeMillis() - (long) lockStartTime;
        String playerNames = String.join(", ",
                lockedPlayersInDungeon.stream().map(p -> p.getName()).collect(Collectors.toList()));

        var newHighscore = new HighScore(timeToCompletion, playerNames);

        int index = Collections.binarySearch(highscores, newHighscore, Comparator.comparingLong(h -> h.getTime()));
        if (index < 0)
            index = -index - 1;
        highscores.add(index, newHighscore);

        if (index <= 4) {
            (new HighscoreEvent(this, newHighscore, index + 1)).callEvent();
        }

        if (highscores.size() > 5) {
            highscores.remove(highscores.size() - 1);
        }

        UnitedDungeons.getInstance().getDungeonManager().saveDungeon(this);
    }

    public void reset() {

        var spawners = getSpawners();
        if (spawners != null) {
            for (Spawner s : spawners) {
                UnitedDungeons.getInstance().getMobManager().removeAllSpawnerMobs(s);
            }
        }

        cooldownStart = 0;
        isOnCooldown = false;

        resetLock();

        for (Room room : rooms) {
            room.reset();
        }

        if (playersInDungeon.size() > 0) {
            for (var player : playersInDungeon) {
                if (player.hasPermission("united.dungeons.admin"))
                    continue;
                player.teleport(this.warpLocation);
                Messenger.sendMessageTemplate(player, "dungeon-reset-teleport", null, true);
            }
        }
        playersInDungeon = new ArrayList<>();

        if (isActive) {
            (new DungeonOpenEvent(this)).callEvent();
        }

        Logger.log("Dungeon " + this.name + " reset");
    }

    public void resetLock() {
        partyLeader = null;
        lockedPlayersInDungeon = new ArrayList<>();
        isLocked = false;
        lockStartTime = 0;
    }

    public Set<Spawner> getSpawners() {
        if (rooms == null || rooms.isEmpty())
            return new HashSet<>();
        return rooms.stream().flatMap(r -> r.getSpawners().stream()).collect(Collectors.toSet());
    }

    public String getCleanName() {
        return this.name.replace("_", " ");
    }

    public Long getRemainingCooldown() {
        if (!isOnCooldown)
            return 0L;
        var cooldownLeft = (cooldownTime * 1000) - (System.currentTimeMillis() - cooldownStart);
        return (long) cooldownLeft;
    }

    public Long getRemainingLockTime() {
        if (!isLocked)
            return 0L;
        var lockLeft = (this.lockTime * 1000) - (System.currentTimeMillis() - lockStartTime);
        return (long) lockLeft;
    }

    // #region Room functions

    public Set<Room> getRooms() {
        if (rooms == null)
            return new HashSet<Room>();
        return rooms;
    }

    public void addRoom(@NotNull Room room) {
        if (rooms == null)
            rooms = new HashSet<>();
        if (!rooms.contains(room))
            rooms.add(room);
    }

    public void removeRoom(@NotNull Room room) {
        if (rooms == null)
            return;
        if (rooms.contains(room))
            rooms.remove(room);
    }

    public void moveRoom(@NotNull Room room, @NotNull String axis, @NotNull Long distance) {
        var bbox = room.getBoundingBox().clone();
        Vector shiftVector = new Vector(0, 0, 0);

        switch (axis) {
            case "x":
                shiftVector = new Vector(distance, 0, 0);
                break;
            case "y":
                shiftVector = new Vector(0, distance, 0);
                break;
            case "z":
                shiftVector = new Vector(0, 0, distance);
                break;
        }
        bbox.shift(shiftVector);

        room.setBoundingBox(bbox);
        moveRoomContents(room, shiftVector);

    }

    public void shiftRoom(@NotNull Room room, @NotNull Location shift) {
        var bbox = room.getBoundingBox().clone();
        Vector shiftVector = new Vector(shift.getX(), shift.getY(), shift.getZ());
        bbox.shift(shiftVector);

        room.setBoundingBox(bbox);
        moveRoomContents(room, shiftVector);
    }

    private void moveRoomContents(Room room, Vector shiftVector) {
        for (Spawner spawner : room.getSpawners()) {
            spawner.getLocation().add(shiftVector);
        }
        for (RewardChest chest : room.getChests()) {
            chest.getLocation().add(shiftVector);
        }
        for (Barrier barrier : room.getBarriers()) {
            barrier.getLocation().add(shiftVector);
        }
    }

    public void expandRoom(@NotNull Room room, @NotNull String axis, @NotNull Long value) {
        var bbox = room.getBoundingBox().clone();
        switch (axis) {
            case "x":
                bbox.expand(value, 0, 0);
                break;
            case "y":
                bbox.expand(0, value, 0);
                break;
            case "z":
                bbox.expand(0, 0, value);
                break;
        }

        Set<Spawner> spawnersToRemove = new HashSet<>();
        for (Spawner spawner : room.getSpawners()) {
            var spawnerLocation = new Vector(spawner.getLocation().getX(), spawner.getLocation().getY(),
                    spawner.getLocation().getZ());
            if (!bbox.contains(spawnerLocation)) {
                spawnersToRemove.add(spawner);
            }
        }
        for (var spawner : spawnersToRemove)
            room.removeSpawner(spawner);

        Set<RewardChest> chestsToRemove = new HashSet<>();
        for (RewardChest chest : room.getChests()) {
            var chestLocation = new Vector(chest.getLocation().getX(), chest.getLocation().getY(),
                    chest.getLocation().getZ());
            if (!bbox.contains(chestLocation)) {
                chestsToRemove.add(chest);
            }
        }
        for (var chest : chestsToRemove)
            room.removeChest(chest);

        Set<Barrier> barriersToRemove = new HashSet<>();
        for (Barrier barrier : room.getBarriers()) {
            var barrierLocation = new Vector(barrier.getLocation().getX(), barrier.getLocation().getY(),
                    barrier.getLocation().getZ());
            if (!bbox.contains(barrierLocation)) {
                barriersToRemove.add(barrier);
            }
        }
        for (var chest : barriersToRemove)
            room.removeBarrier(chest);

        room.setBoundingBox(bbox);
    }

    // #endregion

    // #region Getters / Setters

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(@NotNull UUID uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(@NotNull String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(@NotNull String description) {
        this.description = description;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(@NotNull Location location) {
        this.location = location.getBlock().getLocation().add(0.5, 0.5, 0.5);
    }

    public Location getWarpLocation() {
        return warpLocation;
    }

    public void setWarpLocation(Location warpLocation) {
        var center = warpLocation.getBlock().getLocation().add(0.5, 0.5, 0.5);
        this.warpLocation = new Location(center.getWorld(), center.getX(), center.getY(), center.getZ(),
                warpLocation.getYaw(), warpLocation.getPitch());
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean isPublic) {
        this.isPublic = isPublic;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }

    public boolean isSleeping() {
        return isSleeping;
    }

    public void setSleeping(boolean isSleeping) {
        this.isSleeping = isSleeping;
    }

    public boolean isLocked() {
        return isLocked;
    }

    public void setLocked(boolean isLocked) {
        this.isLocked = isLocked;
    }

    public boolean isLockable() {
        return isLockable;
    }

    public void setLockable(boolean isLockable) {
        this.isLockable = isLockable;
    }

    public boolean isOnCooldown() {
        return isOnCooldown;
    }

    public void setOnCooldown(boolean isOnCooldown) {
        this.isOnCooldown = isOnCooldown;
    }

    public double getCooldownTime() {
        return cooldownTime;
    }

    public double getLockTime() {
        return lockTime;
    }

    public double getCooldownStart() {
        return cooldownStart;
    }

    public int getTicksWithoutPlayers() {
        return ticksWithoutPlayers;
    }

    public Collection<Player> getLockedPlayersInDungeon() {
        return lockedPlayersInDungeon;
    }

    public double getLockStartTime() {
        return lockStartTime;
    }

    public Player getPartyLeader() {
        return partyLeader;
    }

    public ArrayList<HighScore> getHighscores() {
        return highscores;
    }

    public long getPulloutTime() {
        return pulloutTime;
    }

    public void setPulloutTime(long pulloutTime) {
        this.pulloutTime = pulloutTime;
    }

    public Collection<Player> getPlayersInPullout() {
        return playersInPullout;
    }

    // #endregion

}
