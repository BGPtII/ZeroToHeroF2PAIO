package data;

import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.items.Item;

import java.util.List;

/**
 * (De) Serialization
 */
public class PersistedScriptInfo {

    public List<Item> bankCache;

    public byte taskType;
    public byte currentPipelineI;
    public byte currentProgressionTaskI;
    public byte currentSecondaryTaskI;

    public boolean useOnGameMessageEvent;

    public Timer progressionTaskTimer;
    public Timer moneyMakingTaskTimer;

    public byte[] questOrder;
    public byte questOrderI;

    public String[] dialogueOpts;

    public String currentEntityName;

    public Tile currentTile;
    public Area currentArea;
    public Area currentArea2;
    public Area currentArea3;

    public int[] sellID;
    public int[] sellQty;
    public int[] sellPrice;
    public byte sellSize;
    public byte sellStage;
    public byte currentSellI;

    public byte buyStage;
    public int[] buyID;
    public int[] buyQty;
    public int[] buyPrice;
    public byte buySize;
    public int[] missedID;
    public int[] missedQty;
    public int[] missedPrice;
    public byte missedSize;
    public byte currentBuyI;

    public byte unPauseTimer;
    public byte unPauseSetUpClientTimer;

    public Timer changePlayerSetUpTimer;
    public byte[] playerSetUpOpts;
    public byte[] playerSetUpValues;
    public byte playerSetUpI;

    public int[] itemsToEqp;
    public byte itemsToEqpSize;
    public byte currentItemToEqp;
    public byte closeOutOfBankToEqp;
    public byte currentStageI;
    public byte[] stage;
    public boolean locked;
    public boolean restart;

    public int[] invLoadOutIDs;
    public int[] invLoadOutMin;
    public int[] invLoadOutMax;
    public int[] invLoadOutInit;
    public byte invLoadOutSize;
    public int[] eqpLoadOutIDs;
    public int[] eqpLoadOutMin;
    public int[] eqpLoadOutMax;
    public int[] eqpLoadOutInit;
    public byte eqpLoadOutSize;

    public boolean loadOutSetUp;

    public byte bankingReturnToI;

}
