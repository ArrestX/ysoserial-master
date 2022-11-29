package ysoserial.payloads;

import javassist.*;
import ysoserial.payloads.util.Gadgets;

public class javaAssistTest {
    public static void main(String[] args) throws NotFoundException, CannotCompileException {
        ClassPool classPool = ClassPool.getDefault();
        //获取系统的类，已经加载完的
        CtClass ctClass = classPool.makeClass("test.test");
        CtField ctField = new CtField(classPool.get("java.lang.String"),"name",ctClass);
        ctField.setModifiers(Modifier.PUBLIC| Modifier.STATIC);
        ctClass.addField(ctField,"name=\"aaa\";");
        ctClass.debugWriteFile("D:\\");

//        CtClass cc  =classPool.get("java.lang.String");
//        cc.debugWriteFile("D:\\");
        //找对应的javaclsspath
//        classPool.appendClassPath(new ClassClassPath(Gadgets.StubTransletPayload.class));
        //自己定义一个类
//        CtClass cc2 = classPool.makeClass("com.until.text");

    }
}
