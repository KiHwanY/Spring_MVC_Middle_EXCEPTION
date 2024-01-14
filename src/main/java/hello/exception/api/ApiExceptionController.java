package hello.exception.api;

import hello.exception.exception.BadRequestException;
import hello.exception.exception.UserException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@RestController
public class ApiExceptionController {

    @GetMapping("/api/members/{id}")
    public MemberDto getMember(@PathVariable("id") String id) {

        if (id.equals("ex")) {
            throw new RuntimeException("잘못된 사용자");
        }
        if (id.equals("bad")) {
            throw new IllegalArgumentException("잘못된 입력 값");
        }
        if (id.equals("user-ex")) {
            throw new UserException("사용자 오류");
        }

        return new MemberDto(id, "hello " + id);
    }
//      [실행해보면 상태 코드가 500인 것을 확인할 수 있다.]
//    {
//        "status": 500,
//            "error": "Internal Server Error",
//            "exception": "java.lang.IllegalArgumentException",
//            "path": "/api/members/bad"
//    }

    @GetMapping("/api/response-status-ex1")
    public String responseStatusEx1() {
        throw new BadRequestException();
    }

    @GetMapping("/api/response-status-ex2")
    public String responseStatusEx2() {
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "error.bad", new IllegalArgumentException());

        /*
        *  [ ResponseStatusException ]
        *
        *  @ResponseStatus 는 개발자가 직접 변경할 수 없는 예외에는 적용할 수 없다.
        *  (애노테이션을 직접 넣어야 하는데, 내가 코드를 수정할 수 없는 라이브러리의 예외 코드 같은 곳에는 적용할 수 없다.)
        *
        *  추가로 애노테이션을 사용하기 때문에 조건에 따라 동적으로 변경하는 것도 어렵다.
        *  이때는 ResponseStatusException 예외를 사용하면 된다.
        * */
    }

    @GetMapping("/api/default-handler-ex")
    public String defaultException(@RequestParam Integer data) {
        // Integer data에 문자를 입력하면 내부에서 TypeMismatchException이 발생한다.
        return "ok";

        /*
        *   [ExceptionResolver2]
        *
        *   DefaultHandlerExceptionResolver는 스프링 내부에서 발생하는 스프링 예외를 해결한다.
        *   대표적으로 파라미터 바인딩 시점에 타입이 맞지 않으면 내부에서 TypeMismatchException이 발생하는데,
        *   이 경우 예외가 발생했기 때문에 그냥 두면 서블릿 컨테이너까지 오류가 올라가고,
        *   결과적으로 500 오류가 발생한다. 그런데 파라미터 바인딩은 대부분 클라이언트가 HTTP 요청 정보를 잘못 호출해서 발생하는 문제이다.
        *   HTTP 에서는 이런 경우 HTTP 상태 코드 400을 사용하도록 되어 있다.
        *
        *   DefaultHandlerExceptionResolver는 이것을 500 오류가 아니라 HTTP 상태 코드 400 오류로 변경한다.
        *   스프링 내부 오류를 어떻게 처리할지 수 많은 내용이 정의되어 있다.
        *
        *   코드를 확인해보면
        *
        *   DefaultHandlerExceptionResolver.handleTypeMismatch를 보면 다음과 같은 코드를 확인할 수 있다.
        *   response.sendError(HttpServletResponse.SC_BAD_REQUEST)(400)
        *   결국 response.sendError()를 통해서 문제를 해결한다.
        *
        *   sendError(400)를 호출했기 때문에 WAS에서 다시 오류 페이지(/error)를 내부 요청한다.
        *
        * */

        /*
        *   [Spring이 제공하는 ExceptionResolver 2 정리]
        *
        *   지금까지 HTTP 상태 코드를 변경하고, 스프링 내부 예외의 상태코드를 변경하는 기능도 알아보았다.
        *   그런데 HandlerExceptionResolver를 직접 사용하기는 복잡하다. API 오류 응답의 경우 response에 직접 데이터를 넣어야 해서 매우 불편하고 번거롭다.
        *   ModelAndView를 반환해야 하는 것도 API에는 잘 맞지 않는다.
        *
        *   스프링은 이 문제를 해결하기 위해 @ExceptionHandler라는 매우 혁신적인 예외 처리 기능을 제공한다.
        *   그것이 아직 소개하지 않은 ExceptionHandlerExceptionResolver이다.
        * */

    }


    @Data
    @AllArgsConstructor
    static class MemberDto {
        private String memberId;
        private String name;
    }

}
