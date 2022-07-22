package example.graphql.web.controller.gretting;

import static org.springframework.web.context.request.RequestAttributes.SCOPE_REQUEST;

import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

@Controller
public class GreetingController {

  @QueryMapping
  public String greeting() {
    final RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
    return "Hello " + attributes.getAttribute(RequestAttributeFilter.NAME_ATTRIBUTE, SCOPE_REQUEST);
  }
}
