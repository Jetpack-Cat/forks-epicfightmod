package yesman.epicfight.client.renderer;

import java.util.Iterator;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.ArrowLayer;
import net.minecraft.client.renderer.entity.layers.BeeStingerLayer;
import net.minecraft.client.renderer.entity.layers.CapeLayer;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.layers.ElytraLayer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.PlayerItemInHandLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.layers.SpinAttackEffectLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.animation.Pose;
import yesman.epicfight.api.client.model.AnimatedMesh.AnimatedModelPart;
import yesman.epicfight.api.client.model.Meshes;
import yesman.epicfight.api.utils.math.MathUtils;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.client.mesh.HumanoidMesh;
import yesman.epicfight.client.renderer.patched.entity.PatchedLivingEntityRenderer;
import yesman.epicfight.client.renderer.patched.layer.EmptyLayer;
import yesman.epicfight.client.renderer.patched.layer.PatchedItemInHandLayer;
import yesman.epicfight.client.renderer.patched.layer.WearableItemLayer;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import yesman.epicfight.gameasset.Armatures;

@OnlyIn(Dist.CLIENT)
public class FirstPersonRenderer extends PatchedLivingEntityRenderer<LocalPlayer, LocalPlayerPatch, PlayerModel<LocalPlayer>, LivingEntityRenderer<LocalPlayer, PlayerModel<LocalPlayer>>, HumanoidMesh> {
	public FirstPersonRenderer(EntityRendererProvider.Context context, EntityType<?> entityType) {
		super(context, entityType);
		
		this.addPatchedLayer(ElytraLayer.class, new EmptyLayer<>());
		this.addPatchedLayer(PlayerItemInHandLayer.class, new PatchedItemInHandLayer<>());
		this.addPatchedLayer(HumanoidArmorLayer.class, new WearableItemLayer<>(Meshes.BIPED, true, context.getModelManager()));
		this.addPatchedLayer(CustomHeadLayer.class, new EmptyLayer<>());
		this.addPatchedLayer(ArrowLayer.class, new EmptyLayer<>());
		this.addPatchedLayer(BeeStingerLayer.class, new EmptyLayer<>());
		this.addPatchedLayer(SpinAttackEffectLayer.class, new EmptyLayer<>());
		this.addPatchedLayer(CapeLayer.class, new EmptyLayer<>());
	}
	
	@Override
	public void render(LocalPlayer entity, LocalPlayerPatch entitypatch, LivingEntityRenderer<LocalPlayer, PlayerModel<LocalPlayer>> renderer, MultiBufferSource buffer, PoseStack poseStack, int packedLight, float partialTicks) {
		Pose pose = entitypatch.getAnimator().getPose(partialTicks);
		OpenMatrix4f[] poses = entitypatch.getArmature().getPoseAsTransformMatrix(pose, false);
		poseStack.pushPose();
		OpenMatrix4f mat = entitypatch.getArmature().getBindedTransformFor(pose, Armatures.BIPED.head);
		mat.translate(0, 0.2F, 0);
		
		Vec3f translateVectorOfHead = mat.toTranslationVector();
		poseStack.translate(-translateVectorOfHead.x, -translateVectorOfHead.y, -translateVectorOfHead.z);
		HumanoidMesh mesh = this.getMesh(entitypatch);
		this.prepareModel(mesh, entity, entitypatch, renderer);
		
		if (!entitypatch.getOriginal().isInvisible()) {
			for (AnimatedModelPart p : mesh.getAllParts()) {
				p.setHidden(true);
			}
			
			mesh.leftArm.setHidden(false);
			mesh.rightArm.setHidden(false);
			mesh.leftSleeve.setHidden(false);
			mesh.rightSleeve.setHidden(false);
			
			RenderType renderType = RenderType.entityCutoutNoCull(entity.getSkinTextureLocation());
			mesh.draw(poseStack, buffer, renderType, packedLight, 1.0F, 1.0F, 1.0F, 1.0F, OverlayTexture.NO_OVERLAY, entitypatch.getArmature(), poses);
		}
		
		if (!entity.isSpectator()) {
			renderLayer(renderer, entitypatch, entity, poses, buffer, poseStack, packedLight, partialTicks);
		}
		
		poseStack.popPose();
	}
	
	@Override
	protected void renderLayer(LivingEntityRenderer<LocalPlayer, PlayerModel<LocalPlayer>> renderer, LocalPlayerPatch entitypatch, LocalPlayer entity, OpenMatrix4f[] poses, MultiBufferSource buffer, PoseStack poseStack, int packedLight, float partialTicks) {
		Iterator<RenderLayer<LocalPlayer, PlayerModel<LocalPlayer>>> iter = renderer.layers.iterator();
		
		float f = MathUtils.lerpBetween(entity.yBodyRotO, entity.yBodyRot, partialTicks);
        float f1 = MathUtils.lerpBetween(entity.yHeadRotO, entity.yHeadRot, partialTicks);
        float f2 = f1 - f;
		float f7 = entity.getViewXRot(partialTicks);
		float bob = this.getVanillaRendererBob(entity, renderer, partialTicks);
		
		while (iter.hasNext()) {
			RenderLayer<LocalPlayer, PlayerModel<LocalPlayer>> layer = iter.next();
			Class<?> rendererClass = layer.getClass();
			
			if (rendererClass.isAnonymousClass()) {
				rendererClass = rendererClass.getSuperclass();
			}
			
			if (this.patchedLayers.containsKey(rendererClass)) {
				this.patchedLayers.get(rendererClass).renderLayer(entity, entitypatch, layer, poseStack, buffer, packedLight, poses, bob, f2, f7, partialTicks);
			}
		}
	}
	
	@Override
	public HumanoidMesh getMesh(LocalPlayerPatch entitypatch) {
		return entitypatch.getOriginal().getModelName().equals("slim") ? Meshes.ALEX : Meshes.BIPED;
	}
	
	@Override
	protected void prepareModel(HumanoidMesh mesh, LocalPlayer entity, LocalPlayerPatch entitypatch, LivingEntityRenderer<LocalPlayer, PlayerModel<LocalPlayer>> renderer) {
		mesh.initialize();
		mesh.head.setHidden(true);
		mesh.hat.setHidden(true);
	}
}