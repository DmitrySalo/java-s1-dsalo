package ru.my.scents.infra.http.middleware;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.UUID;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import ru.my.scents.infra.http.HttpHeaders;
import ru.my.scents.infra.logger.LogKeys;

@Order(1)
@Component
public class ServletFilter  implements Filter {

    @Value("${spring.application.name}")
    private String applicationName;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;

        String requestId = httpRequest.getHeader(HttpHeaders.REQUEST_ID_HEADER.getValue());

        if (!StringUtils.hasLength(requestId)) {
            requestId = UUID.randomUUID().toString().substring(0, 8);
        }

        MDC.put(LogKeys.REQUEST_ID.getValue(), requestId);
        MDC.put(LogKeys.APP_NAME.getValue(), applicationName);

        try {
            chain.doFilter(request, response);
        } finally {
            MDC.clear();
        }
    }
}
