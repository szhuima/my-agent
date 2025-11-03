package dev.szhuima.agent.domain.support.chain;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractHandler<P, R> implements Handler<P, R> {

    private Handler<P, R> next;

    @Override
    public Handler<P, R> next() {
        return next;
    }


    @Override
    public void setNext(Handler<P, R> next) {
        this.next = next;
    }


    protected HandleResult<R> proceed(ChainContext ctx, P param, HandleResult<R> currentResult) {
        if (currentResult.getDecision() == HandleDecision.STOP) {
            return currentResult;
        }
        if (next == null) {
            return currentResult;
        }
        return next.handle(ctx, param);
    }
}