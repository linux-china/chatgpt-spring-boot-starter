ChatGPT Spring Boot Starter
===========================

Spring Boot ChatGPT starter with ChatGPT chat and functions support.

# Features

* Base on Spring Boot 3.0+
* Async with Spring Webflux
* Support ChatGPT Chat Stream
* Support ChatGPT functions: `@GPTFunction` annotation
* Prompt Management: load prompt templates from `prompt.properties` with `@PropertyKey`, and friendly with IntelliJ IDEA
* Prompt as Lambda: convert prompt template to lambda expression and call it with FP style
* ChatGPT interface: Declare ChatGPT service interface with `@ChatGPTExchange` and `@ChatCompletion` annotations.
* No third-party library: base on Spring 6 HTTP interface
* GraalVM native image support
* Azure OpenAI support

# Get Started

### Add dependency

Add `chatgpt-spring-boot-starter` dependency in your pom.xml.

```xml

<dependency>
    <groupId>org.mvnsearch</groupId>
    <artifactId>chatgpt-spring-boot-starter</artifactId>
    <version>0.4.0</version>
</dependency>
```

### Adjust configuration

Add `openai.api.key` in `application.properties`:

```properties
# OpenAI API Token, or you can set environment variable OPENAI_API_KEY
openai.api.key=sk-xxxx
```

If you want to use Azure OpenAI, you can add `openai.api.url` in `application.properties`:

```properties
openai.api.key=1138xxxx9037
openai.api.url=https://YOUR_RESOURCE_NAME.openai.azure.com/openai/deployments/YOUR_DEPLOYMENT_NAME/chat/completions?api-version=2023-05-15
```

### Call ChatGPT Service

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

# ChatGPT Service Interface

