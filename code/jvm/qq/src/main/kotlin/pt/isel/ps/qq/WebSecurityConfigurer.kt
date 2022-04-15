package pt.isel.ps.qq

import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter


@EnableWebSecurity
class WebSecurityConfigurer: WebSecurityConfigurerAdapter() {

    override fun configure(http: HttpSecurity) {
        http.authorizeRequests().anyRequest().authenticated().and().formLogin().loginPage("/api/web/v1.0/guest/login").permitAll()
    }

}