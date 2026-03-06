package loopinterceptors;

import data.global.ScriptData;
import framework.LoopInterceptor;
import org.dreambot.api.ClientSettings;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.utilities.Timer;

public class ChangePlayerSetUpLI extends LoopInterceptor {

    private boolean successfullyChangedSetting;

    public ChangePlayerSetUpLI() {
        super(() -> ScriptData.changePlayerSetUpTimer != null && ScriptData.changePlayerSetUpTimer.finished());
    }

    @Override
    public int handle() {
        if (ScriptData.playerSetUpI >= ScriptData.playerSetUpOpts.length) {
            if (ClientSettings.isOpen()) {
                if (ClientSettings.closeSettingsInterface()) {
                    return ScriptData.returnMSNormal();
                }
                return ScriptData.returnMSFast();
            }
            else {
                ScriptData.changePlayerSetUpTimer = null;
                ScriptData.playerSetUpI = 0;
                Logger.log("Went through all the playerSetUpOpts, changePlayerSetUpTimer null, playerSetUpI 0");
            }
        }
        else if (successfullyChangedSetting) {
            if (ClientSettings.isOpen()) {
                if (ClientSettings.closeSettingsInterface()) {
                    return ScriptData.returnMSNormal();
                }
                return ScriptData.returnMSFast();
            }
            else {
                ScriptData.changePlayerSetUpTimer = new Timer(ScriptData.SECURE_RANDOM.nextInt(10800000 - 300000 + 1) + 300000); // 30m-3h
                successfullyChangedSetting = false;
                Logger.log("Done changing player settings, new timer created");
            }
        }
        else {
            switch (ScriptData.playerSetUpOpts[ScriptData.playerSetUpI]) {
                case 0: // levelUpInterface
                    if (ScriptData.playerSetUpValues[0] == 1) {
                        if (ClientSettings.isLevelUpInterfaceEnabled()) {
                            if (!successfullyChangedSetting) {
                                ScriptData.playerSetUpI++;
                            }
                        }
                        else if (ClientSettings.toggleLevelUpInterface(true)) {
                            Sleep.sleepUntil(ClientSettings::isLevelUpInterfaceEnabled, ScriptData.SECURE_RANDOM.nextInt(40000 - 10000 + 1) + 10000, 300);
                            if (ClientSettings.isLevelUpInterfaceEnabled()) {
                                successfullyChangedSetting = true;
                            }
                        }
                    }
                    else if (!ClientSettings.isLevelUpInterfaceEnabled()) {
                        if (!successfullyChangedSetting) {
                            ScriptData.playerSetUpI++;
                        }
                    }
                    else if (ClientSettings.toggleLevelUpInterface(false)) {
                        Sleep.sleepUntil(() -> !ClientSettings.isLevelUpInterfaceEnabled(), ScriptData.SECURE_RANDOM.nextInt(40000 - 10000 + 1) + 10000, 300);
                    }
                    return ScriptData.returnMSFast();
                case 1: // worldHopConfirmation
                    if (ScriptData.playerSetUpValues[0] == 1) {
                        if (ClientSettings.isWorldHopConfirmationEnabled()) {
                            if (!successfullyChangedSetting) {
                                ScriptData.playerSetUpI++;
                            }
                        }
                        else if (ClientSettings.toggleWorldHopConfirmation(true)) {
                            Sleep.sleepUntil(ClientSettings::isWorldHopConfirmationEnabled, ScriptData.SECURE_RANDOM.nextInt(40000 - 10000 + 1) + 10000, 300);
                            if (ClientSettings.isWorldHopConfirmationEnabled()) {
                                successfullyChangedSetting = true;
                            }
                        }
                    }
                    else if (!ClientSettings.isWorldHopConfirmationEnabled()) {
                        if (!successfullyChangedSetting) {
                            ScriptData.playerSetUpI++;
                        }
                    }
                    else if (ClientSettings.toggleWorldHopConfirmation(false)) {
                        Sleep.sleepUntil(() -> !ClientSettings.isWorldHopConfirmationEnabled(), ScriptData.SECURE_RANDOM.nextInt(40000 - 10000 + 1) + 10000, 300);
                    }
                    return ScriptData.returnMSFast();
                case 2: // roofsHidden
                    if (ScriptData.playerSetUpValues[0] == 1) {
                        if (ClientSettings.areRoofsHidden()) {
                            if (!successfullyChangedSetting) {
                                ScriptData.playerSetUpI++;
                            }
                        }
                        else if (ClientSettings.toggleRoofs(false)) {
                            Sleep.sleepUntil(ClientSettings::areRoofsHidden, ScriptData.SECURE_RANDOM.nextInt(40000 - 10000 + 1) + 10000, 300);
                            if (ClientSettings.isLevelUpInterfaceEnabled()) {
                                successfullyChangedSetting = true;
                            }
                        }
                    }
                    else if (!ClientSettings.isLevelUpInterfaceEnabled()) {
                        if (!successfullyChangedSetting) {
                            ScriptData.playerSetUpI++;
                        }
                    }
                    else if (ClientSettings.toggleRoofs(true)) {
                        Sleep.sleepUntil(() -> !ClientSettings.isLevelUpInterfaceEnabled(), ScriptData.SECURE_RANDOM.nextInt(40000 - 10000 + 1) + 10000, 300);
                    }
                    return ScriptData.returnMSFast();
            }
        }


        return ScriptData.returnMSNormal();
    }


}
