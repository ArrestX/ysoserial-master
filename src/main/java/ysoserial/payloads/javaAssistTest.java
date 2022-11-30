package ysoserial.payloads;

import javassist.*;
import javassist.expr.ExprEditor;
import javassist.expr.FieldAccess;
import javassist.expr.MethodCall;
import org.apache.commons.collections.functors.ConstantTransformer;
import org.apache.commons.collections.functors.InvokerTransformer;
import ysoserial.payloads.util.Gadgets;

import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class javaAssistTest {
    public static void main(String[] args) throws NotFoundException, CannotCompileException, IOException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
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

        // 创建一个名为printName方法，无参数，无返回值，输出name值
        CtMethod ctMethod = new CtMethod(classPool.get("java.lang.String"), "printName", new
            CtClass[]{}, ctClass);
        ctMethod.setModifiers(Modifier.PUBLIC);
        ctMethod.setBody("{System.out.println(name);return\"abcde\";}");
        ctClass.addMethod(ctMethod);

        CtClass c2 = classPool.get("ysoserial.payloads.aaa");
        CtMethod method1 = c2.getDeclaredMethod("print",new CtClass[]{classPool.get("java.lang.String")});
        method1.insertAfter("System.out.print($1);");

        //方法的替换查询或者修改
//        method1.instrument(
//            new ExprEditor() {
//                public void edit(MethodCall m)
//                    throws CannotCompileException
//                {
//                    System.out.println(m.getClassName()+"\t"+m.getMethodName());
//                }
//            });


        //这里一般放到最后，写道哪个类里边
//        new FileOutputStream("2.class").write(c2.toBytecode());

        //属性的调用
        c2.instrument(
            new ExprEditor() {
                public void edit(FieldAccess f)
                    throws CannotCompileException
                {
                    System.out.println(f.getFieldName()+"\t"+f.getLineNumber());
                    if (f.getLineNumber()==99){
                        f.replace("$_=\"123\";");
                    }
                }
            });
        Method m  = aaa.class.getMethod("print", String.class);
        m.setAccessible(true);
        m.invoke(new aaa(),"aa");
//        m.invoke(new aaa(),"aa");
//        m.invoke(new aaa(),"aa");
//        m.invoke(new aaa(),"aa");
//        m.invoke(new aaa(),"aa");
//        m.invoke(new aaa(),"aa");
//        m.invoke(new aaa(),"aa");
//        m.invoke(new aaa(),"aa");
//        m.invoke(new aaa(),"aa");
//        m.invoke(new aaa(),"aa");
//        m.invoke(new aaa(),"aa");
//        m.invoke(new aaa(),"aa");
//        m.invoke(new aaa(),"aa");
//        m.invoke(new aaa(),"aa");
//        m.invoke(new aaa(),"aa");
//        m.invoke(new aaa(),"aa");
        //这里一般放到最后，写道哪个类里边
        new FileOutputStream("2.class").write(c2.toBytecode());


    }
}
class aaa{
    public String name = "download";
    public void print(String x){
        ConstantTransformer c = new ConstantTransformer(Runtime.class);
        System.out.println(this.name);
//        InvokerTransformer invokerTransformer = new InvokerTransformer();
//        x.split(".");
//        System.out.println(x);
    }
}
