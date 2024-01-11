package hello.exception;

import org.springframework.boot.web.server.ConfigurableWebServerFactory;
import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

// BasicErrorController 를 사용하려면 @Component 를 주석처리 해야 한다.
//@Component
public class WebServerCustomizer implements WebServerFactoryCustomizer<ConfigurableWebServerFactory> {

    /*
    *   [ HTML 페이지 VS API Error ]
    *
    *   BasicErrorController 를 확장하면 JSON 메시지도 변경할 수 있다.
    *   그런데 API Error는 조금 뒤에 설명할 @ExceptionHandler 가 제공하는 기능을 사용하는 것이 더 나은 방법이므로
    *   지금은 BasicErrorController 를 확장해서 JSON 오류 메시지를 변경할 수 있다 정도로만 이해해두자.
    *
    *   Spring Boot가 제공하는 BasicErrorController는 HTML 페이지를 제공하는 경우에는 매우 편리하다.
    *   4xx,5xx 등등 모두 잘 처리해준다.
    *
    *   그런데 API 오류 처리는 다른 차원의 이야기이다.
    *   API 마다, 각각의 Controller , Exception , 서로 다른 응답 결과를 출력해야 할 수도 있다.
    *
    *   예를 들어서 회원과 관련된 API에서
    *   예외가 발생할 때 응답과,
    *   상품과 관련된 API에서 발생하는 예외에 따라 그 결과가 달라질 수 있다.
    *   결과적으로 매우 세밀하고 복잡하다. 따라서 이 방법은 HTML 화면을 처리할 때 사용하고,
    *   API 오류 처리는 뒤에서 설명할 @ExceptionHandler 를 사용하자.
    *
    * */

    @Override
    public void customize(ConfigurableWebServerFactory factory) {

        ErrorPage errorPage404 = new ErrorPage(HttpStatus.NOT_FOUND, "/error-page/404");
        ErrorPage errorPage500 = new ErrorPage(HttpStatus.INTERNAL_SERVER_ERROR, "/error-page/500");

        ErrorPage errorPageEx = new ErrorPage(RuntimeException.class, "/error-page/500");

        factory.addErrorPages(errorPage404, errorPage500, errorPageEx);
    }

    /*
    *   WebServerCustomizer가 다시 사용되도록 하기 위해 @Component 애노테이션에 있는 주석을 풀자
    *   이제 WAS에 예외가 전달되거나, response.sendError()가 호출되면 위에 등록한 예외 페이지 경로가 호출된다.
    *
    * */

    /*
    *   [ Spring Boot 기본 오류 처리 ]
    *
    *   API 예외 처리도 스프링 부타가 제공하는 기본 오류 방식을 사용할 수 있다.
    *   스프링 부트가 제공하는 BasicErrorController 코드를 보면 확인할 수 있다.
    *
    *   @RequestMapping(produces = MediaType.TEXT_HTML_VALUE)
        public ModelAndView errorHtml(HttpServletRequest request, HttpServletResponse
        response) {}

        @RequestMapping
        public ResponseEntity<Map<String, Object>> error(HttpServletRequest request) {}
    *
    *   /error 동일한 경로를 처리하는 errorHtml(),error() 두 메서드를 확인할 수 있다.
    *
    *   errorHtml() : produces= MediaType.TEXT_HTML_VALUE : 클라이언트 요청의 Accept 헤더 값이
    *   text/html인 경우에는 errorHtml()을 호출해서 view를 제공한다.
    *
    *   error(): 그 외 경우에 호출되고 ResponseEntity로 HTTP Body에 JSON 데이터를 반환한다.
    *
    *
    *   [ 결과 ]
    *   {
         "timestamp": "2021-04-28T00:00:00.000+00:00",
         "status": 500,
         "error": "Internal Server Error",
         "exception": "java.lang.RuntimeException",
         "trace": "java.lang.RuntimeException: 잘못된 사용자\n\tat
        hello.exception.web.api.ApiExceptionController.getMember(ApiExceptionController
        .java:19...,
         "message": "잘못된 사용자",
         "path": "/api/members/ex"
        }
     *  ---------------------------------------------------------------------------
     *
     *  Spring Boot BasicErrorController가 제공하는 기본 정보들을 활용해서 Error API를 생성해준다.
    * */
}
