package bbangduck.bd.bbangduck.domain.hello;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HelloController {

    @GetMapping("/hello")
    public String hello(Model model) {
        model.addAttribute("title", "project title.");
        model.addAttribute("content", "project content.");

        return "/hello";
    }

    @GetMapping("/api/hello")
    @ResponseBody
    public ResponseEntity<String> helloApi() {
        return ResponseEntity.ok("hello api project.");
    }
}
