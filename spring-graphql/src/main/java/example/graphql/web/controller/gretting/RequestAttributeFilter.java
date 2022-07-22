package example.graphql.web.controller.gretting;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import org.springframework.stereotype.Component;

@Component
public class RequestAttributeFilter implements Filter {

  public static final String NAME_ATTRIBUTE = RequestAttributeFilter.class.getName() + ".name";

  @Override
  public void doFilter(
      final ServletRequest request, final ServletResponse response, final FilterChain chain)
      throws IOException, ServletException {
    request.setAttribute(NAME_ATTRIBUTE, "007");
    chain.doFilter(request, response);
  }

}
