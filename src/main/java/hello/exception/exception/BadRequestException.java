package hello.exception.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


/*
*  BadRequestException 예외가 컨트롤러 밖으로 넘어가면 ResponseStatusExceptionResolver 예외가 해당 애노테이션을 확인해서 오류 코드를
*  HttpStatus.BAD_REQUEST(400)으로 변경하고, 메시지도 담는다.
*
*  ResponseStatusExceptionResolver 코드를 확인해보면 결국 response.sendError(statusCode, resolvedReason)를 호출하는 것을 확인할 수 있다.
*  sendError(400) 를 호출했기 때문에 WAS에서 다시 오류 페이지(/error)를 내부 요청한다.
*
* */
@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "error.bad")
public class BadRequestException extends RuntimeException {

}

/*
*   [API 예외 처리 - 스프링이 제공하는 ExceptionResolver1]
*
*   스프링 부트가 기본으로 제공하는 ExceptionResolver는 다음과 같다.
*   (HandlerExceptionResolverComposite에 다음 순서로 등록)
*
*   1. ExceptionHandlerExceptionResolver
*   2. ResponseStatusExceptionResolver
*   3. DefaultHandlerExceptionResolver -> 우선 순위가 가장 낮다.
*
*   ExceptionHandlerExceptionResolver
*   -> @ExceptionHandler을 처리한다. API 예외 처리는 대부분 이 기능으로 해결한다. 조금 뒤에 자세히 설명한다.
*
*   ResponseStatusExceptionResolver
*   -> HTTP 상태 코드를 지정해준다.
*   -> 예외에 따라서 HTTP 상태 코드를 지정해주는 역할을 한다.
*   (예 : @ResponseStatus(value = HttpStatus.NOT_FOUND))
*
*     다음 두 가지 경우를 처리한다.
*       -> @ResponseStatus가 달려있는 예외
*       -> ResponseStatusException 예외
*
*   DefaultHandlerExceptionResolver
*   -> 스프링 내부 기본 예외를 처리한다.
*
*
* */
