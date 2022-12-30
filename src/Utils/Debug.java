package Utils;

public class Debug {
    public static void log(Object o) {
        try {
            String className = Class.forName(Thread.currentThread().getStackTrace()[2].getClassName()).toString();
            String print;
            if(o == null) print = "null";
            else print = o.toString();
            System.out.print("(" + className + ")");
            for (int i = 0; i < (40 - className.length()); i++) {
                System.out.print(" ");
            }
            System.out.println(print);
        }catch(ClassNotFoundException e){
            System.out.println(e);
        }
    }
}
