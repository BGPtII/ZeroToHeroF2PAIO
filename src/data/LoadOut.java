package data;

import org.dreambot.api.utilities.impl.Condition;

/**
 *
 */
public class LoadOut {

    private int[] invIDs;
    private int[] invQtyMin; // < means reFill
    private int[] invQtyMax; // > means depositExtra
    private int[] invQtyInit; // < means reFill if initial == true
    private byte invSize;

    private int[] eqpIDs;
    private int[] eqpQtyMin;
    private int[] eqpQtyMax;
    private int[] eqpQtyInit;
    private byte eqpSize;

    private final Condition shouldBank;
    private boolean setUp;

    public LoadOut(int invSize, int eqpSize, Condition shouldBank) {
        setUp = true;
        invIDs = new int[invSize];
        invQtyMin = new int[invSize];
        invQtyMax = new int[invSize];
        invQtyInit = new int[invSize];

        eqpIDs = new int[eqpSize];
        eqpQtyMin = new int[eqpSize];
        eqpQtyMax = new int[eqpSize];
        eqpQtyInit = new int[eqpSize];

        if (shouldBank == null) {
            this.shouldBank = () -> setUp;
        }
        else {
            this.shouldBank = () -> shouldBank.verify() || setUp;
        }
    }

    public void exportToPersistedScriptInfo(PersistedScriptInfo p) {
        p.invLoadOutIDs = invIDs;
        p.invLoadOutMin = invQtyMin;
        p.invLoadOutMax = invQtyMax;
        p.invLoadOutInit = invQtyInit;
        p.invLoadOutSize = invSize;

        p.eqpLoadOutIDs = eqpIDs;
        p.eqpLoadOutMin = eqpQtyMin;
        p.eqpLoadOutMax = eqpQtyMax;
        p.eqpLoadOutInit = eqpQtyInit;
        p.eqpLoadOutSize = eqpSize;

        p.loadOutSetUp = setUp;
    }

    public void importFromPersistedScriptInfo(PersistedScriptInfo p) {
        invIDs = p.invLoadOutIDs;
        invQtyMin = p.invLoadOutMin;
        invQtyMax = p.invLoadOutMax;
        invQtyInit = p.invLoadOutInit;
        invSize = p.invLoadOutSize;

        eqpIDs = p.eqpLoadOutIDs;
        eqpQtyMin = p.eqpLoadOutMin;
        eqpQtyMax = p.eqpLoadOutMax;
        eqpQtyInit = p.eqpLoadOutInit;
        eqpSize = p.eqpLoadOutSize;

        setUp = p.loadOutSetUp;
    }

    public Condition shouldBank() {
        return shouldBank;
    }

    public boolean shouldSetUp() {
        return setUp;
    }

    public void setSetUp(boolean setUp) {
        this.setUp = setUp;
    }

    public void addInventoryItem(int id, int min, int max, int init) {
        invIDs[invSize] = id;
        invQtyMin[invSize] = min;
        invQtyMax[invSize] = max;
        invQtyInit[invSize++] = init;
    }
    public void setInventoryItem(int index, int min, int max, int init) {
        invQtyMin[index] = min;
        invQtyMax[index] = max;
        invQtyInit[index] = init;
    }
    public void setInventoryItem(int index, int id, int min, int max, int init) {
        invIDs[index] = id;
        invQtyMin[index] = min;
        invQtyMax[index] = max;
        invQtyInit[index] = init;
    }
    public void addEquipmentItem(int id, int min, int max, int init) {
        eqpIDs[eqpSize] = id;
        eqpQtyMin[eqpSize] = min;
        eqpQtyMax[eqpSize] = max;
        eqpQtyInit[eqpSize++] = init;
    }

    public void setEquipmentItem(int index, int id, int min, int max, int init) {
        eqpIDs[index] = id;
        eqpQtyMin[index] = min;
        eqpQtyMax[index] = max;
        eqpQtyInit[index] = init;
    }

    public byte getEqpSize() {
        return eqpSize;
    }
    public byte getInvSize() {
        return invSize;
    }

    public int getInvItemID(int index) {
        return invIDs[index];
    }
    public int getInvItemQtyInit(byte index) {
        return invQtyInit[index];
    }
    public int getInvItemQtyMin(int index)  {
        return invQtyMin[index];
    }
    public int getInvItemQtyMax(int index) {
        return invQtyMax[index];
    }

    public int getEqpItemID(int index) {
        return eqpIDs[index];
    }

    public int getEqpItemQtyInit(int index) {
        return eqpQtyInit[index];
    }
    public int getEqpItemQtyMin(int index) {
        return eqpQtyMin[index];
    }
    public int getEqpItemQtyMax(int index) {
        return eqpQtyMax[index];
    }

}
