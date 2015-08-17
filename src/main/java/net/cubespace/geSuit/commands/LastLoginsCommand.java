package net.cubespace.geSuit.commands;

import net.cubespace.geSuit.managers.BansManager;
import net.cubespace.geSuit.managers.ConfigManager;
import net.cubespace.geSuit.managers.PlayerManager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

/**
 * Created by narimm on 17/08/2015.
 */
public class LastLoginsCommand extends Command {

    public LastLoginsCommand() {
        super("!lastlogins", "", "");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof ProxiedPlayer) {
            return;
        }
        if (args.length == 0) {
            PlayerManager.sendMessageToTarget(sender, ConfigManager.messages.BUNGEE_COMMAND_LASTLOGINS_USAGE);
            return;
        }
        int num = 5;
        if (args.length == 2) {
            try {
                num = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                PlayerManager.sendMessageToTarget(sender, "You specified an invalid number.");
                return;
            }
        }
            BansManager.displayLastLogins(sender.getName(), args[0], num);
        }
    }
