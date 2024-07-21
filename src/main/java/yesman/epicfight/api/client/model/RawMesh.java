package yesman.epicfight.api.client.model;

import java.util.List;
import java.util.Map;

import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.client.model.RawMesh.RawModelPart;
import yesman.epicfight.main.EpicFightMod;

@OnlyIn(Dist.CLIENT)
public class RawMesh extends Mesh<RawModelPart, BlenderVertexBuilder> {
	public RawMesh(Map<String, float[]> arrayMap, Map<String, List<BlenderVertexBuilder>> partBuilders, RawMesh parent, RenderProperties properties) {
		super(arrayMap, partBuilders, parent, properties);
	}
	
	@Override
	protected Map<String, RawModelPart> createModelPart(Map<String, List<BlenderVertexBuilder>> partBuilders) {
		Map<String, RawModelPart> parts = Maps.newHashMap();
		
		partBuilders.forEach((partName, vertexBuilder) -> {
			parts.put(partName, new RawModelPart(vertexBuilder));
		});
		
		return parts;
	}
	
	@Override
	protected RawModelPart getOrLogException(Map<String, RawModelPart> parts, String name) {
		if (!parts.containsKey(name)) {
			EpicFightMod.LOGGER.debug("Can not find the mesh part named " + name + " in " + this.getClass().getCanonicalName());
			return null;
		}
		
		return parts.get(name);
	}
	
	public void draw(PoseStack poseStack, VertexConsumer builder, Mesh.DrawingFunction drawingFunction, int packedLight, float r, float g, float b, float a, int overlay) {
		for (RawModelPart part : this.parts.values()) {
			part.draw(poseStack, builder, drawingFunction, packedLight, r, g, b, a, overlay);
		}
	}
	
	@OnlyIn(Dist.CLIENT)
	public class RawModelPart extends ModelPart<BlenderVertexBuilder> {
		public RawModelPart(List<BlenderVertexBuilder> verticies) {
			super(verticies);
		}
		
		public void draw(PoseStack poseStack, VertexConsumer builder, Mesh.DrawingFunction drawingFunction, int packedLight, float r, float g, float b, float a, int overlay) {
			if (!this.isHidden()) {
				return;
			}
			
			Matrix4f matrix4f = poseStack.last().pose();
			Matrix3f matrix3f = poseStack.last().normal();
			
			for (BlenderVertexBuilder vi : this.getVertices()) {
				int pos = vi.position * 3;
				int norm = vi.normal * 3;
				int uv = vi.uv * 2;
				Vector4f posVec = new Vector4f(positions[pos], positions[pos + 1], positions[pos + 2], 1.0F);
				Vector3f normVec = new Vector3f(normals[norm], normals[norm + 1], normals[norm + 2]);
				posVec.mul(matrix4f);
				normVec.mul(matrix3f);
				drawingFunction.draw(builder, posVec, normVec, packedLight, r, g, b, a, uvs[uv], uvs[uv + 1], overlay);
			}
		}
	}
}