package net.cubespace.geSuit.moderation;

import net.cubespace.geSuit.config.ConfigManager;
import net.cubespace.geSuit.config.ConfigReloadListener;
import net.cubespace.geSuit.config.ModerationConfig;
import net.cubespace.geSuit.core.objects.DateDiff;
import net.cubespace.geSuit.core.objects.Result;
import net.cubespace.geSuit.core.objects.Result.Type;
import net.cubespace.geSuit.remote.moderation.LockDownActions;

import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created for use for the Add5tar MC Minecraft server
 * Created by Narimm on 6/02/2016.
 */
public class LockdownManager implements LockDownActions, ConfigReloadListener {

    private boolean lockedDown = false;
    private long expiryTime = 0; //The expiry time in Millisecs
    private String optionalMessage = "";
    private ModerationConfig config;
    private final Logger logger;

    public LockdownManager(Logger logger) {
        this.logger = logger;
        this.expiryTime = 0;
        this.optionalMessage = "";
        this.lockedDown = false;
    }

    public boolean isLockedDown() {
        return lockedDown;
    }

    public void setLockedDown(boolean lockedDown) {
        this.lockedDown = lockedDown;
    }

    public long getExpiryTime() {
        return expiryTime;
    }

    public String getExpiryTimeString() {
        return new DateDiff(expiryTime).toString();
    }

    public String getExpiryIn(){
        Long cur = System.currentTimeMillis();
        if (expiryTime>cur){
            return new DateDiff(expiryTime-cur).toString();
        }
        return "0s";
    }


    public void setExpiryTime(long expiryTime) {
        this.expiryTime = expiryTime;
    }

    public String getOptionalMessage() {
        return optionalMessage;
    }

    public void setOptionalMessage(String optionalMessage) {
        this.optionalMessage = optionalMessage;
    }


    public void initialize() {
        this.expiryTime = DateDiff.valueOf(config.LockdownTime).fromNow();
        setLockedDown(config.LockedDown);
        setOptionalMessage(config.LockDownStartupMsg);
    }

    public void loadConfig(ModerationConfig config) {
        this.config = config;

    }

    private Result startLockDown(String sender, UUID senderID, Long expiryTime, String msg) {
        setExpiryTime(expiryTime);
        setOptionalMessage(msg);
        setLockedDown(true);
        logger.log(Level.INFO,"Lockdown Started by" + sender+" Ends in"+ getExpiryIn());
        if (isLockedDown()) {
            String message = "Server is locked down until: " + getExpiryTimeString();
            return new Result(Type.Success, message);
        } else {
            return new Result(Type.Fail, "Lockdown failed to start");
        }
    }

    private Result endLockDown(String sender){
        setExpiryTime(0);
        setLockedDown(false);
        setOptionalMessage(config.LockDownStartupMsg);

        if(isLockedDown()){
            logger.log(Level.WARNING,"Lockdown was attempted to end by" + sender +" but it is still active");
            return new Result(Type.Fail,"Lockdown did not end. Critical Error contact Admins");
        }
        logger.log(Level.INFO,"Lockdown ended by" + sender);
        return new Result(Type.Success,"Lockdown has been ended. Time and message reset to default");
    }

    public boolean checkExpiry() {
        Result result = checkExpiryResult();
        if (result.getType() == Type.Fail){
            logger.log(Level.INFO,result.getMessage());
            return false;
        }
        logger.log(Level.INFO,result.getMessage());
        return true;
    }

    public Result checkExpiryResult() {
        if (isLockedDown()) {
            if (System.currentTimeMillis() >= getExpiryTime()) {
                setExpiryTime(0);
                setLockedDown(false);
                setOptionalMessage(null);
                return new Result(Type.Success,"Lockdown has expired automatically, time and message cleared.");
            } else {
                return new Result(Type.Fail, "Server is locked down until: " + getExpiryTimeString());
                }
            }

        return new Result(Type.Success, "Server is not LockedDown");
        }
    public String denyMessage(){
        if(optionalMessage != null){
            return getOptionalMessage();
        }
        return "Server is undergoing maintenance. Please try again later";
    }



    @Override
    public Result lockdown(String by, UUID byUUID, String reason, long expiryTime) {
        return startLockDown(by, byUUID, expiryTime, reason);
    }

    @Override
    public Result unLock(String sender) {
        return endLockDown(sender);
    }

    @Override
    public Result status(String by) {
        return checkExpiryResult();
    }

    @Override
    public void onConfigReloaded(ConfigManager manager) {
        loadConfig(manager.moderation());
    }
}
