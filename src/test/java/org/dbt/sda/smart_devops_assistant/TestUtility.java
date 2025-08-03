package org.dbt.sda.smart_devops_assistant;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

public class TestUtility {

    @Test
    public void test(){
        String url = "https://github.com/ankitagrahari/smart-devops-assistant/pull/3";
        System.out.println(Arrays.toString(url.split("/")));

        List<String> fileName = List.of("a/c/v/bc.java", "a/c/v/qa.java", "a/d/s.java");
        System.out.println(String.join("','", fileName));
    }
}
