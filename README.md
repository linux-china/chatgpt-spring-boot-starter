ChatGPT Spring Boot Starter
===========================

# Get Started

* Add dependency in your pom.xml

```xml

<dependency>
    <groupId>org.mvnsearch</groupId>
    <artifactId>chatgpt-spring-boot-starter</artifactId>
    <version>0.1.0-SNAPSHOT</version>
</dependency>
```

* Add `openai.api.key` in `application.properties`:

```properties
# OpenAI API Token, or you can set environment variable OPENAI_API_KEY
openai.api.key=xxxx
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
}
```

# ChatGPT functions

* Create a Spring Bean with `@Component` and implement `GPTFunctionsStub` interface.

```java

@Component
public class GPTFunctions implements GPTFunctionsStub {

    public record SendEmailRequest(
            @Nonnull @Parameter("Receivers' email list") List<String> emails,
            @Nonnull @Parameter("Subject of email") String subject,
            @Parameter("Content of email") String content) {
    }

    @GPTFunction(name = "send_email", value = "Send email to receiver")
    public List<String> sendEmail(SendEmailRequest request) {
        System.out.println("send email to :" + String.join(",", request.emails));
        return request.emails;
    }
}
```

* Call GPT function by `chatMessage.getFunctionCall().getFunctionStub().call()`:

```java
public class ChatGPTServiceImplTest {
    @Test
    public void testChatWithFunctions() throws Exception {
        final ChatCompletionRequest request = ChatCompletionRequest.functions("Hi Jackie. If you have time, could you send an email to libing.chen@gmail.com and linux_china@hotmail.com and invite to join the party on tomorrow? Thanks!",
                List.of("send_email"));
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

* Spring Boot 3.0+
* Spring Boot Webflux
* Spring HTTP interface

# References

* OpenAI chat API: https://platform.openai.com/docs/api-reference/chat