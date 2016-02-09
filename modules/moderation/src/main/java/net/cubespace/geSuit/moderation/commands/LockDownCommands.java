/*
 *     Copyright 2016 AddstarMC
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.cubespace.geSuit.moderation.commands;

import org.bukkit.command.CommandSender;


import net.cubespace.geSuit.core.commands.Command;
import net.cubespace.geSuit.core.commands.Optional;
import net.cubespace.geSuit.core.commands.Varargs;
import net.cubespace.geSuit.core.objects.DateDiff;
import net.cubespace.geSuit.core.objects.Result;
import net.cubespace.geSuit.remote.moderation.LockDownActions;

/**
 * Created for the AddstarMC Project.
 * Created by Narimm on 9/02/2016.
 */

public class LockDownCommands {
    private LockDownActions actions;

    public LockDownCommands(LockDownActions actions) {
        this.actions = actions;
    }

    /**
     * Locks the Server Down until a  time in the future.
     *
     * @param sender
     * @param time
     * @param reason
     */
    @Command(name = "LockDown", permission = "gesuit.lockdown.command.lockdown", usage = "/<command> <time> [reason]")
    public void lockdown(CommandSender sender, DateDiff time, @Optional @Varargs String reason) {
        Result result = actions.lockdown(sender.getName(), null, reason, time.fromNow());
        if (result.getMessage() != null) {
            sender.sendMessage(result.getMessage());
        }
    }

    /**
     * Checks the status of a lockdown and expires it if required.
     *
     * @param sender
     */

    @Command(name = "LockDownStatus", permission = "gesuit.lockdown.command.status", usage = "/<command>")
    public void lockDownStatus(CommandSender sender) {
        Result result = actions.status(sender.getName());
        if (result.getMessage() != null) {
            sender.sendMessage(result.getMessage());
        }
    }

    /**
     * Forces a lockdown to expire
     *
     * @param sender
     */
    @Command(name = "LockDownEnd", permission = "gesuit.lockdown.command.end", usage = "/<command>")
    public void LockDownEnd(CommandSender sender) {
        Result result = actions.unLock(sender.getName());
        if (result.getMessage() != null) {
            sender.sendMessage(result.getMessage());
        }
    }

}
