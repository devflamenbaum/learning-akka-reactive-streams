package br.com.devflamenbaum;

import akka.Done;
import akka.NotUsed;
import akka.actor.typed.ActorSystem;
import akka.actor.typed.javadsl.Behaviors;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;

import java.util.Random;
import java.util.concurrent.CompletionStage;

public class ExploringMaterializedValues {
    public static void main(String[] args) {

        ActorSystem actorSystem = ActorSystem.create(Behaviors.empty(), "ExploringMaterializedValues");

        Random random = new Random();

        Source<Integer, NotUsed> source = Source.range(1, 100).map(x -> random.nextInt(1000) + 1);

        Flow<Integer, Integer, NotUsed> greaterThan200 = Flow.of(Integer.class)
                .filter(x -> x > 200);

        Flow<Integer, Integer, NotUsed> evenNumberFilter = Flow.of(Integer.class)
                .filter(x -> x % 2 == 0);

        Sink<Integer, CompletionStage<Done>> sink = Sink.foreach(System.out::println);

        source.via(greaterThan200).via(evenNumberFilter).to(sink).run(actorSystem);
    }
}
