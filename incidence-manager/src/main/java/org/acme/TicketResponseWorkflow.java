package org.acme;

import static io.serverlessworkflow.fluent.func.FuncWorkflowBuilder.workflow;
import static io.serverlessworkflow.fluent.func.dsl.FuncDSL.agent;
import static io.serverlessworkflow.fluent.func.dsl.FuncDSL.consume;
import static io.serverlessworkflow.fluent.func.dsl.FuncDSL.consumed;
import static io.serverlessworkflow.fluent.func.dsl.FuncDSL.emitJson;
import static io.serverlessworkflow.fluent.func.dsl.FuncDSL.function;
import static io.serverlessworkflow.fluent.func.dsl.FuncDSL.listen;
import static io.serverlessworkflow.fluent.func.dsl.FuncDSL.switchWhenOrElse;
import static io.serverlessworkflow.fluent.func.dsl.FuncDSL.toOne;

import dev.langchain4j.agentic.scope.ResultWithAgenticScope;
import io.quarkiverse.flow.Flow;
import io.serverlessworkflow.api.types.FlowDirectiveEnum;
import io.serverlessworkflow.api.types.Workflow;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.acme.ai.Sentiment;
import org.acme.ai.TicketResponseExperts;

@ApplicationScoped
public class TicketResponseWorkflow extends Flow {

    @Inject
    TicketResponseExperts.AutoDraftFeedbackAgent autoDraftFeedbackAgent;

    @Override
    public Workflow descriptor() {
        return workflow("support-ticket")
            .tasks(
                agent("draftAgent", autoDraftFeedbackAgent::generateFeedback, String.class),
                switchWhenOrElse(tr -> Sentiment.POSITIVE == tr.sentiment().result(), "automatic", "manual", TiquetResponse.class),
                consume("automatic", System.out::println, TiquetResponse.class).then(FlowDirectiveEnum.END),
                emitJson("manual", "org.acme.email.review.required", TiquetResponse.class),
                listen("waitHumanReview",
                    toOne(consumed("org.acme.newsletter.review.done").extensionByInstanceId("flowinstanceid"))),
                consume("sendEmail", System.out::println, TiquetResponse.class)

            )
            .build();
    }
}
