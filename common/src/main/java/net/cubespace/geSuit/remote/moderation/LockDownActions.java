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

package net.cubespace.geSuit.remote.moderation;

import net.cubespace.geSuit.core.objects.Result;

import java.util.UUID;

/**
 * Created for the AddstarMC Project.
 * Created by Narimm on 6/02/2016.
 */
public interface LockDownActions {

    public Result lockdown(String by, UUID byUUID, String reason, long expiryTime);

    public Result unLock(String by);

    public Result status(String by);
}
