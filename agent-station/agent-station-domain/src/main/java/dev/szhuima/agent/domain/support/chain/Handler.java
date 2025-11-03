package dev.szhuima.agent.domain.support.chain;

public interface Handler<P, R> {

    HandleResult<R> handle(ChainContext ctx, P param);

    /**
     * 处理器名称，默认是类名
     *
     * @return
     */
    default String name() {
        return this.getClass().getSimpleName();
    }


    Handler<P, R> next();

    void setNext(Handler<P, R> next);
}