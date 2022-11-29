package ysoserial.payloads;

import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;
import ysoserial.payloads.util.Gadgets;

public class javaAssistTest {
    public static void main(String[] args) throws NotFoundException {
        ClassPool classPool = ClassPool.getDefault();
        //获取系统的类，已经加载完的
        CtClass cc  =classPool.get("java.lang.String");
        cc.debugWriteFile("D:\\");
        //找对应的javaclsspath
//        classPool.appendClassPath(new ClassClassPath(Gadgets.StubTransletPayload.class));
        //自己定义一个类
//        CtClass cc2 = classPool.makeClass("com.until.text");

    }
}
