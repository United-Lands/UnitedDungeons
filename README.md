# United Dungeons

**United Dungeons** is a custom-made plugin for United Lands for the creation and management of dungeons on the server. It supports both the use of ItemsAdder custom items and MythicMob entities.

# Permissions

All admin commands need the permission `united.dungeons.admin`. Player commands don't require a permission to use.

# Commands

## Player commands

`/ud` is the main command to use for players. The following subcommands are available by default:

**Available from anywhere:**
- `/ud warp <dungeonname>` - teleports the player to the dungeon of that name. If not an admin, players can only warp to dungeons that are toggled public.

**Available when inside of a dungeon:**
- `/ud info` - shows information on the current dungeon
- `/ud start` - locks the dungeon. The players currently within its boundaries become the dungeon party. No other player can enter the dungeon until the lock timer expires, the dungeon is completed, the last party member leaves, or an admin overrides the lock. (*Note: the dungeon needs to be lockable, not already locked, and not on cooldown*)
- `/ud leave` - lets players leave the dungeon party. When the last player leaves, the dungeon is unlocked
- `/ud entrance` - instantly teleports the player to the current dungeon's entrance/exit location

## Admin commands

### Dungeon management

`/uddungeon` / `/udd` - main command for all dungeon related commands

**General commands:**

- `/udd list` - lists all registered dungeons.
- `/udd create <name>` - creates a new dungeon at the current location

**Dungeon commands**

`/udd <dungeon_name> ...`

- `info` - displays information on a dungeon
- `rename <new_name>` - renames a dungeon
- `delete` - deletes a dungeon
- `toggle` - toggles a dungeon between active and inactive
- `complete` - triggers the completion of a dungeon (removes all spawned mobs and activates rewards)
- `reset` - resets a dungeon (removes all mobs & rewards, unlocks the dungeon)
- `unlock` - removes the lock on a dungeon
- `showmarkers` - shows particles for the location of spawners, POIs, and the dungeon bounding box

**Dungeon setter commands**

`/udd <dungeon_name> set ...`

These four commands use the current player position:
- `location` - moves the dungeon center location 
- `exitLocation` - sets the dungeon entrance/exit location at the player's position. Also sets the direction the player will be looking after `/ud warp`
- `chestPosition` - sets the position where a reward chest will spawn when the dungeon is completed 
- `platePosition` - sets the position where a pressure plate will spawn when the dungeon is completed

These commands are independent of the player's position:
- `description <text>` - sets the description of the dungeon.
- `isPublicWarp <true|false>` - sets whether or not players can warp to this dungeon
- `isLockable <true|false>` - sets whether or not players can lock this dungeon with `/ud start`
- `width <number>` - sets the width (X-axis) of the dungeon (in blocks)
- `length <number>` - sets the width (Z-axis) of the dungeon (in blocks)
- `height <number>`- sets the width (Y-axis) of the dungeon (in blocks)
- `doRewardDrop <true|false>` - sets whether or not the dungeon will spawn a shulker chest with reward loot when it is completed  
- `staticRewards <string>` - sets the rewards that will *always* be in the reward loot chest (if activated). Needs to be a string of the form ITEM#AMOUNT;ITEM#AMOUNT, e.g. GOLD_INGOT#32;IRON_INGOT#16
- `randomRewards <string>`- sets the rewards from which random loot can be picked for the reward loot chest (if activated). Needs to be a string of the form ITEM#AMOUNT;ITEM#AMOUNT, e.g. GOLD_INGOT#32;IRON_INGOT#16
- `randomRewardsCount <number>` - sets how many random items should be picked for the reward loot chest
- `doPressurePlate <true|false>` - sets whether or not the dungeon will spawn a pressure plate when it is completed
- `cooldownTime <seconds>` - sets the number of seconds that the dungeon will be on cooldown after it has been completed
- `lockTime <seconds>` - sets the number of seconds after which a dungeon will automatically unlock again after being locked by a player party

### Spawn management

All spawer commands need to be executed in the location of a spawner. User `/ud <dungeon_name> showmarkers` to find the spawner locations.

`/udspawner` / `/uds` - main command for all spawner related commands

**General spawner commmands**

- `/uds create <dungeon_name>` - creates a new spawner for the provided dungeon
- `/uds info` - displays information on a spawner
- `/uds delete` - removes a spawner

**Spawner setter commands**

`/uds set ...`

- `radius <number>` - sets how close a player needs to be to the spawner before it starts spawning mobs (in blocks)
- `mobType <mob_name>` - the name of the mob to spawn, either vanilla (e.g. ZOMBIE, SKELETON, COW...) or a MythicMob name (e.g. SkeletalKnight)
- `isMythicMob <true|false>` - whether or not the mob to spawn is a custom MythicMob entity 
- `maxMobs <number>` - sets how many mobs spawned by this spawner can be alive at the same time 
- `spawnFrequency <milliseconds>` - the time that has to elapse between spawn events
- `isGroupSpawn <true|false>` - If false, the spawner will spawn one new mob per spawn event until `maxMobs` is reached. If true, the spawner will always spawn as many mobs as needed to reach `maxMobs` in every spawn event.
- `killsToComplete <number>` - sets the number of mobs the players need to kill in order for this spawner to be considered *complete*. Completed spawners will cease to spawn new mobs until the dungeon is reset.

