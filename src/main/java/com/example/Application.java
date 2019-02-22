package com.example;

import com.example.core.annotations.Controller;
import com.example.util.ClassUtils;

import java.util.List;

@Controller
public class Application {

    public static void main(String[] args) {
        List<Class<?>> classList = ClassUtils.getClassListByAnnotation("", Controller.class, o -> o instanceof Object);
        System.out.println(classList);
    }
}
