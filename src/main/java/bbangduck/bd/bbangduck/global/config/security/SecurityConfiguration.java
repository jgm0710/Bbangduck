package bbangduck.bd.bbangduck.global.config.security;

import bbangduck.bd.bbangduck.domain.auth.service.AccountService;
import bbangduck.bd.bbangduck.domain.auth.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * 작성자 : 정구민 <br><br>
 * <p>
 * 인증에 대한 Configuration Class
 */
@EnableWebSecurity
@RequiredArgsConstructor
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
@Profile("!web-test")
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final JwtTokenProvider jwtTokenProvider;

    private final AuthenticationEntryPointImpl authenticationEntryPoint;

    private final AccessDeniedHandlerImpl accessDeniedHandler;

    /**
     * 정적 리소스에 대한 요청은 filter 적용을 무시하도록 설정
     */
    @Override
    public void configure(WebSecurity web) {
        web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .httpBasic().disable()  //token 기반이므로 httpBasic 사용 x
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) //token 기반이므로 session 사용 x -> UsernamePasswordAuthenticationFilter 사용하지 않게 됨
                .and()
                .authorizeRequests()
                .anyRequest().permitAll()   //모든 요청 허용
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(authenticationEntryPoint) //custom authenticationEntryPoint 등록
                .accessDeniedHandler(accessDeniedHandler)   //custom accessDeniedHandler 등록
                .and()
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class)
        ;

    }
}
