package hello.exception.filter;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.UUID;

@Slf4j
public class LogFilter implements Filter {

    /*
    *   [ DispatcherType ]
    *
    *   필터는 이런 경우를 위해서 DispatcherType라는 옵션을 제공한다.
    *   이전 강의의 마지막에 다음 로그를 추가했다.
    *   log.info("dispatchType={}", request.getDispatcherType())
    *   그리고 출력해보면 오류 페이지에서 dispatchType=ERROR 로 나오는 것을 확인할 수 있다.
    *
    *   고객이 처음 요청하면 dispatcherType=REQUEST 이다.
    *   이렇듯 서블릿 스펙은 실제 고객이 요청한 것인지, 서버가 내부에서 오류 페이지를 요청하는 것인지
    *   DispatcherType으로 구분할 수 있는 방법을 제공한다.
    *
    *   REQUEST : 클라이언트 요청
    *   ERROR : 오류 요청
    *   FORWARD : MVC에서 배웠던 서블릿에서 다른 서블릿이나 JSP를 호출할 때 -> RequestDispatcher.forward(request, response);
    *   INCLUDE : 서블릿에서 다른 서블릿이나 JSP의 결과를 포함할 때 -> RequestDispatcher.include(request, response);
    *   ASYNC : 서블릿 비동기 호출
    *
    * */




    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.info("log filter init");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String requestURI = httpRequest.getRequestURI();

        String uuid = UUID.randomUUID().toString();

        try {
            log.info("REQUEST  [{}][{}][{}]", uuid, request.getDispatcherType(), requestURI);
            chain.doFilter(request, response);
        } catch (Exception e) {
            log.info("EXCEPTION {}", e.getMessage());
            throw e;
        } finally {
            log.info("RESPONSE [{}][{}][{}]", uuid, request.getDispatcherType(), requestURI);
        }

    }

    @Override
    public void destroy() {
        log.info("log filter destroy");
    }
}