package it.uniroma3.siw.authentication;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

import static it.uniroma3.siw.model.Credentials.ADMIN_ROLE;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    private final DataSource dataSource;

    public SecurityConfiguration(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Bean
    public UserDetailsService userDetailsService() {
        JdbcUserDetailsManager manager = new JdbcUserDetailsManager(dataSource);
        manager.setUsersByUsernameQuery(
                "SELECT username, password, 1 as enabled FROM credentials WHERE username=?");
        manager.setAuthoritiesByUsernameQuery(
                "SELECT username, role FROM credentials WHERE username=?");
        return manager;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Vite dev server. In produzione il build di React viene copiato
        // in resources/static e servito dalla stessa origin: questa lista
        // serve solo per lo sviluppo locale.
        configuration.setAllowedOrigins(List.of("http://localhost:5173"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        // Necessario per inviare il cookie di sessione (JSESSIONID) cross-origin
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration);
        return source;
    }

    @Bean
    protected SecurityFilterChain configure(final HttpSecurity httpSecurity) throws Exception {

        // DA RIFARE, MI SONO DIMENTICATO DI CAMBIARE DA TORNEI
        //authorize.requestMatchers("/bacheca/seguiti", "/utenti/*/segui", "/utenti/*/smetti-di-seguire").authenticated();

        httpSecurity.authorizeHttpRequests(authorize -> {
            // 1. Pagine PUBBLICHE (chiunque può vedere la lista delle squadre o i dettagli)
            authorize.requestMatchers(HttpMethod.GET, "/", "/index", "/css/**", "/images/**").permitAll();
            authorize.requestMatchers(HttpMethod.GET, "/squadre", "/squadre/**", "/utenti","/bacheca/index", "/bacheca/generale").permitAll();
            authorize.requestMatchers("/login", "/register").permitAll();

            // 2. AZIONI CHE RICHIEDONO LOGIN (Utenti Registrati)
            // L'asterisco (*) indica "qualsiasi ID"
            authorize.requestMatchers(HttpMethod.POST, "/squadre/*/iscriviti").authenticated();
            authorize.requestMatchers(HttpMethod.POST, "/squadre/abbandona").authenticated();

            authorize.requestMatchers("/allenamenti/nuovo", "/scarpe/nuova").authenticated();

            // 2b. API REST per il frontend React — tutte richiedono sessione autenticata
            authorize.requestMatchers("/api/**").authenticated();

            // 3. ADMIN
            authorize.requestMatchers("/admin/**").hasAnyAuthority("ADMIN");

            // 4. Tutto il resto richiede il login di default
            authorize.anyRequest().authenticated();
        });

        httpSecurity.cors(cors -> cors.configurationSource(corsConfigurationSource()));

        httpSecurity.csrf(csrf -> csrf.ignoringRequestMatchers("/api/**"));

        httpSecurity.formLogin(form -> {
            form.loginPage("/login").permitAll();
            form.defaultSuccessUrl("/", true);
            form.failureUrl("/login?error=true");
        });

        httpSecurity.logout(logout -> {
            logout.logoutUrl("/logout");
            logout.logoutSuccessUrl("/");
            logout.invalidateHttpSession(true);
            logout.deleteCookies("JSESSIONID");
            logout.clearAuthentication(true);
            logout.permitAll();
        });

        return httpSecurity.build();
    }
}