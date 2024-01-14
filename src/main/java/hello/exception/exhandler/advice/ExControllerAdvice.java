package hello.exception.exhandler.advice;

import hello.exception.exception.UserException;
import hello.exception.exhandler.ErrorResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/*
*   [API 예외 처리 - @ControllerAdvice]
*
*   @ExceptionHandler를 사용해서 예외를 깔끔하게 처리할 수 있게 되었지만, 정상 코드와 예외 처리 코드가 하나의 컨트롤러에 섞여 있다.
*   @ControllerAdvice 또는 @RestControllerAdvice를 사용하면 둘을 분리할 수 있다.
*
*
*   [ @ControllerAdvice ]
*
*   -> @ControllerAdvice는 대상으로 지정한 여러 컨트롤러에 @ExceptionHandler, @InitBinder 기능을 부여해주는 역할을 한다.
*   -> @ControllerAdvice에 대상을 지정하지 않으면 모든 컨트롤러에 적용된다.(글로벌 적용)
*   -> @RestControllerAdvice는 @ControllerAdvice와 같고, @ResponseBody가 추가되어 있다.
*      @Controller, @RestController의 차이와 같다.
*
*   스프링 공식 문서 예제에서 보는 것 처럼 특정 애노테이션이 있는 컨트롤러를 지정할 수 있고, 특정 패키지를 직접 지정 할 수도 있다.
*   패키지 지정의 경우 해당 패키지와 그 하위에 있는 컨트롤러가 대상이 된다.
*   그리고 특정 클래스를 지정할 수도 있다.
*
*   대상 컨트롤러 지정을 생략하면 모든 컨트롤러에 적용된다.
* */
@Slf4j
@RestControllerAdvice(basePackages = "hello.exception.api")
public class ExControllerAdvice {



    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public ErrorResult illegalExHandler(IllegalArgumentException e) {
        log.error("[exceptionHandler] ex", e);
        return new ErrorResult("BAD", e.getMessage());
        /*
        *   [ @ExceptionHandler 예외 처리 방법 ]
        *   @ExceptionHandler 애노테이션을 선언하고, 해당 컨트롤러에서 처리하고 싶은 예외를 지정해주면 된다.
        *   해당 컨트롤러에서 예외가 발생하면 이 메서드가 호출된다. 참고로 지정한 예외 또는 그 예외의 자식 클래스는 모두 잡을 수 있다.
        *
        *   위 예제는 IllegalArgumentException 또는 그 하위 자식 클래스를 모두 처리할 수 있다.
        * */


        /*
        *   [ 실행 흐름 ]
        *
        *   -> 컨트롤러를 호출한 결과 IllegalArgumentException 예외가 컨트롤러 밖으로 던져진다.
        *   -> 예외가 발생했으므로 ExceptionResolver가 작동한다. 가장 우선순위가 높은 ExceptionHandlerExceptionResolver가 실행된다.
        *   -> ExceptionHandlerExceptionResolver는 해당 컨트롤러에 IllegalArgumentException을 처리 할 수 있는 @ExceptionHandler가 있는지 확인한다.
        *   -> illegalExHandle()를 실행한다. @RestController 이므로 illegalExHandle()에도 @ResponseBody가 적용된다.
        *      따라서 HTTP 컨버터가 사용되고, 응답이 다음과 같은 JSON으로 반환된다.
        *   -> @ResponseStatus(HttpStatus.BAD_REQUEST)를 지정했으므로 HTTP 상태 코드 400 으로 응답한다.
        *
        * */
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResult> userExHandler(UserException e) {
        /*
        *   [ 예외 생략 ]
        *   @ExceptionHandler에 예외를 생략할 수 있다. 생략하면 메서드 파라미터의 예외가 지정된다.
        * */
        log.error("[exceptionHandler] ex", e);
        ErrorResult errorResult = new ErrorResult("USER-EX", e.getMessage());
        return new ResponseEntity(errorResult, HttpStatus.BAD_REQUEST);

        /*
        *   [UserException 처리]
        *
        *   -> @ExceptionHandler 에 예외를 지정하지 않으면 해당 메서드 파라미터 예외를 사용한다. 여기서는 UserException을 사용한다.
        *   -> ResponseEntity를 사용해서 HTTP 메시지 바디에 직접 응답한다. 물론 HTTP 컨버터가 사용된다.
        *   -> ResponseEntity를 사용하면 HTTP 응답 코드를 프로그래밍해서 동적으로 변경할 수 있다.
        *      앞서 살펴본 @ResponseEntity는 애노테이션이므로 HTTP 응답 코드를 동적으로 변경할 수 없다.
        * */
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler
    public ErrorResult exHandler(Exception e) {
        log.error("[exceptionHandler] ex", e);
        return new ErrorResult("EX", "내부 오류");

        /*
        *   [Exception]
        *
        *   -> throw new RuntimeException("잘못된 사용자") 이 코드가 실행되면서, 컨트롤러 밖으로 RuntimeException이 던져진다.
        *   -> RuntimeException은 Exception의 자식 클래스이다. 따라서 이 메서드가 호출된다.
        *   -> @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)로 HTTP 상태 코드를 500으로 응답한다.
        * */


    }

    /*
     *   [API 예외 처리 - @ExceptionHandler]
     *
     *  < HTML 화면 오류 VS API 오류 >
     *
     *  웹 브라우저에 HTML 화면을 제공할 때는 오류가 발생한면 BasicErrorController 를 사용하는게 편하다.
     *  이때는 단순히 5xx, 4xx 관련된 오류 화면을 보여주면 된다. BasicErrorController는 이런 메커니즘을 모두 구현해두었다.
     *
     *  그런데 API는 각 시스템 마다 응답의 모양도 다르고, 스페도 모두 다르다.
     *  예외 상황에 단순히 오류 화면을 보여주는 것이 아니라, 예외에 따라서 각각 다른 데이터를 출력해야 할 수도 있다.
     *  그리고 같은 예외라고 해도 어떤 컨트롤러에서 발생했는가에 따라서 다른 예외 응답을 내려주어야 할 수 있다. 한마디로 매우 세밀한 제어가 필요하다.
     *  앞서 이야기했지만, 예를 들어서 상품 API와 주문 API는 오류가 발생했을 때 응답의 모양이 완전히 다를 수 있다.
     *
     *  결국 지금까지 살펴본 BasicErrorController를 사용하거나 HandlerExceptionResolver를 직접 구현하는 방식으로 API 예외를 다루기는 쉽지 않다.
     *
     *  < API 예외처리의 어려운 점 >
     *
     *  -> HandlerExceptionResolver를 떠올려 보면 ModelAndView를 반환해야 했다. 이것은 API 응답에는 필요하지 않다/
     *  -> API 응답을 위해서 HttpServletResponse에 직접 응답 데이터를 넣어주었다. 이것은 매우 불편하다. 스프링 컨트롤러에 비유하면 마치 과거 서블릿을 사용하던 시절로 돌아간 것 같다.
     *  -> 특정 컨트롤러에서만 발생하는 예외를 별도로 처리하기 어렵다.
     *  예를 들어서 회원을 처리하는 컨트롤러에서 발생하는 RunTimeException 예외와 상품을 관리하는 컨트롤러에서 발생하는 동일한 RuntimeException 예외를 서로 다른 방식으로 처리하고 싶다면 어떻게 해야할까?
     *
     *  < @ExceptionHandler >

     *  스프링은 API 예외 처리 문제를 해결하기 위해 @ExceptionHandler 라는 애노테이션을 사용하는 매우 편리한 예외 처리 기능을 제공하는데,
     *  이것이 바로 ExceptionHandlerExceptionResolver이다.
     *  스프링은 ExceptionHandlerExceptionResolver를 기본으로 제공하고, 기본으로 제공하는 ExceptionResolver 중에 우선 순위도 가장 높다.
     *  실무에서 API 예외 처리는 대부분 이 기능을 사용한다.
     * */

}
