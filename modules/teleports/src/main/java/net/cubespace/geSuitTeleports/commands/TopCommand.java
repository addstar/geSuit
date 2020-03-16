/*
 * The MIT License (MIT)
 * Copyright (c) 2015 - 2020., AddstarMC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to
 * deal  in the Software without restriction, including without limitation the
 * rights  to use, copy, modify, merge, publish, distribute, sublicense,and/or
 * sell  copies of the Software, and to permit persons to whom the Software
 * is  furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE
 * OR OTHER DEALINGS IN THE SOFTWARE.
 */

package net.cubespace.geSuitTeleports.commands;

import net.cubespace.geSuit.managers.CommandManager;
import net.cubespace.geSuitTeleports.geSuitTeleports;
import net.cubespace.geSuitTeleports.managers.TeleportsManager;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

public class TopCommand extends CommandManager<TeleportsManager> {

    public TopCommand(TeleportsManager manager) {
        super(manager);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "You need to be a player to do this");
            return true;
        }
        Player player = (Player) sender;
        if (!player.hasPermission("gesuit.teleports.bypass.delay")) {
            player.sendMessage(geSuitTeleports.teleportinitiated);
            instance.getServer().getScheduler().runTaskLater(instance, () -> doTeleport(player), 60L);
        } else {
            doTeleport(player);
        }
        return true;
    }

    private void doTeleport(Player player) {
        Location current = player.getLocation();
        Location location = new Location(current.getWorld(), current.getX(), current.getWorld().getMaxHeight(), current.getZ(), current.getYaw(), current.getPitch());
        player.teleport(manager.getUtil().getSafeDestination(location), TeleportCause.COMMAND);
        player.sendMessage(geSuitTeleports.tptop);
    }
}
