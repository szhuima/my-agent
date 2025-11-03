package dev.szhuima.agent.domain.support.chain;

import java.util.Optional;

public class HandlerChain<P, R> {

    private Handler<P, R> head;

    private Handler<P, R> tail;


    public HandlerChain() {
    }

    public HandlerChain(Handler<P, R> head) {
        this.head = head;
    }

    public HandlerChain<P, R> addHandler(Handler<P, R> handler) {
        if (head == null) {
            head = tail = handler;
        } else {
            tail.setNext(handler);
            tail = handler;
        }
        return this;
    }


    public Optional<R> handle(ChainContext ctx, P param) {
        if (head == null) return Optional.empty();
        HandleResult<R> res = head.handle(ctx, param);
        return Optional.ofNullable(res.getData());
    }

    public Optional<R> handleWithEmptyCtx(P param) {
        ChainContext ctx = new DefaultChainContext();
        if (head == null) return Optional.empty();
        HandleResult<R> res = head.handle(ctx, param);
        return Optional.ofNullable(res.getData());
    }


}