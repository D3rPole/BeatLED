package Utils;

public class Debug {
    public static void log(Object o) {
        try {
            String className = Class.forName(Thread.currentThread().getStackTrace()[2].getClassName()).toString();
            String print;
            String prefix = "(" + className + ")";
            if(o == null) print = "null";
            else print = o.toString();
            for (int i = 0; i < (40 - className.length()); i++) {
                prefix += " ";
            }
            if(Utils.ui != null) Utils.ui.logs.log(prefix + print);
            System.out.println(prefix + print);
        }catch(ClassNotFoundException e){
            System.out.println(e);
        }
    }
}
