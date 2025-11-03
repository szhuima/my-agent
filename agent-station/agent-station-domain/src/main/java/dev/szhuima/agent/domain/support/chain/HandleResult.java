package dev.szhuima.agent.domain.support.chain;

public class HandleResult<R> {
    private final R data;
    private final HandleDecision decision;


    public HandleResult(R data, HandleDecision decision) {
        this.data = data;
        this.decision = decision;
    }


    public R getData() {
        return data;
    }

    public HandleDecision getDecision() {
        return decision;
    }

    public static <R> HandleResult<R> keepGoing() {
        return new HandleResult<>(null, HandleDecision.CONTINUE);
    }

    public static <R> HandleResult<R> continueWith(R data) {
        return new HandleResult<>(data, HandleDecision.CONTINUE);
    }

    public static <R> HandleResult<R> stopWith(R data) {
        return new HandleResult<>(data, HandleDecision.STOP);
    }
}