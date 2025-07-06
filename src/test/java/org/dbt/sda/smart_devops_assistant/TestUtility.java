package org.dbt.sda.smart_devops_assistant;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

public class TestUtility {

    @Test
    public void test(){
        String url = "https://github.com/ankitagrahari/smart-devops-assistant/pull/3";
        System.out.println(Arrays.toString(url.split("/")));
    }
}
