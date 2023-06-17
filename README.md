ChatGPT Spring Boot Starter
===========================

Spring Boot ChatGPT starter with ChatGPT chat and functions support.

# Features

* Base on Spring Boot 3.0+
* Async with Spring Webflux
* Support ChatGPT Chat Stream
* Support ChatGPT functions
* No third-party library: base on Spring 6 HTTP interface

# Get Started

* Add `chatgpt-spring-boot-starter` dependency in your pom.xml.

```xml

<dependency>
    <groupId>org.mvnsearch</groupId>
    <artifactId>chatgpt-spring-boot-starter</artifactId>
    <version>0.1.0</version>
</dependency>
```

* Add `openai.api.key` in `application.properties`:

```properties
# OpenAI API Token, or you can set environment variable OPENAI_API_KEY
openai.api.key=sk-xxxx
```

* Call `ChatGPTService` in your code

```java

@RestController
public class ChatRobotController {
    @Autowired
    private ChatGPTService chatGPTService;

    @PostMapping("/chat")
    public Mono<String> chat(@RequestBody String content) {
        return chatGPTService.chat(ChatCompletionRequest.of(content))
                .map(ChatCompletionResponse::getReplyText);
    }
  
   @GetMapping("/stream-chat")
   public Flux<String> streamChat(@RequestParam String content) {
       return chatGPTService.stream(ChatCompletionRequest.of(content))
               .map(ChatCompletionResponse::getReplyText);
   }
}
```

# ChatGPT functions

* Create a Spring Bean with `@Component` and implement `GPTFunctionsStub` interface. Annotate GPT functions
  with `@GPTFunction` annotation, and annotate function parameters with `@Parameter` annotation.  `@Nonnull` means that
  the parameter is required.

```java

import jakarta.annotation.Nonnull;

@Component
public class GPTFunctions implements GPTFunctionsStub {

    public record SendEmailRequest(
            @Nonnull @Parameter("Recipients of email") List<String> recipients,
            @Nonnull @Parameter("Subject of email") String subject,
            @Parameter("Content of email") String content) {
    }

    @GPTFunction(name = "send_email", value = "Send email to receiver")
    public String sendEmail(SendEmailRequest request) {
        System.out.println("Recipients :" + String.join(",", request.recipients));
        System.out.println("Subject :" + request.subject);
        System.out.println("Content : \n" + request.content);
        return "Email sent to " + String.join(",", request.recipients) + " successfully!";
    }
}
```

* Call GPT function by `chatMessage.getFunctionCall().getFunctionStub().call()`:

```java
public class ChatGPTServiceImplTest {
  @Test
  public void testChatWithFunctions() throws Exception {
    final String prompt = "Hi Jackie, could you write an email to Libing(libing.chen@gmail.com) and Sam(linux_china@hotmail.com) and invite them to join Mike's birthday party at 4 tomorrow by yourself? Thanks!";
    final ChatCompletionRequest request = ChatCompletionRequest.functions(prompt, List.of("send_email"));
    final ChatCompletionResponse response = chatGPTService.chat(request).block();
    // display reply combined text with function call
    System.out.println(response.getReplyCombinedText());
    // call function manually
    for (ChatMessage chatMessage : response.getReply()) {
      final FunctionCall functionCall = chatMessage.getFunctionCall();
      if (functionCall != null) {
        final Object result = functionCall.getFunctionStub().call();
        System.out.println(result);
      }
    }
  }
}
```

# Tech Stack

* [Spring Boot 3.0+](https://docs.spring.io/spring-boot/docs/current/reference/html/)
* [Spring Boot Webflux](https://docs.spring.io/spring-framework/reference/web/webflux.html)
* [Spring 6 HTTP interface](https://docs.spring.io/spring-framework/reference/integration/rest-clients.html#rest-http-interface)

# References

* OpenAI chat API: https://platform.openai.com/docs/api-reference/chat