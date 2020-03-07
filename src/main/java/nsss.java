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


        for (MethodNode methodNode : methods) {
            if (methodNode.name.equals("<init>"))
                continue;
            InsnList instructions = methodNode.instructions;
            List<Integer> blocks = new ArrayList<>();
            for (int i = 0; i < instructions.size(); ++i) {
                if (instructions.get(i).getType() == AbstractInsnNode.LINE) {
                    blocks.add(((LineNumberNode) instructions.get(i)).line);
                }
            }


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
            while (tmp.getType() != AbstractInsnNode.LINE) {
                if (tmp == null || tmp.getPrevious() == null)
                    break;
                tmp = tmp.getPrevious();

            }
            if (tmp.getType() == AbstractInsnNode.LINE) {
                System.out.print(((LineNumberNode) tmp).line + " -> ");
                System.out.println(((LineNumberNode) (((JumpInsnNode) abstractInsnNode).label.getNext())).line);
            }





            if (tmp.getType() == AbstractInsnNode.LINE) {
                System.out.print(((LineNumberNode) tmp).line + " -> ");
                System.out.print(((LineNumberNode) abstractInsnNode.getNext().getNext()).line+"\n");
            }


        }
    }
    private static void insnRelations(AbstractInsnNode abstractInsnNode) {
        if (abstractInsnNode.getType() == AbstractInsnNode.INSN) {

            AbstractInsnNode backward = abstractInsnNode;
            AbstractInsnNode forward = abstractInsnNode;
            while (backward.getType() != AbstractInsnNode.LINE) {
                if (backward == null || backward.getPrevious() == null)
                    break;
                backward = backward.getPrevious();

            }
            while (forward.getType() != AbstractInsnNode.LINE) {
                if (forward == null || forward.getNext() == null)
                    break;
                forward = forward.getNext();

            }


            if (forward.getType() == AbstractInsnNode.LINE) {
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

