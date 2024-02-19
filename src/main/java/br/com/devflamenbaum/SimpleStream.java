package br.com.devflamenbaum;

import akka.Done;
import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.actor.typed.javadsl.Behaviors;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.RunnableGraph;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;

import java.util.List;
import java.util.concurrent.CompletionStage;

public class SimpleStream {
    public static void main(String[] args) {

//        Source<Integer, NotUsed> source = Source.single(17); good for test and debugging
        Source<Integer, NotUsed> source = Source.range(1, 10);

        List<String> names = List.of("John", "Paul", "George", "Ringo");
        Source<String, NotUsed> namesSource = Source.from(names);

        Flow<Integer, String, NotUsed> flow = Flow.of(Integer.class).map(value -> "The next values is " + value);
        Flow<String, String, NotUsed> flow2 = Flow.of(String.class).map(value -> "The next name is " + value);

        Sink<String, CompletionStage<Done>> sink = Sink.foreach(System.out::println);

        RunnableGraph<NotUsed> graph = source.via(flow).to(sink);
        RunnableGraph<NotUsed> graph2 = namesSource.via(flow2).to(sink);

        ActorSystem actorSystem = ActorSystem.create("actorSystem");

        graph.run(actorSystem);
        graph2.run(actorSystem);

    }
}
