package hello.exception.resolver;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class MyHandlerExceptionResolver implements HandlerExceptionResolver {

    /*
    *   [ HandlerExceptionResolver ]
    *
    *   SpringMVC 는 Controller(Handler) 밖으로 예외를 던져진 경우 예외를 해결하고,
    *   동작을 새로 정의할 수 있는 방법을 제공한다.
    *
    *   Controller 밖으로 던져진 예외를 해결하고, 동작 방식을 변경하고 싶으면
    *   HandlerExceptionResolver를 사용하면 된다.
    *
    *   줄여서 Exceptionresolver라 한다.
    *
    *
    *   참고 : ExceptionResolver로 예외를 해결해도 postHandle()은 호출되지 않는다.
    * */

    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {

        log.info("call resolver", ex);

        try {
            if (ex instanceof IllegalArgumentException) {
                log.info("IllegalArgumentException resolver to 400");
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, ex.getMessage());
                return new ModelAndView();
            }

        } catch (IOException e) {
            log.error("resolver ex", e);
        }

        return null;
    }
}