ChatGPT service interface is almost like [Spring 6 HTTP Interface](https://docs.spring.io/spring-framework/reference/integration/rest-clients.html#rest-http-interface).
You can declare a ChatGPT service interface with `@ChatGPTExchange` annotation, and declare completion methods
with `@ChatCompletion` annotation, then you just call service interface directly.

```java

@GPTExchange
public interface GPTHelloService {

    @ChatCompletion("You are a language translator, please translate the below text to Chinese.\n")
    Mono<String> translateIntoChinese(String text);

    @ChatCompletion("You are a language translator, please translate the below text from {0} to {1}.\n {2}")
    Mono<String> translate(String sourceLanguage, String targetLanguage, String text);
}
```

Create ChatGPT interface service bean:

```
    @Bean
    public GPTHelloService gptHelloService(ChatGPTServiceProxyFactory proxyFactory) {
        return proxyFactory.createClient(GPTHelloService.class);
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
        System.out.println("Recipients: " + String.join(",", request.recipients));
        System.out.println("Subject: " + request.subject);
        System.out.println("Content:\n" + request.content);
        return "Email sent to " + String.join(",", request.recipients) + " successfully!";
    }

    public record SQLQueryRequest(
            @Parameter(required = true, value = "SQL to query") String sql) {
    }

    @GPTFunction(name = "execute_sql_query", value = "Execute SQL query and return the result set")
    public String executeSQLQuery(SQLQueryRequest request) {
        System.out.println("Execute SQL: " + request.sql);
        return "id, name, salary\n1,Jackie,8000\n2,Libing,78000\n3,Sam,7500";
    }
}
```

* Call GPT function by `response.getReplyCombinedText()` or `chatMessage.getFunctionCall().getFunctionStub().call()`:

```java
public class ChatGPTServiceImplTest {
    @Test
    public void testChatWithFunctions() throws Exception {
        final String prompt = "Hi Jackie, could you write an email to Libing(libing.chen@gmail.com) and Sam(linux_china@hotmail.com) and invite them to join Mike's birthday party at 4 pm tomorrow? Thanks!";
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

    @Test
    public void testExecuteSQLQuery() {
        String context = "You are SQL developer. Write SQL according to requirements, and execute it in MySQL database.";
        final String prompt = "Query all employees whose salary is greater than the average.";
        final ChatCompletionRequest request = ChatCompletionRequest.functions(prompt, List.of("execute_sql_query"));
        // add prompt context as system message
        request.addMessage(ChatMessage.systemMessage(context));
        final ChatCompletionResponse response = chatGPTService.chat(request).block();
        System.out.println(response.getReplyCombinedText());
    }
}
```

**Note**: `@GPTExchange` and `@ChatCompletion` has functions built-in, so you just need to fill functions parameters.

### ChatGPT Functions use cases:

* Structure Output: such as SQL, JSON, CSV, YAML etc., then delegate functions to process them.
* Commands: such as send_email, post on Twitter.
* DevOps: such as generate K8S yaml file, then call K8S functions to deploy it.
* Search Matching: bind search with functions, such as search for a book, then call function to show it.
* Spam detection: email spam, advertisement spam etc
* PipeLine: you can think function as a node in pipeline. After process by function, and you can pass it to ChatGPT
  again.

If you want to have a simple test for ChatGPT functions, you can install [ChatGPT with Markdown
JetBrains IDE Plugin](https://plugins.jetbrains.com/plugin/21671-chatgpt-with-markdown), and take a look
at [chat.gpt file](./chat.gpt).

# Prompt Templates

How to manage prompts in Java? Now ChatGPT starter adopts `prompts.properties` to save prompt templates,
and uses MessageFormat to format template value.`PromptPropertiesStoreImpl` will load all ` prompts.properties` files
from classpath. You can extend `PromptStore` to load prompts from database or other sources.

You can load prompt template by [PromptManager](src/main/java/org/mvnsearch/chatgpt/spring/service/PromptManager.java).

Tips:

* Prompt template code completion: support by `@PropertyKey(resourceBundle = PROMPTS_FQN)`
* `@ChatCompletion` annotation has built-in prompt template support for `user`,`system` and `assistant` messages.

### Prompt Template as Lambda

For some case you want to use prompt template as lambda, such as translate first, then send it as email.
You can declare prompt as function and chain them together.

```
    @Test
    public void testPromptAsFunction() {
        Function<String, Mono<String>> translateIntoChineseFunction = chatGPTService.promptAsFunction("translate-into-chinese");
        Function<String, Mono<String>> sendEmailFunction = chatGPTService.promptAsFunction("send-email");
        String result = Mono.just("Hi Jackie, could you write an email to Libing(libing.chen@exaple.com) and Sam(linux_china@example.com) and invite them to join Mike's birthday party at 4 pm tomorrow? Thanks!")
                .flatMap(translateIntoChineseFunction)
                .flatMap(sendEmailFunction)
                .block();
        System.out.println(result);
    }
```

To keep placeholders safe in prompt template, you can use record as Lambda parameter.

```java
    public record TranslateRequest(String from, String to, String text) {
    }

    @Test
    public void testLambdaWithRecord() {
        Function<TranslateRequest, Mono<String>> translateFunction = chatGPTService.promptAsLambda("translate");
        String result = Mono.just(new TranslateRequest("Chinese", "English", "你好！"))
                .flatMap(translateFunction)
                .block();
        System.out.println(result);
    }
```

# FAQ

### OpenAI REST API proxy

Please refer [OpenAIProxyController](src/test/java/org/mvnsearch/chatgpt/demo/OpenAIProxyController.java).

```java

@RestController
public class OpenAIProxyController {
    @Autowired
    private OpenAIChatAPI openAIChatAPI;

    @PostMapping("/v1/chat/completions")
    public Publisher<ChatCompletionResponse> completions(@RequestBody ChatCompletionRequest request) {
        return openAIChatAPI.proxy(request);
    }
}
```

Of course, you can use standard URL `http://localhost:8080/v1/chat/completions` to call Azure OpenAI API.

### How to use ChatGPT with Spring Web?

Now ChatGPT starter use Reactive style API, and you know Reactive still hard to understand.
Could ChatGPT starter work with Spring Web? Yes, you can use `Mono` or `Flux` with Spring Web and Virtual Threads,
please refer [Spring Boot Virtual Thread Demo](https://github.com/linux-china/spring-boot-virtual-thread-demo#async-method-on-virtual-threads) for details.

# References

* [OpenAI chat API](https://platform.openai.com/docs/api-reference/chat)
* [Spring Boot 3.0+](https://docs.spring.io/spring-boot/docs/current/reference/html/)
* [Spring Boot Webflux](https://docs.spring.io/spring-framework/reference/web/webflux.html)
* [Spring 6 HTTP interface](https://docs.spring.io/spring-framework/reference/integration/rest-clients.html#rest-http-interface)
* [Properties File Format](https://docs.oracle.com/cd/E23095_01/Platform.93/ATGProgGuide/html/s0204propertiesfileformat01.html)
* [MessageFormat JavaDoc](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/text/MessageFormat.html)
