package systems.thedawn.espresso.client.render;

import com.mojang.blaze3d.vertex.VertexConsumer;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;

public final class RenderUtil {
    static void renderCuboid(VertexConsumer vertexConsumer, Matrix4f transform, TextureAtlasSprite texture, float x0, float y0, float z0, float x1, float y1, float z1, int color, int packedLight, int packedOverlay) {
        renderFace(vertexConsumer, transform, texture, x0, z0, x1, z1, y0, Direction.DOWN, color, packedLight, packedOverlay);
        renderFace(vertexConsumer, transform, texture, x0, z0, x1, z1, y1, Direction.UP, color, packedLight, packedOverlay);
        renderFace(vertexConsumer, transform, texture, x0, y0, x1, y1, z0, Direction.NORTH, color, packedLight, packedOverlay);
        renderFace(vertexConsumer, transform, texture, x0, y0, x1, y1, z1, Direction.SOUTH, color, packedLight, packedOverlay);
        renderFace(vertexConsumer, transform, texture, z0, y0, z1, y1, x0, Direction.WEST, color, packedLight, packedOverlay);
        renderFace(vertexConsumer, transform, texture, z0, y0, z1, y1, x1, Direction.EAST, color, packedLight, packedOverlay);
    }

    static void renderFace(VertexConsumer vertexConsumer, Matrix4f transform, TextureAtlasSprite texture, float su0, float sv0, float su1, float sv1, float c, Direction normal, int color, int packedLight, int packedOverlay) {
        var n = normal.getNormal();
        var scratch = new Vector3f();
        float x0, y0, z0, x1, y1, z1, x2, y2, z2, x3, y3, z3;
        var texU0 = texture.getU(su0);
        var texV0 = texture.getV(sv0);
        var texU1 = texture.getU(su1);
        var texV1 = texture.getV(sv1);
        switch(normal) {
            case UP -> {
                scratch = transform.transformPosition(su0, c, sv0, scratch);
                x0 = scratch.x;
                y0 = scratch.y;
                z0 = scratch.z;
                scratch = transform.transformPosition(su0, c, sv1, scratch);
                x1 = scratch.x;
                y1 = scratch.y;
                z1 = scratch.z;
                scratch = transform.transformPosition(su1, c, sv1, scratch);
                x2 = scratch.x;
                y2 = scratch.y;
                z2 = scratch.z;
                scratch = transform.transformPosition(su1, c, sv0, scratch);
                x3 = scratch.x;
                y3 = scratch.y;
                z3 = scratch.z;
            }
            case DOWN -> {
                scratch = transform.transformPosition(su1, c, sv0, scratch);
                x0 = scratch.x;
                y0 = scratch.y;
                z0 = scratch.z;
                scratch = transform.transformPosition(su1, c, sv1, scratch);
                x1 = scratch.x;
                y1 = scratch.y;
                z1 = scratch.z;
                scratch = transform.transformPosition(su0, c, sv1, scratch);
                x2 = scratch.x;
                y2 = scratch.y;
                z2 = scratch.z;
                scratch = transform.transformPosition(su0, c, sv0, scratch);
                x3 = scratch.x;
                y3 = scratch.y;
                z3 = scratch.z;
            }
            case SOUTH -> {
                scratch = transform.transformPosition(su1, sv0, c, scratch);
                x0 = scratch.x;
                y0 = scratch.y;
                z0 = scratch.z;
                scratch = transform.transformPosition(su1, sv1, c, scratch);
                x1 = scratch.x;
                y1 = scratch.y;
                z1 = scratch.z;
                scratch = transform.transformPosition(su0, sv1, c, scratch);
                x2 = scratch.x;
                y2 = scratch.y;
                z2 = scratch.z;
                scratch = transform.transformPosition(su0, sv0, c, scratch);
                x3 = scratch.x;
                y3 = scratch.y;
                z3 = scratch.z;
            }
            case NORTH -> {
                scratch = transform.transformPosition(su0, sv0, c, scratch);
                x0 = scratch.x;
                y0 = scratch.y;
                z0 = scratch.z;
                scratch = transform.transformPosition(su0, sv1, c, scratch);
                x1 = scratch.x;
                y1 = scratch.y;
                z1 = scratch.z;
                scratch = transform.transformPosition(su1, sv1, c, scratch);
                x2 = scratch.x;
                y2 = scratch.y;
                z2 = scratch.z;
                scratch = transform.transformPosition(su1, sv0, c, scratch);
                x3 = scratch.x;
                y3 = scratch.y;
                z3 = scratch.z;
            }
            case WEST -> {
                scratch = transform.transformPosition(c, sv0, su1, scratch);
                x0 = scratch.x;
                y0 = scratch.y;
                z0 = scratch.z;
                scratch = transform.transformPosition(c, sv1, su1, scratch);
                x1 = scratch.x;
                y1 = scratch.y;
                z1 = scratch.z;
                scratch = transform.transformPosition(c, sv1, su0, scratch);
                x2 = scratch.x;
                y2 = scratch.y;
                z2 = scratch.z;
                scratch = transform.transformPosition(c, sv0, su0, scratch);
                x3 = scratch.x;
                y3 = scratch.y;
                z3 = scratch.z;
            }
            case EAST -> {
                scratch = transform.transformPosition(c, sv0, su0, scratch);
                x0 = scratch.x;
                y0 = scratch.y;
                z0 = scratch.z;
                scratch = transform.transformPosition(c, sv1, su0, scratch);
                x1 = scratch.x;
                y1 = scratch.y;
                z1 = scratch.z;
                scratch = transform.transformPosition(c, sv1, su1, scratch);
                x2 = scratch.x;
                y2 = scratch.y;
                z2 = scratch.z;
                scratch = transform.transformPosition(c, sv0, su1, scratch);
                x3 = scratch.x;
                y3 = scratch.y;
                z3 = scratch.z;
            }
            default -> throw new AssertionError();
        }
        vertexConsumer.addVertex(x0, y0, z0, color, texU0, texV0, packedOverlay, packedLight, n.getX(), n.getY(), n.getZ());
        vertexConsumer.addVertex(x1, y1, z1, color, texU0, texV1, packedOverlay, packedLight, n.getX(), n.getY(), n.getZ());
        vertexConsumer.addVertex(x2, y2, z2, color, texU1, texV1, packedOverlay, packedLight, n.getX(), n.getY(), n.getZ());
        vertexConsumer.addVertex(x3, y3, z3, color, texU1, texV0, packedOverlay, packedLight, n.getX(), n.getY(), n.getZ());
    }
}
