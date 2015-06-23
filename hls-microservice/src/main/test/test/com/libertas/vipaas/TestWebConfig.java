package test.com.libertas.vipaas;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
@EnableWebMvc
@ComponentScan("test.com.libertas.vipaas")
public class TestWebConfig extends WebMvcConfigurerAdapter {

}