package com.example;

import com.example.core.annotations.Controller;
import com.example.util.ClassUtils;

import java.util.List;

@Controller
public class Application {

    public static void main(String[] args) {
        List<Class<?>> classList = ClassUtils.getClassListByCondition("", (Class o) -> o.isAnnotationPresent(Controller.class));
        System.out.println(classList);
    }
}
