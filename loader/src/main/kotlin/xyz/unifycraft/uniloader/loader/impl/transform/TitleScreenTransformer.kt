package xyz.unifycraft.uniloader.loader.impl.transform

import org.apache.logging.log4j.LogManager
import org.objectweb.asm.Handle
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type
import org.objectweb.asm.tree.*
import xyz.unifycraft.uniloader.loader.api.UniLoader

object TitleScreenTransformer {

    private val logger = LogManager.getLogger()

    fun transform(node: ClassNode) {

        val syncOpenWebsiteMethod = MethodNode(Opcodes.ACC_PRIVATE + Opcodes.ACC_SYNTHETIC, "syncOpenWebsite","(Lnet/minecraft/client/gui/widget/ButtonWidget;)V",null,null)

        val syncInsc = syncOpenWebsiteMethod.instructions

        syncInsc.add(makeSyncFunction())

        node.methods.add(syncOpenWebsiteMethod)

        for (method in node.methods) {
            if (method.name != "init") continue
            val iterator = method.instructions.iterator()
            while (iterator.hasNext()) {
                val insn = iterator.next()
                if (insn.opcode != Opcodes.INVOKEVIRTUAL) continue
                if ((insn as MethodInsnNode).name != "setConnectedToRealms") continue

                val prev = insn.previous.previous.previous.previous

                method.instructions.insertBefore(prev, makeCustomBranding())
            }
        }
    }

    fun makeSyncFunction(): InsnList {
        val list = InsnList()

        list.add(MethodInsnNode(Opcodes.INVOKESTATIC, Hooks.internalName, "openWebsite", "()V", false))
        list.add(InsnNode(Opcodes.RETURN))

        return list
    }

    fun makeCustomBranding(): InsnList {
        val list = InsnList()

        list.add(VarInsnNode(Opcodes.ALOAD, 0))
        list.add(TypeInsnNode(Opcodes.NEW, "net/minecraft/client/gui/widget/PressableTextWidget"))
        list.add(InsnNode(Opcodes.DUP))
        list.add(VarInsnNode(Opcodes.BIPUSH, 0))
        list.add(VarInsnNode(Opcodes.ALOAD, 0))
        list.add(FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/gui/screen/TitleScreen", "height", "I"))
        list.add(VarInsnNode(Opcodes.BIPUSH, 20))
        list.add(InsnNode(Opcodes.ISUB))
        list.add(VarInsnNode(Opcodes.ILOAD, 1))
        list.add(VarInsnNode(Opcodes.BIPUSH, 10))
        list.add(LdcInsnNode("${UniLoader.NAME} ${UniLoader.VERSION} (${UniLoader.getInstance().getAllMods().size} Mods loaded)"))
        list.add(MethodInsnNode(Opcodes.INVOKESTATIC, "net/minecraft/text/Text", "literal", "(Ljava/lang/String;)Lnet/minecraft/text/MutableText;",true))
        list.add(VarInsnNode(Opcodes.ALOAD, 0))
        list.add(
            InvokeDynamicInsnNode(
                "onPress",
                "(Lnet/minecraft/client/gui/screen/TitleScreen;)Lnet/minecraft/client/gui/widget/ButtonWidget\$PressAction;",
                Handle(
                    Opcodes.H_INVOKESTATIC,
                    "java/lang/invoke/LambdaMetafactory",
                    "metafactory",
                    "(Ljava/lang/invoke/MethodHandles\$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;",
                    false
                ),
                Type.getType("(Lnet/minecraft/client/gui/widget/ButtonWidget;)V"),
                Handle(
                    Opcodes.H_INVOKEVIRTUAL,
                    "net/minecraft/client/gui/screen/TitleScreen",
                    "syncOpenWebsite",
                    "(Lnet/minecraft/client/gui/widget/ButtonWidget;)V",
                    false
                ),
                Type.getType("(Lnet/minecraft/client/gui/widget/ButtonWidget;)V")
            )
        )
        list.add(VarInsnNode(Opcodes.ALOAD, 0))
        list.add(
            FieldInsnNode(
                Opcodes.GETFIELD,
                "net/minecraft/client/gui/screen/TitleScreen",
                "textRenderer",
                "Lnet/minecraft/client/font/TextRenderer;"
            )
        )
        list.add(
            MethodInsnNode(
                Opcodes.INVOKESPECIAL,
                "net/minecraft/client/gui/widget/PressableTextWidget",
                "<init>",
                "(IIIILnet/minecraft/text/Text;Lnet/minecraft/client/gui/widget/ButtonWidget\$PressAction;Lnet/minecraft/client/font/TextRenderer;)V"
            )
        )
        list.add(
            MethodInsnNode(
                Opcodes.INVOKEVIRTUAL,
                "net/minecraft/client/gui/screen/TitleScreen",
                "addDrawableChild",
                "(Lnet/minecraft/client/gui/Element;)Lnet/minecraft/client/gui/Element;"
            )
        )
        list.add(InsnNode(Opcodes.POP))

        return list
    }

}