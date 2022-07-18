package io.smallrye.reactive.converters.rxjava3;

import java.util.concurrent.CompletionException;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Flow;

import org.reactivestreams.Publisher;

import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.FlowableEmitter;
import io.smallrye.reactive.converters.ReactiveTypeConverter;
import mutiny.zero.flow.adapters.AdaptersToFlow;
import mutiny.zero.flow.adapters.AdaptersToReactiveStreams;

/**
 * Converter handling the RX Java 3 {@link Flowable} type.
 *
 *
 * <strong>toCompletionStage</strong><br>
 * The {@link #toCompletionStage(Flowable)} method returns a {@link CompletionStage} instance completed or failed
 * according to the stream emissions. The returned {@link CompletionStage} is redeemed with the first emitted value,
 * {@code null} if the stream is empty. If the stream emits multiple values, the first one is used, and the
 * {@link CompletionStage} is completed with the first emitted item. Other items and potential error are ignored. If
 * the stream fails before emitting a first item, the {@link CompletionStage} is completed with the failure.
 *
 *
 * <strong>fromCompletionStage</strong><br>
 * The {@link #fromCompletionStage(CompletionStage)} method returns a {@link Flowable} instance completed or failed
 * according to the passed {@link CompletionStage} completion. Note that if the future emits a {@code null} value,
 * the {@link Flowable} fails. If the future completes with a value, the observable emits the value and then completes.
 * If the future completes with a failure, the stream emits the failure.
 *
 *
 * <strong>fromPublisher</strong><br>
 * The {@link #fromPublisher(Publisher)} method returns a {@link Flowable} emitting the same items, failure and
 * completion as the passed {@link Publisher}. If the passed {@link Publisher} is empty, the returned {@link Flowable}
 * is also empty. This operation is a pass-through for back-pressure and its behavior is determined by the back-pressure
 * behavior of the passed publisher. This operation uses {@link Flowable#fromPublisher(Publisher)}. If the passed
 * {@link Publisher} is already a {@link Flowable}, the instance is used directly.
 *
 *
 * <strong>toRSPublisher</strong><br>
 * The {@link #toRSPublisher(Flowable)} method returns a {@link Publisher} emitting the same events as the source
 * {@link Flowable}. This operation is a pass-through for back-pressure and its behavior is determined by the
 * back-pressure behavior of the passed {@link Flowable}. This operation returns the passed {@link Flowable} directly.
 *
 */
@SuppressWarnings("rawtypes")
public class FlowableConverter implements ReactiveTypeConverter<Flowable> {

    @SuppressWarnings("unchecked")
    @Override
    public <T> Publisher<T> toRSPublisher(Flowable instance) {
        return instance;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <X> Flow.Publisher<X> toFlowPublisher(Flowable instance) {
        return AdaptersToFlow.publisher(instance);
    }

    @Override
    public Flowable fromPublisher(Publisher publisher) {
        return Flowable.fromPublisher(publisher);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> CompletionStage<T> toCompletionStage(Flowable instance) {
        return ((Flowable<T>) instance).firstElement().toCompletionStage(null);
    }

    @Override
    public <X> Flowable fromCompletionStage(CompletionStage<X> cs) {
        return Flowable.create(emitter -> toStreamEvents(cs, emitter), BackpressureStrategy.BUFFER);
    }

    @Override
    public <X> Flowable fromFlowPublisher(Flow.Publisher<X> publisher) {
        return fromPublisher(AdaptersToReactiveStreams.publisher(publisher));
    }

    static <X> void toStreamEvents(CompletionStage<X> cs, FlowableEmitter<Object> emitter) {
        cs.whenComplete((X res, Throwable err) -> {
            emitter.setCancellable(emitter::onComplete);
            if (!emitter.isCancelled()) {
                if (res != null) {
                    emitter.onNext(res);
                    emitter.onComplete();
                } else {
                    if (err != null) {
                        emitter.onError(err instanceof CompletionException ? err.getCause() : err);
                    } else {
                        emitter.onComplete();
                    }
                }
            }
        });
    }

    @Override
    public Class<Flowable> type() {
        return Flowable.class;
    }

    @Override
    public boolean emitItems() {
        return true;
    }

    @Override
    public boolean emitAtMostOneItem() {
        return false;
    }

    @Override
    public boolean supportNullValue() {
        return false;
    }
}
