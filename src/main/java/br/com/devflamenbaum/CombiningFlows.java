package br.com.devflamenbaum;

import akka.Done;
import akka.NotUsed;
import akka.actor.typed.ActorSystem;
import akka.actor.typed.javadsl.Behaviors;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;

import java.util.List;
import java.util.concurrent.CompletionStage;

public class CombiningFlows {
    public static void main(String[] args) {
        ActorSystem actorSystem = ActorSystem.create(Behaviors.empty(), "actorSystem");

        Source<String, NotUsed> sentencesSource = Source.from(
                List.of("The first sentence", "The second sentence", "The third sentence",
                        "The fourth sentence"));

        Flow<String, Integer, NotUsed> howManyWordsFlow = Flow.of(String.class)
                .map(sentence -> sentence.split(" ").length);

        Source<Integer, NotUsed> howManyWordsSource = sentencesSource.via(howManyWordsFlow);

        Source<Integer, NotUsed> sentencesSource2 = Source.from(
                List.of("The first sentence", "The second sentence", "The third sentence",
                        "The fourth sentence"))
                .map(sentence -> sentence.split(" ").length);

        Sink<Integer, CompletionStage<Done>> sink = Sink.ignore();

        Sink<String, NotUsed> combinedStage = howManyWordsFlow.to(sink);
    }
}
