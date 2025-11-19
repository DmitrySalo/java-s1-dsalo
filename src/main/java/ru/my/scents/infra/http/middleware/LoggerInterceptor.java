package ru.my.scents.infra.http.middleware;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import ru.my.scents.infra.logger.LogKeys;

@Slf4j
@Component
public class LoggerInterceptor implements HandlerInterceptor {

    private static final String REQUEST_START_TIME = "requestStartTime";
    private static final String REQUEST_ID = "requestId";
    private static final Set<String> IMPORTANT_HEADERS = Set.of("Content-Type", "Content-Length", "Authorization",
            "Accept", "Accept-Language", "X-Forwarded-For");

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request,
                             @NonNull HttpServletResponse response,
                             @NonNull Object handler) {

        String requestId = MDC.get(LogKeys.REQUEST_ID.getValue());
        request.setAttribute(REQUEST_ID, requestId);

        long startTime = System.currentTimeMillis();
        request.setAttribute(REQUEST_START_TIME, startTime);

        log.info("[{}] === Incoming Request ===", requestId);
        log.info("[{}] Method: {} | URL: {}", requestId, request.getMethod(), request.getRequestURL());
        log.info("[{}] Remote Address: {}", requestId, request.getRemoteAddr());
        log.info("[{}] User Agent: {}", requestId, request.getHeader("User-Agent"));

        if (!request.getParameterMap().isEmpty()) {
            log.info("[{}] Query Parameters:", requestId);
            request.getParameterMap().forEach((key, values) ->
                    log.info("[{}]   {}: {}", requestId, key, String.join(", ", values)));
        }

        this.logImportantHeaders(request, requestId);

        return true;
    }

    @Override
    public void postHandle(@NonNull HttpServletRequest request,
                           @NonNull HttpServletResponse response,
                           @NonNull Object handler,
                           @Nullable ModelAndView modelAndView) {

        String requestId = (String) request.getAttribute(REQUEST_ID);
        log.info("[{}] Request processed successfully", requestId);
    }

    @Override
    public void afterCompletion(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull Object handler,
            @Nullable Exception ex) {

        String requestId = (String) request.getAttribute(REQUEST_ID);
        Long startTime = (Long) request.getAttribute(REQUEST_START_TIME);

        long duration = System.currentTimeMillis() - startTime;

        if (ex != null) {
            log.error("[{}] === Request Completed with Exception ===", requestId);
            log.error("[{}] Exception: {} - {}", requestId, ex.getClass().getSimpleName(), ex.getMessage());
            log.error("[{}] Status: {} | Duration: {}ms", requestId, response.getStatus(), duration);
        } else {
            log.info("[{}] === Request Completed Successfully ===", requestId);
            log.info("[{}] Status: {} | Duration: {}ms", requestId, response.getStatus(), duration);
        }

        this.logResponseHeaders(response, requestId);

        log.info("[{}] ========================", requestId);
    }

    private void logImportantHeaders(HttpServletRequest request, String requestId) {
        for (String headerName : IMPORTANT_HEADERS) {
            String headerValue = request.getHeader(headerName);
            if (headerValue != null) {
                if ("Authorization".equalsIgnoreCase(headerName)) {
                    headerValue = this.maskSensitiveData(headerValue);
                }
                log.info("[{}] Header {}: {}", requestId, headerName, headerValue);
            }
        }
    }

    private void logResponseHeaders(HttpServletResponse response, String requestId) {
        String contentType = response.getContentType();
        if (contentType != null) {
            log.info("[{}] Response Content-Type: {}", requestId, contentType);
        }
    }

    private String maskSensitiveData(String value) {
        if (value == null || value.length() <= 10) {
            return "***";
        }
        return value.substring(0, 6) + "***" + value.substring(value.length() - 4);
    }
}
