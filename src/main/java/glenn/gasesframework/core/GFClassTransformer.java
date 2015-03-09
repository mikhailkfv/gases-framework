package glenn.gasesframework.core;

import static org.objectweb.asm.Opcodes.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraft.launchwrapper.LaunchClassLoader;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.tree.*;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;

public class GFClassTransformer implements IClassTransformer
{
	private Map<String, String> c = new HashMap<String, String>();
	
	{
		c.put("Block", "aji");
		c.put("Blocks", "ajn");
		c.put("BlockFire", "alb");
		c.put("ItemRenderer", "bly");
		c.put("Entity", "sa");
		c.put("EntityLivingBase", "sv");
		c.put("ItemGlassBottle", "abl");
		c.put("BlockLiquid", "alw");
		c.put("BlockDynamicLiquid", "akr");
		c.put("EntityRenderer", "blt");
		c.put("WorldProvider", "aqo");
		c.put("World", "ahb");
		c.put("Material", "awt");
		c.put("MaterialLiquid", "aws");
		c.put("EntityPlayer", "yz");
		c.put("MovingObjectPosition", "azu");
		c.put("ItemStack", "add");
		c.put("Item", "adb");
		c.put("InventoryPlayer", "yx");
		c.put("ItemPotion", "adp");
		c.put("EntityItem", "xk");
		c.put("MathHelper", "qh");
		c.put("DamageSource", "ro");
		c.put("Minecraft", "bao");
		c.put("ResourceLocation", "bqx");
		c.put("EntityClientPlayerMP", "bjk");
		c.put("TextureManager", "bqf");
		c.put("Tessellator", "bmh");
		c.put("WorldClient", "bjf");
		c.put("GuiIngame", "bbv");
		c.put("IBlockAccess", "ahl");
	}
	
	@Override
	public byte[] transform(String className, String arg1, byte[] data)
	{
		byte[] newData = data;

		if(className.equals("net.minecraftforge.client.GuiIngameForge"))
		{
			//newData = patchClassGuiIngame(className, data);
		}
		else if(className.equals(c.get("ItemRenderer")))
		{
			System.out.println("[GasesFrameworkCore]Patching obfuscated class: " + className + "(ItemRenderer)...");
			newData = patchClassItemRenderer(data, true);
		}
		else if(className.equals("net.minecraft.client.renderer.ItemRenderer"))
		{
			System.out.println("[GasesFrameworkCore]Patching class: " + className + "(ItemRenderer)...");
			newData = patchClassItemRenderer(data, false);
		}
		else if(className.equals(c.get("Entity")))
		{
			System.out.println("[GasesFrameworkCore]Patching obfuscated class: " + className + "(Entity)...");
			newData = patchClassEntity(data, true);
		}
		else if(className.equals("net.minecraft.entity.Entity"))
		{
			System.out.println("[GasesFrameworkCore]Patching class: " + className + "(Entity)...");
			newData = patchClassEntity(data, false);
		}
		else if(className.equals(c.get("EntityLivingBase")))
		{
			System.out.println("[GasesFrameworkCore]Patching obfuscated class: " + className + "(EntityLivingBase)...");
			newData = patchClassEntityLivingBase(data, true);
		}
		else if(className.equals("net.minecraft.entity.EntityLivingBase"))
		{
			System.out.println("[GasesFrameworkCore]Patching class: " + className + "(EntityLivingBase)...");
			newData = patchClassEntityLivingBase(data, false);
		}

		if(newData != data)
		{
			System.out.println("[GasesFrameworkCore]Patch OK!");
		}
		
		return newData;
	}
	
