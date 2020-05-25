package me.donlis.annotationcompiler;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedOptions;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import me.donlis.annotation.Router;
import me.donlis.annotation.RouterBean;

@AutoService(Processor.class)
@SupportedOptions("moduleName")
@SupportedSourceVersion(SourceVersion.RELEASE_7)
@SupportedAnnotationTypes("me.donlis.annotation.Router")
public class MRouterCompiler extends AbstractProcessor {

    private Map<String, RouterBean> groups = new HashMap<>();

    private Filer mFiler;

    private Elements elementUtils;

    private Types typeUtils;

    private String moduleName;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        Messager messager = processingEnvironment.getMessager();

        elementUtils = processingEnvironment.getElementUtils();
        typeUtils = processingEnvironment.getTypeUtils();
        mFiler = processingEnvironment.getFiler();

        Map<String, String> options = processingEnvironment.getOptions();
        if(options != null && options.size() > 0){
            moduleName = options.get("moduleName");
        }
        if(moduleName == null || moduleName.equals("")){
            throw new RuntimeException("moduleName is null");
        }
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        if(set == null || set.size() == 0){
            return false;
        }

        Set<? extends Element> elementsAnnotatedWith = roundEnvironment.getElementsAnnotatedWith(Router.class);
        process(elementsAnnotatedWith);

        return true;
    }

    private void process(Set<? extends Element> elements){
        TypeElement activity = elementUtils.getTypeElement("android.app.Activity");

        for (Element element : elements) {
            if(!typeUtils.isSubtype(element.asType(),activity.asType())){
                continue;
            }

            TypeElement typeElement = (TypeElement) element;
            Router annotation = typeElement.getAnnotation(Router.class);
            String path = annotation.path();

            RouterBean bean = new RouterBean(element, ClassName.get(typeElement).getClass(), path);
            groups.put(path,bean);
        }

        if(groups.size() > 0){
            generated();
        }
    }

    private void generated(){
        TypeElement irouter = elementUtils.getTypeElement("me.donlis.router.IRoute");

        ParameterizedTypeName parameterizedTypeName = ParameterizedTypeName.get(
                ClassName.get(Map.class),
                ClassName.get(String.class),
                ClassName.get(RouterBean.class)
        );

        ParameterSpec maps = ParameterSpec.builder(parameterizedTypeName, "maps").build();

        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("load")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .addParameter(maps);

        for (Map.Entry<String, RouterBean> entry : groups.entrySet()) {
            String path = entry.getKey();
            RouterBean routerBean = entry.getValue();
            methodBuilder.addStatement("maps.put($S,$T.newInstance($T.class, $S))",
                    path,
                    ClassName.get(RouterBean.class),
                    ClassName.get((TypeElement) routerBean.getElement()),
                    path);
        }

        TypeSpec typeSpec = TypeSpec.classBuilder("MRouter_" + moduleName)
                .addSuperinterface(ClassName.get(irouter))
                .addModifiers(Modifier.PUBLIC)
                .addMethod(methodBuilder.build())
                .build();

        JavaFile javaFile = JavaFile.builder("me.donlis.mrouter.router", typeSpec).build();

        try {
            javaFile.writeTo(mFiler);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

