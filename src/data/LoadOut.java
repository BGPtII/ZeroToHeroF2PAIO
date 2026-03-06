package data;

import org.dreambot.api.utilities.impl.Condition;

import java.util.Arrays;

/**
 *
 */
public class LoadOut {

    private int[] INV_ID;
    private int[] INV_QTY_MIN; // < means reFill
    private int[] INV_QTY_MAX; // > means depositExtra
    private int[] INV_QTY_INIT; // < means reFill if initial == true
    private byte invSize;

    private int[] EQP_ID;
    private int[] EQP_QTY_MIN;
    private int[] EQP_QTY_MAX;
    private int[] EQP_QTY_INIT;
    private byte eqpSize;

    private final Condition shouldBank;
    private boolean setUp;

    public LoadOut(int invSize, int eqpSize, Condition shouldBank) {
        setUp = true;
        INV_ID = new int[invSize];
        INV_QTY_MIN = new int[invSize];
        INV_QTY_MAX = new int[invSize];
        INV_QTY_INIT = new int[invSize];

        EQP_ID = new int[eqpSize];
        EQP_QTY_MIN = new int[eqpSize];
        EQP_QTY_MAX = new int[eqpSize];
        EQP_QTY_INIT = new int[eqpSize];

        if (shouldBank == null) {
            this.shouldBank = () -> setUp;
        }
        else {
            this.shouldBank = () -> shouldBank.verify() || setUp;
        }
    }

    public void exportToPersistedScriptInfo(PersistedScriptInfo p) {
        p.invLoadOutMin = INV_QTY_MIN;
        p.invLoadOutMax = INV_QTY_MAX;
        p.invLoadOutInit = INV_QTY_INIT;
        p.invLoadOutSize = invSize;

        p.eqpLoadOutMin = EQP_QTY_MIN;
        p.eqpLoadOutMax = EQP_QTY_MAX;
        p.eqpLoadOutInit = EQP_QTY_INIT;
        p.eqpLoadOutSize = eqpSize;

        p.loadOutSetUp = setUp;
    }

    public void importFromPersistedScriptInfo(PersistedScriptInfo p) {
        INV_QTY_MIN = p.invLoadOutMin;
        INV_QTY_MAX = p.invLoadOutMax;
        INV_QTY_INIT = p.invLoadOutInit;
        invSize = p.invLoadOutSize;

        EQP_QTY_MIN = p.eqpLoadOutMin;
        EQP_QTY_MAX = p.eqpLoadOutMax;
        EQP_QTY_INIT = p.eqpLoadOutInit;
        eqpSize = p.eqpLoadOutSize;

        setUp = p.loadOutSetUp;
    }

    public Condition shouldBank() {
        return shouldBank;
    }

    public void setSetUp(boolean setUp) {
        this.setUp = setUp;
    }

    public void addInventoryItem(int id, int min, int max, int init) {
        INV_ID[invSize] = id;
        INV_QTY_MIN[invSize] = min;
        INV_QTY_MAX[invSize] = max;
        INV_QTY_INIT[invSize++] = init;
    }
    public void setInventoryItem(int index, int min, int max, int init) {
        INV_QTY_MIN[index] = min;
        INV_QTY_MAX[index] = max;
        INV_QTY_INIT[index] = init;
    }
    public void setInventoryItem(int index, int id, int min, int max, int init) {
        INV_ID[index] = id;
        INV_QTY_MIN[index] = min;
        INV_QTY_MAX[index] = max;
        INV_QTY_INIT[index] = init;
    }
    public void addEquipmentItem(int id, int min, int max, int init) {
        EQP_ID[eqpSize] = id;
        EQP_QTY_MIN[eqpSize] = min;
        EQP_QTY_MAX[eqpSize] = max;
        EQP_QTY_INIT[eqpSize++] = init;
    }
    public void setEquipmentItem(int index, int min, int init) {
        EQP_QTY_MIN[index] = min;
        EQP_QTY_INIT[index] = init;
    }
    public void setEquipmentItem(int index, int id, int min, int max, int init) {
        EQP_ID[index] = id;
        EQP_QTY_MIN[index] = min;
        EQP_QTY_MAX[index] = max;
        EQP_QTY_INIT[index] = init;
    }

    public byte getEqpSize() {
        return eqpSize;
    }
    public byte getInvSize() {
        return invSize;
    }

    public int getInvItemID(int index) {
        return INV_ID[index];
    }
    public int getInvItemQtyInit(byte index) {
        return INV_QTY_INIT[index];
    }
    public int getInvItemQtyMin(int index)  {
        return INV_QTY_MIN[index];
    }
    public int getInvItemQtyMax(int index) {
        return INV_QTY_MAX[index];
    }

    public int getEqpItemID(int index) {
        return EQP_ID[index];
    }

    public int getEqpItemQtyInit(int index) {
        return EQP_QTY_INIT[index];
    }
    public int getEqpItemQtyMin(int index) {
        return EQP_QTY_MIN[index];
    }
    public int getEqpItemQtyMax(int index) {
        return EQP_QTY_MAX[index];
    }

}
