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

		if(className.equals(c.get("Entity")))
		{
			FMLLog.fine("[GasesFrameworkCore]Patching obfuscated class: %s(Entity)...", className);
			newData = patchClassEntity(data, true);
		}
		else if(className.equals("net.minecraft.entity.Entity"))
		{
			FMLLog.fine("[GasesFrameworkCore]Patching class: %s(Entity)...", className);
			newData = patchClassEntity(data, false);
		}
		else if(className.equals(c.get("EntityLivingBase")))
		{
			FMLLog.fine("[GasesFrameworkCore]Patching obfuscated class: %s(EntityLivingBase)...", className);
			newData = patchClassEntityLivingBase(data, true);
		}
		else if(className.equals("net.minecraft.entity.EntityLivingBase"))
		{
			FMLLog.fine("[GasesFrameworkCore]Patching class: %s(EntityLivingBase)...", className);
			newData = patchClassEntityLivingBase(data, false);
		}

		if(newData != data)
		{
			FMLLog.fine("[GasesFrameworkCore]Patch OK!");
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
						newInstructions.add(new FieldInsnNode(GETFIELD, "glenn/gasesframework/block/BlockGas", "type", "Lglenn/gasesframework/api/gastype/GasType;"));
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
						newInstructions.add(new MethodInsnNode(INVOKEVIRTUAL, "glenn/gasesframework/api/gastype/GasType", "getMinY", "(L" + interfaceBlockAccess + ";IIII)D"));
						newInstructions.add(new InsnNode(DCMPL));
						newInstructions.add(new JumpInsnNode(IFLE, l8));
						newInstructions.add(new VarInsnNode(DLOAD, 2));
						newInstructions.add(new VarInsnNode(ILOAD, 5));
						newInstructions.add(new InsnNode(I2D));
						newInstructions.add(new InsnNode(DSUB));
						newInstructions.add(new VarInsnNode(ALOAD, 7));
						newInstructions.add(new TypeInsnNode(CHECKCAST, "glenn/gasesframework/block/BlockGas"));
						newInstructions.add(new FieldInsnNode(GETFIELD, "glenn/gasesframework/block/BlockGas", "type", "Lglenn/gasesframework/api/gastype/GasType;"));
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
						newInstructions.add(new MethodInsnNode(INVOKEVIRTUAL, "glenn/gasesframework/api/gastype/GasType", "getMaxY", "(L" + interfaceBlockAccess + ";IIII)D"));
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
}