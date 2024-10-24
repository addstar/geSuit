package net.cubespace.geSuit.tasks;

import net.cubespace.geSuit.Utilities;
import net.cubespace.geSuit.geSuit;
import net.cubespace.geSuit.managers.ConfigManager;
import net.md_5.bungee.api.config.ServerInfo;

import java.io.ByteArrayOutputStream;

public class SendPluginMessage implements Runnable {
	
	  private final String channel;
	    private final ByteArrayOutputStream bytes;
	    private final ServerInfo server;
		private int sendAttempts = 0;
		private final int maxAttempts = 70;
		private final int sendDelay = 75;
	    
	    public SendPluginMessage(geSuit.CHANNEL_NAMES channel, ServerInfo server, ByteArrayOutputStream bytes) {
            this.channel = ConfigManager.main.enableLegacy ? channel.getLegacy() : channel.toString();
	        this.bytes = bytes;
	        this.server = server;
	    }

	    public void run() {
			if (server.getPlayers().size() == 0) {
				// If no players are online on target server, increment attempts and try again
				sendAttempts++;
				if (sendAttempts < maxAttempts) {
					geSuit.proxy.getScheduler().schedule(
						geSuit.getInstance(), this, sendDelay, java.util.concurrent.TimeUnit.MILLISECONDS);
					return;
				}
			}

			// Message debugging (can be toggled live)
			if (geSuit.getInstance().isDebugEnabled()) {
				Utilities.dumpPacket(channel, "SEND", bytes.toByteArray(), true);
				if (sendAttempts > 0) {
					geSuit.getInstance().DebugMsg("Message waited " + (sendAttempts * sendDelay) + "ms (and " + sendAttempts + " attempts) for a player to be present on " + server.getName() + " server");
				}
			}

			server.sendData(channel, bytes.toByteArray());
	    }

}