	public byte[] patchClassEntityLivingBase(byte[] data, boolean obfuscated)
	{
		String classEntityLivingBase = obfuscated ? c.get("EntityLivingBase") : "net/minecraft/entity/EntityLivingBase";
		String classMaterial = obfuscated ? c.get("Material") : "net/minecraft/block/material/Material";
		String classMathHelper = obfuscated ? c.get("MathHelper") : "net/minecraft/util/MathHelper";
		String classBlock = obfuscated ? c.get("Block") : "net/minecraft/block/Block";
		String classWorld = obfuscated ? c.get("World") : "net/minecraft/world/World";

		String methodOnEntityUpdate = obfuscated ? "C" : "onEntityUpdate";
		String methodIsInsideOfMaterial = obfuscated ? "a" : "isInsideOfMaterial";
		String methodGetEyeHeight = obfuscated ? "g" : "getEyeHeight";
		String methodFloor_double = obfuscated ? "c" : "floor_double";
		String methodFloor_float = obfuscated ? "d" : "floor_float";
		String methodGetBlock = obfuscated ? "a" : "getBlock";
		String methodUpdatePotionEffects = obfuscated ? "aO" : "updatePotionEffects";
		
		String fieldPosX = obfuscated ? "s" : "posX";
		String fieldPosY = obfuscated ? "t" : "posY";
		String fieldPosZ = obfuscated ? "u" : "posZ";
		String fieldMotionX = obfuscated ? "v" : "motionX";
		String fieldMotionZ = obfuscated ? "x" : "motionZ";
		String fieldWorldObj = obfuscated ? "o" : "worldObj";
		
		String descriptor = "(L" + classMaterial + ";)Z";

		ClassNode classNode = new ClassNode();
		ClassReader classReader = new ClassReader(data);
		classReader.accept(classNode, 0);
		
		for(int i = 0; i < classNode.methods.size(); i++)
		{
			MethodNode method = (MethodNode)classNode.methods.get(i);
			if(method.name.equals(methodOnEntityUpdate) & method.desc.equals("()V"))
			{
				InsnList newInstructions = new InsnList();
				for(int j = 0; j < method.instructions.size(); j++)
				{
					AbstractInsnNode instruction = method.instructions.get(j);
					newInstructions.add(instruction);
					
					if(instruction.getOpcode() == INVOKEVIRTUAL)
					{
						MethodInsnNode methodInstruction = (MethodInsnNode)instruction;
						if(methodInstruction.name.equals(methodUpdatePotionEffects) && methodInstruction.desc.equals("()V"))
						{
							newInstructions.add(new VarInsnNode(ALOAD, 0));
							newInstructions.add(new MethodInsnNode(INVOKEVIRTUAL, classEntityLivingBase, "handleGasEffects", "()V"));
						}
					}
				}
				method.instructions = newInstructions;
			}
		}
		
		{
			classNode.visitMethod(ACC_PROTECTED, "handleGasEffects", "()V", null, null);
			MethodNode method = classNode.methods.get(classNode.methods.size() - 1);
			
			LabelNode l0 = new LabelNode();
			method.instructions.add(l0);
			method.instructions.add(new LdcInsnNode(0.1D));
			method.instructions.add(new IntInsnNode(SIPUSH, 1000));
			method.instructions.add(new VarInsnNode(ALOAD, 0));
			method.instructions.add(new MethodInsnNode(INVOKESTATIC, "glenn/gasesframework/api/ExtendedGasEffectsBase", "get", "(L" + classEntityLivingBase + ";)Lglenn/gasesframework/api/ExtendedGasEffectsBase;"));
			method.instructions.add(new FieldInsnNode(GETSTATIC, "glenn/gasesframework/api/ExtendedGasEffectsBase", "SLOWNESS_WATCHER", "I"));
			method.instructions.add(new MethodInsnNode(INVOKEVIRTUAL, "glenn/gasesframework/api/ExtendedGasEffectsBase", "get", "(I)I"));
			method.instructions.add(new InsnNode(ISUB));
			method.instructions.add(new InsnNode(I2D));
			method.instructions.add(new LdcInsnNode(1000.0D));
			method.instructions.add(new InsnNode(DDIV));
			method.instructions.add(new LdcInsnNode(0.9D));
			method.instructions.add(new InsnNode(DMUL));
			method.instructions.add(new InsnNode(DADD));
			method.instructions.add(new VarInsnNode(DSTORE, 1));
			method.instructions.add(new VarInsnNode(ALOAD, 0));
			method.instructions.add(new InsnNode(DUP));
			method.instructions.add(new FieldInsnNode(GETFIELD, classEntityLivingBase, fieldMotionX, "D"));
			method.instructions.add(new VarInsnNode(DLOAD, 1));
			method.instructions.add(new InsnNode(DMUL));
			method.instructions.add(new FieldInsnNode(PUTFIELD, classEntityLivingBase, fieldMotionX, "D"));
			method.instructions.add(new VarInsnNode(ALOAD, 0));
			method.instructions.add(new InsnNode(DUP));
			method.instructions.add(new FieldInsnNode(GETFIELD, classEntityLivingBase, fieldMotionZ, "D"));
			method.instructions.add(new VarInsnNode(DLOAD, 1));
			method.instructions.add(new InsnNode(DMUL));
			method.instructions.add(new FieldInsnNode(PUTFIELD, classEntityLivingBase, fieldMotionZ, "D"));
			method.instructions.add(new InsnNode(RETURN));
			LabelNode l1 = new LabelNode();
			method.instructions.add(l1);
			
			method.localVariables.add(new LocalVariableNode("this", "L" + classEntityLivingBase + ";", null, l0, l1, 0));
			method.localVariables.add(new LocalVariableNode("slowness", "D", null, l0, l1, 1));
			
			method.maxStack = 5;
			method.maxLocals = 2;
		}
		
		ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		classNode.accept(writer);
		return writer.toByteArray();
	}

