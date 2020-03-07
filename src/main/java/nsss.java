import jdk.internal.org.objectweb.asm.ClassReader;
import jdk.internal.org.objectweb.asm.tree.*;
import jdk.internal.org.objectweb.asm.tree.analysis.AnalyzerException;

import javax.sound.sampled.Line;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class nsss {

    private static Map<Object, String> sIds = null;
    private static int sNextId = 1;

    public static void main(String[] args) throws IOException {
        InputStream in = new FileInputStream("target/classes/TestData.class");


        ClassReader cr = new ClassReader(in);
        ClassNode classNode = new ClassNode();

        //ClassNode is a ClassVisitor
        cr.accept(classNode, ClassReader.EXPAND_FRAMES);
        List<MethodNode> methods = classNode.methods;
        List<String> print=

        for (MethodNode methodNode : methods) {
            if (methodNode.name.equals("<init>"))
                continue;
            InsnList instructions = methodNode.instructions;


            AbstractInsnNode abstractInsnNode;
            Integer i = 0;
            System.out.println("digraph G{");
            for (; i < instructions.size(); ++i) {
                abstractInsnNode = instructions.get(i);


                if (!(abstractInsnNode == null && abstractInsnNode.getNext() == null)) {

                    jumpRelations(abstractInsnNode);
                    insnRelations(abstractInsnNode);



                }
            }
            System.out.println("}");
        }
    }

    private static void jumpRelations(AbstractInsnNode abstractInsnNode) {
        if (abstractInsnNode.getType() == AbstractInsnNode.JUMP_INSN) {
            AbstractInsnNode tmp = abstractInsnNode;
            AbstractInsnNode fwd=abstractInsnNode;
            while (tmp!=null&& tmp.getType() != AbstractInsnNode.LINE) {
                tmp = tmp.getPrevious();

            }
            while (fwd != null&&fwd.getType() != AbstractInsnNode.LINE) {
                fwd = fwd.getNext();

            }

//            if (tmp.getType() == AbstractInsnNode.LINE) {
//                System.out.print(((LineNumberNode) tmp).line + " -> ");
//                System.out.println(((LineNumberNode)fwd).line);
//            }




            if (tmp.getType() == AbstractInsnNode.LINE&&(((JumpInsnNode)abstractInsnNode).label.getNext()).getType()!=AbstractInsnNode.FRAME) {
                System.out.print(((LineNumberNode) tmp).line + " -> ");
                System.out.print(((LineNumberNode)(((JumpInsnNode)abstractInsnNode).label.getNext())).line+"\n");
            }


        }
    }
    private static void insnRelations(AbstractInsnNode abstractInsnNode) {
        if (abstractInsnNode.getType() == AbstractInsnNode.LABEL) {

            AbstractInsnNode backward = abstractInsnNode;
            AbstractInsnNode forward = abstractInsnNode;
            while (backward != null&& backward.getType() != AbstractInsnNode.LINE) {
                backward = backward.getPrevious();

            }
            while (forward!=null&&forward.getType() != AbstractInsnNode.LINE) {
                forward = forward.getNext();

            }


            if ((backward!=null&&forward!=null)&&forward.getType() == AbstractInsnNode.LINE) {
                System.out.print(((LineNumberNode) backward).line + " -> ");
                System.out.print(((LineNumberNode) forward).line + "\n");

            }







        }
    }

    private static String getId(Object object) {
        if (sIds == null) {
            sIds = new HashMap();
        }
        String id = sIds.get(object);
        if (id == null) {
            id = Integer.toString(sNextId++);
            sIds.put(object, id);
        }
        return id;
    }
}

