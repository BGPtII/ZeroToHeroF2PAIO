package pipelines;

import data.PersistedScriptInfo;
import data.global.ScriptData;
import framework.LoopInterceptor;
import framework.Pipeline;
import loopinterceptors.*;
import org.dreambot.api.utilities.Logger;

import java.util.Arrays;

/**
 * Order rules: WalkToOpenBankLI needs to come before banking operations, BankWithdrawModeLI needs to come before withdraw operations, and deposit operations need to come before withdraw operations
 */
public class BankingPipeline extends Pipeline {

    private byte bankingReturnToI; // Pipeline index to return to

    public void setBankingReturnToI(byte i) {
        bankingReturnToI = i;
    }

    public BankingPipeline(ContinueDialogueLI continueDialogueLI, DialogueOptionsLI dialogueOptionsLI, WalkToOpenBankLI walkToOpenBankLI, BankWithdrawModeLI bankWithdrawModeLI, WithdrawLI withdrawLI, DepositLI depositLI, DepositAllInvLI depositAllInvLI, DepositAllEqpLI depositAllEqpLI) {
        super(null);
        setLoopInterceptors(new LoopInterceptor[] { continueDialogueLI, dialogueOptionsLI, walkToOpenBankLI, bankWithdrawModeLI, withdrawLI, depositLI, depositAllInvLI, depositAllEqpLI });
    }

    public void exportToPersistedScriptInfo(PersistedScriptInfo p) {
        p.bankingReturnToI = bankingReturnToI;
    }
    public void importFromPersistedScriptInfo(PersistedScriptInfo p) {
        bankingReturnToI = p.bankingReturnToI;
    }

    @Override
    public void shuffleLoopInterceptors() {
        for (int i = loopInterceptors.length - 1; i > 1; i--) { // Don't include 0
            int j = 1 + ScriptData.SECURE_RANDOM.nextInt(i);
            LoopInterceptor tmp = loopInterceptors[i];
            loopInterceptors[i] = loopInterceptors[j];
            loopInterceptors[j] = tmp;
        }

        boolean swapped;
        do {
            swapped = false;
            for (int i = 0; i < loopInterceptors.length; i++) {
                int priorityI = priority(loopInterceptors[i]);
                if (priorityI == -1) {
                    continue;
                }
                for (int j = i + 1; j < loopInterceptors.length; j++) {
                    int priorityJ = priority(loopInterceptors[j]);
                    if (priorityJ == -1) {
                        continue;
                    }
                    if (priorityI > priorityJ) {
                        LoopInterceptor tmp = loopInterceptors[j];
                        loopInterceptors[j] = loopInterceptors[i];
                        loopInterceptors[i] = tmp;
                        swapped = true;
                        break;
                    }
                }
            }
        } while (swapped);
        Logger.log("ShuffledBankingPipeline: " + Arrays.toString(loopInterceptors));
    }

    @Override
    public int run() {
        for (LoopInterceptor loopInterceptor : loopInterceptors) {
            if (loopInterceptor.shouldHandle()) {
                return loopInterceptor.handle();
            }
        }
        if (ScriptData.sellItemsLI.getSellSize() != 0) {
            Logger.log("Needs to return to sell pipeline after banking, routing...");
            ScriptData.currentPipelineI = 36;
        }
        else if (ScriptData.buyItemsLI.getBuySize() != 0) {
            Logger.log("Needs to return to buy pipeline after banking, routing...");
            ScriptData.currentPipelineI = 35;
        }
        else if (ScriptData.taskType == 1) {
            Logger.log("Needs to return to secondaryTask pipeline after banking, routing...");
            ScriptData.currentPipelineI = ScriptData.currentSecondaryTaskI;
        }
        else {
            Logger.log("Needs to return to secondaryTask pipeline after banking, routing...");
            ScriptData.currentPipelineI = ScriptData.currentProgressionTaskI;
        }
        return 0;
    }

    private int priority(LoopInterceptor li) {
        if (li instanceof WalkToOpenBankLI) {
            return 1;
        }
        if (li instanceof BankWithdrawModeLI || li instanceof DepositLI || li instanceof DepositAllInvLI) {
            return 2;
        }
        if (li instanceof WithdrawLI) {
            return 3;
        }
        return -1; // Dialogue - Can go anywhere
    }

}
