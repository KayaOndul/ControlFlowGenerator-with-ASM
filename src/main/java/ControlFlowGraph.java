import jdk.internal.org.objectweb.asm.ClassReader;
import jdk.internal.org.objectweb.asm.tree.*;

import java.io.*;
import java.util.*;

public class ControlFlowGraph {

    private static Map<Object, String> sIds = null;
    private static int sNextId = 1;
    private static final String WORKDIR = "/home/kaya/Downloads/proj/src/main/java/TestData.java";

    public static void main(String[] args) throws IOException {
        InputStream in = new FileInputStream("target/classes/TestData.class");


        File f = new File(WORKDIR);
        FileInputStream stream = new FileInputStream(f);
        InputStreamReader conexion = new InputStreamReader(stream);
        LineNumberReader reader = new LineNumberReader(conexion);
        HashMap<Integer, String> labels = new HashMap<>();
        String x;


        while ((x = reader.readLine()) != null) {
            if (x.length() < 6)
                continue;
            labels.put(reader.getLineNumber(), x);

        }


        ClassReader cr = new ClassReader(in);
        ClassNode classNode = new ClassNode();

        //ClassNode is a ClassVisitor
        cr.accept(classNode, ClassReader.EXPAND_FRAMES);
        List<MethodNode> methods = classNode.methods;


        File fout = new File("Output.txt");
        FileOutputStream fos = new FileOutputStream(fout);

        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));

        for (MethodNode methodNode : methods) {
            if (methodNode.name.equals("<init>"))
                continue;


            InsnList instructions = methodNode.instructions;


            AbstractInsnNode abstractInsnNode;
            Integer i = 0;

            List<String> printList = new ArrayList<>();
            printList.add("digraph G{");
            for (; i < instructions.size(); ++i) {
                abstractInsnNode = instructions.get(i);
                if (!(abstractInsnNode == null && abstractInsnNode.getNext() == null)) {
                    printList = jumpRelations(abstractInsnNode, printList);
                    printList = insnRelations(abstractInsnNode, printList);
                }
            }

            StringBuilder sb= new StringBuilder();
            printList.stream().distinct().forEach(e -> {
                try {
                    bw.write(e);
                    if (Character.isDigit(e.trim().substring(0, 1).toCharArray()[0])) {
                        String inter=e.trim().substring(0, 3).replace("-"," ").trim();
                        Integer lineNum=Integer.parseInt(inter);
                        String search = labels.get(lineNum).trim();
                        if (search != null)
                            sb.append(lineNum+" [label=" +'"'+" L# "+lineNum+" "+ search.trim().replace('"','`') +'"'+ "]"+"\n");
                    }

                    bw.newLine();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });
            bw.newLine();
            bw.write(sb.toString());
            bw.write("}");
            bw.newLine();
        }
        bw.close();

    }

    private static List jumpRelations(AbstractInsnNode abstractInsnNode, List print) {

        if (abstractInsnNode.getType() == AbstractInsnNode.JUMP_INSN) {
            AbstractInsnNode tmp = abstractInsnNode;
            AbstractInsnNode fwd = abstractInsnNode;
            while (tmp != null && tmp.getType() != AbstractInsnNode.LINE) {
                tmp = tmp.getPrevious();

            }
            while (fwd != null && fwd.getType() != AbstractInsnNode.LINE) {
                fwd = fwd.getNext();

            }

            if (tmp.getType() == AbstractInsnNode.LINE && (((JumpInsnNode) abstractInsnNode).label.getNext()).getType() != AbstractInsnNode.FRAME) {
                String s;
                s = (((LineNumberNode) tmp).line + " -> ");
                s += (((LineNumberNode) (((JumpInsnNode) abstractInsnNode).label.getNext())).line);
                print.add(s.trim());
            }


        }

        return print;
    }

    private static List insnRelations(AbstractInsnNode abstractInsnNode, List print) {

        if (abstractInsnNode.getType() == AbstractInsnNode.LABEL) {

            AbstractInsnNode backward = abstractInsnNode;
            AbstractInsnNode forward = abstractInsnNode;
            while (backward != null && backward.getType() != AbstractInsnNode.LINE) {
                backward = backward.getPrevious();

            }
            while (forward != null && forward.getType() != AbstractInsnNode.LINE) {
                forward = forward.getNext();

            }


            if ((backward != null && forward != null) && forward.getType() == AbstractInsnNode.LINE) {
                String s;
                s = (((LineNumberNode) backward).line + " -> ");
                s += (((LineNumberNode) forward).line);
                print.add(s.trim());

            }
        }

        return print;

    }


}

