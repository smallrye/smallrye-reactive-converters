package io.smallrye.reactive.converters.mutiny;

import java.util.concurrent.CompletionException;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Flow;

import org.reactivestreams.Publisher;

import io.smallrye.mutiny.Multi;
import io.smallrye.reactive.converters.ReactiveTypeConverter;
import mutiny.zero.flow.adapters.AdaptersToFlow;
import mutiny.zero.flow.adapters.AdaptersToReactiveStreams;

@SuppressWarnings("rawtypes")
public class MultiConverter implements ReactiveTypeConverter<Multi> {

    @SuppressWarnings("unchecked")
    @Override
    public <X> CompletionStage<X> toCompletionStage(Multi instance) {
        return instance.toUni().subscribeAsCompletionStage();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <X> Publisher<X> toRSPublisher(Multi instance) {
        return AdaptersToReactiveStreams.publisher(instance);
    }

    @Override
    public <X> Flow.Publisher<X> toFlowPublisher(Multi instance) {
        return instance;
    }

    @Override
    public <X> Multi fromCompletionStage(CompletionStage<X> cs) {
        return Multi.createFrom().completionStage(cs)
                .onFailure(CompletionException.class).transform(Throwable::getCause);
    }

    @Override
    public <X> Multi fromPublisher(Publisher<X> publisher) {
        return Multi.createFrom().publisher(AdaptersToFlow.publisher(publisher));
    }

    @Override
    public <X> Multi fromFlowPublisher(Flow.Publisher<X> publisher) {
        return Multi.createFrom().publisher(publisher);
    }

    @Override
    public Class<Multi> type() {
        return Multi.class;
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