	public byte[] patchClassEntity(byte[] data, boolean obfuscated)
	{
		String classMinecraft = obfuscated ? c.get("Minecraft") : "net/minecraft/client/Minecraft";
		String classBlock = obfuscated ? c.get("Block") : "net/minecraft/block/Block";
		String classEntity = obfuscated ? c.get("Entity") : "net/minecraft/entity/Entity";
		String classMaterial = obfuscated ? c.get("Material") : "net/minecraft/block/material/Material";
		String classWorld = obfuscated ? c.get("World") : "net/minecraft/world/World";
		
		String interfaceBlockAccess = obfuscated ? c.get("IBlockAccess") : "net/minecraft/world/IBlockAccess";

		String methodIsInsideOfMaterial = obfuscated ? "a" : "isInsideOfMaterial";
		String methodGetBlockMetadata = obfuscated ? "e" : "getBlockMetadata";
		String methodGetMaterial = obfuscated ? "o" : "getMaterial";

		String fieldWorldObj = obfuscated ? "o" : "worldObj";
		
		String descriptor = "(L" + classMaterial + ";)Z";

		ClassNode classNode = new ClassNode();
		ClassReader classReader = new ClassReader(data);
		classReader.accept(classNode, 0);

		for(int i = 0; i < classNode.methods.size(); i++)
		{
			MethodNode method = (MethodNode)classNode.methods.get(i);
			if(method.name.equals(methodIsInsideOfMaterial) && method.desc.equals(descriptor))
			{
				InsnList newInstructions = new InsnList();
				for(int j = 0; j < method.instructions.size(); j++)
				{
					AbstractInsnNode instruction = (AbstractInsnNode)method.instructions.get(j);
					newInstructions.add(instruction);

					if(instruction.getOpcode() == ASTORE && ((VarInsnNode)instruction).var == 7)
					{
						LabelNode l6 = (LabelNode)method.instructions.get(j + 1);
						LabelNode l8 = new LabelNode();
						
						newInstructions.add(new VarInsnNode(ALOAD, 7));
						newInstructions.add(new MethodInsnNode(INVOKEVIRTUAL, classBlock, methodGetMaterial, "()L" + classMaterial + ";"));
						newInstructions.add(new FieldInsnNode(GETSTATIC, "glenn/gasesframework/api/block/MaterialGas", "INSTANCE", "L" + classMaterial + ";"));
						newInstructions.add(new JumpInsnNode(IF_ACMPNE, l6));
						newInstructions.add(new VarInsnNode(ALOAD, 7));
						newInstructions.add(new MethodInsnNode(INVOKEVIRTUAL, classBlock, methodGetMaterial, "()L" + classMaterial + ";"));
						newInstructions.add(new VarInsnNode(ALOAD, 1));
						newInstructions.add(new JumpInsnNode(IF_ACMPNE, l6));
						newInstructions.add(new VarInsnNode(DLOAD, 2));
						newInstructions.add(new VarInsnNode(ILOAD, 5));
						newInstructions.add(new InsnNode(I2D));
						newInstructions.add(new InsnNode(DSUB));
						newInstructions.add(new VarInsnNode(ALOAD, 7));
						newInstructions.add(new TypeInsnNode(CHECKCAST, "glenn/gasesframework/block/BlockGas"));
						newInstructions.add(new FieldInsnNode(GETFIELD, "glenn/gasesframework/block/BlockGas", "type", "Lglenn/gasesframework/api/type/GasType;"));
						newInstructions.add(new VarInsnNode(ALOAD, 0));
						newInstructions.add(new FieldInsnNode(GETFIELD, classEntity, fieldWorldObj, "L" + classWorld + ";"));
						newInstructions.add(new TypeInsnNode(CHECKCAST, interfaceBlockAccess));
						newInstructions.add(new VarInsnNode(ILOAD, 4));
						newInstructions.add(new VarInsnNode(ILOAD, 5));
						newInstructions.add(new VarInsnNode(ILOAD, 6));
						newInstructions.add(new VarInsnNode(ALOAD, 0));
						newInstructions.add(new FieldInsnNode(GETFIELD, classEntity, fieldWorldObj, "L" + classWorld + ";"));
						newInstructions.add(new VarInsnNode(ILOAD, 4));
						newInstructions.add(new VarInsnNode(ILOAD, 5));
						newInstructions.add(new VarInsnNode(ILOAD, 6));
						newInstructions.add(new MethodInsnNode(INVOKEVIRTUAL, classWorld, methodGetBlockMetadata, "(III)I"));
						newInstructions.add(new MethodInsnNode(INVOKEVIRTUAL, "glenn/gasesframework/api/type/GasType", "getMinY", "(L" + interfaceBlockAccess + ";IIII)D"));
						newInstructions.add(new InsnNode(DCMPL));
						newInstructions.add(new JumpInsnNode(IFLE, l8));
						newInstructions.add(new VarInsnNode(DLOAD, 2));
						newInstructions.add(new VarInsnNode(ILOAD, 5));
						newInstructions.add(new InsnNode(I2D));
						newInstructions.add(new InsnNode(DSUB));
						newInstructions.add(new VarInsnNode(ALOAD, 7));
						newInstructions.add(new TypeInsnNode(CHECKCAST, "glenn/gasesframework/block/BlockGas"));
						newInstructions.add(new FieldInsnNode(GETFIELD, "glenn/gasesframework/block/BlockGas", "type", "Lglenn/gasesframework/api/type/GasType;"));
						newInstructions.add(new VarInsnNode(ALOAD, 0));
						newInstructions.add(new FieldInsnNode(GETFIELD, classEntity, fieldWorldObj, "L" + classWorld + ";"));
						newInstructions.add(new TypeInsnNode(CHECKCAST, interfaceBlockAccess));
						newInstructions.add(new VarInsnNode(ILOAD, 4));
						newInstructions.add(new VarInsnNode(ILOAD, 5));
						newInstructions.add(new VarInsnNode(ILOAD, 6));
						newInstructions.add(new VarInsnNode(ALOAD, 0));
						newInstructions.add(new FieldInsnNode(GETFIELD, classEntity, fieldWorldObj, "L" + classWorld + ";"));
						newInstructions.add(new VarInsnNode(ILOAD, 4));
						newInstructions.add(new VarInsnNode(ILOAD, 5));
						newInstructions.add(new VarInsnNode(ILOAD, 6));
						newInstructions.add(new MethodInsnNode(INVOKEVIRTUAL, classWorld, methodGetBlockMetadata, "(III)I"));
						newInstructions.add(new MethodInsnNode(INVOKEVIRTUAL, "glenn/gasesframework/api/type/GasType", "getMaxY", "(L" + interfaceBlockAccess + ";IIII)D"));
						newInstructions.add(new InsnNode(DCMPG));
						newInstructions.add(new JumpInsnNode(IFGE, l8));
						newInstructions.add(new InsnNode(ICONST_1));
						newInstructions.add(new InsnNode(IRETURN));
						newInstructions.add(l8);
						newInstructions.add(new InsnNode(ICONST_0));
						newInstructions.add(new InsnNode(IRETURN));
					}
				}
				method.instructions = newInstructions;
			}
		}

		ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		classNode.accept(writer);
		return writer.toByteArray();
	}

