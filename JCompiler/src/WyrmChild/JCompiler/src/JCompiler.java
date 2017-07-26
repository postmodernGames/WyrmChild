package WyrmChild.JCompiler.src;
import java.lang.reflect.*;
import java.net.*;
import java.util.Iterator;
import java.util.NoSuchElementException;
import javax.tools.*;
import javax.tools.SimpleJavaFileObject;


/**
 * A file object used to represent source coming from a string.
 */
public class JCompiler {

    public static void main(String[] args) {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) {
            System.err.println("compiler not available");
            return;
        }

        String program =
                "class Test " +
                        "{" +
                        " public static void main(String [] main args)" +
                        "{" +
                        "System.out..println(\"Hello, World\");" +
                        "Systen.out.println(args.length);" +
                        "}" +
                        "}";

        Iterable<? extends JavaFileObject> fileObjects;
        fileObjects = getJavaSourceFromString(program);
        compiler.getTask(null, null, null, null, null, fileObjects).call();


        try {
            Class<?> clazz = Class.forName("Test");
            Method m = clazz.getMethod("main", new Class[]{String[].class});
        } catch (Exception e) {
            System.err.println("unable to load and run Test");
        }
    }


    /**
     * Constructs a new JavaSourceFromString.
     *
     * @param name the name of the compilation unit represented by this file object
     * @param code the source code for the compilation unit represented by this file object
     */
    static Iterable<JavaSourceFromString> getJavaSourceFromString(String code) {
        final JavaSourceFromString jsfs;
        jsfs = new JavaSourceFromString("code", code);

        return new Iterable<JavaSourceFromString() {
            public Iterator<JavaSourceFromString> iterator() {
                return new Iterator<JavaSourceFromString>() {
                    boolean isNext = true;

                    public boolean hasNext() {
                        return isNext;
                    }

                    public JavaSourceFromString next() {
                        if (!isNext) throw new NoSuchElementException();
                        isNext = false;
                        return jsfs;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };

    }
}

    /**
     * A file object used to represent source coming from a string.
     */
     class JavaSourceFromString extends SimpleJavaFileObject {
        /**
         * The source code of this "file".
         */
        final String code;

        /**
         * Constructs a new JavaSourceFromString.
         * @param name the name of the compilation unit represented by this file object
         * @param code the source code for the compilation unit represented by this file object
         */
        JavaSourceFromString(String name, String code) {
            super(URI.create("string:///" + name.replace('.','/') + Kind.SOURCE.extension),
                    Kind.SOURCE);
            this.code = code;
        }


        public CharSequence getCharContent(boolean ignoreEncodingErrors) {
            return code;
        }
    }
