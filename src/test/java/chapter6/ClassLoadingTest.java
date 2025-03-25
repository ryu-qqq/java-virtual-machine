package chapter6;

/**
 *
 * javac ClassLoadingTest.java
 * 명령어로 컴파일 한다.
 *
 * ClassLoadingTest.class 파일이 생성될것이다.
 *
 * javap -v ClassLoadingTest 를 해보면
 *
 * 아래와 같은 클래스파일 정보가 출력 된다.
 *
 */


public class ClassLoadingTest {

    static {
        System.out.println("ClassLoadingTest loaded!");
    }


    void shouldLoadClassAndPrintStaticBlock() {
        new ClassLoadingTest();
    }

}


/**
 *
 * ^[[HWarning: File ./ClassLoadingTest.class does not contain class ClassLoadingTest
 * Classfile /Users/sangwon-ryu/java-virtual-machine/src/test/java/chapter6/ClassLoadingTest.class
 *   Last modified 2025. 3. 25.; size 531 bytes
 *   SHA-256 checksum cb1c82820d87df36f6324b22006fa3c5f4c9068e0182ef18f850c9e177193a8c
 *   Compiled from "ClassLoadingTest.java"
 * public class chapter6.ClassLoadingTest
 *   minor version: 0
 *   major version: 65
 *   flags: (0x0021) ACC_PUBLIC, ACC_SUPER
 *   this_class: #7                          // chapter6/ClassLoadingTest
 *   super_class: #2                         // java/lang/Object
 *   interfaces: 0, fields: 0, methods: 3, attributes: 1
 * Constant pool:
 *    #1 = Methodref          #2.#3          // java/lang/Object."<init>":()V
 *    #2 = Class              #4             // java/lang/Object
 *    #3 = NameAndType        #5:#6          // "<init>":()V
 *    #4 = Utf8               java/lang/Object
 *    #5 = Utf8               <init>
 *    #6 = Utf8               ()V
 *    #7 = Class              #8             // chapter6/ClassLoadingTest
 *    #8 = Utf8               chapter6/ClassLoadingTest
 *    #9 = Methodref          #7.#3          // chapter6/ClassLoadingTest."<init>":()V
 *   #10 = Fieldref           #11.#12        // java/lang/System.out:Ljava/io/PrintStream;
 *   #11 = Class              #13            // java/lang/System
 *   #12 = NameAndType        #14:#15        // out:Ljava/io/PrintStream;
 *   #13 = Utf8               java/lang/System
 *   #14 = Utf8               out
 *   #15 = Utf8               Ljava/io/PrintStream;
 *   #16 = String             #17            // ClassLoadingTest loaded!
 *   #17 = Utf8               ClassLoadingTest loaded!
 *   #18 = Methodref          #19.#20        // java/io/PrintStream.println:(Ljava/lang/String;)V
 *   #19 = Class              #21            // java/io/PrintStream
 *   #20 = NameAndType        #22:#23        // println:(Ljava/lang/String;)V
 *   #21 = Utf8               java/io/PrintStream
 *   #22 = Utf8               println
 *   #23 = Utf8               (Ljava/lang/String;)V
 *   #24 = Utf8               Code
 *   #25 = Utf8               LineNumberTable
 *   #26 = Utf8               shouldLoadClassAndPrintStaticBlock
 *   #27 = Utf8               <clinit>
 *   #28 = Utf8               SourceFile
 *   #29 = Utf8               ClassLoadingTest.java
 * {
 *   public chapter6.ClassLoadingTest();
 *     descriptor: ()V
 *     flags: (0x0001) ACC_PUBLIC
 *     Code:
 *       stack=1, locals=1, args_size=1
 *          0: aload_0
 *          1: invokespecial #1                  // Method java/lang/Object."<init>":()V
 *          4: return
 *       LineNumberTable:
 *         line 3: 0
 *
 *   void shouldLoadClassAndPrintStaticBlock();
 *     descriptor: ()V
 *     flags: (0x0000)
 *     Code:
 *       stack=2, locals=1, args_size=1
 *          0: new           #7                  // class chapter6/ClassLoadingTest
 *          3: dup
 *          4: invokespecial #9                  // Method "<init>":()V
 *          7: pop
 *          8: return
 *       LineNumberTable:
 *         line 11: 0
 *         line 12: 8
 *
 *   static {};
 *     descriptor: ()V
 *     flags: (0x0008) ACC_STATIC
 *     Code:
 *       stack=2, locals=0, args_size=0
 *          0: getstatic     #10                 // Field java/lang/System.out:Ljava/io/PrintStream;
 *          3: ldc           #16                 // String ClassLoadingTest loaded!
 *          5: invokevirtual #18                 // Method java/io/PrintStream.println:(Ljava/lang/String;)V
 *          8: return
 *       LineNumberTable:
 *         line 6: 0
 *         line 7: 8
 * }
 * SourceFile: "ClassLoadingTest.java"
 */