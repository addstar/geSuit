/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.cubespace.geSuit.events;

import net.md_5.bungee.api.plugin.Event;

/**
 *
 * @author JR
 */
public class NewPlayerJoinEvent extends Event
{

    private final String message;
    private final String player;

    public NewPlayerJoinEvent(String playerName, String message)
    {
        this.player = playerName;
        this.message = message;
    }

    public String getMessage()
    {
        return message;
    }

    public String getPlayer()
    {
        return player;
    }
}
