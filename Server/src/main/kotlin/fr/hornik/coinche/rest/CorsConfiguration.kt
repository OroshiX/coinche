package fr.hornik.coinche.rest

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class CorsConfiguration {
    @Bean
    fun corsConfigurer(): WebMvcConfigurer {
        return object : WebMvcConfigurer {
            override fun addCorsMappings(registry: CorsRegistry) {
                registry.addMapping("/**")
                        .allowedOrigins("http://localhost:8080",
                                        "http://localhost:4200",
                                        "http://www.hornik.fr:8081")
                        .allowCredentials(true)
                        .allowedMethods(HttpMethod.GET.name,
                                        HttpMethod.POST.name,
                                        HttpMethod.DELETE.name,
                                        HttpMethod.OPTIONS.name)
            }
        }
    }
}