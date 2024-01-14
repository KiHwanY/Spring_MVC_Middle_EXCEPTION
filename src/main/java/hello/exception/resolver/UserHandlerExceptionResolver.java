package hello.exception.resolver;

import com.fasterxml.jackson.databind.ObjectMapper;
import hello.exception.exception.UserException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class UserHandlerExceptionResolver implements HandlerExceptionResolver {

    /*
    *   HTTP 요청 헤더의 ACCEPT 값이 application/json 이면 JSON 으로 오류를 내려주고, 그 외 경우에는 error/500에 있는
    *   HTML 오류 페이지를 보여준다.
    * */

    /*
    *   [정리]
    *
    *   ExceptionResolver를 사용하면 Controller에서 예외가 발생해도 ExceptionResolver에서 예외를 처리해버린다.
    *   따라서 예외가 발생해도 서블릿 컨테이너까지 예외가 전달되지 않고, SpringMVC 에서 예외 처리는 끝이 난다.
    *
    *   결과적으로 WAS 입장에서는 정상 처리가 된 것이다. 이렇게 예외를 이곳에서 모두 처리할 수 있다는 것이 핵심이다.
    *
    *   서블릿 컨테이너까지 예외가 올라가면 복잡하고 지ㅈ분하게 추가 프로세스가 실행된다.
    *   반면에 ExceptionResolver를 사용하면 예외 처리가 상당히 깔끔해진다.
    *
    *   그런데 직접 ExceptionResolver를 구현하려고 하니 상당히 복잡하다.
    *   지금부터 스프링이 제공하는 ExceptionResolver들을 알아보자.
    * */



    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {

        try {

            if (ex instanceof UserException) {
                log.info("UserException resolver to 400");
                String acceptHeader = request.getHeader("accept");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);

                if ("application/json".equals(acceptHeader)) {
                    Map<String, Object> errorResult = new HashMap<>();
                    errorResult.put("ex", ex.getClass());
                    errorResult.put("message", ex.getMessage());
                    String result = objectMapper.writeValueAsString(errorResult);

                    response.setContentType("application/json");
                    response.setCharacterEncoding("utf-8");
                    response.getWriter().write(result);
                    return new ModelAndView();
                } else {
                    // TEXT/HTML
                    return new ModelAndView("error/500");
                }
            }

        } catch (IOException e) {
            log.error("resolver ex", e);
        }

        return null;
    }


}
