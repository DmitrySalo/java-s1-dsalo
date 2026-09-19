package ru.my.scents.infra.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.my.scents.adapter.controller.http.fragrance.FragranceController;
import ru.my.scents.boundary.usecase.FragranceUseCase;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FragranceController.class)
@Import(LocalSecurityConfiguration.class)
@ActiveProfiles("local")
class LocalSecurityConfigurationTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FragranceUseCase fragranceUseCase;

    @Test
    @DisplayName("Локальный профиль разрешает запрос к API без аутентификации")
    void shouldPermitApiRequestWithoutAuthenticationWhenLocalProfileIsActive() throws Exception {
        mockMvc.perform(get("/api/v1/fragrances"))
                .andExpect(status().isBadRequest());
    }
}
