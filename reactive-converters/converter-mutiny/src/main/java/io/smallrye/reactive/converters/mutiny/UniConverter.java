package io.smallrye.reactive.converters.mutiny;

import java.util.concurrent.CompletionException;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Flow;

import org.reactivestreams.Publisher;

import io.smallrye.mutiny.Uni;
import io.smallrye.reactive.converters.ReactiveTypeConverter;
import mutiny.zero.flow.adapters.AdaptersToFlow;
import mutiny.zero.flow.adapters.AdaptersToReactiveStreams;

@SuppressWarnings("rawtypes")
public class UniConverter implements ReactiveTypeConverter<Uni> {

    @SuppressWarnings("unchecked")
    @Override
    public <X> CompletionStage<X> toCompletionStage(Uni instance) {
        return instance.subscribeAsCompletionStage();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <X> Publisher<X> toRSPublisher(Uni instance) {
        return AdaptersToReactiveStreams.publisher(instance.toMulti());
    }

    @Override
    @SuppressWarnings("unchecked")
    public <X> Flow.Publisher<X> toFlowPublisher(Uni instance) {
        return instance.toMulti();
    }

    @Override
    public <X> Uni fromCompletionStage(CompletionStage<X> cs) {
        return Uni.createFrom().completionStage(cs)
                .onFailure(CompletionException.class).transform(Throwable::getCause);
    }

    @Override
    public <X> Uni fromPublisher(Publisher<X> publisher) {
        return Uni.createFrom().publisher(AdaptersToFlow.publisher(publisher));
    }

    @Override
    public <X> Uni fromFlowPublisher(Flow.Publisher<X> publisher) {
        return Uni.createFrom().publisher(publisher);
    }

    @Override
    public Class<Uni> type() {
        return Uni.class;
    }

    @Override
    public boolean emitItems() {
        return true;
    }

    @Override
    public boolean emitAtMostOneItem() {
        return true;
    }

    @Override
    public boolean supportNullValue() {
        return true;
    }
}
