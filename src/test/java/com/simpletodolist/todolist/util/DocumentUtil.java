package com.simpletodolist.todolist.util;

import org.springframework.restdocs.operation.preprocess.OperationRequestPreprocessor;
import org.springframework.restdocs.operation.preprocess.OperationResponsePreprocessor;

import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;

public class DocumentUtil {

    public static OperationRequestPreprocessor commonRequestPreprocessor = preprocessRequest(
            modifyUris()
                .scheme("https")
                .host("simple-todolist-springboot.herokuapp.com")
                .port(80),
            prettyPrint());

    public static OperationResponsePreprocessor commonResponsePreprocessor = preprocessResponse(
            prettyPrint());
}
