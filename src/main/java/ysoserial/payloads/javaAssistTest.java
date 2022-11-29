package ysoserial.payloads;

import javassist.*;
import ysoserial.payloads.util.Gadgets;

import java.io.FileOutputStream;
import java.io.IOException;

public class javaAssistTest {
    public static void main(String[] args) throws NotFoundException, CannotCompileException, IOException, InstantiationException, IllegalAccessException {
        ClassPool classPool = ClassPool.getDefault();
        //获取系统的类，已经加载完的
        CtClass ctClass = classPool.makeClass("test.test");
        //获取父类
        ctClass.setInterfaces(new CtClass[]{classPool.get("java.io.Serializable")});
        ctClass.setSuperclass(classPool.get("ysoserial.GeneratePayload"));
        CtField ctField = new CtField(classPool.get("java.lang.String"), "name", ctClass);
        ctField.setModifiers(Modifier.PUBLIC | Modifier.STATIC);
        ctClass.addField(ctField, "name=\"aaa\";");
        ctClass.getField("name");
        //javaassist构造方法
        CtConstructor ctConstructor = new CtConstructor(new CtClass[]{},ctClass);
        ctConstructor.setBody("{System.out.print(999);}");
        ctClass.addConstructor(ctConstructor);

        //todo 这里有bug 修改不成功，注意类对地址的引用


//        CtClass c2 = classPool.get("ysoserial.payloads.aaa");
//        CtConstructor ctConstructor2 = c2.getConstructors()[0];
//        c2.makeClassInitializer().setBody("{System.out.print(33333);}");
//        ctConstructor2.insertBefore("System.out.print(567);");
//        ctConstructor2.insertAfter("System.out.print(789);");
//        ctClass.debugWriteFile("D:\\");
//接着写入某个class里
//        new FileOutputStream("2.class").write(c2.toBytecode());
//        ctClass.toBytecode();
        //然后再一步步反射，defineclassloader去调用
//        ctClass.toClass().newInstance();
//        CtClass cc  =classPool.get("java.lang.String");
//        cc.debugWriteFile("D:\\");
        //找对应的javaclsspath
//        classPool.appendClassPath(new ClassClassPath(Gadgets.StubTransletPayload.class));
        //自己定义一个类
//        CtClass cc2 = classPool.makeClass("com.until.text");
        CtConstructor ct2 = new CtConstructor(new CtClass[]{classPool.get("java.lang.String"),classPool.get("java.lang.String")},ctClass);
        ct2.setBody("{$0.name=$1;}");
        ctClass.addConstructor(ct2);
        new FileOutputStream("2.class").write(ctClass.toBytecode());


    }
}
class aaa{
    public static String name = "aaa";
    public aaa(String b,int c) {
        this.name = b;
        System.out.print(222);
    }
}
