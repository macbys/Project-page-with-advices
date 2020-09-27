package com.maxbys.page_with_tips_project.randomFacts;

import com.google.gson.JsonObject;
import com.google.gson.JsonStreamParser;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

@Service
public class RandomFactsService {

    public String getRandomFact() throws IOException {
        URL url = new URL("https://uselessfacts.jsph.pl/random.json?language=en");
        InputStreamReader inputStreamReader = new InputStreamReader(url.openStream());
        JsonObject asJsonObject = new JsonStreamParser(inputStreamReader).next().getAsJsonObject();
        return asJsonObject.get("text").getAsString();
    }
}
