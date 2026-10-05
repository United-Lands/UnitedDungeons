package org.unitedlands.dungeons.commands.admin.dungeon.handlers;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.unitedlands.annotations.UnitedSubCommand;
import org.unitedlands.dungeons.commands.admin.dungeon.CmdDungeonAdminDungeon;
import org.unitedlands.dungeons.managers.DungeonManager;
import org.unitedlands.registrars.command.UnitedCommandExecutor;
import org.unitedlands.unitedlands.classes.metadata.StringMetaDataField;
import org.unitedlands.unitedlands.managers.UnitedLandsDataManager;
import org.unitedlands.utils.United;

@UnitedSubCommand(
    parent = CmdDungeonAdminDungeon.class, 
    name = "register", 
    usage = "/uda dungeon register <settlement_name> <dungeon_name | clear>", 
    catchAll = true, 
    requirePlugins = { "UnitedLands" }
)
public class CmdDungeonAdminDungeonRegister implements UnitedCommandExecutor {

    private final String META_KEYNAME = "uniteddungeons_dungeon";

    @Override
    public List<String> handleTab(CommandSender sender, String[] args) {
        return switch (args.length) {
            case 1 -> UnitedLandsDataManager.instance().getSettlementNames();
            case 2 -> DungeonManager.instance().getDungeonNames();
            default -> null;
        };
    }

    @Override
    public void handleCommand(CommandSender sender, String[] args) {
        if (args.length != 2) {
            sendUsage(sender);
        }
        var settlement = UnitedLandsDataManager.instance().getSettlement(args[0]);
        if (settlement == null)
            return;

        if (args[1].equalsIgnoreCase("clear")) {
            settlement.removeMetadata(META_KEYNAME);
            settlement.save();
        } else {
            var dungeon = DungeonManager.instance().getDungeon(args[1]);
            if (dungeon == null) {
                United.messenger().sendRaw(sender, "<red>Unknown dungeon: " + args[2]);
            } else {
                settlement.removeMetadata(META_KEYNAME);
                settlement.addMetadata(new StringMetaDataField(META_KEYNAME, dungeon.getUuid().toString()));
                settlement.save();
            }
        }
    }

}
