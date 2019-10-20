package net.cubespace.geSuit.task;

import net.cubespace.geSuit.BukkitModule;
import net.cubespace.geSuit.managers.LoggingManager;
import net.cubespace.geSuit.utils.Utilities;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.ByteArrayOutputStream;
import java.util.Iterator;

public class PluginMessageTask extends BukkitRunnable
{

    private final ByteArrayOutputStream bytes;
    private final BukkitModule module;
    
    
    public PluginMessageTask(ByteArrayOutputStream bytes, BukkitModule module)
    {
        this.bytes = bytes;
        this.module = module;
    }

    public void run()
    {
        Iterator<? extends Player> iterator = Bukkit.getOnlinePlayers().iterator();
		if (iterator.hasNext()) {
		    Player player = iterator.next();
			player.sendPluginMessage(
					module,
                    module.getCHANNEL_NAME(),
					bytes.toByteArray());
            LoggingManager.debug("[" + module.getName() + "] " +
                        Utilities.dumpPacket(module.getCHANNEL_NAME(), "SEND", bytes.toByteArray()));

		} else {
            LoggingManager.debug("[" + module.getName() + "] Unable to send Message-No players online.- " + Utilities.dumpPacket(module.getCHANNEL_NAME(), "SEND", bytes.toByteArray()));
        }
    }
}
