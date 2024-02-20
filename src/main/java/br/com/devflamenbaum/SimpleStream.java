package br.com.devflamenbaum;

import akka.Done;
import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.actor.typed.javadsl.Behaviors;
import akka.stream.ThrottleMode;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.RunnableGraph;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;

import java.time.Duration;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.stream.Stream;

public class SimpleStream {
    public static void main(String[] args) {

//        Source<Integer, NotUsed> source = Source.single(17); good for test and debugging

        List<String> names = List.of("John", "Paul", "George", "Ringo");
        Iterator<Integer> infiniteRange = Stream.iterate(0, n -> n + 1).iterator();

        Source<Integer, NotUsed> source = Source.range(1, 10);
        Source<String, NotUsed> namesSource = Source.from(names);
        Source<Double, NotUsed> sourcePi = Source.repeat(Math.PI);
        Source<String, NotUsed> repeatingNames = Source.cycle(names::iterator);
//        Source<Integer, NotUsed> infiniteSource = Source.fromIterator(() -> infiniteRange);
        Source<Integer, NotUsed> infiniteSource = Source
                .fromIterator(() -> infiniteRange)
                .throttle(1, Duration.ofSeconds(3))
                .take(5);

        Flow<Integer, String, NotUsed> flow = Flow.of(Integer.class).map(value -> "The next values is " + value);
        Flow<String, String, NotUsed> nameFlow = Flow.of(String.class).map(value -> "The next name is " + value);
        Flow<Double, String, NotUsed> piFlow = Flow.of(Double.class).map(value -> "The next pi is " + value);

        Sink<String, CompletionStage<Done>> sink = Sink.foreach(System.out::println);
        Sink<String, CompletionStage<Done>> sink2 = Sink.ignore();

        RunnableGraph<NotUsed> graph = source.via(flow).to(sink);
        RunnableGraph<NotUsed> graph2 = namesSource.via(nameFlow).to(sink);
        RunnableGraph<NotUsed> graph3 = sourcePi.via(piFlow).to(sink);
        RunnableGraph<NotUsed> graph4 = repeatingNames.via(nameFlow).to(sink);
        RunnableGraph<NotUsed> graph5 = infiniteSource.via(flow).to(sink);

        ActorSystem actorSystem = ActorSystem.create("actorSystem");

//        graph.run(actorSystem);
//        graph2.run(actorSystem);
//        graph3.run(actorSystem);
//        graph4.run(actorSystem);
//        graph5.run(actorSystem);

//        sink.runWith(repeatingNames.via(nameFlow), actorSystem);
//        equivalent to: repeatingNames.via(nameFlow).to(sink).run(actorSystem);

        repeatingNames.via(nameFlow).runForeach(System.out::println, actorSystem);
    }
}
