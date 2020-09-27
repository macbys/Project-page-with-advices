package com.maxbys.page_with_tips_project.randomFacts;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.io.IOException;

@RestController
public class RandomFactsRestController {

    private final RandomFactsService randomFactsService;

    public RandomFactsRestController(RandomFactsService randomFactsService) {
        this.randomFactsService = randomFactsService;
    }

    @GetMapping("/random-fact")
    public String getRandomFact() throws IOException {
        return randomFactsService.getRandomFact();
    }
}