	public byte[] patchClassItemRenderer(byte[] data, boolean obfuscated)
	{
		String classResourceLocation = obfuscated ? c.get("ResourceLocation") : "net/minecraft/util/ResourceLocation";
		String classItemRenderer = obfuscated ? c.get("ItemRenderer") : "net/minecraft/client/renderer/ItemRenderer";
		String classMinecraft = obfuscated ? c.get("Minecraft") : "net/minecraft/client/Minecraft";
		String classEntityClientPlayerMP = obfuscated ? c.get("EntityClientPlayerMP") : "net/minecraft/client/entity/EntityClientPlayerMP";
		String classMaterial = obfuscated ? c.get("Material") : "net/minecraft/block/material/Material";
		String classTextureManager = obfuscated ? c.get("TextureManager") : "net/minecraft/client/renderer/texture/TextureManager";
		String classTessellator = obfuscated ? c.get("Tessellator") : "net/minecraft/client/renderer/Tessellator";
		String classMathHelper = obfuscated ? c.get("MathHelper") : "net/minecraft/util/MathHelper";
		String classWorldClient = obfuscated ? c.get("WorldClient") : "net/minecraft/client/multiplayer/WorldClient";
		String classBlock = obfuscated ? c.get("Block") : "net/minecraft/block/Block";

		String methodRenderOverlays = obfuscated ? "b" : "renderOverlays";
		String methodRenderWarpedTextureOverlay = obfuscated ? "c" : "renderWarpedTextureOverlay";
		String methodIsInsideOfMaterial = obfuscated ? "a" : "isInsideOfMaterial";
		String methodFloor_double = obfuscated ? "c" : "floor_double";
		String methodGetBrightness = obfuscated ? "d" : "getBrightness";
		String methodGetBlock = obfuscated ? "a" : "getBlock";
		String methodGetEyeHeight = obfuscated ? "g" : "getEyeHeight";
		String methodStartDrawingQuads = obfuscated ? "b" : "startDrawingQuads";
		String methodAddVertexWithUV = obfuscated ? "a" : "addVertexWithUV";
		String methodDraw = obfuscated ? "a" : "draw";
		String methodGetTextureManager = obfuscated ? "P" : "getTextureManager";
		String methodBindTexture = obfuscated ? "a" : "bindTexture";

		String fieldMc = obfuscated ? "d" : "mc";
		String fieldThePlayer = obfuscated ? "h" : "thePlayer";
		String fieldInstance = obfuscated ? "a" : "instance";
		String fieldPosX = obfuscated ? "s" : "posX";
		String fieldPosY = obfuscated ? "t" : "posY";
		String fieldPosZ = obfuscated ? "u" : "posZ";
		String fieldTheWorld = obfuscated ? "f" : "theWorld";
		String fieldRotationYaw = obfuscated ? "y" : "rotationYaw";
		String fieldRotationPitch = obfuscated ? "z" : "rotationPitch";
		
		String descriptor = "(L" + classMaterial + ";)Z";

		ClassNode classNode = new ClassNode();
		ClassReader classReader = new ClassReader(data);
		classReader.accept(classNode, 0);

		for(int i = 0; i < classNode.methods.size(); i++)
		{
			MethodNode method = (MethodNode)classNode.methods.get(i);
			if(method.name.equals(methodRenderOverlays) & method.desc.equals("(F)V"))
			{
				InsnList newInstructions = new InsnList();
				LabelNode theLabelNode = null;

				Label label1 = new Label();
				LabelNode labelNode1 = new LabelNode(label1);

				for(int j = 0; j < method.instructions.size(); j++)
				{
					AbstractInsnNode instruction = (AbstractInsnNode)method.instructions.get(j);
					newInstructions.add(instruction);
					if(instruction.getOpcode() == INVOKEVIRTUAL)
					{
						MethodInsnNode invokeInstruction = (MethodInsnNode)instruction;
						
						if(invokeInstruction.name.equals(methodIsInsideOfMaterial) & invokeInstruction.desc.equals(descriptor))
						{
							if(instruction.getNext().getOpcode() == IFEQ)
							{
								theLabelNode = ((JumpInsnNode)instruction.getNext()).label;
							}
						}
						else if(invokeInstruction.name.equals(methodRenderWarpedTextureOverlay) & invokeInstruction.desc.equals("(F)V"))
						{
							newInstructions.add(new JumpInsnNode(GOTO, labelNode1));
						}
					}
					else if(instruction instanceof LabelNode)
					{
						if(theLabelNode != null && theLabelNode == (LabelNode)instruction)
						{
							newInstructions.add(new VarInsnNode(ALOAD, 0));
							newInstructions.add(new FieldInsnNode(GETFIELD, classItemRenderer, fieldMc, "L" + classMinecraft + ";"));
							newInstructions.add(new FieldInsnNode(GETFIELD, classMinecraft, fieldThePlayer, "L" + classEntityClientPlayerMP + ";"));
							newInstructions.add(new FieldInsnNode(GETSTATIC, "glenn/gasesframework/api/block/MaterialGas", "INSTANCE", "L" + classMaterial + ";"));
							newInstructions.add(new MethodInsnNode(INVOKEVIRTUAL, classEntityClientPlayerMP, methodIsInsideOfMaterial, "(L" + classMaterial + ";)Z"));
							newInstructions.add(new JumpInsnNode(IFEQ, labelNode1));
							newInstructions.add(new VarInsnNode(ALOAD, 0));
							newInstructions.add(new VarInsnNode(FLOAD, 1));
							newInstructions.add(new MethodInsnNode(INVOKESPECIAL, classItemRenderer, "renderGasOverlay", "(F)V"));
							newInstructions.add(labelNode1);

							theLabelNode = null;
						}
					}
				}
				method.instructions = newInstructions;
			}
		}

		MethodNode renderGasOverlay = new MethodNode(2, "renderGasOverlay", "(F)V", null, new String[0]);
		{
			renderGasOverlay.visitCode();
			Label l0 = new Label();
			renderGasOverlay.visitLabel(l0);
			//renderGasOverlay.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
			//renderGasOverlay.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "()V");
			/*renderGasOverlay.visitVarInsn(ALOAD, 0);
			renderGasOverlay.visitFieldInsn(GETFIELD, classItemRenderer, fieldMc, "L" + classMinecraft + ";");
			renderGasOverlay.visitMethodInsn(INVOKEVIRTUAL, classMinecraft, methodGetTextureManager, "()L" + classTextureManager+ ";");
			renderGasOverlay.visitFieldInsn(GETSTATIC, classItemRenderer, "gasOverlay", "L" + classResourceLocation + ";");
			renderGasOverlay.visitMethodInsn(INVOKEVIRTUAL, classTextureManager, methodBindTexture, "(L" + classResourceLocation + ";)V");*/
			renderGasOverlay.visitFieldInsn(GETSTATIC, classTessellator, fieldInstance, "L" + classTessellator + ";");
			renderGasOverlay.visitVarInsn(ASTORE, 2);
			Label l2 = new Label();
			renderGasOverlay.visitLabel(l2);
			renderGasOverlay.visitVarInsn(ALOAD, 0);
			renderGasOverlay.visitFieldInsn(GETFIELD, classItemRenderer, fieldMc, "L" + classMinecraft + ";");
			renderGasOverlay.visitFieldInsn(GETFIELD, classMinecraft, fieldThePlayer, "L" + classEntityClientPlayerMP + ";");
			renderGasOverlay.visitVarInsn(FLOAD, 1);
			renderGasOverlay.visitMethodInsn(INVOKEVIRTUAL, classEntityClientPlayerMP, methodGetBrightness, "(F)F");
			renderGasOverlay.visitVarInsn(FSTORE, 3);
			Label l3 = new Label();
			renderGasOverlay.visitLabel(l3);
			renderGasOverlay.visitVarInsn(ALOAD, 0);
			renderGasOverlay.visitFieldInsn(GETFIELD, classItemRenderer, fieldMc, "L" + classMinecraft + ";");
			renderGasOverlay.visitFieldInsn(GETFIELD, classMinecraft, fieldThePlayer, "L" + classEntityClientPlayerMP + ";");
			renderGasOverlay.visitFieldInsn(GETFIELD, classEntityClientPlayerMP, fieldPosX, "D");
			renderGasOverlay.visitMethodInsn(INVOKESTATIC, classMathHelper, methodFloor_double, "(D)I");
			renderGasOverlay.visitVarInsn(ISTORE, 4);
			Label l4 = new Label();
			renderGasOverlay.visitLabel(l4);
			renderGasOverlay.visitVarInsn(ALOAD, 0);
			renderGasOverlay.visitFieldInsn(GETFIELD, classItemRenderer, fieldMc, "L" + classMinecraft + ";");
			renderGasOverlay.visitFieldInsn(GETFIELD, classMinecraft, fieldThePlayer, "L" + classEntityClientPlayerMP + ";");
			renderGasOverlay.visitFieldInsn(GETFIELD, classEntityClientPlayerMP, fieldPosY, "D");
			renderGasOverlay.visitVarInsn(ALOAD, 0);
			renderGasOverlay.visitFieldInsn(GETFIELD, classItemRenderer, fieldMc, "L" + classMinecraft + ";");
			renderGasOverlay.visitFieldInsn(GETFIELD, classMinecraft, fieldThePlayer, "L" + classEntityClientPlayerMP + ";");
			renderGasOverlay.visitMethodInsn(INVOKEVIRTUAL, classEntityClientPlayerMP, methodGetEyeHeight, "()F");
			renderGasOverlay.visitInsn(F2D);
			renderGasOverlay.visitInsn(DADD);
			renderGasOverlay.visitMethodInsn(INVOKESTATIC, classMathHelper, methodFloor_double, "(D)I");
			renderGasOverlay.visitVarInsn(ISTORE, 5);
			Label l5 = new Label();
			renderGasOverlay.visitLabel(l5);
			renderGasOverlay.visitVarInsn(ALOAD, 0);
			renderGasOverlay.visitFieldInsn(GETFIELD, classItemRenderer, fieldMc, "L" + classMinecraft + ";");
			renderGasOverlay.visitFieldInsn(GETFIELD, classMinecraft, fieldThePlayer, "L" + classEntityClientPlayerMP + ";");
			renderGasOverlay.visitFieldInsn(GETFIELD, classEntityClientPlayerMP, fieldPosZ, "D");
			renderGasOverlay.visitMethodInsn(INVOKESTATIC, classMathHelper, methodFloor_double, "(D)I");
			renderGasOverlay.visitVarInsn(ISTORE, 6);
			Label l6 = new Label();
			renderGasOverlay.visitLabel(l6);
			renderGasOverlay.visitVarInsn(ALOAD, 0);
			renderGasOverlay.visitFieldInsn(GETFIELD, classItemRenderer, fieldMc, "L" + classMinecraft + ";");
			renderGasOverlay.visitFieldInsn(GETFIELD, classMinecraft, fieldTheWorld, "L" + classWorldClient + ";");
			renderGasOverlay.visitVarInsn(ILOAD, 4);
			renderGasOverlay.visitVarInsn(ILOAD, 5);
			renderGasOverlay.visitVarInsn(ILOAD, 6);
			renderGasOverlay.visitMethodInsn(INVOKEVIRTUAL, classWorldClient, methodGetBlock, "(III)L" + classBlock + ";");
			renderGasOverlay.visitVarInsn(ASTORE, 7);
			Label l7 = new Label();
			renderGasOverlay.visitLabel(l7);
			renderGasOverlay.visitVarInsn(ALOAD, 7);
			renderGasOverlay.visitTypeInsn(INSTANCEOF, "glenn/gasesframework/block/BlockGas");
			Label l8 = new Label();
			renderGasOverlay.visitJumpInsn(IFNE, l8);
			renderGasOverlay.visitInsn(RETURN);
			renderGasOverlay.visitLabel(l8);
			renderGasOverlay.visitVarInsn(ALOAD, 7);
			renderGasOverlay.visitTypeInsn(CHECKCAST, "glenn/gasesframework/block/BlockGas");
			renderGasOverlay.visitFieldInsn(GETFIELD, "glenn/gasesframework/block/BlockGas", "type", "Lglenn/gasesframework/api/type/GasType;");
			renderGasOverlay.visitInsn(DUP);
			renderGasOverlay.visitMethodInsn(INVOKEVIRTUAL, "glenn/gasesframework/api/type/GasType", "getOverlayImage", "()L" + classResourceLocation + ";");
			renderGasOverlay.visitVarInsn(ALOAD, 0);
			renderGasOverlay.visitFieldInsn(GETFIELD, classItemRenderer, fieldMc, "L" + classMinecraft + ";");
			renderGasOverlay.visitMethodInsn(INVOKEVIRTUAL, classMinecraft, methodGetTextureManager, "()L" + classTextureManager+ ";");
			renderGasOverlay.visitInsn(SWAP);
			renderGasOverlay.visitMethodInsn(INVOKEVIRTUAL, classTextureManager, methodBindTexture, "(L" + classResourceLocation + ";)V");
			renderGasOverlay.visitFieldInsn(GETFIELD, "glenn/gasesframework/api/type/GasType", "color", "I");
			renderGasOverlay.visitVarInsn(ISTORE, 8);
			Label l10 = new Label();
			renderGasOverlay.visitLabel(l10);
			renderGasOverlay.visitVarInsn(FLOAD, 3);
			renderGasOverlay.visitVarInsn(ILOAD, 8);
			renderGasOverlay.visitIntInsn(BIPUSH, 24);
			renderGasOverlay.visitInsn(ISHR);
			renderGasOverlay.visitIntInsn(SIPUSH, 255);
			renderGasOverlay.visitInsn(IAND);
			renderGasOverlay.visitInsn(I2F);
			renderGasOverlay.visitInsn(FMUL);
			renderGasOverlay.visitLdcInsn(255.0F);
			renderGasOverlay.visitInsn(FDIV);
			renderGasOverlay.visitVarInsn(FSTORE, 9);
			Label l11 = new Label();
			renderGasOverlay.visitLabel(l11);
			renderGasOverlay.visitVarInsn(FLOAD, 3);
			renderGasOverlay.visitVarInsn(ILOAD, 8);
			renderGasOverlay.visitIntInsn(BIPUSH, 16);
			renderGasOverlay.visitInsn(ISHR);
			renderGasOverlay.visitIntInsn(SIPUSH, 255);
			renderGasOverlay.visitInsn(IAND);
			renderGasOverlay.visitInsn(I2F);
			renderGasOverlay.visitInsn(FMUL);
			renderGasOverlay.visitLdcInsn(255.0F);
			renderGasOverlay.visitInsn(FDIV);
			renderGasOverlay.visitVarInsn(FSTORE, 10);
			Label l12 = new Label();
			renderGasOverlay.visitLabel(l12);
			renderGasOverlay.visitVarInsn(FLOAD, 3);
			renderGasOverlay.visitVarInsn(ILOAD, 8);
			renderGasOverlay.visitIntInsn(BIPUSH, 8);
			renderGasOverlay.visitInsn(ISHR);
			renderGasOverlay.visitIntInsn(SIPUSH, 255);
			renderGasOverlay.visitInsn(IAND);
			renderGasOverlay.visitInsn(I2F);
			renderGasOverlay.visitInsn(FMUL);
			renderGasOverlay.visitLdcInsn(255.0F);
			renderGasOverlay.visitInsn(FDIV);
			renderGasOverlay.visitVarInsn(FSTORE, 11);
			Label l13 = new Label();
			renderGasOverlay.visitLabel(l13);
			renderGasOverlay.visitVarInsn(FLOAD, 9);
			renderGasOverlay.visitVarInsn(FLOAD, 10);
			renderGasOverlay.visitVarInsn(FLOAD, 11);
			//renderGasOverlay.visitInsn(FCONST_1);

			renderGasOverlay.visitVarInsn(ILOAD, 8);
			renderGasOverlay.visitIntInsn(SIPUSH, 255);
			renderGasOverlay.visitInsn(IAND);
			renderGasOverlay.visitInsn(I2F);
			renderGasOverlay.visitLdcInsn(255.0F);
			renderGasOverlay.visitInsn(FDIV);
			
			renderGasOverlay.visitMethodInsn(INVOKESTATIC, "org/lwjgl/opengl/GL11", "glColor4f", "(FFFF)V");
			renderGasOverlay.visitIntInsn(SIPUSH, 3042);
			renderGasOverlay.visitMethodInsn(INVOKESTATIC, "org/lwjgl/opengl/GL11", "glEnable", "(I)V");
			renderGasOverlay.visitIntInsn(SIPUSH, 2929);
			renderGasOverlay.visitMethodInsn(INVOKESTATIC, "org/lwjgl/opengl/GL11", "glDisable", "(I)V");
			renderGasOverlay.visitIntInsn(SIPUSH, 770);
			renderGasOverlay.visitIntInsn(SIPUSH, 771);
			renderGasOverlay.visitMethodInsn(INVOKESTATIC, "org/lwjgl/opengl/GL11", "glBlendFunc", "(II)V");
			renderGasOverlay.visitMethodInsn(INVOKESTATIC, "org/lwjgl/opengl/GL11", "glPushMatrix", "()V");
			renderGasOverlay.visitLdcInsn(4.0F);
			renderGasOverlay.visitVarInsn(FSTORE, 12);
			Label l19 = new Label();
			renderGasOverlay.visitLabel(l19);
			renderGasOverlay.visitLdcInsn(-1.0F);
			renderGasOverlay.visitVarInsn(FSTORE, 13);
			Label l20 = new Label();
			renderGasOverlay.visitLabel(l20);
			renderGasOverlay.visitInsn(FCONST_1);
			renderGasOverlay.visitVarInsn(FSTORE, 14);
			Label l21 = new Label();
			renderGasOverlay.visitLabel(l21);
			renderGasOverlay.visitLdcInsn(-1.0F);
			renderGasOverlay.visitVarInsn(FSTORE, 15);
			Label l22 = new Label();
			renderGasOverlay.visitLabel(l22);
			renderGasOverlay.visitInsn(FCONST_1);
			renderGasOverlay.visitVarInsn(FSTORE, 16);
			Label l23 = new Label();
			renderGasOverlay.visitLabel(l23);
			renderGasOverlay.visitLdcInsn(-0.5F);
			renderGasOverlay.visitVarInsn(FSTORE, 17);
			Label l24 = new Label();
			renderGasOverlay.visitLabel(l24);
			renderGasOverlay.visitVarInsn(ALOAD, 0);
			renderGasOverlay.visitFieldInsn(GETFIELD, classItemRenderer, fieldMc, "L" + classMinecraft + ";");
			renderGasOverlay.visitFieldInsn(GETFIELD, classMinecraft, fieldThePlayer, "L" + classEntityClientPlayerMP + ";");
			renderGasOverlay.visitFieldInsn(GETFIELD, classEntityClientPlayerMP, fieldRotationYaw, "F");
			renderGasOverlay.visitInsn(FNEG);
			renderGasOverlay.visitLdcInsn(64.0F);
			renderGasOverlay.visitInsn(FDIV);
			renderGasOverlay.visitVarInsn(FSTORE, 18);
			Label l25 = new Label();
			renderGasOverlay.visitLabel(l25);
			renderGasOverlay.visitVarInsn(ALOAD, 0);
			renderGasOverlay.visitFieldInsn(GETFIELD, classItemRenderer, fieldMc, "L" + classMinecraft + ";");
			renderGasOverlay.visitFieldInsn(GETFIELD, classMinecraft, fieldThePlayer, "L" + classEntityClientPlayerMP + ";");
			renderGasOverlay.visitFieldInsn(GETFIELD, classEntityClientPlayerMP, fieldRotationPitch, "F");
			renderGasOverlay.visitLdcInsn(64.0F);
			renderGasOverlay.visitInsn(FDIV);
			renderGasOverlay.visitVarInsn(FSTORE, 19);
			Label l26 = new Label();
			renderGasOverlay.visitLabel(l26);
			renderGasOverlay.visitVarInsn(ALOAD, 2);
			renderGasOverlay.visitMethodInsn(INVOKEVIRTUAL, classTessellator, methodStartDrawingQuads, "()V");
			renderGasOverlay.visitVarInsn(ALOAD, 2);
			renderGasOverlay.visitVarInsn(FLOAD, 13);
			renderGasOverlay.visitInsn(F2D);
			renderGasOverlay.visitVarInsn(FLOAD, 15);
			renderGasOverlay.visitInsn(F2D);
			renderGasOverlay.visitVarInsn(FLOAD, 17);
			renderGasOverlay.visitInsn(F2D);
			renderGasOverlay.visitVarInsn(FLOAD, 12);
			renderGasOverlay.visitVarInsn(FLOAD, 18);
			renderGasOverlay.visitInsn(FADD);
			renderGasOverlay.visitInsn(F2D);
			renderGasOverlay.visitVarInsn(FLOAD, 12);
			renderGasOverlay.visitVarInsn(FLOAD, 19);
			renderGasOverlay.visitInsn(FADD);
			renderGasOverlay.visitInsn(F2D);
			renderGasOverlay.visitMethodInsn(INVOKEVIRTUAL, classTessellator, methodAddVertexWithUV, "(DDDDD)V");
			renderGasOverlay.visitVarInsn(ALOAD, 2);
			renderGasOverlay.visitVarInsn(FLOAD, 14);
			renderGasOverlay.visitInsn(F2D);
			renderGasOverlay.visitVarInsn(FLOAD, 15);
			renderGasOverlay.visitInsn(F2D);
			renderGasOverlay.visitVarInsn(FLOAD, 17);
			renderGasOverlay.visitInsn(F2D);
			renderGasOverlay.visitInsn(FCONST_0);
			renderGasOverlay.visitVarInsn(FLOAD, 18);
			renderGasOverlay.visitInsn(FADD);
			renderGasOverlay.visitInsn(F2D);
			renderGasOverlay.visitVarInsn(FLOAD, 12);
			renderGasOverlay.visitVarInsn(FLOAD, 19);
			renderGasOverlay.visitInsn(FADD);
			renderGasOverlay.visitInsn(F2D);
			renderGasOverlay.visitMethodInsn(INVOKEVIRTUAL, classTessellator, methodAddVertexWithUV, "(DDDDD)V");
			renderGasOverlay.visitVarInsn(ALOAD, 2);
			renderGasOverlay.visitVarInsn(FLOAD, 14);
			renderGasOverlay.visitInsn(F2D);
			renderGasOverlay.visitVarInsn(FLOAD, 16);
			renderGasOverlay.visitInsn(F2D);
			renderGasOverlay.visitVarInsn(FLOAD, 17);
			renderGasOverlay.visitInsn(F2D);
			renderGasOverlay.visitInsn(FCONST_0);
			renderGasOverlay.visitVarInsn(FLOAD, 18);
			renderGasOverlay.visitInsn(FADD);
			renderGasOverlay.visitInsn(F2D);
			renderGasOverlay.visitInsn(FCONST_0);
			renderGasOverlay.visitVarInsn(FLOAD, 19);
			renderGasOverlay.visitInsn(FADD);
			renderGasOverlay.visitInsn(F2D);
			renderGasOverlay.visitMethodInsn(INVOKEVIRTUAL, classTessellator, methodAddVertexWithUV, "(DDDDD)V");
			renderGasOverlay.visitVarInsn(ALOAD, 2);
			renderGasOverlay.visitVarInsn(FLOAD, 13);
			renderGasOverlay.visitInsn(F2D);
			renderGasOverlay.visitVarInsn(FLOAD, 16);
			renderGasOverlay.visitInsn(F2D);
			renderGasOverlay.visitVarInsn(FLOAD, 17);
			renderGasOverlay.visitInsn(F2D);
			renderGasOverlay.visitVarInsn(FLOAD, 12);
			renderGasOverlay.visitVarInsn(FLOAD, 18);
			renderGasOverlay.visitInsn(FADD);
			renderGasOverlay.visitInsn(F2D);
			renderGasOverlay.visitInsn(FCONST_0);
			renderGasOverlay.visitVarInsn(FLOAD, 19);
			renderGasOverlay.visitInsn(FADD);
			renderGasOverlay.visitInsn(F2D);
			renderGasOverlay.visitMethodInsn(INVOKEVIRTUAL, classTessellator, methodAddVertexWithUV, "(DDDDD)V");
			renderGasOverlay.visitVarInsn(ALOAD, 2);
			renderGasOverlay.visitMethodInsn(INVOKEVIRTUAL, classTessellator, methodDraw, "()I");
			renderGasOverlay.visitInsn(POP);
			renderGasOverlay.visitMethodInsn(INVOKESTATIC, "org/lwjgl/opengl/GL11", "glPopMatrix", "()V");
			renderGasOverlay.visitInsn(FCONST_1);
			renderGasOverlay.visitInsn(FCONST_1);
			renderGasOverlay.visitInsn(FCONST_1);
			renderGasOverlay.visitInsn(FCONST_1);
			renderGasOverlay.visitMethodInsn(INVOKESTATIC, "org/lwjgl/opengl/GL11", "glColor4f", "(FFFF)V");
			renderGasOverlay.visitIntInsn(SIPUSH, 3042);
			renderGasOverlay.visitMethodInsn(INVOKESTATIC, "org/lwjgl/opengl/GL11", "glDisable", "(I)V");
			renderGasOverlay.visitIntInsn(SIPUSH, 2929);
			renderGasOverlay.visitMethodInsn(INVOKESTATIC, "org/lwjgl/opengl/GL11", "glEnable", "(I)V");
			renderGasOverlay.visitInsn(RETURN);
			Label l37 = new Label();
			renderGasOverlay.visitLabel(l37);
			renderGasOverlay.visitLocalVariable("this", "L" + classItemRenderer + ";", null, l0, l37, 0);
			renderGasOverlay.visitLocalVariable("par1", "F", null, l0, l37, 1);
			renderGasOverlay.visitLocalVariable("var2", "L" + classTessellator + ";", null, l2, l37, 2);
			renderGasOverlay.visitLocalVariable("var3", "F", null, l3, l37, 3);
			renderGasOverlay.visitLocalVariable("playerPosX", "I", null, l4, l37, 4);
			renderGasOverlay.visitLocalVariable("playerPosY", "I", null, l5, l37, 5);
			renderGasOverlay.visitLocalVariable("playerPosZ", "I", null, l6, l37, 6);
			renderGasOverlay.visitLocalVariable("block", "L" + classBlock + ";", null, l7, l37, 7);
			renderGasOverlay.visitLocalVariable("color", "I", null, l10, l37, 8);
			renderGasOverlay.visitLocalVariable("red", "F", null, l11, l37, 9);
			renderGasOverlay.visitLocalVariable("green", "F", null, l12, l37, 10);
			renderGasOverlay.visitLocalVariable("blue", "F", null, l13, l37, 11);
			renderGasOverlay.visitLocalVariable("var4", "F", null, l19, l37, 12);
			renderGasOverlay.visitLocalVariable("var5", "F", null, l20, l37, 13);
			renderGasOverlay.visitLocalVariable("var6", "F", null, l21, l37, 14);
			renderGasOverlay.visitLocalVariable("var7", "F", null, l22, l37, 15);
			renderGasOverlay.visitLocalVariable("var8", "F", null, l23, l37, 16);
			renderGasOverlay.visitLocalVariable("var9", "F", null, l24, l37, 17);
			renderGasOverlay.visitLocalVariable("var10", "F", null, l25, l37, 18);
			renderGasOverlay.visitLocalVariable("var11", "F", null, l26, l37, 19);
			renderGasOverlay.visitMaxs(11, 20);
			renderGasOverlay.visitEnd();
		}

		classNode.methods.add(renderGasOverlay);

		ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		classNode.accept(writer);
		return writer.toByteArray();
	}
}