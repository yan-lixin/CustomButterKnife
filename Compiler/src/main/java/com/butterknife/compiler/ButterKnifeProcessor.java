package com.butterknife.compiler;

import com.butterknife.annotation.BindView;
import com.butterknife.annotation.OnClick;
import com.google.auto.service.AutoService;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.JavaFileObject;

/**
 * Copyright (c), 2018-2019
 *
 * @author: lixin
 * Date: 2019-04-23
 * Description:
 */
@AutoService(Processor.class) //触发注解处理器
//@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class ButterKnifeProcessor extends AbstractProcessor {

    /**
     * 用来报告错误，警告和其他提示信息
     */
    private Messager mMessager;

    /**
     * Elements包含用于操作Element的工具方法
     */
    private Elements mElementUtils;

    /**
     * Filter用来创建新的源文件，class文件以及辅助文件
     */
    private Filer mFiler;

    /**
     * Types中包含用于操作TypeMirror的工具方法
     */
    private Types mTypeUtils;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        mElementUtils = processingEnv.getElementUtils();
        mFiler = processingEnv.getFiler();
    }

    /**
     * 添加支持BindView和OnClick的注解类型
     * @return
     */
    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new LinkedHashSet<>();
        types.add(BindView.class.getCanonicalName());
        types.add(OnClick.class.getCanonicalName());
        return types;
    }

    /**
     * 返回此注释Processor支持的最新的版本，该方法可以通过注解@SupportedSourceVersion指定
     * @return
     */
    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latest();
    }

    /**
     * 注解处理器的核心方法，处理具体的注解，生成Java文件
     * @param annotations
     * @param roundEnv
     * @return
     */
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        //获取MainActivity中所有带BindView注解的属性
        Set<? extends Element> bindViewSet = roundEnv.getElementsAnnotatedWith(BindView.class);

        //key:   MainActivity的全类名
        //value: MainActivity中的所有带BindView注解的属性
        Map<String, List<VariableElement>> bindViewMap = new HashMap<>();
        // 遍历所有带BindView注解的属性
        for (Element element : bindViewSet) {
            // 转成原始属性元素（结构体元素）
            VariableElement variableElement = (VariableElement) element;
            // 通过属性元素获取它所属的MainActivity类名，如：com.butterknife.sample.MainActivity
            String activityName = getActivityName(variableElement);
            // 从缓存集合中获取MainActivity所有带BindView注解的属性集合
            List<VariableElement> list = bindViewMap.get(activityName);
            if (list == null) {
                list = new ArrayList<>();
                // 先加入map集合，引用变量list可以动态改变值
                bindViewMap.put(activityName, list);
            }
            // 将MainActivity所有带BindView注解的属性加入到list集合
            list.add(variableElement);
            // 测试打印：每个属性的名字
            System.out.println("variableElement >>> " + variableElement.getSimpleName().toString());
        }

        System.out.println("所有带OnClick注解的方法 ----------------------------------->");
        // 获取MainActivity中所有带OnClick注解的方法
        Set<? extends Element> onClickSet = roundEnv.getElementsAnnotatedWith(OnClick.class);
        Map<String, List<ExecutableElement>> onClickMap = new HashMap<>();
        // 遍历所有带OnClick注解的方法
        for (Element element : onClickSet) {
            // 转成原始属性元素（结构体元素）
            ExecutableElement executableElement = (ExecutableElement) element;
            // 通过属性元素获取它所属的MainActivity类名，如：com.butterknife.sample.MainActivity
            String activityName = getActivityName(executableElement);
            // 从缓存集合中获取MainActivity所有带OnClick注解的方法集合
            List<ExecutableElement> list = onClickMap.get(activityName);
            if (list == null) {
                list = new ArrayList<>();
                // 先加入map集合，引用变量list可以动态改变值
                onClickMap.put(activityName, list);
            }
            // 将MainActivity所有带OnClick注解的方法加入到list集合
            list.add(executableElement);
            // 测试打印：每个方法的名字
            System.out.println("executableElement >>> " + executableElement.getSimpleName().toString());
        }

        // 获取Activity完整的字符串类名（包名 + 类名）
        for (String activityName : bindViewMap.keySet()) {
            // 获取"com.butterknife.sample.MainActivity"中所有控件属性的集合
            List<VariableElement> cacheElements = bindViewMap.get(activityName);
            List<ExecutableElement> clickElements = onClickMap.get(activityName);

            try {
                // 创建一个新的源文件（Class），并返回一个对象以允许写入它
                JavaFileObject javaFileObject = mFiler.createSourceFile(activityName + "$ViewBinder");
                // 通过属性标签获取包名标签（任意一个属性标签的父节点都是同一个包名）
                String packageName = getPackageName(cacheElements.get(0));
                // 定义Writer对象
                Writer writer = javaFileObject.openWriter();

                // 通过属性元素获取它所属的MainActivity类名，再拼接后结果为：MainActivity$ViewBinder
                String activitySimpleName = cacheElements.get(0).getEnclosingElement()
                        .getSimpleName().toString() + "$ViewBinder";

                System.out.println("activityName >>> " + activityName + "\nactivitySimpleName >>> " + activitySimpleName);

                System.out.println("开始造币 ----------------------------------->");
                // 第一行生成包
                writer.write("package " + packageName + ";\n");
                // 第二行生成要导入的接口类（必须手动导入）
                writer.write("import com.butterknife.library.ViewBinder;\n");
                writer.write("import com.butterknife.library.DebouncingOnClickListener;\n");
                writer.write("import android.view.View;\n");

                // 第三行生成类
                writer.write("public class " + activitySimpleName +
                        " implements ViewBinder<" + activityName + "> {\n");
                // 第四行生成bind方法
                writer.write("public void bind(final " + activityName + " target) {\n");

                System.out.println("每个控件属性 ----------------------------------->");
                // 循环生成MainActivity每个控件属性
                for (VariableElement variableElement : cacheElements) {
                    // 控件属性名
                    String fieldName = variableElement.getSimpleName().toString();
                    // 获取控件的注解
                    BindView bindView = variableElement.getAnnotation(BindView.class);
                    // 获取控件注解的id值
                    int id = bindView.value();
                    // 生成：target.tv = target.findViewById(xxx);
                    writer.write("target." + fieldName + " = " + "target.findViewById(" + id + ");\n");
                }

                System.out.println("每个点击事件 ----------------------------------->");
                // 循环生成MainActivity每个点击事件
                for (ExecutableElement executableElement : clickElements) {
                    // 获取方法名
                    String methodName = executableElement.getSimpleName().toString();
                    // 获取方法的注解
                    OnClick onClick = executableElement.getAnnotation(OnClick.class);
                    // 获取方法注解的id值
                    int id = onClick.value();
                    // 获取方法参数
                    List<? extends VariableElement> parameters = executableElement.getParameters();

                    // 生成点击事件
                    writer.write("target.findViewById(" + id + ").setOnClickListener(new DebouncingOnClickListener() {\n");
                    writer.write("public void doClick(View view) {\n");
                    if (parameters.isEmpty()) {
                        writer.write("target." + methodName + "();\n}\n});\n");
                    } else {
                        writer.write("target." + methodName + "(view);\n}\n});\n");
                    }
                }

                // 最后结束标签，造币完成
                writer.write("\n}\n}");
                System.out.println("结束 ----------------------------------->");
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 通过属性标签获取类名标签，再通过类名标签获取包名标签
     *
     * @param variableElement 属性标签
     * @return com.butterknife.sample.MainActivity（包名 + 类名）
     */
    private String getActivityName(VariableElement variableElement) {
        // 通过属性标签获取类名标签，再通过类名标签获取包名标签
        String packageName = getPackageName(variableElement);
        // 通过属性标签获取类名标签
        TypeElement typeElement = (TypeElement) variableElement.getEnclosingElement();
        // 完整字符串拼接：com.butterknife.sample + "." + MainActivity
        return packageName + "." + typeElement.getSimpleName().toString();
    }

    /**
    * 通过属性标签获取类名标签，再通过类名标签获取包名标签（通过属性节点，找到父节点、再找到父节点的父节点）
    */
    private String getPackageName(VariableElement variableElement) {
        // 通过属性标签获取类名标签
        TypeElement typeElement = (TypeElement) variableElement.getEnclosingElement();
        // 通过类名标签获取包名标签
        String packageName = mElementUtils.getPackageOf(typeElement).getQualifiedName().toString();
        System.out.println("packageName >>>  " + packageName);
        return packageName;
    }

    private String getActivityName(ExecutableElement executableElement) {
        // 通过方法标签获取类名标签，再通过类名标签获取包名标签
        String packageName = getPackageName(executableElement);
        // 通过方法标签获取类名标签
        TypeElement typeElement = (TypeElement) executableElement.getEnclosingElement();
        // 完整字符串拼接：com.butterknife.sample + "." + MainActivity
        return packageName + "." + typeElement.getSimpleName().toString();
    }

    private String getPackageName(ExecutableElement executableElement) {
        // 通过方法标签获取类名标签
        TypeElement typeElement = (TypeElement) executableElement.getEnclosingElement();
        // 通过类名标签获取包名标签
        String packageName = mElementUtils.getPackageOf(typeElement).getQualifiedName().toString();
        System.out.println("packageName >>>  " + packageName);
        return packageName;
    }
}
