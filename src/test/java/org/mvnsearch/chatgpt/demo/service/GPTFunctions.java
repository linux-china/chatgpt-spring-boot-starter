package org.mvnsearch.chatgpt.demo.service;

import jakarta.annotation.Nonnull;
import org.mvnsearch.chatgpt.model.function.GPTFunction;
import org.mvnsearch.chatgpt.model.function.Parameter;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Component
class GPTFunctions {

	public record SQLQueryRequest(@Parameter(required = true, value = "SQL to query") String sql) {
	}

	public record SendEmailRequest(@Nonnull @Parameter("Recipients of email") List<String> recipients,
			@Nonnull @Parameter("Subject of email") String subject, @Parameter("Content of email") String content) {
	}

	@GPTFunction(name = "execute_sql_query", value = "Execute SQL and return the result set")
	Mono<List<String>> executeSQLQuery(SQLQueryRequest request) {
		System.out.println("Execute SQL: " + request.sql);
		List<String> lines = new ArrayList<>();
		lines.add("id, name, salary");
		lines.add("1, Jackie, 8000");
		lines.add("2, Libing, 78000");
		lines.add("3, Sam, 7500");
		return Mono.just(lines);
	}

	@GPTFunction(name = "send_email", value = "Send email to recipients")
	String sendEmail(SendEmailRequest request) {
		System.out.println("Recipients: " + String.join(",", request.recipients));
		System.out.println("Subject:" + request.subject);
		System.out.println("Content:\n" + request.content);
		return "Email sent to " + String.join(",", request.recipients) + " successfully!";
	}

	public record PlaySongsRequest(
			@Nonnull @Parameter("Song's name, singer's name, playlist's name or station's name") String name) {

	}

	@GPTFunction(name = "play_songs", value = "Play music songs")
	String playSongs(PlaySongsRequest request) {
		return "Songs: " + request.name() + " played successfully!";
	}

}
