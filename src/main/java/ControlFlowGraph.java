import jdk.internal.org.objectweb.asm.ClassReader;
import jdk.internal.org.objectweb.asm.tree.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class ControlFlowGraph {

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


            AbstractInsnNode abstractInsnNode;
            Integer i = 0;
            System.out.println("digraph G{");
            List<String> printList=new ArrayList<>();
            for (; i < instructions.size(); ++i) {
                abstractInsnNode = instructions.get(i);


                if (!(abstractInsnNode == null && abstractInsnNode.getNext() == null)) {

                    printList=jumpRelations(abstractInsnNode,printList);
                    printList=insnRelations(abstractInsnNode,printList);



                }
            }
            printList.add("}");
            printList.stream().distinct().forEach(e-> System.out.println(e));
        }
    }

    private static List jumpRelations(AbstractInsnNode abstractInsnNode,List print) {

        if (abstractInsnNode.getType() == AbstractInsnNode.JUMP_INSN) {
            AbstractInsnNode tmp = abstractInsnNode;
            AbstractInsnNode fwd=abstractInsnNode;
            while (tmp!=null&& tmp.getType() != AbstractInsnNode.LINE) {
                tmp = tmp.getPrevious();

            }
            while (fwd != null&&fwd.getType() != AbstractInsnNode.LINE) {
                fwd = fwd.getNext();

            }

            if (tmp.getType() == AbstractInsnNode.LINE&&(((JumpInsnNode)abstractInsnNode).label.getNext()).getType()!=AbstractInsnNode.FRAME) {
                String s;
                s=(((LineNumberNode) tmp).line + " -> ");
                s+=(((LineNumberNode)(((JumpInsnNode)abstractInsnNode).label.getNext())).line);
                print.add(s.trim());
            }


        }

        return print;
    }
    private static List insnRelations(AbstractInsnNode abstractInsnNode,List print) {

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
                String s;
                s=(((LineNumberNode) backward).line + " -> ");
                s+=(((LineNumberNode) forward).line );
                print.add(s.trim());

            }
        }

        return print;

    }


}

