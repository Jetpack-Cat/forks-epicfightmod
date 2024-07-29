package yesman.epicfight.client.renderer.shader;

import java.io.IOException;

import javax.annotation.Nullable;

import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.vertex.VertexFormat;

import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceProvider;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class VanillaAnimationShader extends ShaderInstance implements AnimationShaderInstance {
	@Nullable
	public final Uniform COLOR;
	@Nullable
	public final Uniform UV1;
	@Nullable
	public final Uniform UV2;
	@Nullable
	public final Uniform NORMAL_MODEL_VIEW_MATRIX;
	@Nullable
	public final Uniform[] POSES;
	
	public VanillaAnimationShader(ResourceProvider resourceProvider, ResourceLocation resourceLocation, VertexFormat vertexFormat) throws IOException {
		super(resourceProvider, resourceLocation, vertexFormat);
		
		this.COLOR = this.getUniform("Color");
		this.UV1 = this.getUniform("UV1");
		this.UV2 = this.getUniform("UV2");
		this.NORMAL_MODEL_VIEW_MATRIX = this.getUniform("Normal_Mv_Matrix");
		
		this.POSES = new Uniform[ShaderParser.MAX_JOINTS];
		
		for (int i = 0; i < ShaderParser.MAX_JOINTS; i++) {
			this.POSES[i] = this.getUniform("Poses[" + String.valueOf(i) + "]");
		}
	}
	
	@Override
	public Uniform getColorUniform() {
		return this.COLOR;
	}
	
	@Override
	public Uniform getOverlayUniform() {
		return this.UV1;
	}
	
	@Override
	public Uniform getLightUniform() {
		return this.UV2;
	}
	
	@Override
	public Uniform getNormalMatrixUniform() {
		return this.NORMAL_MODEL_VIEW_MATRIX;
	}
	
	@Override
	public Uniform getPoses(int index) {
		return this.POSES[index];
	}
	
	/**
	 * Vanilla Shader Instance Uniform Gettes
	 */
	@Override
	public Uniform getModelViewMatrixUniform() {
		return this.MODEL_VIEW_MATRIX;
	}

	@Override
	public Uniform getProjectionMatrixUniform() {
		return this.PROJECTION_MATRIX;
	}

	@Override
	public Uniform getInverseViewRotationMatrixUniform() {
		return this.INVERSE_VIEW_ROTATION_MATRIX;
	}

	@Override
	public Uniform getColorModulatorUniform() {
		return this.COLOR_MODULATOR;
	}

	@Override
	public Uniform getGlintAlphaUniform() {
		return this.GLINT_ALPHA;
	}

	@Override
	public Uniform getFogStartUniform() {
		return this.FOG_START;
	}

	@Override
	public Uniform getFogEndUniform() {
		return this.FOG_END;
	}

	@Override
	public Uniform getFogColorUniform() {
		return this.FOG_COLOR;
	}

	@Override
	public Uniform getFogShapeUniform() {
		return this.FOG_SHAPE;
	}

	@Override
	public Uniform getTextureMatrixUniform() {
		return this.TEXTURE_MATRIX;
	}

	@Override
	public Uniform getGameTimeUniform() {
		return this.GAME_TIME;
	}

	@Override
	public Uniform getScreenSizeUniform() {
		return this.SCREEN_SIZE;
	}

	@Override
	public String _getName() {
		return super.getName();
	}
	
	@Override
	public void _apply() {
		this.apply();
	}

	@Override
	public void _clear() {
		this.clear();
	}
	
	@Override
	public void _close() {
		this.close();
	}
	
	@Override
	public VertexFormat _getVertexFormat() {
		return this.getVertexFormat();
	}
}
