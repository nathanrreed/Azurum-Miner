package com.nred.azurum_miner.handler;

import net.neoforged.neoforge.transfer.RangedResourceHandler;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.resource.Resource;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import org.apache.commons.lang3.IntegerRange;

public class AwareRangedResourceHandler<T extends Resource> extends RangedResourceHandler<T> {
    private final IOState ioState;

    public AwareRangedResourceHandler(ResourceHandler<T> delegate, IntegerRange range, IOState ioState) {
        super(delegate, range.getMinimum(), range.getMaximum() + 1);
        this.ioState = ioState;
    }

    @Override
    public int insert(int index, T resource, int amount, TransactionContext transaction) {
        if (ioState == IOState.OUTPUT) return 0; // Don't pipe into output
        return super.insert(index, resource, amount, transaction);
    }

    @Override
    public int extract(int index, T resource, int amount, TransactionContext transaction) {
        if (ioState == IOState.INPUT) return 0; // Don't pipe out of input
        return super.extract(index, resource, amount, transaction);
    }

    public enum IOState {
        INPUT, OUTPUT, INPUT_OUTPUT
    }
}