package bbangduck.bd.bbangduck.security;

import bbangduck.bd.bbangduck.security.jwt.JwtAuthenticationFilter;
import bbangduck.bd.bbangduck.security.jwt.JwtTokenProvider;
import bbangduck.bd.bbangduck.security.login.LoginService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final PasswordEncoder passwordEncoder;

    private final LoginService loginService;

    private final JwtTokenProvider jwtTokenProvider;

    private final ObjectMapper objectMapper;

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    /**
     * 정적 리소스에 대한 요청은 filter 적용을 무시하도록 설정
     */
    @Override
    public void configure(WebSecurity web) {
//        super.configure(web);
        web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
//        super.configure(http);
        http
                .httpBasic().disable()  //token 기반이므로 httpBasic 사용 x
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) //token 기반이므로 session 사용 x -> UsernamePasswordAuthenticationFilter 사용하지 않게 됨
                .and()
                .authorizeRequests()
                .anyRequest().permitAll()   //모든 요청 허용
                .and()
                .addFilterBefore(new JwtAuthenticationFilter(authenticationManagerBean(), jwtTokenProvider, objectMapper), UsernamePasswordAuthenticationFilter.class)
        ;

    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        super.configure(auth);
        auth.userDetailsService(loginService).passwordEncoder(passwordEncoder);
    }
}
