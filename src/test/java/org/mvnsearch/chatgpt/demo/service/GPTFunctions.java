package org.mvnsearch.chatgpt.demo.service;


import jakarta.annotation.Nonnull;
import org.mvnsearch.chatgpt.model.function.GPTFunction;
import org.mvnsearch.chatgpt.model.function.GPTFunctionsStub;
import org.mvnsearch.chatgpt.model.function.Parameter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GPTFunctions implements GPTFunctionsStub {

    public record SQLQueryRequest(
            @Parameter(required = true, value = "SQL to query") String sql) {
    }

    @GPTFunction(name = "execute_sql_query", value = "Execute SQL query and return the result set")
    public String executeSQLQuery(SQLQueryRequest request) {
        System.out.println("Execute SQL: " + request.sql);
        return "id, name, salary\n1,Jackie,8000\n2,Libing,78000\n3,Sam,7500";
    }

    public record SendEmailRequest(
            @Nonnull @Parameter("Recipients of email") List<String> recipients,
            @Nonnull @Parameter("Subject of email") String subject,
            @Parameter("Content of email") String content) {
    }

    @GPTFunction(name = "send_email", value = "Send email to recipients")
    public String sendEmail(SendEmailRequest request) {
        System.out.println("Recipients: " + String.join(",", request.recipients));
        System.out.println("Subject:" + request.subject);
        System.out.println("Content:\n" + request.content);
        return "Email sent to " + String.join(",", request.recipients) + " successfully!";
    }
}
